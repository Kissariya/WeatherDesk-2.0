package com.weatherdesk.ui.animations

import javafx.animation.*
import javafx.scene.Node
import javafx.util.Duration
import kotlin.math.pow
import kotlin.math.sin

object ElasticOutInterpolator : Interpolator() {
    override fun curve(t: Double): Double {
        return if (t == 0.0 || t == 1.0) {
            t
        } else {
            val p = 0.3
            val s = p / 4
            (2.0.pow(-10 * t) * sin((t - s) * (2 * Math.PI) / p) + 1)
        }
    }
}

/**
 * UI Animation utilities to add "wow" factor and keep users engaged
 * Includes fade-in, slide-in, pulse, and rotation effects
 */
object UIAnimations {
    
    /**
     * Fade in animation - smooth entrance
     */
    fun fadeIn(node: Node, duration: Double = 500.0, delay: Double = 0.0) {
        node.opacity = 0.0
        val fadeIn = FadeTransition(Duration.millis(duration), node)
        fadeIn.fromValue = 0.0
        fadeIn.toValue = 1.0
        fadeIn.delay = Duration.millis(delay)
        fadeIn.interpolator = Interpolator.EASE_BOTH
        fadeIn.play()
    }
    
    /**
     * Slide in from top - premium feel
     */
    fun slideInFromTop(node: Node, duration: Double = 600.0, delay: Double = 0.0) {
        val startY = node.translateY
        node.translateY = -50.0
        node.opacity = 0.0
        
        val timeline = Timeline(
            KeyFrame(Duration.millis(0.0),
                KeyValue(node.translateYProperty(), -50.0),
                KeyValue(node.opacityProperty(), 0.0)
            ),
            KeyFrame(Duration.millis(duration),
                KeyValue(node.translateYProperty(), startY, Interpolator.EASE_OUT),
                KeyValue(node.opacityProperty(), 1.0, Interpolator.EASE_IN)
            )
        )
        timeline.delay = Duration.millis(delay)
        timeline.play()
    }
    
    /**
     * Pulse animation - draws attention
     */
    fun pulse(node: Node, repeat: Boolean = true) {
        val timeline = Timeline(
            KeyFrame(Duration.ZERO,
                KeyValue(node.scaleXProperty(), 1.0),
                KeyValue(node.scaleYProperty(), 1.0)
            ),
            KeyFrame(Duration.millis(1000.0),
                KeyValue(node.scaleXProperty(), 1.05, Interpolator.EASE_BOTH),
                KeyValue(node.scaleYProperty(), 1.05, Interpolator.EASE_BOTH)
            ),
            KeyFrame(Duration.millis(2000.0),
                KeyValue(node.scaleXProperty(), 1.0, Interpolator.EASE_BOTH),
                KeyValue(node.scaleYProperty(), 1.0, Interpolator.EASE_BOTH)
            )
        )
        timeline.cycleCount = if (repeat) Animation.INDEFINITE else 1
        timeline.play()
    }
    
    /**
     * Scale up animation - pop effect
     */
    fun scaleUp(node: Node, duration: Double = 400.0) {
        node.scaleX = 0.0
        node.scaleY = 0.0
        
        val timeline = Timeline(
            KeyFrame(
                Duration.millis(duration),
                KeyValue(node.scaleXProperty(), 1.0, ElasticOutInterpolator),
                KeyValue(node.scaleYProperty(), 1.0, ElasticOutInterpolator)
            )
        )
        timeline.play()
    }
    
    /**
     * Fade out and remove - smooth exit
     */
    fun fadeOut(node: Node, duration: Double = 300.0, onFinish: () -> Unit = {}) {
        val fadeOut = FadeTransition(Duration.millis(duration), node)
        fadeOut.fromValue = 1.0
        fadeOut.toValue = 0.0
        fadeOut.interpolator = Interpolator.EASE_BOTH
        fadeOut.setOnFinished { onFinish() }
        fadeOut.play()
    }
    
    /**
     * Transition fade - crossfade between content
     */
    fun transitionFade(oldNode: Node, newNode: Node, duration: Double = 500.0) {
        // Fade out old
        val fadeOut = FadeTransition(Duration.millis(duration / 2), oldNode)
        fadeOut.fromValue = 1.0
        fadeOut.toValue = 0.0
        
        // Fade in new
        newNode.opacity = 0.0
        val fadeIn = FadeTransition(Duration.millis(duration / 2), newNode)
        fadeIn.fromValue = 0.0
        fadeIn.toValue = 1.0
        fadeIn.delay = Duration.millis(duration / 2)
        
        fadeOut.play()
        fadeIn.play()
    }
}
