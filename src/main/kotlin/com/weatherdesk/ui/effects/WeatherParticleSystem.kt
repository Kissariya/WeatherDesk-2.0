package com.weatherdesk.ui.effects

import com.weatherdesk.model.WeatherCondition
import javafx.animation.AnimationTimer
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.effect.BlurType
import javafx.scene.effect.DropShadow
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import kotlin.math.*
import kotlin.random.Random

/**
 * Advanced particle system for weather effects
 * Renders rain, snow, lightning, clouds, and ambient particles
 */
class WeatherParticleSystem(private val parent: Pane) {

    private val canvas = Canvas()
    private val gc: GraphicsContext = canvas.graphicsContext2D
    private val particles = mutableListOf<Particle>()
    private var animationTimer: AnimationTimer? = null
    private var currentCondition: WeatherCondition = WeatherCondition.CLEAR

    // Configuration
    private var maxParticles = 100
    private var spawnRate = 3
    private var time = 0L

    init {
        canvas.widthProperty().bind(parent.widthProperty())
        canvas.heightProperty().bind(parent.heightProperty())
        canvas.isMouseTransparent = true

        parent.children.add(0, canvas) // Add to back
    }

    /**
     * Base particle class
     */
    abstract class Particle(
        var x: Double,
        var y: Double,
        var vx: Double = 0.0,
        var vy: Double = 0.0,
        var life: Double = 1.0,
        var maxLife: Double = 1.0,
        var size: Double = 5.0,
        var color: Color = Color.WHITE
    ) {
        abstract fun update(deltaTime: Double, width: Double, height: Double)
        abstract fun draw(gc: GraphicsContext)

        fun isDead(canvasHeight: Double): Boolean = life <= 0 || y > canvasHeight + 50
    }

    /**
     * Raindrop particle
     */
    class RainDrop(x: Double, y: Double) : Particle(
        x, y,
        vx = Random.nextDouble(-1.0, 1.0),
        vy = Random.nextDouble(400.0, 600.0),
        life = 1.0,
        maxLife = 1.0,
        size = Random.nextDouble(1.0, 3.0),
        color = Color.rgb(100, 150, 255, 0.6)
    ) {
        override fun update(deltaTime: Double, width: Double, height: Double) {
            x += vx * deltaTime
            y += vy * deltaTime
            life -= deltaTime * 0.5
        }

        override fun draw(gc: GraphicsContext) {
            val alpha = life * 0.6
            gc.stroke = Color.rgb(100, 150, 255, alpha)
            gc.lineWidth = size
            gc.strokeLine(x, y, x + vx * 0.1, y + vy * 0.05)
        }
    }

    /**
     * Snowflake particle
     */
    class Snowflake(x: Double, y: Double) : Particle(
        x, y,
        vx = Random.nextDouble(-20.0, 20.0),
        vy = Random.nextDouble(30.0, 80.0),
        life = 1.0,
        maxLife = 1.0,
        size = Random.nextDouble(2.0, 6.0),
        color = Color.WHITE
    ) {
        private val rotation = Random.nextDouble(0.0, 360.0)
        private val rotationSpeed = Random.nextDouble(-50.0, 50.0)
        private var currentRotation = rotation

        override fun update(deltaTime: Double, width: Double, height: Double) {
            // Drift left and right
            vx += sin(y * 0.01) * 5
            x += vx * deltaTime
            y += vy * deltaTime
            currentRotation += rotationSpeed * deltaTime
            life -= deltaTime * 0.3
        }

        override fun draw(gc: GraphicsContext) {
            gc.save()
            gc.translate(x, y)
            gc.rotate(currentRotation)

            val alpha = life * 0.9
            gc.fill = Color.rgb(255, 255, 255, alpha)
            gc.stroke = Color.rgb(200, 220, 255, alpha * 0.5)
            gc.lineWidth = 1.0

            // Draw snowflake shape
            for (i in 0..5) {
                val angle = Math.toRadians(i * 60.0)
                val x1 = cos(angle) * size
                val y1 = sin(angle) * size
                gc.strokeLine(0.0, 0.0, x1, y1)

                // Add branches
                val x2 = cos(angle) * size * 0.6
                val y2 = sin(angle) * size * 0.6
                val branchAngle1 = angle + Math.toRadians(30.0)
                val branchAngle2 = angle - Math.toRadians(30.0)
                gc.strokeLine(x2, y2, x2 + cos(branchAngle1) * size * 0.3, y2 + sin(branchAngle1) * size * 0.3)
                gc.strokeLine(x2, y2, x2 + cos(branchAngle2) * size * 0.3, y2 + sin(branchAngle2) * size * 0.3)
            }

            gc.restore()
        }
    }

    /**
     * Cloud particle
     */
    class CloudPuff(x: Double, y: Double) : Particle(
        x, y,
        vx = Random.nextDouble(5.0, 15.0),
        vy = Random.nextDouble(-2.0, 2.0),
        life = 1.0,
        maxLife = 1.0,
        size = Random.nextDouble(40.0, 80.0),
        color = Color.rgb(255, 255, 255, 0.3)
    ) {
        override fun update(deltaTime: Double, width: Double, height: Double) {
            x += vx * deltaTime
            y += vy * deltaTime
            life -= deltaTime * 0.1

            // Wrap around
            if (x > width + size) {
                x = -size
            }
        }

        override fun draw(gc: GraphicsContext) {
            val alpha = (life * 0.3).coerceIn(0.0, 0.3)
            gc.fill = Color.rgb(255, 255, 255, alpha)

            // Draw fluffy cloud shape
            gc.fillOval(x - size / 2, y - size / 3, size, size * 0.6)
            gc.fillOval(x - size / 3, y - size / 2, size * 0.7, size * 0.7)
            gc.fillOval(x, y - size / 3, size * 0.8, size * 0.5)
        }
    }

    /**
     * Star particle for night sky
     */
    class Star(x: Double, y: Double) : Particle(
        x, y,
        life = Random.nextDouble(0.5, 1.0),
        maxLife = 1.0,
        size = Random.nextDouble(1.0, 3.0),
        color = Color.WHITE
    ) {
        private val twinkleSpeed = Random.nextDouble(0.5, 2.0)
        private var twinklePhase = Random.nextDouble(0.0, Math.PI * 2)

        override fun update(deltaTime: Double, width: Double, height: Double) {
            twinklePhase += twinkleSpeed * deltaTime
        }

        override fun draw(gc: GraphicsContext) {
            val alpha = (sin(twinklePhase) * 0.3 + 0.7) * life
            gc.fill = Color.rgb(255, 255, 255, alpha)

            // Draw star with glow
            val glow = DropShadow(BlurType.GAUSSIAN, Color.rgb(255, 255, 255, alpha * 0.5), size * 2, 0.8, 0.0, 0.0)
            gc.save()
            gc.setEffect(glow)
            gc.fillOval(x - size / 2, y - size / 2, size, size)
            gc.restore()
        }
    }

    /**
     * Lightning bolt particle
     */
    class Lightning(startX: Double, startY: Double, endY: Double) : Particle(
        startX, startY,
        life = 0.3,
        maxLife = 0.3,
        size = 3.0,
        color = Color.rgb(255, 255, 100)
    ) {
        private val segments = mutableListOf<Pair<Double, Double>>()

        init {
            // Generate lightning path
            var currentX = startX
            var currentY = startY
            segments.add(Pair(currentX, currentY))

            while (currentY < endY) {
                currentX += Random.nextDouble(-30.0, 30.0)
                currentY += Random.nextDouble(20.0, 50.0)
                segments.add(Pair(currentX, currentY))
            }
        }

        override fun update(deltaTime: Double, width: Double, height: Double) {
            life -= deltaTime
        }

        override fun draw(gc: GraphicsContext) {
            val alpha = (life / maxLife).coerceIn(0.0, 1.0)
            gc.stroke = Color.rgb(255, 255, 200, alpha)
            gc.lineWidth = size

            // Draw main bolt
            for (i in 0 until segments.size - 1) {
                val (x1, y1) = segments[i]
                val (x2, y2) = segments[i + 1]
                gc.strokeLine(x1, y1, x2, y2)
            }

            // Draw glow
            gc.stroke = Color.rgb(255, 255, 255, alpha * 0.5)
            gc.lineWidth = size * 3
            for (i in 0 until segments.size - 1) {
                val (x1, y1) = segments[i]
                val (x2, y2) = segments[i + 1]
                gc.strokeLine(x1, y1, x2, y2)
            }
        }
    }

    /**
     * Ambient light particle (for sunny days)
     */
    class LightParticle(x: Double, y: Double) : Particle(
        x, y,
        vx = Random.nextDouble(-10.0, 10.0),
        vy = Random.nextDouble(-10.0, 10.0),
        life = Random.nextDouble(2.0, 5.0),
        maxLife = 5.0,
        size = Random.nextDouble(5.0, 15.0),
        color = Color.rgb(255, 235, 150, 0.3)
    ) {
        override fun update(deltaTime: Double, width: Double, height: Double) {
            x += vx * deltaTime
            y += vy * deltaTime
            life -= deltaTime * 0.2

            // Wrap around
            if (x < 0) x = width
            if (x > width) x = 0.0
            if (y < 0) y = height
            if (y > height) y = 0.0
        }

        override fun draw(gc: GraphicsContext) {
            val alpha = (life / maxLife * 0.3).coerceIn(0.0, 0.3)
            gc.fill = Color.rgb(255, 235, 150, alpha)
            gc.fillOval(x - size / 2, y - size / 2, size, size)
        }
    }

    /**
     * Start the particle system
     */
    fun start(condition: WeatherCondition) {
        stop()
        currentCondition = condition
        configureForWeather(condition)

        animationTimer = object : AnimationTimer() {
            private var lastTime = 0L

            override fun handle(now: Long) {
                if (lastTime == 0L) {
                    lastTime = now
                    return
                }

                val deltaTime = (now - lastTime) / 1_000_000_000.0
                lastTime = now
                time += (deltaTime * 1000).toLong()

                update(deltaTime)
                render()
            }
        }
        animationTimer?.start()
    }

    /**
     * Configure particle system based on weather
     */
    private fun configureForWeather(condition: WeatherCondition) {
        particles.clear()
        when (condition) {
            WeatherCondition.RAIN, WeatherCondition.SHOWER_RAIN -> {
                maxParticles = 200
                spawnRate = 5
            }
            WeatherCondition.THUNDERSTORM -> {
                maxParticles = 150
                spawnRate = 4
            }
            WeatherCondition.SNOW -> {
                maxParticles = 100
                spawnRate = 3
            }
            WeatherCondition.CLOUDS, WeatherCondition.BROKEN_CLOUDS -> {
                maxParticles = 20
                spawnRate = 1
            }
            WeatherCondition.CLEAR -> {
                maxParticles = 50
                spawnRate = 2
            }
            else -> {
                maxParticles = 30
                spawnRate = 1
            }
        }
    }

    /**
     * Update particles
     */
    private fun update(deltaTime: Double) {
        val width = canvas.width
        val height = canvas.height

        // Update existing particles
        particles.forEach { it.update(deltaTime, width, height) }
        particles.removeAll { it.isDead(canvas.height) }

        // Spawn new particles
        repeat(spawnRate) {
            if (particles.size < maxParticles) {
                spawnParticle()
            }
        }

        // Special effects
        when (currentCondition) {
            WeatherCondition.THUNDERSTORM -> {
                // Random lightning
                if (Random.nextDouble() < 0.005) {
                    val x = Random.nextDouble(width)
                    particles.add(Lightning(x, 0.0, height))
                }
            }
            else -> {}
        }
    }

    /**
     * Spawn a particle based on current weather
     */
    private fun spawnParticle() {
        val width = canvas.width
        val height = canvas.height
        val x = Random.nextDouble(width)

        val particle = when (currentCondition) {
            WeatherCondition.RAIN, WeatherCondition.SHOWER_RAIN ->
                RainDrop(x, -10.0)
            WeatherCondition.THUNDERSTORM ->
                RainDrop(x, -10.0)
            WeatherCondition.SNOW ->
                Snowflake(x, -10.0)
            WeatherCondition.CLOUDS, WeatherCondition.BROKEN_CLOUDS ->
                CloudPuff(x, Random.nextDouble(0.0, height * 0.3))
            WeatherCondition.CLEAR ->
                LightParticle(Random.nextDouble(width), Random.nextDouble(height))
            else ->
                LightParticle(Random.nextDouble(width), Random.nextDouble(height))
        }

        particles.add(particle)
    }

    /**
     * Render all particles
     */
    private fun render() {
        gc.clearRect(0.0, 0.0, canvas.width, canvas.height)
        particles.forEach { it.draw(gc) }
    }

    /**
     * Stop the particle system
     */
    fun stop() {
        animationTimer?.stop()
        animationTimer = null
        particles.clear()
    }

    /**
     * Cleanup
     */
    fun dispose() {
        stop()
        parent.children.remove(canvas)
    }
}
