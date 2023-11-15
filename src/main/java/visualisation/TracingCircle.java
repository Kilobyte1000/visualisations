package visualisation;

import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

public class TracingCircle extends Circle {
    private double step = 1;
    private Circle target = null;
    private final Polyline path = new Polyline();

    private double deltaX = Double.NaN;
    private double deltaY = Double.NaN;

    public void calculateStep() {
        if (target == null) {
            return;
        }
        var height = target.getCenterY() - getCenterY();
        var base = target.getCenterX() - getCenterX();

        var tan = height / base;
        var cos = Math.copySign(1.0 / Math.sqrt(1 + tan * tan), base);
        var sin = Math.copySign(Math.sqrt(1 - cos * cos), height);

        deltaX = cos * step;
        deltaY = sin * step;
    }

    public void move() {
        if (!Double.isNaN(deltaX) && !Double.isNaN(deltaY)) {
            var x = getCenterX() + deltaX;
            var y = getCenterY() + deltaY;
            setCenterX(x);
            setCenterY(y);
            path.getPoints().addAll(x, y);
            deltaX = Double.NaN;
            deltaY = Double.NaN;
        } else {
            throw new IllegalStateException("Call calculate step first");
        }
    }

    public double getStep() {
        return step;
    }

    public void setStep(double step) {
        this.step = step;
    }

    public Circle getTarget() {
        return target;
    }

    public void setTarget(Circle target) {
        this.target = target;
    }

    public Polyline getPath() {
        return path;
    }

    public TracingCircle(double v) {
        super(v);
    }

    public TracingCircle(double v, Paint paint) {
        super(v, paint);
        setCache(false);
        path.setCache(false);
    }

    public TracingCircle() {
    }

    public TracingCircle(double v, double v1, double v2) {
        super(v, v1, v2);
    }

    public TracingCircle(double v, double v1, double v2, Paint paint) {
        super(v, v1, v2, paint);
    }
}
