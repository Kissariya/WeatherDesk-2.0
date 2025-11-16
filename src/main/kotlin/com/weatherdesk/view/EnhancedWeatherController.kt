package com.weatherdesk.view

import com.weatherdesk.model.*
import com.weatherdesk.ui.animations.EngagementManager
import com.weatherdesk.ui.animations.UIAnimations
import com.weatherdesk.ui.components.ActivityRecommendationPanel
import com.weatherdesk.ui.components.ForecastCarousel
import com.weatherdesk.ui.components.InteractiveGlobe
import com.weatherdesk.ui.content.WeatherContent
import com.weatherdesk.ui.effects.WeatherParticleSystem
import com.weatherdesk.ui.theme.ThemeManager
import com.weatherdesk.viewmodel.WeatherViewModel
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.scene.text.Font
import mu.KotlinLogging
import java.time.format.DateTimeFormatter

private val logger = KotlinLogging.logger {}

/**
 * Enhanced JavaFX Controller with creative UI components
 * Integrates ThemeManager, ParticleSystem, Globe, Carousel, and Content
 * Follows MVVM pattern, binding UI to ViewModel
 */
class EnhancedWeatherController {

    // Existing FXML components
    @FXML private lateinit var coordinateModeToggle: ToggleButton
    @FXML private lateinit var cityInputBox: VBox
    @FXML private lateinit var coordinateInputBox: VBox
    @FXML private lateinit var cityTextField: TextField
    @FXML private lateinit var latitudeTextField: TextField
    @FXML private lateinit var longitudeTextField: TextField

    @FXML private lateinit var errorLabel: Label
    @FXML private lateinit var successLabel: Label
    @FXML private lateinit var loadingBox: HBox
    @FXML private lateinit var weatherDataBox: VBox
    @FXML private lateinit var emptyStateBox: VBox

    @FXML private lateinit var cityLabel: Label
    @FXML private lateinit var dateLabel: Label
    @FXML private lateinit var temperatureLabel: Label
    @FXML private lateinit var conditionLabel: Label
    @FXML private lateinit var weatherIcon: Label
    @FXML private lateinit var humidityLabel: Label
    @FXML private lateinit var windSpeedLabel: Label
    @FXML private lateinit var tempUnitComboBox: ComboBox<TemperatureUnit>

    @FXML private lateinit var ratingStars: HBox
    @FXML private lateinit var averageRatingLabel: Label
    @FXML private lateinit var backendStatusLabel: Label

    // New FXML components for creative UI
    @FXML private lateinit var backgroundPane: Pane
    @FXML private lateinit var globeContainer: StackPane
    @FXML private lateinit var forecastCarouselContainer: StackPane
    @FXML private lateinit var triviaLabel: Label
    @FXML private lateinit var quoteLabel: Label
    @FXML private lateinit var activityLabel: Label
    @FXML private lateinit var activitiesRecommendationContainer: StackPane

    // ViewModel
    lateinit var viewModel: WeatherViewModel

    // Creative UI components
    private var particleSystem: WeatherParticleSystem? = null
    private var globe: InteractiveGlobe? = null
    private var forecastCarousel: ForecastCarousel? = null
    private var engagementManager: EngagementManager? = null

    // State
    private var activityPanel: ActivityRecommendationPanel? = null
    private var currentRating = 0

    @FXML
    fun initialize() {
        logger.info { "Initializing EnhancedWeatherController" }

        setupTempUnitComboBox()
        setupRatingStars()
        setupEnhancedUI()
    }

    /**
     * Setup creative UI components
     */
    private fun setupEnhancedUI() {
        try {
            // Initialize particle system
            logger.info { "Initializing particle system" }
            particleSystem = WeatherParticleSystem(backgroundPane)

            // Initialize interactive globe
            logger.info { "Initializing interactive globe" }
            globe = InteractiveGlobe()
            globe?.onLocationSelected = { lat, lon ->
                logger.info { "Globe location selected: ($lat, $lon)" }
                Platform.runLater {
                    latitudeTextField.text = String.format("%.4f", lat)
                    longitudeTextField.text = String.format("%.4f", lon)

                    // Auto-search when globe location is selected
                    if (!coordinateModeToggle.isSelected) {
                        coordinateModeToggle.isSelected = true
                        toggleInputMode()
                    }
                    searchByCoordinates()
                }
            }
            globeContainer.children.add(globe)

            // Initialize forecast carousel
            logger.info { "Initializing forecast carousel" }
            forecastCarousel = ForecastCarousel()
            forecastCarouselContainer.children.add(forecastCarousel)

            // Initialize activity recommendation panel (CORE FEATURE)
            logger.info { "Initializing activity recommendation panel" }
            activityPanel = ActivityRecommendationPanel()
            activitiesRecommendationContainer.children.add(activityPanel)

            // Initialize engagement manager with content rotation
            engagementManager = EngagementManager(
                triviaLabel = triviaLabel,
                quoteLabel = quoteLabel,
                activityLabel = activityLabel,
                forecastCarousel = forecastCarousel
            )

            // Wire user interaction -> stop autoplay (safe)
            forecastCarousel?.onUserInteraction = {
                try {
                    engagementManager?.stopCarouselAutoPlay()
                } catch (e: Exception) {
                    logger.warn(e) { "Failed to stop autoplay on user interaction" }
                }
            }

            // Apply entrance animations for premium feel
            UIAnimations.fadeIn(weatherDataBox, 1000.0, 200.0)
            UIAnimations.slideInFromTop(globeContainer, 800.0, 100.0)
            UIAnimations.scaleUp(forecastCarouselContainer, 600.0)
            UIAnimations.fadeIn(triviaLabel, 800.0, 400.0)
            UIAnimations.fadeIn(quoteLabel, 800.0, 500.0)
            UIAnimations.fadeIn(activityLabel, 800.0, 600.0)

            logger.info { "Enhanced UI components initialized successfully" }
        } catch (e: Exception) {
            logger.error(e) { "Error initializing enhanced UI components" }
        }
    }

    fun setViewModel(viewModel: WeatherViewModel) {
        this.viewModel = viewModel
        bindViewModel()
        updateBackendStatus()
    }

    private fun setupTempUnitComboBox() {
        tempUnitComboBox.items.addAll(TemperatureUnit.entries)
        tempUnitComboBox.selectionModel.select(TemperatureUnit.CELSIUS)
    }

    private fun setupRatingStars() {
        ratingStars.children.clear()
        for (i in 1..5) {
            val star = Label("⭐")
            star.styleClass.add("rating-star")
            star.styleClass.add("rating-star-empty")
            star.font = Font.font(28.0)

            star.setOnMouseClicked {
                currentRating = i
                updateStarDisplay()
            }

            star.setOnMouseEntered {
                highlightStars(i)
            }

            ratingStars.children.add(star)
        }

        ratingStars.setOnMouseExited {
            updateStarDisplay()
        }
    }

    private fun highlightStars(count: Int) {
        ratingStars.children.forEachIndexed { index, node ->
            if (node is Label) {
                node.styleClass.removeAll("rating-star-empty", "rating-star-filled")
                if (index < count) {
                    node.styleClass.add("rating-star-filled")
                } else {
                    node.styleClass.add("rating-star-empty")
                }
            }
        }
    }

    private fun updateStarDisplay() {
        highlightStars(currentRating)
    }

    private fun bindViewModel() {
        // Bind input fields
        cityTextField.textProperty().bindBidirectional(viewModel.cityInput)
        latitudeTextField.textProperty().bindBidirectional(viewModel.latitudeInput)
        longitudeTextField.textProperty().bindBidirectional(viewModel.longitudeInput)

        // Bind loading state
        viewModel.isLoading.addListener { _, _, isLoading ->
            Platform.runLater {
                loadingBox.isVisible = isLoading
                loadingBox.isManaged = isLoading

                if (isLoading) {
                    weatherDataBox.isVisible = false
                    weatherDataBox.isManaged = false
                    emptyStateBox.isVisible = false
                    emptyStateBox.isManaged = false
                }
            }
        }

        // Bind error message
        viewModel.errorMessage.addListener { _, _, newValue ->
            Platform.runLater {
                if (newValue.isNullOrBlank()) {
                    errorLabel.isVisible = false
                    errorLabel.isManaged = false
                } else {
                    errorLabel.text = newValue
                    errorLabel.isVisible = true
                    errorLabel.isManaged = true
                }
            }
        }

        // Bind success message
        viewModel.successMessage.addListener { _, _, newValue ->
            Platform.runLater {
                if (newValue.isNullOrBlank()) {
                    successLabel.isVisible = false
                    successLabel.isManaged = false
                } else {
                    successLabel.text = newValue
                    successLabel.isVisible = true
                    successLabel.isManaged = true
                }
            }
        }

        // Bind weather data
        viewModel.currentWeather.addListener { _, _, weather ->
            Platform.runLater {
                if (weather != null) {
                    updateWeatherDisplay(weather)
                    emptyStateBox.isVisible = false
                    emptyStateBox.isManaged = false
                    weatherDataBox.isVisible = true
                    weatherDataBox.isManaged = true
                } else if (!viewModel.isLoading.get()) {
                    weatherDataBox.isVisible = false
                    weatherDataBox.isManaged = false
                    emptyStateBox.isVisible = true
                    emptyStateBox.isManaged = true
                }
            }
        }

        // Bind forecast
        viewModel.forecast.addListener { _, _, forecasts ->
            Platform.runLater {
                updateForecastDisplay(forecasts?.toList() ?: emptyList())
            }
        }

        // Bind temperature unit
        viewModel.temperatureUnit.addListener { _, _, unit ->
            Platform.runLater {
                tempUnitComboBox.selectionModel.select(unit)
                viewModel.currentWeather.get()?.let { updateWeatherDisplay(it) }
                // Update carousel with new unit
                viewModel.forecast.get()?.let { forecasts ->
                    forecastCarousel?.setForecasts(forecasts.toList(), unit)
                }
            }
        }

        // Bind average rating
        viewModel.averageRating.addListener { _, _, rating ->
            Platform.runLater {
                if (rating.toDouble() > 0) {
                    averageRatingLabel.text = String.format("Average rating: %.1f / 5.0", rating.toDouble())
                    averageRatingLabel.isVisible = true
                    averageRatingLabel.isManaged = true
                }
            }
        }
    }

    /**
     * Update weather display with enhanced UI features
     */
    private fun updateWeatherDisplay(weather: CurrentWeather) {
        val tempUnit = viewModel.temperatureUnit.get()
        val windUnit = viewModel.windSpeedUnit.get()

        // Basic weather info
        cityLabel.text = weather.city
        dateLabel.text = weather.date.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"))
        temperatureLabel.text = String.format("%.0f", weather.getTemperature(tempUnit))
        conditionLabel.text = weather.conditionDescription.replaceFirstChar { it.uppercase() }
        humidityLabel.text = "${weather.humidity}%"
        windSpeedLabel.text = weather.getFormattedWindSpeed(windUnit)
        val conditionEnum = WeatherCondition.fromDescription(weather.condition)
        weatherIcon.text = getWeatherIcon(conditionEnum)

        // Update theme based on weather and time
        updateTheme(conditionEnum)

        // Update particle effects
        updateParticles(conditionEnum)

        // Update contextual content
        updateWeatherContent(weather)

        // Update activity recommendations panel
        try {
            activityPanel?.updateWeather(conditionEnum, weather.getTemperature(tempUnit))
            logger.info { "Activity panel updated with weather data" }
        } catch (e: Exception) {
            logger.error(e) { "Error updating activity panel" }
        }

        // Engagement features to keep users hooked
        try {
            engagementManager?.startContentRotation(conditionEnum,weather.getTemperature(tempUnit))
            engagementManager?.startCarouselAutoPlay()

            // Setup callback to stop auto-play on user interaction
            forecastCarousel?.onUserInteraction = {
                engagementManager?.stopCarouselAutoPlay()
            }
            logger.info { "Engagement features started successfully" }
        } catch (e: Exception) {
            logger.error("Failed to start engagement features", e)
        }
    }

    // Update theme
    private fun updateTheme(condition: WeatherCondition) {
        try {
            val timeOfDay = ThemeManager.getTimeOfDay()
            val theme = ThemeManager.getTheme(condition, timeOfDay)
            ThemeManager.applyThemeToPane(backgroundPane, theme, animate = true)
            logger.info { "Theme updated: $condition at $timeOfDay" }
        } catch (e: Exception) {
            logger.error(e) { "Error updating theme" }
        }
    }

    /**
     * Update particle system based on weather
     */
    private fun updateParticles(condition: WeatherCondition) {
        try {
            particleSystem?.start(condition)
            logger.info { "Particle system started for $condition" }
        } catch (e: Exception) {
            logger.error(e) { "Error updating particles" }
        }
    }

    /**
     * Update contextual weather content (trivia, quotes, activities)
     */
    private fun updateWeatherContent(weather: CurrentWeather) {
        try {
            val conditionString = weather.condition
            val conditionEnum = WeatherCondition.fromDescription(conditionString)

            triviaLabel.text = WeatherContent.getWeatherTrivia(conditionEnum)
            quoteLabel.text = WeatherContent.getMotivationalQuote(
                conditionEnum,
                weather.temperatureCelsius
            )
            activityLabel.text = WeatherContent.getActivitySuggestion(
                conditionEnum,
                weather.temperatureCelsius
            )
            logger.info { "Weather content updated" }
        } catch (e: Exception) {
            logger.error(e) { "Error updating weather content" }
        }
    }

    /**
     * Update forecast display using carousel
     */
    private fun updateForecastDisplay(forecasts: List<DailyForecast>) {
        try {
            val tempUnit = viewModel.temperatureUnit.get()
            forecastCarousel?.setForecasts(forecasts, tempUnit)
            logger.info { "Forecast carousel updated with ${forecasts.size} days" }
        } catch (e: Exception) {
            logger.error(e) { "Error updating forecast display" }
        }
    }

    private fun getWeatherIcon(condition: WeatherCondition): String {
        return when (condition) {
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

    private fun updateBackendStatus() {
        try {
            if (viewModel.isBackendAvailable()) {
                backendStatusLabel.text = "Backend: Connected ✓"
                backendStatusLabel.style = "-fx-text-fill: #388E3C;"
            } else {
                backendStatusLabel.text = "Backend: Not Connected"
                backendStatusLabel.style = "-fx-text-fill: #FFA500;"
            }
        } catch (e: Exception) {
            backendStatusLabel.isVisible = false
            backendStatusLabel.isManaged = false
        }
    }

    @FXML
    fun toggleInputMode() {
        val isCoordMode = coordinateModeToggle.isSelected

        cityInputBox.isVisible = !isCoordMode
        cityInputBox.isManaged = !isCoordMode

        coordinateInputBox.isVisible = isCoordMode
        coordinateInputBox.isManaged = isCoordMode

        viewModel.toggleInputMode()
    }

    @FXML
    fun searchByCity() {
        viewModel.clearMessages()
        viewModel.fetchWeatherByCity()
    }

    @FXML
    fun searchByCoordinates() {
        viewModel.clearMessages()
        viewModel.fetchWeatherByCoordinates()
    }

    @FXML
    fun changeTempUnit() {
        val selectedUnit = tempUnitComboBox.selectionModel.selectedItem
        if (selectedUnit != null) {
            viewModel.setTemperatureUnit(selectedUnit)
        }
    }

    @FXML
    fun submitRating() {
        if (currentRating > 0) {
            viewModel.currentRating.set(currentRating)
            viewModel.submitRating()
            currentRating = 0
            updateStarDisplay()
        }
    }

    /**
     * Cleanup resources when controller is destroyed
     */
    fun cleanup() {
        logger.info { "Cleaning up EnhancedWeatherController" }
        try {
            particleSystem?.dispose()
            globe?.dispose()
            activityPanel?.dispose()
            engagementManager?.stopAll()
            logger.info { "Enhanced UI components cleaned up successfully" }
        } catch (e: Exception) {
            logger.error(e) { "Error during cleanup" }
        }
    }
}