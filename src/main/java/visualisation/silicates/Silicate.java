package visualisation.silicates;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import util.FormUtil;

import static java.lang.Math.*;
import static util.FormUtil.withLabel;

public class Silicate extends Application {

    private static final Paint BACKGROUND = Color.grayRgb(15);
    private static final Paint FOREGROUND = Color.grayRgb(220);
    static final double ANGLE = Math.PI / 3;
    public static final Paint O_ATOM_FILL = Color.grayRgb(2);
    public static final double O_ATOM_FACTOR = 2.0;

    private final Timeline loop = new Timeline();
    private final Canvas canvas = new Canvas(800, 600);
    private double radius = 5;
    private double length;
    private double median;
    private double verticalDistance;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        setLength(20.0);
        var brush = canvas.getGraphicsContext2D();
        var root = new Group(canvas);
        canvas.widthProperty().bind(primaryStage.widthProperty());
        canvas.heightProperty().bind(primaryStage.heightProperty());

        brush.setFill(FOREGROUND);
        brush.setStroke(FOREGROUND);

        var dialog = showControlDialog(brush, root);

        root.setAutoSizeChildren(true);
        var scene = new Scene(root, 800, 600);
        scene.setFill(BACKGROUND);
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(windowEvent -> {
            System.out.println("closed");
            dialog.setResult(Boolean.TRUE);
            dialog.close();
        });
        primaryStage.show();

    }


    private void paint(GraphicsContext brush, Parent root) {
        double centerX = root.getLayoutBounds().getCenterX();
        double centerY = root.getLayoutBounds().getCenterY();
        var init = new DrawData(centerX, centerY, null, null, true);
        paintHexagon(brush, init);
        paintBonds(brush, init);
        paintAtoms(brush, init);

        DrawData drawDown = new DrawData(centerX - 2 * length,
                centerY,
                DirectionX.LEFT,
                DirectionY.DOWN,
                true);
        DrawData drawUp = new DrawData(centerX + 2 * length,
                centerY,
                DirectionX.RIGHT,
                DirectionY.UP,
                true);


        while (drawUp != null && drawDown != null) {
            drawDown = paintUnit(brush, drawDown, root);
            drawUp = paintUnit(brush, drawUp, root);
        }
        /*var frame = new KeyFrame(Duration.millis(5), actionEvent -> {
            drawDown = paintUnit(brush, drawDown, root);
            drawUp = paintUnit(brush, drawUp, root);
            if (drawUp == null && drawDown == null) {
                loop.stop();
            }
        });
        loop.getKeyFrames().add(frame);
        loop.setCycleCount(Animation.INDEFINITE);
        loop.play();*/

    }

    private DrawData paintUnit(GraphicsContext brush, DrawData drawData, Parent root) {
        if (drawData == null) {
            System.out.println("null");
            return null;
        }
        var x = drawData.centerX();
        var y = drawData.centerY();

        var bounds = root.getLayoutBounds();
        var inWidth = x >= 0 && x <= bounds.getWidth();
        var inHeight = y >= 0 && y <= bounds.getHeight();

        paintHexagon(brush, drawData);
        paintBonds(brush, drawData);
        paintAtoms(brush, drawData);

        if (!drawData.moveWhenOffScreen() || (inWidth && inHeight)) {

            return drawData.spreadDirectionX() == DirectionX.LEFT? dataLeft(drawData, inWidth): dataRight(drawData, inWidth);

        } else {
            return inHeight?
                    drawData.spreadDirectionY() == DirectionY.UP? dataUp(drawData): dataDown(drawData)
                    : null;
        }



    }


    private DrawData dataRight(DrawData drawData, boolean moveWhenOffScreen) {
        return new DrawData(
                drawData.centerX() + 2 * length,
                drawData.centerY(),
                DirectionX.RIGHT,
                drawData.spreadDirectionY(),
                moveWhenOffScreen
        );
    }

    private DrawData dataLeft(DrawData drawData, boolean moveWhenOffScreen) {
        return new DrawData(
                drawData.centerX() - 2 * length,
                drawData.centerY(),
                DirectionX.LEFT,
                drawData.spreadDirectionY(),
                moveWhenOffScreen
        );
    }

    private DrawData dataUp(DrawData drawData) {
        double xOffset = drawData.spreadDirectionX() == DirectionX.LEFT? -length: length;
        return new DrawData(
                drawData.centerX() + xOffset,
                drawData.centerY() - verticalDistance,
                drawData.spreadDirectionX().reverse(),
                DirectionY.UP,
                false
        );
    }

    private DrawData dataDown(DrawData drawData) {
        double xOffset = drawData.spreadDirectionX() == DirectionX.LEFT? -length: length;

        return new DrawData(
                drawData.centerX() + xOffset,
                drawData.centerY() + verticalDistance,
                drawData.spreadDirectionX().reverse(),
                DirectionY.DOWN,
                false
        );    }


    private void paintHexagon(GraphicsContext brush, DrawData drawData) {
        brush.setLineDashes(3);

        double x = drawData.centerX();
        double y = drawData.centerY();

        double oldX = length;
        double oldY = 0;
        double angle = ANGLE;
        for (int i = 0; i < 5; i++) {
            double newX = cos(angle) * length;
            double newY = sin(angle) * length;

            brush.strokeLine(x + oldX, y + oldY,
                    x + newX, y + newY);
            oldX = newX;
            oldY = newY;

            angle += ANGLE;
        }

        brush.strokeLine(x + oldX, y + oldY, x + length, y + 0);
    }

    private void paintBonds(GraphicsContext brush, DrawData drawData) {
        brush.setLineDashes(0);
        var angle = Math.PI / 2;

        var x = drawData.centerX() + length;
        var y = drawData.centerY() + 0;

        for (int i = 0; i < 6; i++) {
            //continue old line
            var deltaX = cos(angle) * median;
            var deltaY = sin(angle) * median;

            var newX = x + deltaX;
            var newY = y + deltaY;
            brush.strokeLine(x, y, newX, newY);

            x = newX;
            y = newY;
            angle += ANGLE;

            //new line at an angle
            deltaX = cos(angle) * median;
            deltaY = sin(angle) * median;

            newX = x + deltaX;
            newY = y + deltaY;
            brush.strokeLine(x, y, newX, newY);

            x = newX;
            y = newY;

        }
    }

    private void paintAtoms(GraphicsContext brush, DrawData drawData) {
        var angle = Math.PI / 2;

        double twiceRadius = radius * 2.0;
        double x = drawData.centerX() + length;
        double y = drawData.centerY();

        var sAtomRadius = radius / O_ATOM_FACTOR;
        var twiceSAtomRadius = sAtomRadius * 2.0;

        for (int i = 0; i < 6; i++) {
            //shared oxygen atom
            var deltaX = cos(angle) * median;
            var deltaY = sin(angle) * median;

            var newX = x + deltaX;
            var newY = y + deltaY;
            brush.fillOval(newX - radius, newY - radius, twiceRadius, twiceRadius);

            x = newX;
            y = newY;
            angle += ANGLE;

            //unshared oxygen atom
            double cos = cos(angle);
            double sin = sin(angle);
            deltaX = cos * median;
            deltaY = sin * median;

            newX = x + deltaX;
            newY = y + deltaY;
            brush.fillOval(newX - radius, newY - radius, twiceRadius, twiceRadius);

            brush.setFill(O_ATOM_FILL);
            brush.fillOval(newX - sAtomRadius, newY - sAtomRadius, twiceSAtomRadius, twiceSAtomRadius);
            brush.setFill(FOREGROUND);

            x = newX;
            y = newY;

        }
    }

    private void setLength(double length) {
        this.length = length;
        median = length / sqrt(3);
        verticalDistance = length * sqrt(3);
    }

    private Dialog<Boolean> showControlDialog(GraphicsContext brush, Parent pane) {
        Dialog<Boolean> dialog = new Dialog<>();
        var root = dialog.getDialogPane();
        root.setHeaderText("Controls");

        Spinner<Double> lengthIn = new Spinner<>(
                new SpinnerValueFactory
                        .DoubleSpinnerValueFactory(0, Double.MAX_VALUE, 50));

        Spinner<Double> radiusIn = new Spinner<>(
                new SpinnerValueFactory
                        .DoubleSpinnerValueFactory(0, Double.MAX_VALUE, 5));

        lengthIn.setEditable(true);
        radiusIn.setEditable(true);

        var draw = new Button("Draw");
        root.getButtonTypes().add(new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE));

        draw.setOnAction(actionEvent -> {
            loop.stop();
            brush.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            setLength(lengthIn.getValue());
            radius = radiusIn.getValue();
            paint(brush, pane);
        });
        lengthIn.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) draw.fire();
        });
        radiusIn.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) draw.fire();
        });

        root.setContent(new HBox(
                10,
                withLabel(lengthIn, "Bond Length"),
                withLabel(radiusIn, "O Atom Radius"),
                draw
        ));

        new JMetro(root, Style.DARK);

        dialog.initModality(Modality.NONE);
        dialog.show();

        return dialog;
    }
}

/*class Offsets {
    public final double[] HexXOff;

    Offsets(int length, int med) {
        for (int i = 0; i < 5; i++) {
            double newX = cos(angle) * length;
            double newY = sin(angle) * length;

            brush.strokeLine(x + oldX, y + oldY,
                    x + newX, y + newY);
            oldX = newX;
            oldY = newY;

            angle += Silicate.ANGLE;
        }
    }
}*/

enum DirectionX {
    LEFT, RIGHT;
    public DirectionX reverse() {
        return this == LEFT? RIGHT: LEFT;
    }
}

enum DirectionY {
    UP, DOWN
}

record DrawData(double centerX, double centerY, DirectionX spreadDirectionX, DirectionY spreadDirectionY, boolean moveWhenOffScreen) {}
