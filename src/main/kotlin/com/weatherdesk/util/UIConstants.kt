package com.weatherdesk.util

import javafx.util.Duration

/**
 * UI animation and timing constants
 */
object UIConstants {
    // Padding and Spacing
    const val STANDARD_SPACING: Double = 12.0
    const val STANDARD_PADDING: Double = 12.0
    const val SMALL_PADDING: Double = 6.0
    const val LARGE_PADDING: Double = 20.0

    // Animation durations
    const val SHORT_ANIMATION_DURATION: Long = 300
    const val LONG_ANIMATION_DURATION: Long = 600
    val FADE_DURATION: Duration = Duration.millis(300.0)
    val ACTIVITY_LABEL_FADE_DURATION: Duration = Duration.millis(300.0)
    
    // Timing intervals
    const val CAROUSEL_AUTO_PLAY_DELAY_MS: Long = 5000L  // First advance after 5 seconds
    const val CAROUSEL_AUTO_PLAY_INTERVAL_MS: Long = 5000L  // Then every 5 seconds
    const val CONTENT_ROTATION_INTERVAL_MS: Long = 1000L  // Content updates every 1 second
    
    // Carousel card positioning
    const val CARD_OFFSET_BASE: Double = 250.0
    const val CARD_SCALE_FACTOR: Double = 0.15
    const val CARD_OPACITY_FACTOR: Double = 0.3
    
    // Globe rendering
    const val GLOBE_SIZE_RATIO: Double = 0.8
    const val GLOBE_DISTANCE: Double = 1000.0
}
