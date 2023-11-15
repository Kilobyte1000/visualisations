package visualisation

import builder.Background
import javafx.animation.AnimationTimer
import javafx.application.Application
import javafx.application.Platform
import javafx.geometry.Insets
import javafx.scene.Scene
import visualisation.VisualizingCanvas
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.paint.Color
import javafx.stage.Stage
import visualisation.circles.CirclePane
import java.lang.Runnable
import java.lang.InterruptedException

class Main : Application() {

    override fun start(primaryStage: Stage) {
        val root = VisualizingCanvas()
        val scene = Scene(root)
        scene.fill = Color.grayRgb(15)
        root.background = Background(Color.grayRgb(15))


        primaryStage.scene = scene
        primaryStage.isFullScreen = true
        primaryStage.show()

        root.constructPoints(15)
        root.constPolygonAndTurtles()

        Thread {
            Thread.sleep(3000)
            println("started")
            Platform.runLater { root.startAnim() }

        }.start()
    }

    private companion object {
        const val CENTER_X = 600
        const val length = 200
    }
}

fun main(args: Array<String>) {
    Application.launch(Main::class.java, *args)
}