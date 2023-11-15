package visualisation.circles

import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.shape.Circle
import javafx.scene.shape.Line
import kotlin.math.*

class CirclePane: Pane() {

    private val rootCircle = Circle(RADIUS, FOREGROUND)
    private val targetCircle = Circle(RADIUS, FOREGROUND)
    private val rod = Line().apply {
        startXProperty().bind(rootCircle.centerXProperty())
        startYProperty().bind(rootCircle.centerYProperty())

        endXProperty().bind(targetCircle.centerXProperty())
        endYProperty().bind(targetCircle.centerYProperty())

        stroke = FOREGROUND
    }

    private var tangential1 = .0
    private var radial1 = .0

    private var tangential2 = .0
    private var radial2 = .0

    var isRelative = true

    var theta1: Double = Math.PI / 3.0
        set(value) {
            field = value
            setVelocity()
        }
    var speed1: Double = 5.0
        set(value) {
            field = value
            setVelocity()
        }

    var theta2 = Math.PI / 6.0
        set(value) {
            field = value
            setTargetVelocity()
        }

    private fun setVelocity() {
        tangential1 = speed1 * sin(theta1)
        radial1 = speed1 * cos(theta1)
        setTargetVelocity()
    }
    private fun setTargetVelocity() {
        radial2 = radial1
        tangential2 = radial1 * tan(theta2)
    }

    fun add() {
        val bounds = layoutBounds
        rootCircle.apply {
            centerX = bounds.centerX
            centerY = bounds.centerY
        }
        targetCircle.apply {
            centerX = rootCircle.centerX + LENGTH
            centerY = rootCircle.centerY
        }
        setVelocity()

        children.addAll(rootCircle, targetCircle, rod)
    }

    fun move() {
        val height = targetCircle.centerY - rootCircle.centerY
        val base   = targetCircle.centerX - rootCircle.centerX

        val tan = height / base
        val cos = (1.0 / sqrt(1 + tan * tan)).withSign(base)
        val sin = sqrt(1 - cos * cos).withSign(height)


        val deltaX1 = radial1 * cos - tangential1 * sin
        val deltaY1 = radial1 * sin + tangential1 * cos

        val deltaX2 = radial2 * cos - tangential2 * sin
        val deltaY2 = radial2 * sin + tangential2 * cos

        if (!isRelative) {
            rootCircle.apply {
                centerX += deltaX1
                centerY += deltaY1
            }

            targetCircle.apply {
                centerX += deltaX2
                centerY += deltaY2
            }
        } else {
            /*targetCircle.apply {
                centerX += deltaX2 - deltaX1
                centerY += deltaY2 - deltaY1
            }*/
            targetCircle.apply {
                /*centerX += (-tangential2 + tangential1) * sin
                centerY += (tangential2 - tangential1) * cos*/

                centerX += (tangential2 - tangential1) * -sin
                centerY += (tangential2 - tangential1) * cos
            }
        }
    }

    private companion object {
        val FOREGROUND: Paint = Color.grayRgb(220)
        const val RADIUS = 20.0
        const val LENGTH = 100
    }
}