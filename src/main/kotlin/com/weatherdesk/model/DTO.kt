package com.weatherdesk.model

import java.time.LocalDate

data class WeatherData(
    val current: CurrentWeather,
    val forecast: List<DailyForecast>
)

data class CurrentWeather(
    val city: String,
    val temperatureCelsius: Double,
    val condition: String,
    val conditionDescription: String,
    val humidity: Int,
    val windSpeedMps: Double,
    val date: LocalDate,
    val latitude: Double? = null,
    val longitude: Double? = null
) {
    fun getTemperature(unit: TemperatureUnit) =
        if (unit == TemperatureUnit.CELSIUS) temperatureCelsius else temperatureCelsius * 9 / 5 + 32

    fun getFormattedWindSpeed(unit: WindSpeedUnit): String {
        return when (unit) {
            WindSpeedUnit.KILOMETERS_PER_HOUR -> String.format("%.1f km/h", windSpeedMps * 3.6)
            WindSpeedUnit.METERS_PER_SECOND -> String.format("%.1f m/s", windSpeedMps)
            WindSpeedUnit.MILES_PER_HOUR -> String.format("%.1f mph", windSpeedMps * 2.23694)
        }
    }
}

data class DailyForecast(
    val date: LocalDate,
    val highTempCelsius: Double,
    val lowTempCelsius: Double,
    val condition: String,
    val conditionDescription: String
) {
    fun getFormattedTemps(unit: TemperatureUnit): String {
        val high = if (unit == TemperatureUnit.CELSIUS) highTempCelsius else highTempCelsius * 9 / 5 + 32
        val low = if (unit == TemperatureUnit.CELSIUS) lowTempCelsius else lowTempCelsius * 9 / 5 + 32
        val symbol = if (unit == TemperatureUnit.CELSIUS) "°C" else "°F"
        return "${high.toInt()}$symbol / ${low.toInt()}$symbol"
    }
}

data class Location(
    val name: String,
    val latitude: Double,
    val longitude: Double
)

data class GeocodingResult(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String?
)

sealed class LocationInput {
    data class City(val name: String) : LocationInput()
    data class Coordinates(val latitude: Double, val longitude: Double) : LocationInput()
}

// Reuse enums
enum class TemperatureUnit { CELSIUS, FAHRENHEIT }
enum class WindSpeedUnit { KILOMETERS_PER_HOUR, METERS_PER_SECOND, MILES_PER_HOUR }
enum class ThemeMode { LIGHT, DARK, AUTO }
enum class WeatherCondition(val apiName: String) {
    CLEAR("clear"),
    FEW_CLOUDS("few clouds"),
    SCATTERED_CLOUDS("scattered clouds"),
    BROKEN_CLOUDS("broken clouds"),
    CLOUDS("clouds"),
    RAIN("rain"),
    SHOWER_RAIN("shower rain"),
    THUNDERSTORM("thunderstorm"),
    SNOW("snow"),
    MIST("mist"),
    UNKNOWN("unknown");

    companion object {
        /**
         * Convert raw OpenWeatherMap/WeatherAPI strings → enum
         */
        fun fromDescription(desc: String?): WeatherCondition {
            if (desc == null) return UNKNOWN
            val normalized = desc.lowercase().trim()

            return entries.firstOrNull { normalized.contains(it.apiName) }
                ?: UNKNOWN
        }
    }
}

// Requests / responses
data class LoginRequest(val email: String, val password: String)
data class LoginResponse(val token: String)
data class RegisterRequest(val email: String, val password: String)
data class RatingRequest(val city: String, val rating: Int, val date: String?)
data class UserPreferences(
    val preferredTempUnit: TemperatureUnit = TemperatureUnit.CELSIUS,
    val preferredWindUnit: WindSpeedUnit = WindSpeedUnit.KILOMETERS_PER_HOUR,
    val theme: ThemeMode = ThemeMode.AUTO,
    val lastSearchedCity: String? = null,
    val lastSearchedLatitude: Double? = null,
    val lastSearchedLongitude: Double? = null
) {
    fun getTemperatureUnit(): TemperatureUnit {
        return preferredTempUnit
    }
}