package com.weatherdesk.repository

import com.weatherdesk.model.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.gson.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class BackendRepository(
    private val baseUrl: String,
    private val tokenProvider: () -> String? = { null }
) {
    private val client = HttpClient {
        install(ContentNegotiation) { gson() }
    }

    private fun addAuth(builder: HttpRequestBuilder) {
        tokenProvider()?.let { token -> builder.headers.append("Authorization", "Bearer $token") }
    }

    // ---------------------------------------
    // Weather
    suspend fun getWeather(location: LocationInput): WeatherData {
        val url = when (location) {
            is LocationInput.City -> "$baseUrl/weather?city=${location.name}"
            is LocationInput.Coordinates -> "$baseUrl/weather?lat=${location.latitude}&lon=${location.longitude}"
        }
        return try {
            client.get(url).body()
        } catch (e: Exception) {
            logger.error(e) { "Failed to fetch weather from backend" }
            throw e
        }
    }

    suspend fun getWeatherByCity(city: String): WeatherData = withContext(Dispatchers.IO) {
        client.get("$baseUrl/weather") {
            url.parameters.append("city", city)
        }.body()
    }

    suspend fun getWeatherByCoordinates(lat: Double, lon: Double): WeatherData = withContext(Dispatchers.IO) {
        client.get("$baseUrl/weather") {
            url.parameters.append("lat", lat.toString())
            url.parameters.append("lon", lon.toString())
        }.body()
    }
    // ---------------------------------------
    // Ratings
    suspend fun submitRating(city: String, rating: Int) = withContext(Dispatchers.IO) {
        try {
            client.post("$baseUrl/weather/rating") {
                addAuth(this)
                contentType(ContentType.Application.Json)
                setBody(mapOf("city" to city, "rating" to rating))
            }
        } catch (e: Exception) {
            logger.error(e) { "Failed to submit rating" }
            throw e
        }
    }

    suspend fun getAverageRating(city: String): Double? = withContext(Dispatchers.IO) {
        val resp: Map<String, Any> = client.get("$baseUrl/weather/rating") {
            url.parameters.append("city", city)
            addAuth(this)
        }.body()
        (resp["average"] as? Number)?.toDouble()
    }
    // ---------------------------------------
    // UserPreferences
    suspend fun getUserPreferences(): UserPreferences = withContext(Dispatchers.IO) {
        try {
            client.get("$baseUrl/user/preferences") {
                addAuth(this)
            }.body()
        } catch (e: Exception) {
            logger.warn(e) { "Failed to fetch preferences, returning defaults" }
            UserPreferences()
        }
    }

    suspend fun saveUserPreferences(prefs: UserPreferences) = withContext(Dispatchers.IO) {
        try {
            client.post("$baseUrl/user/preferences") {
                addAuth(this)
                contentType(ContentType.Application.Json)
                setBody(prefs)
            }
        } catch (e: Exception) {
            logger.error(e) { "Failed to fetch preferences" }
            throw e
        }
    }
    // ---------------------------------------
    // Account
    suspend fun login(email: String, password: String): LoginResponse = withContext(Dispatchers.IO) {
        client.post("$baseUrl/auth/login") {
            setBody(LoginRequest(email = email, password = password))
        }.body()
    }

    suspend fun register(email: String, password: String) = withContext(Dispatchers.IO) {
        client.post("$baseUrl/auth/register") {
            setBody(RegisterRequest(email = email, password = password))
        }
    }
    // ---------------------------------------
    // Location
    suspend fun saveLocation(location: Location) = withContext(Dispatchers.IO) {
        client.post("$baseUrl/location") {
            addAuth(this)
            setBody(location)
        }
    }

    suspend fun getSavedLocations(): List<Location> =
        client.get("$baseUrl/locations/saved").body()

    suspend fun deleteSavedLocation(name: String) = withContext(Dispatchers.IO) {
        client.delete("$baseUrl/locations/saved/$name")
    }
    // --------------------------------------------

    fun isBackendAvailable(): Boolean {
        return true
    }

    fun close() {
        client.close()
    }
}