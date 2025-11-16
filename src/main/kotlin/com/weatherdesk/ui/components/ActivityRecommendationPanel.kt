package com.weatherdesk.ui.components

import com.weatherdesk.model.WeatherCondition
import com.weatherdesk.ui.content.WeatherContent
import com.weatherdesk.util.UIConstants
import javafx.animation.FadeTransition
import javafx.animation.RotateTransition
import javafx.animation.TranslateTransition
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.layout.*
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.scene.text.TextAlignment
import javafx.util.Duration
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

/**
 * Activity Recommendation Panel - CORE UI Feature
 * 
 * Displays weather-based activity suggestions prominently in the UI
 * Shows multiple recommendations with weather context and interactive features
 * Updates automatically when weather changes
 * 
 * Design: Modern card-based layout with smooth animations
 */
class ActivityRecommendationPanel : VBox() {
    
    // UI Components
    private val titleLabel = Label("Things to Do Today")
    private val weatherContextLabel = Label()
    private val activitiesContainer = VBox(UIConstants.STANDARD_SPACING)
    private val refreshButton = Button("Get New Suggestions 🔄")
    private val scrollPane = ScrollPane()
    
    // State
    private var currentCondition: WeatherCondition = WeatherCondition.UNKNOWN
    private var currentTemperature: Double = 20.0
    private var isExpanded = true
    
    init {
        setupLayout()
        setupStyling()
        setupInteractions()
        logger.info { "Activity Recommendation Panel initialized" }
    }
    
    /**
     * Setup the layout structure
     */
    private fun setupLayout() {
        // Main container styling
        spacing = UIConstants.STANDARD_SPACING
        padding = Insets(UIConstants.LARGE_PADDING)
        alignment = Pos.TOP_CENTER
        styleClass.add("activity-recommendation-panel")
        
        // Title section
        titleLabel.font = Font.font("System", FontWeight.BOLD, 24.0)
        titleLabel.textAlignment = TextAlignment.CENTER
        titleLabel.styleClass.add("panel-title")
        
        // Weather context label
        weatherContextLabel.font = Font.font("System", FontWeight.NORMAL, 14.0)
        weatherContextLabel.textAlignment = TextAlignment.CENTER
        weatherContextLabel.styleClass.add("weather-context")
        weatherContextLabel.isWrapText = true
        
        // Activities container
        activitiesContainer.styleClass.add("activities-container")
        activitiesContainer.padding = Insets(UIConstants.SMALL_PADDING)
        
        // ScrollPane for activities
        scrollPane.content = activitiesContainer
        scrollPane.isFitToWidth = true
        scrollPane.hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
        scrollPane.vbarPolicy = ScrollPane.ScrollBarPolicy.AS_NEEDED
        scrollPane.styleClass.add("activities-scroll")
        setVgrow(scrollPane, Priority.ALWAYS)
        
        // Refresh button
        refreshButton.styleClass.addAll("refresh-button", "action-button")
        refreshButton.maxWidth = Double.MAX_VALUE
        
        // Add all components
        children.addAll(
            titleLabel,
            weatherContextLabel,
            scrollPane,
            refreshButton
        )
        
        // Set preferred size
        minWidth = 300.0
        prefWidth = 400.0
        maxWidth = 600.0
        prefHeight = 500.0
    }
    
    /**
     * Setup component styling
     */
    private fun setupStyling() {
        // Panel background and effects
        style = """
            -fx-background-color: linear-gradient(to bottom, rgba(255,255,255,0.95), rgba(245,245,250,0.95));
            -fx-background-radius: 15px;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0.3, 0, 3);
            -fx-border-radius: 15px;
            -fx-border-color: rgba(200,200,220,0.3);
            -fx-border-width: 1px;
        """.trimIndent()
        
        // Title styling
        titleLabel.style = """
            -fx-text-fill: linear-gradient(to right, #4A90E2, #7B68EE);
            -fx-font-smoothing-type: lcd;
        """.trimIndent()
        
        // Scroll pane styling
        scrollPane.style = """
            -fx-background-color: transparent;
            -fx-background: transparent;
        """.trimIndent()
        
        // Refresh button styling
        refreshButton.style = """
            -fx-background-color: linear-gradient(to bottom, #4A90E2, #357ABD);
            -fx-text-fill: white;
            -fx-font-weight: bold;
            -fx-background-radius: 8px;
            -fx-padding: 10 20;
            -fx-cursor: hand;
        """.trimIndent()
        
        // Hover effect for button
        refreshButton.setOnMouseEntered {
            refreshButton.style += """
                -fx-effect: dropshadow(gaussian, rgba(74,144,226,0.6), 8, 0.4, 0, 2);
                """.trimIndent()
        }
        refreshButton.setOnMouseExited {
            refreshButton.style = """
                -fx-background-color: linear-gradient(to bottom, #4A90E2, #357ABD);
                -fx-text-fill: white;
                -fx-font-weight: bold;
                -fx-background-radius: 8px;
                -fx-padding: 10 20;
                -fx-cursor: hand;
            """.trimIndent()
        }
    }
    
    /**
     * Setup user interactions
     */
    private fun setupInteractions() {
        refreshButton.setOnAction {
            refreshActivities()
            animateRefresh()
        }
    }
    
    /**
     * Update panel with new weather data - MAIN PUBLIC API
     */
    fun updateWeather(condition: WeatherCondition, temperature: Double) {
        try {
            logger.info { "Updating ActivityRecommendationPanel: $condition, ${temperature}°C" }
            
            currentCondition = condition
            currentTemperature = temperature
            
            // Update weather context
            updateWeatherContext()
            
            // Generate and display activities
            refreshActivities()
            
            // Animate the update
            animateUpdate()
            
        } catch (e: Exception) {
            logger.error(e) { "Error updating Activity Recommendation Panel" }
        }
    }
    
    /**
     * Update the weather context label
     */
    private fun updateWeatherContext() {
        val emoji = WeatherContent.getWeatherEmoji(currentCondition)
        val tempDesc = when {
            currentTemperature > 30 -> "Hot day"
            currentTemperature > 25 -> "Warm day"
            currentTemperature > 15 -> "Pleasant day"
            currentTemperature > 5 -> "Cool day"
            currentTemperature > 0 -> "Cold day"
            else -> "Freezing day"
        }
        
        weatherContextLabel.text = "$emoji $tempDesc • ${String.format("%.0f", currentTemperature)}°C"
    }
    
    /**
     * Refresh the activities list
     */
    private fun refreshActivities() {
        try {
            activitiesContainer.children.clear()
            
            // Get 5 unique activity suggestions
            val activities = generateActivities(5)
            
            activities.forEachIndexed { index, activity ->
                val activityCard = createActivityCard(activity, index)
                activitiesContainer.children.add(activityCard)
            }
            
            logger.info { "Refreshed ${activities.size} activity suggestions" }
            
        } catch (e: Exception) {
            logger.error(e) { "Error refreshing activities" }
        }
    }
    
    /**
     * Generate multiple unique activity suggestions
     */
    private fun generateActivities(count: Int): List<String> {
        val activities = mutableSetOf<String>()
        var attempts = 0
        val maxAttempts = count * 3  // Prevent infinite loop
        
        while (activities.size < count && attempts < maxAttempts) {
            val activity = WeatherContent.getActivitySuggestion(currentCondition, currentTemperature)
            // Remove prefix like "Perfect day for: " to get clean activity text
            val cleanActivity = activity.substringAfter(": ").takeIf { it.isNotEmpty() } ?: activity
            activities.add(cleanActivity)
            attempts++
        }
        
        return activities.toList()
    }
    
    /**
     * Create an activity card UI component
     */
    private fun createActivityCard(activity: String, index: Int): HBox {
        val card = HBox(UIConstants.STANDARD_SPACING)
        
        // Icon/Number label
        val numberLabel = Label("${index + 1}")
        numberLabel.font = Font.font("System", FontWeight.BOLD, 20.0)
        numberLabel.alignment = Pos.CENTER
        numberLabel.prefWidth = 40.0
        numberLabel.prefHeight = 40.0
        numberLabel.style = """
            -fx-background-color: linear-gradient(to bottom right, #4A90E2, #7B68EE);
            -fx-text-fill: white;
            -fx-background-radius: 20px;
        """.trimIndent()
        numberLabel.alignment = Pos.CENTER
        
        // Activity text label
        val activityLabel = Label(activity)
        activityLabel.font = Font.font("System", FontWeight.NORMAL, 16.0)
        activityLabel.isWrapText = true
        activityLabel.maxWidth = Double.MAX_VALUE
        HBox.setHgrow(activityLabel, Priority.ALWAYS)
        
        // Card container
        card.children.addAll(numberLabel, activityLabel)
        card.alignment = Pos.CENTER_LEFT
        card.padding = Insets(UIConstants.STANDARD_PADDING)
        card.style = """
            -fx-background-color: white;
            -fx-background-radius: 10px;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0.2, 0, 2);
        """.trimIndent()
        
        // Hover effect
        card.setOnMouseEntered {
            card.style = """
                -fx-background-color: #F8F9FA;
                -fx-background-radius: 10px;
                -fx-effect: dropshadow(gaussian, rgba(74,144,226,0.3), 8, 0.3, 0, 3);
                -fx-cursor: hand;
            """.trimIndent()
        }
        card.setOnMouseExited {
            card.style = """
                -fx-background-color: white;
                -fx-background-radius: 10px;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0.2, 0, 2);
            """.trimIndent()
        }
        
        return card
    }
    
    /**
     * Animate the panel update
     */
    private fun animateUpdate() {
        val fade = FadeTransition(Duration.millis(UIConstants.SHORT_ANIMATION_DURATION.toDouble()), activitiesContainer)
        fade.fromValue = 0.3
        fade.toValue = 1.0
        fade.play()
    }
    
    /**
     * Animate the refresh action
     */
    private fun animateRefresh() {
        // Rotate button
        val rotate = RotateTransition(Duration.millis(500.0), refreshButton)
        rotate.byAngle = 360.0
        rotate.play()
        
        // Slide activities
        val slide = TranslateTransition(Duration.millis(300.0), activitiesContainer)
        slide.fromX = -20.0
        slide.toX = 0.0
        slide.play()
    }
    
    /**
     * Cleanup resources
     */
    fun dispose() {
        logger.info { "Disposing ActivityRecommendationPanel" }
        activitiesContainer.children.clear()
    }
}
