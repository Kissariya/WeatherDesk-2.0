package com.weatherdesk.ui.animations

import com.weatherdesk.model.WeatherCondition
import com.weatherdesk.ui.components.ForecastCarousel
import com.weatherdesk.ui.content.WeatherContent
import javafx.application.Platform
import javafx.scene.control.Label
import java.util.Timer
import java.util.TimerTask
import com.weatherdesk.util.UIConstants

/**
 * Manages engagement features like content rotation and auto-play
 * Keeps users engaged by constantly updating content
 */
class EngagementManager(
    private val triviaLabel: Label,
    private val quoteLabel: Label,
    private val activityLabel: Label,
    private val forecastCarousel: ForecastCarousel?
) {
    
    private var contentRotationTimer: Timer? = null
    private var carouselAutoPlayTimer: Timer? = null
    private var currentWeatherCondition: WeatherCondition = WeatherCondition.UNKNOWN
    private var currentTemperature: Double = 20.0
    
    /**
     * Start auto-rotating content every 30 seconds
     * Keeps trivia, quotes, and activities fresh
     */
    fun startContentRotation(condition: WeatherCondition, temperature: Double) {
        currentWeatherCondition = condition
        currentTemperature = temperature
        
        // Cancel existing timer
        contentRotationTimer?.cancel()
        
        // Create new timer for content rotation
        contentRotationTimer = Timer(true)
        contentRotationTimer?.scheduleAtFixedRate(
            object : TimerTask() {
                override fun run() {
                    Platform.runLater {
                        rotateContent()
                    }
                }
            },
            30000L, // First rotation after 30 seconds
            30000L  // Then every 30 seconds
        )
    }
    
    /**
     * Rotate the displayed content
     */
    private fun rotateContent() {
        try {
            // Fade out current content
            UIAnimations.fadeOut(triviaLabel, 300.0) {
                // Update trivia
                triviaLabel.text = WeatherContent.getWeatherTrivia(currentWeatherCondition)
                UIAnimations.fadeIn(triviaLabel, 300.0)
            }

            // Fade out and update quote with delay
            Timer().schedule(
                object : TimerTask() {
                    override fun run() {
                        Platform.runLater {
                            UIAnimations.fadeOut(quoteLabel, 300.0) {
                                quoteLabel.text = WeatherContent.getMotivationalQuote(
                                    currentWeatherCondition,
                                    currentTemperature
                                )
                                UIAnimations.fadeIn(quoteLabel, 300.0)
                            }
                        }
                    }
                },
                500L
            )

            // Fade out and update activity with delay
            Timer(true).schedule(
                object : TimerTask() {
                    override fun run() {
                        Platform.runLater {
                            UIAnimations.fadeOut(activityLabel, 300.0) {
                                activityLabel.text = WeatherContent.getActivitySuggestion(
                                    currentWeatherCondition,
                                    currentTemperature
                                )
                                UIAnimations.fadeIn(activityLabel, 300.0)
                            }
                        }
                    }
                },
                UIConstants.CONTENT_ROTATION_INTERVAL_MS
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Start carousel auto-play - advances every 5 seconds
     */
    fun startCarouselAutoPlay() {
        // Cancel existing timer
        carouselAutoPlayTimer?.cancel()
        
        // Create new timer for carousel
        carouselAutoPlayTimer = Timer(true)
        carouselAutoPlayTimer?.scheduleAtFixedRate(
            object : TimerTask() {
                override fun run() {
                    Platform.runLater {
                        try {
                            forecastCarousel?.showNext()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            },
            UIConstants.CAROUSEL_AUTO_PLAY_DELAY_MS, // First advance after 5 seconds
            UIConstants.CAROUSEL_AUTO_PLAY_INTERVAL_MS  // Then every 5 seconds
        )
    }

    /**
     * Stop carousel auto-play (when user interacts)
     */
    fun stopCarouselAutoPlay() {
        carouselAutoPlayTimer?.cancel()
        carouselAutoPlayTimer = null
    }
    
    /**
     * Stop all timers
     */
    fun stopAll() {
        contentRotationTimer?.cancel()
        contentRotationTimer = null
        carouselAutoPlayTimer?.cancel()
        carouselAutoPlayTimer = null
    }
}
