package com.weatherdesk

import com.weatherdesk.repository.BackendRepository
import com.weatherdesk.view.EnhancedWeatherController
import com.weatherdesk.viewmodel.WeatherViewModel
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.Stage
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

/**
 * Main JavaFX Application class for WeatherDesk
 */
class WeatherDeskApplication : Application() {

    private lateinit var backendRepository: BackendRepository
    private lateinit var viewModel: WeatherViewModel
    private lateinit var controller: EnhancedWeatherController

    override fun start(primaryStage: Stage) {
        try {
            logger.info { "Starting WeatherDesk application..." }

            // Initialize backend and ViewModel
            backendRepository = BackendRepository("http://localhost:8080",
                tokenProvider = { viewModel.authToken.get() }
            )
            viewModel = WeatherViewModel(backendRepository)

            // Load Enhanced FXML
            val loader = FXMLLoader(javaClass.getResource("/fxml/EnhancedWeatherView.fxml"))
            val root = loader.load<javafx.scene.Parent>()

            // Get controller and inject ViewModel
            controller = loader.getController<EnhancedWeatherController>()
            controller.setViewModel(viewModel)

            // Setup scene with larger size for enhanced UI
            val scene = Scene(root, 1200.0, 900.0)

            // Setup stage
            primaryStage.apply {
                title = "WeatherDesk - Where Weather Meets Wonder"
                this.scene = scene
                minWidth = 1000.0
                minHeight = 800.0

                // Try to set icon (optional)
                try {
                    val iconStream = javaClass.getResourceAsStream("/images/icon.png")
                    if (iconStream != null) {
                        icons.add(Image(iconStream))
                    }
                } catch (e: Exception) {
                    logger.warn { "Could not load application icon" }
                }

                show()
            }

            logger.info { "WeatherDesk application started successfully" }

        } catch (e: Exception) {
            logger.error(e) { "Failed to start application" }
            showErrorDialog(
                "Startup Error",
                "Failed to start WeatherDesk",
                "Error: ${e.message}"
            )
        }
    }

    override fun stop() {
        try {
            logger.info { "Shutting down WeatherDesk application..." }

            // Cleanup enhanced UI components
            if (::controller.isInitialized) {
                controller.cleanup()
            }

            viewModel.onDestroy()
            backendRepository.close()
            logger.info { "WeatherDesk application stopped successfully" }
        } catch (e: Exception) {
            logger.error(e) { "Error during shutdown" }
        }
    }

    private fun showErrorDialog(title: String, header: String, content: String) {
        javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR).apply {
            this.title = title
            this.headerText = header
            this.contentText = content
            showAndWait()
        }
    }
}

/**
 * Main entry point
 */
fun main(args: Array<String>) {
    try {
        logger.info { "WeatherDesk application starting..." }
        Application.launch(WeatherDeskApplication::class.java, *args)
    } catch (e: Exception) {
        logger.error(e) { "Fatal error in main" }
        System.err.println("Fatal error: ${e.message}")
        e.printStackTrace()
    }
}