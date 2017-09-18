package ui;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

public class PointBox extends StackPane {
    private DoubleProperty centerX = new SimpleDoubleProperty();
    private DoubleProperty centerY = new SimpleDoubleProperty();

    public PointBox() {
        super();
        centerX.bind(this.layoutXProperty().add(this.prefWidthProperty().divide(2)));
        centerY.bind(this.layoutYProperty().add(this.prefHeightProperty().divide(2)));
    }

    public final double getCenterX() {
        return this.centerX.get();
    }

    public final void setCenterX(double value) {
        this.centerX.set(value);
    }

    public final double getCenterY() {
        return this.centerY.get();
    }

    public final void setCenterY(double value) {
        this.centerY.set(value);
    }

    public DoubleProperty centerXProperty() {
        return centerX;
    }

    public DoubleProperty centerYProperty() {
        return centerY;
    }

    public Circle getCircle() {
        for (Node node : this.getChildren()) {
            if (node instanceof Circle) {
                return (Circle)node;
            }
        }
        return null;
    }

    public Text getText() {
        for (Node node : this.getChildren()) {
            if (node instanceof Text) {
                return (Text)node;
            }
        }
        return null;
    }
}
