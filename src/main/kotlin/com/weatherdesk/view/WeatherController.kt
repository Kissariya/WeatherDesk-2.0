package com.weatherdesk.view

import com.weatherdesk.model.DailyForecast
import com.weatherdesk.model.TemperatureUnit
import com.weatherdesk.model.WeatherCondition
import com.weatherdesk.viewmodel.WeatherViewModel
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.scene.text.Font
import java.time.format.DateTimeFormatter

/**
 * JavaFX Controller for the Weather View
 * Follows MVVM pattern, binding UI to ViewModel
 */
class WeatherController {

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

    @FXML private lateinit var forecastGrid: GridPane
    @FXML private lateinit var ratingStars: HBox
    @FXML private lateinit var averageRatingLabel: Label

    @FXML private lateinit var backendStatusLabel: Label

    lateinit var viewModel: WeatherViewModel

    private var currentRating = 0

    @FXML
    fun initialize() {
        setupTempUnitComboBox()
        setupRatingStars()
    }

    fun setViewModel(viewModel: WeatherViewModel) {
        this.viewModel = viewModel
        bindViewModel()
        updateFirebaseStatus()
    }

    private fun setupTempUnitComboBox() {
        tempUnitComboBox.items.addAll(TemperatureUnit.entries)
        tempUnitComboBox.selectionModel.select(TemperatureUnit.CELSIUS)
    }

    private fun setupRatingStars() {
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

            ratingStars.setOnMouseExited {
                updateStarDisplay()
            }

            ratingStars.children.add(star)
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

    private fun updateWeatherDisplay(weather: com.weatherdesk.model.CurrentWeather) {
        val tempUnit = viewModel.temperatureUnit.get()
        val windUnit = viewModel.windSpeedUnit.get()
        val conditionEnum = try {
            WeatherCondition.valueOf(weather.condition.uppercase())
        } catch (e: Exception) {
            WeatherCondition.UNKNOWN
        }

        cityLabel.text = weather.city
        dateLabel.text = weather.date.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"))
        temperatureLabel.text = String.format("%.0f", weather.getTemperature(tempUnit))
        conditionLabel.text = weather.conditionDescription.replaceFirstChar { it.uppercase() }
        humidityLabel.text = "${weather.humidity}%"
        windSpeedLabel.text = weather.getFormattedWindSpeed(windUnit)
        weatherIcon.text = getWeatherIcon(conditionEnum)
    }

    private fun updateForecastDisplay(forecasts: List<DailyForecast>) {
        forecastGrid.children.clear()

        forecasts.forEachIndexed { index, forecast ->
            val card = createForecastCard(forecast)
            forecastGrid.add(card, index % 5, index / 5)
        }
    }

    private fun createForecastCard(forecast: DailyForecast): VBox {
        val card = VBox(10.0)
        card.styleClass.add("forecast-card")
        card.alignment = Pos.CENTER

        val dayLabel = Label(forecast.date.dayOfWeek.name.substring(0, 3))
        dayLabel.styleClass.add("forecast-day")

        val conditionEnum = try {
            WeatherCondition.valueOf(forecast.condition.uppercase())
        } catch (e: Exception) {
            WeatherCondition.UNKNOWN
        }
        val iconLabel = Label(getWeatherIcon(conditionEnum))
        iconLabel.styleClass.add("forecast-icon")

        val tempUnit = viewModel.temperatureUnit.get()
        val tempLabel = Label(forecast.getFormattedTemps(tempUnit))
        tempLabel.styleClass.add("forecast-temp")

        card.children.addAll(dayLabel, iconLabel, tempLabel)
        return card
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

    private fun updateFirebaseStatus() {
        if (viewModel.isBackendAvailable()) {
            backendStatusLabel.text = "Firebase: Connected ✓"
            backendStatusLabel.style = "-fx-text-fill: #388E3C;"
        } else {
            backendStatusLabel.text = "Firebase: Not Connected (using local storage)"
            backendStatusLabel.style = "-fx-text-fill: #FFA500;"
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
}