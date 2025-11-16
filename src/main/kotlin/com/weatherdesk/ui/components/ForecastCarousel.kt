package com.weatherdesk.ui.components

import com.weatherdesk.model.*
import javafx.animation.*
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.effect.DropShadow
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.util.Duration
import kotlin.math.*

/**
 * 3D Swipeable carousel for forecast cards.
 * Cards appear from a 3D perspective with smooth animations
 */
class ForecastCarousel : StackPane() {

    private val cardsContainer = HBox(20.0)
    private val cards = mutableListOf<ForecastCard>()
    private val loadingLabel = Label("Loading forecast...").apply {
        font = Font.font("System", FontWeight.BOLD, 20.0)
        textFill = Color.WHITE
        opacity = 0.8
        isVisible = false
    }

    private val emptyLabel = Label("No forecast data available").apply {
        font = Font.font("System", FontWeight.NORMAL, 16.0)
        textFill = Color.LIGHTGRAY
        opacity = 0.8
        isVisible = false
    }

    private val errorLabel = Label("Error loading forecast data").apply {
        font = Font.font("System", FontWeight.NORMAL, 16.0)
        textFill = Color.rgb(255, 100, 100)  // Light red for error
        opacity = 0.8
        isVisible = false
    }

    private var currentIndex = 0
    private var isLoading = false
    var onUserInteraction: (() -> Unit)? = null

    // Gesture tracking
    private var startX = 0.0
    private var isDragging = false

    init {
        prefWidth = 800.0
        prefHeight = 300.0

        cardsContainer.alignment = Pos.CENTER
        children.add(cardsContainer)
        children.add(loadingLabel)
        children.add(emptyLabel)
        children.add(errorLabel)

        setupGestureHandling()
    }

    /**
     * Set forecast data and create cards
     */
    fun setForecasts(forecasts: List<DailyForecast>, unit: TemperatureUnit) {
        // Show loading indicator
        showLoading()
        cards.clear()
        cardsContainer.children.clear()

        forecasts.forEachIndexed { index, forecast ->
            val card = ForecastCard(forecast, unit)
            cards.add(card)
            cardsContainer.children.add(card)

            // Apply 3D perspective effect
            apply3DPerspective(card, index)
        }

        if (cards.isNotEmpty()) {
            highlightCard(0)
        }

        // Hide loading indicator
        hideLoading()
    }

    /**
     * Apply 3D perspective transformation
     */
    private fun apply3DPerspective(card: ForecastCard, index: Int) {
        val offset = index - currentIndex
        val translateX = offset * 250.0
        val scale = max(0.7, 1.0 - abs(offset) * 0.15)
        val rotateY = offset * -15.0

        card.translateX = translateX
        card.scaleX = scale
        card.scaleY = scale
        card.opacity = max(0.4, 1.0 - abs(offset) * 0.3)
        card.rotate = rotateY
    }

    /**
     * Setup swipe gesture handling
     */
    private fun setupGestureHandling() {
        setOnMousePressed { event ->
            startX = event.x
            isDragging = true
            onUserInteraction?.invoke()
        }

        setOnMouseDragged { event ->
            if (isDragging) {
                onUserInteraction?.invoke()
                val deltaX = event.x - startX
                // Visual feedback during drag
                cardsContainer.translateX = deltaX * 0.5
            }
        }

        setOnMouseReleased { event ->
            if (isDragging) {
                onUserInteraction?.invoke()
                val deltaX = event.x - startX
                if (abs(deltaX) > 50) {
                    if (deltaX > 0 && currentIndex > 0) {
                        showPrevious()
                    } else if (deltaX < 0 && currentIndex < cards.size - 1) {
                        showNext()
                    }
                }
                animateToPosition()
                isDragging = false
            }
        }

        // Arrow key support
        setOnKeyPressed { event ->
            when (event.code.toString()) {
                "LEFT" -> { showPrevious(); onUserInteraction?.invoke()}
                "RIGHT" -> { showNext(); onUserInteraction?.invoke()}
            }
        }

        setOnMouseDragged { event ->
            if (isDragging) {
                onUserInteraction?.invoke()
                val deltaX = event.x - startX
                cardsContainer.translateX = deltaX * 0.5
            }
        }

        setOnMouseReleased { event ->
            if (isDragging) {
                onUserInteraction?.invoke()
                val deltaX = event.x - startX
                if (abs(deltaX) > 50) {
                    if (deltaX > 0 && currentIndex > 0) showPrevious()
                    else if (deltaX < 0 && currentIndex < cards.size - 1) showNext()
                }
                animateToPosition()
                isDragging = false
            }
        }

        setOnScroll {
            onUserInteraction?.invoke()

            if (it.deltaX < 0 || it.deltaY < 0) showNext()
            if (it.deltaX > 0 || it.deltaY > 0) showPrevious()
        }
    }

    /**
     * Show next card
     */
    fun showNext() {
        if (currentIndex < cards.size - 1) {
            currentIndex++
            animateCardsTransition()
        }
    }

    /**
     * Show previous card
     */
    fun showPrevious() {
        if (currentIndex > 0) {
            currentIndex--
            animateCardsTransition()
        }
    }

    /**
     * Animate cards to their new positions
     */
    private fun animateCardsTransition() {
        cards.forEachIndexed { index, card ->
            val offset = index - currentIndex
            val translateX = offset * 250.0
            val scale = max(0.7, 1.0 - abs(offset) * 0.15)
            val opacity = max(0.4, 1.0 - abs(offset) * 0.3)
            val rotateY = offset * -15.0

            val timeline = Timeline(
                KeyFrame(
                    Duration.millis(500.0),
                    KeyValue(card.translateXProperty(), translateX, Interpolator.EASE_BOTH),
                    KeyValue(card.scaleXProperty(), scale, Interpolator.EASE_BOTH),
                    KeyValue(card.scaleYProperty(), scale, Interpolator.EASE_BOTH),
                    KeyValue(card.opacityProperty(), opacity, Interpolator.EASE_BOTH),
                    KeyValue(card.rotateProperty(), rotateY, Interpolator.EASE_BOTH)
                )
            )
            timeline.play()
        }

        highlightCard(currentIndex)
    }

    /**
     * Animate back to centered position
     */
    private fun animateToPosition() {
        val timeline = Timeline(
            KeyFrame(
                Duration.millis(300.0),
                KeyValue(cardsContainer.translateXProperty(), 0.0, Interpolator.EASE_BOTH)
            )
        )
        timeline.play()
    }

    /**
     * Highlight the centered card
     */
    private fun highlightCard(index: Int) {
        cards.forEachIndexed { i, card ->
            card.setHighlighted(i == index)
        }
    }

    /**
    * Show loading indicator
    */
    private fun showLoading() {
        isLoading = true
        loadingLabel.isVisible = true
        cardsContainer.isVisible = false
    }

    /**
    * Hide loading indicator
    */
    private fun hideLoading() {
        isLoading = false
        loadingLabel.isVisible = false
        cardsContainer.isVisible = true
    }

    /**
    * Show empty state message
    */
    fun showEmptyState() {
        emptyLabel.isVisible = true
        errorLabel.isVisible = false
        loadingLabel.isVisible = false
        cardsContainer.isVisible = false
    }

    /**
     * Show error state message
     */
    fun showErrorState() {
        errorLabel.isVisible = true
        emptyLabel.isVisible = false
        loadingLabel.isVisible = false
        cardsContainer.isVisible = false
    }

    /**
     * Hide all state messages and show content
     */
    fun hideAllStates() {
        emptyLabel.isVisible = false
        errorLabel.isVisible = false
        loadingLabel.isVisible = false
        cardsContainer.isVisible = true
    }

    /**
     * Individual forecast card with glassmorphism effect
     */
    class ForecastCard(
        private val forecast: DailyForecast,
        private val unit: TemperatureUnit
    ) : VBox(15.0) {

        private val glowEffect = DropShadow()
        private var isHighlighted = false

        init {
            prefWidth = 200.0
            prefHeight = 280.0
            alignment = Pos.CENTER

            // Glassmorphism style
            style = """
                -fx-background-color: rgba(255, 255, 255, 0.1);
                -fx-background-radius: 20px;
                -fx-border-color: rgba(255, 255, 255, 0.3);
                -fx-border-width: 1px;
                -fx-border-radius: 20px;
                -fx-padding: 20px;
            """.trimIndent()

            // Glow effect
            glowEffect.color = Color.rgb(100, 150, 255, 0.0)
            glowEffect.radius = 20.0
            glowEffect.spread = 0.3
            effect = glowEffect

            buildCard()

            // Hover effect
            setOnMouseEntered {
                if (!isHighlighted) {
                    animateHover(true)
                }
            }

            setOnMouseExited {
                if (!isHighlighted) {
                    animateHover(false)
                }
            }
        }

        /**
         * Build card content
         */
        private fun buildCard() {
            val conditionEnum = WeatherCondition.fromDescription(forecast.condition)
            val icon = getWeatherIcon(conditionEnum)

            // Day name
            val dayLabel = Label(forecast.date.dayOfWeek.name.take(3))
            dayLabel.font = Font.font("System", FontWeight.BOLD, 24.0)
            dayLabel.textFill = Color.WHITE
            dayLabel.style = "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 5, 0, 0, 2);"

            // Weather icon
            val iconLabel = Label(icon)
            iconLabel.font = Font.font(64.0)
            iconLabel.style = "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 3);"

            // Condition description
            val conditionLabel = Label(forecast.conditionDescription.split(" ").joinToString("\n") { it.replaceFirstChar { c -> c.uppercaseChar() } })
            conditionLabel.font = Font.font("System", FontWeight.NORMAL, 14.0)
            conditionLabel.textFill = Color.rgb(220, 220, 255)
            conditionLabel.isWrapText = true
            conditionLabel.alignment = Pos.CENTER

            // Temperature range
            val tempLabel = Label(forecast.getFormattedTemps(unit))
            tempLabel.font = Font.font("System", FontWeight.BOLD, 28.0)
            tempLabel.textFill = Color.WHITE
            tempLabel.style = "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 5, 0, 0, 2);"

            // Temperature unit indicator
            val unitSymbol = if (unit == TemperatureUnit.CELSIUS) "°C" else "°F"
            val unitLabel = Label(unitSymbol)
            unitLabel.font = Font.font("System", FontWeight.NORMAL, 14.0)
            unitLabel.textFill = Color.rgb(200, 200, 255)

            children.addAll(dayLabel, iconLabel, conditionLabel, tempLabel, unitLabel)
        }

        /**
         * Set highlighted state
         */
        fun setHighlighted(highlighted: Boolean) {
            isHighlighted = highlighted
            animateGlow(highlighted)
        }

        /**
         * Animate hover effect
         */
        private fun animateHover(hover: Boolean) {
            val targetScale = if (hover) 1.05 else 1.0
            val timeline = Timeline(
                KeyFrame(
                    Duration.millis(200.0),
                    KeyValue(scaleXProperty(), targetScale, Interpolator.EASE_BOTH),
                    KeyValue(scaleYProperty(), targetScale, Interpolator.EASE_BOTH)
                )
            )
            timeline.play()
        }

        /**
         * Animate glow effect
         */
        private fun animateGlow(glow: Boolean) {
            val targetRadius = if (glow) 30.0 else 20.0
            val targetColor = if (glow) Color.rgb(100, 150, 255, 0.6) else Color.rgb(100, 150, 255, 0.0)
            val timeline = Timeline(
                KeyFrame(
                    Duration.millis(400.0),
                    KeyValue(glowEffect.radiusProperty(), targetRadius, Interpolator.EASE_BOTH),
                    KeyValue(glowEffect.colorProperty(), targetColor, Interpolator.EASE_BOTH)
                )
            )
            timeline.play()
        }

        /**
         * Get weather emoji icon
         */
        private fun getWeatherIcon(condition: WeatherCondition) = when (condition) {
                WeatherCondition.CLEAR -> "☀️"
                WeatherCondition.CLOUDS, WeatherCondition.BROKEN_CLOUDS -> "☁️"
                WeatherCondition.FEW_CLOUDS, WeatherCondition.SCATTERED_CLOUDS -> "⛅"
                WeatherCondition.RAIN, WeatherCondition.SHOWER_RAIN -> "🌧️"
                WeatherCondition.THUNDERSTORM -> "⛈️"
                WeatherCondition.SNOW -> "❄️"
                WeatherCondition.MIST -> "🌫️"
                WeatherCondition.UNKNOWN -> "🌤️"
        }
    }
}
