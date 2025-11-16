package com.weatherdesk.viewmodel

import com.weatherdesk.model.*
import com.weatherdesk.repository.BackendRepository
import javafx.application.Platform
import javafx.beans.property.*
import kotlinx.coroutines.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

/**
 * ViewModel for weather application following MVVM pattern
 * Manages UI state and business logic
 */
class WeatherViewModel(private val repository: BackendRepository) {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    // Observable properties for UI binding
    val currentWeather = SimpleObjectProperty<CurrentWeather?>()
    val forecast = SimpleListProperty<DailyForecast>(
        javafx.collections.FXCollections.observableArrayList()
    )
    val isLoading = SimpleBooleanProperty(false)
    val errorMessage = SimpleStringProperty("")
    val successMessage = SimpleStringProperty("")

    // User preferences
    val temperatureUnit = SimpleObjectProperty<TemperatureUnit>(TemperatureUnit.CELSIUS)
    val windSpeedUnit = SimpleObjectProperty<WindSpeedUnit>(WindSpeedUnit.KILOMETERS_PER_HOUR)

    // Input validation
    val cityInput = SimpleStringProperty("")
    val latitudeInput = SimpleStringProperty("")
    val longitudeInput = SimpleStringProperty("")
    val isCoordinateMode = SimpleBooleanProperty(false)
    val locationInput = SimpleStringProperty("")

    // Rating
    val currentRating = SimpleIntegerProperty(0)
    val averageRating = SimpleDoubleProperty(0.0)

    //

    init {
        loadUserPreferences()
    }

    /**
     * Load user preferences from repository
     */
    private fun loadUserPreferences() {
        scope.launch {
            try {
                val prefs = repository.getUserPreferences()
                Platform.runLater {
                    temperatureUnit.set(prefs.preferredTempUnit)
                    windSpeedUnit.set(prefs.preferredWindUnit)
                }
                if (prefs.lastSearchedCity != null) {
                    Platform.runLater { cityInput.set(prefs.lastSearchedCity) }
                    fetchWeatherByCity()
                } else if (prefs.lastSearchedLatitude != null && prefs.lastSearchedLongitude != null) {
                    Platform.runLater {
                        latitudeInput.set(prefs.lastSearchedLatitude.toString())
                        longitudeInput.set(prefs.lastSearchedLongitude.toString())
                        isCoordinateMode.set(true)
                    }
                    fetchWeatherByCoordinates()
                }
            } catch (e: Exception) {
                logger.error(e) { "Failed to load prefs" }
            }
        }
    }

    /**
     * Save current preferences
     */
    private fun savePreferences() {
        scope.launch {
            try {
                val prefs = UserPreferences(
                    preferredTempUnit = temperatureUnit.get(),
                    preferredWindUnit = windSpeedUnit.get()
                )
                repository.saveUserPreferences(prefs)
            } catch (e: Exception) {
                logger.error(e) { "Error saving preferences" }
            }
        }
    }

    /**
     * Fetch weather by city name
     */
    fun fetchWeatherByCity() {
        val city = cityInput.get()?.trim()

        if (city.isNullOrBlank()) {
            updateError("City name cannot be empty")
            return
        }

        if (!isValidCityName(city)) {
            updateError("Invalid city name. Please use letters, spaces, and hyphens only.")
            return
        }

        fetchWeather(LocationInput.City(city))
    }

    /**
     * Fetch weather by coordinates
     */
    fun fetchWeatherByCoordinates() {
        val latStr = latitudeInput.get()?.trim()
        val lonStr = longitudeInput.get()?.trim()

        if (latStr.isNullOrBlank() || lonStr.isNullOrBlank()) {
            updateError("Latitude and longitude cannot be empty")
            return
        }

        val lat = latStr.toDoubleOrNull()
        val lon = lonStr.toDoubleOrNull()

        if (lat == null || lon == null) {
            updateError("Invalid coordinates. Please enter valid numbers.")
            return
        }

        try {
            val location = LocationInput.Coordinates(lat, lon)
            fetchWeather(location)
        } catch (e: IllegalArgumentException) {
            updateError(e.message ?: "Invalid coordinates")
        }
    }

    /**
     * Core method to fetch weather data
     */
    private fun fetchWeather(location: LocationInput) {
        scope.launch {
            isLoading.set(true)
            errorMessage.set("")
            successMessage.set("")

            try {
                val data = when (location) {
                    is LocationInput.City -> repository.getWeatherByCity(location.name)
                    is LocationInput.Coordinates -> repository.getWeatherByCoordinates(location.latitude, location.longitude)
                }

                currentWeather.set(data.current)
                    forecast.set(javafx.collections.FXCollections.observableArrayList(data.forecast))
                    updateSuccess("Weather data loaded successfully for ${data.current.city}")
                    loadAverageRating(data.current.city)
                    logger.info { "Weather fetched successfully for ${data.current.city}" }
            } catch (e: Exception) {
                logger.error(e) { "Failed to fetch weather data" }
            }

            isLoading.set(false)
        }
    }

    /**
     * Submit rating for current forecast
     */
    fun submitRating() {
        val rating = currentRating.get()
        val city = currentWeather.get()?.city

        if (rating == 0) {
            updateError("Please select a rating")
            return
        }

        if (city == null) {
            updateError("No city selected")
            return
        }

        scope.launch {
            try {
                repository.submitRating(city, rating)
                updateSuccess("Thank you for rating the forecast for $city!")
                currentRating.set(0)
                loadAverageRating(city)
            } catch (e: Exception) {
                updateError("Failed to submit rating: ${e.message}")
            }
        }
    }

    /**
     * Load average rating for a city
     */
    private fun loadAverageRating(city: String) {
        scope.launch {
            try {
                val rating = repository.getAverageRating(city)
                if (rating != null) {
                    Platform.runLater {
                        averageRating.set(rating)
                    }
                }
            } catch (e: Exception) {
                logger.error(e) { "Failed to load average rating" }
            }
        }
    }

    /**
     * Change temperature unit
     */
    fun setTemperatureUnit(unit: TemperatureUnit) {
        temperatureUnit.set(unit)
        savePreferences()
    }

    /**
     * Change wind speed unit
     */
    fun setWindSpeedUnit(unit: WindSpeedUnit) {
        windSpeedUnit.set(unit)
        savePreferences()
    }

    /**
     * Toggle between city and coordinate input modes
     */
    fun toggleInputMode() {
        isCoordinateMode.set(!isCoordinateMode.get())
        errorMessage.set("")
    }

    /**
     * Validate city name
     */
    private fun isValidCityName(city: String): Boolean {
        return city.matches(Regex("^[a-zA-Z\\s\\-,]+$"))
    }

    /**
     * Update error message
     */
    private fun updateError(message: String) {
        Platform.runLater {
            errorMessage.set(message)
            successMessage.set("")
        }
    }

    /**
     * Update success message
     */
    private fun updateSuccess(message: String) {
        Platform.runLater {
            successMessage.set(message)
            errorMessage.set("")
        }
    }

    /**
     * Clear messages
     */
    fun clearMessages() {
        errorMessage.set("")
        successMessage.set("")
    }

    /**
     * Check if Backend is available
     */
    fun isBackendAvailable(): Boolean {
        return repository.isBackendAvailable()
    }

    /**
     * Cleanup when ViewModel is destroyed
     */
    fun onDestroy() {
        scope.cancel()
    }
}
