package sample;

import com.google.gson.annotations.SerializedName;

public class Point {

    @SerializedName("coordonate_x")
    private double x;

    @SerializedName("coordonate_y")
    private double y;

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
}
