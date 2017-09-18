package ui;

import javafx.beans.InvalidationListener;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Transform;

public class Arrow extends Group {
    private Line line;

    private static final double arrow_size = 8;

    public Arrow(Line line, int weight) {
        this(line, new Polygon(), new Text(String.valueOf(weight)));
    }

    private Arrow(Line line, Polygon triangle, Text text) {
        super(line, triangle, text);
        this.line = line;

        double sxInit = getStartX();
        double syInit = getStartY();
        double exInit = getEndX();
        double eyInit = getEndY();

        double dxInit = exInit - sxInit;
        double dyInit = eyInit - syInit;
        double angleInit = Math.atan2(dyInit, dxInit);

        Transform transInit = Transform.translate(exInit, eyInit);
        transInit = transInit.createConcatenation(Transform.rotate(Math.toDegrees(angleInit), 0, 0));
        triangle.getPoints().clear();
        triangle.getPoints().addAll(
                0.0, 0.0,
                - arrow_size, arrow_size / 2,
                - arrow_size, - arrow_size / 2);
        triangle.getTransforms().clear();
        triangle.getTransforms().add(transInit);
        triangle.setFill(Color.PURPLE);

        text.setWrappingWidth(40);
        text.setTextAlignment(TextAlignment.CENTER);
        text.setTextOrigin(VPos.CENTER);
        text.setFill(Color.PURPLE);
        text.setLayoutX((sxInit + exInit) / 2 - text.getWrappingWidth() / 2);
        text.setLayoutY((syInit + eyInit) / 2 - text.getScaleY());

        InvalidationListener updater = o -> {
            double sx = getStartX();
            double sy = getStartY();
            double ex = getEndX();
            double ey = getEndY();

            double dx = ex - sx;
            double dy = ey - sy;
            double angle = Math.atan2(dy, dx);

            Transform transform = Transform.translate(ex, ey);
            transform = transform.createConcatenation(Transform.rotate(Math.toDegrees(angle), 0, 0));
            triangle.getPoints().clear();
            triangle.getPoints().addAll(
                    0.0, 0.0,
                    - arrow_size, arrow_size / 2,
                    - arrow_size, - arrow_size / 2);
            triangle.getTransforms().clear();
            triangle.getTransforms().add(transform);
            triangle.setFill(Color.PURPLE);

            text.setLayoutX((sx + ex) / 2 - text.getWrappingWidth() / 2);
            text.setLayoutY((sy + ey) / 2 - text.getScaleY());
        };

        startXProperty().addListener(updater);
        startYProperty().addListener(updater);
        endXProperty().addListener(updater);
        endYProperty().addListener(updater);
    }

    public void setStroke(Paint value) {
        this.line.setStroke(value);
    }

    public void setStrokeWidth(double value) {
        this.line.setStrokeWidth(value);
    }

    public final void setStartX(double value) {
        line.setStartX(value);
    }

    public final double getStartX() {
        return line.getStartX();
    }

    public final DoubleProperty startXProperty() {
        return line.startXProperty();
    }

    public final void setStartY(double value) {
        line.setStartY(value);
    }

    public final double getStartY() {
        return line.getStartY();
    }

    public final DoubleProperty startYProperty() {
        return line.startYProperty();
    }

    public final void setEndX(double value) {
        line.setEndX(value);
    }

    public final double getEndX() {
        return line.getEndX();
    }

    public final DoubleProperty endXProperty() {
        return line.endXProperty();
    }

    public final void setEndY(double value) {
        line.setEndY(value);
    }

    public final double getEndY() {
        return line.getEndY();
    }

    public final DoubleProperty endYProperty() {
        return line.endYProperty();
    }
}
