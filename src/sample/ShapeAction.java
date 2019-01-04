package sample;

import com.google.gson.annotations.SerializedName;

public class ShapeAction extends Action {
    @SerializedName("x")
    private double x;
    @SerializedName("y")
    private double y;
    @SerializedName("width")
    private double width;
    @SerializedName("height")
    private double height;
    @SerializedName("fill")
    private boolean fill;

    public boolean isFill() {
        return fill;
    }

    public void setFill(boolean fill) {
        this.fill = fill;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }
}
