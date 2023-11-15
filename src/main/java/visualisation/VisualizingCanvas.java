package visualisation;

import javafx.animation.AnimationTimer;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;

import java.util.Arrays;

public class VisualizingCanvas extends Pane {
    private static final int RADIUS = 5;
    private double[][] points;
    private final Paint foreground = Color.grayRgb(220);
    private TracingCircle[] circles;

    public void constructPoints(int sides) {
        points = new double[sides][2];

        var centerX = getWidth() / 2;
        var centerY = getHeight() / 2;
        var radius = Math.min(centerX, centerY);

        var angle = 2 * Math.PI / sides;
        var firstAngle = angle - Math.PI / sides;

        for (int i = 0; i < sides; i++) {
            points[i][0] = centerX + radius * Math.cos(firstAngle + i * angle);
            points[i][1] = centerY + radius * Math.sin(firstAngle + i * angle);
        }
    }

    public void constPolygonAndTurtles() {
        var children = getChildren();
        children.clear();

        //polygon sides
        for (int i = 0; i < points.length - 1; i++) {
            var line = new Line(points[i][0], points[i][1], points[i+1][0], points[i+1][1]);
            line.setStroke(foreground);
            children.add(line);
        }
        {
            var i = points.length - 1;
            var line = new Line(points[0][0], points[0][1], points[i][0], points[i][1]);
            line.setStroke(foreground);
            children.add(line);
        }

        //create circles
        circles = new TracingCircle[points.length];
        for (int i = 0; i < circles.length; i++) {
            var circle = new TracingCircle(RADIUS, foreground);
            circle.setCenterX(points[i][0]);
            circle.setCenterY(points[i][1]);
            circles[i] = circle;
        }
        //set targets
        for (int i = 0; i < circles.length - 1; i++) {
            circles[i].setTarget(circles[i+1]);
        }
        circles[circles.length - 1].setTarget(circles[0]);

        //add paths
        for (var circle: circles) {
            var path = circle.getPath();
            path.setStroke(foreground);
            children.add(path);
        }
        //add circles
        children.addAll(circles);
    }

    public void startAnim() {
        new AnimationTimer() {
            @Override
            public void handle(long l) {
                for (var circle: circles) {
                    circle.calculateStep();
                }
                for (var circle: circles) {
                    circle.move();
                }
            }
        }.start();
    }
}
