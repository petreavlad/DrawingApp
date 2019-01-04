package sample;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class DrawingAction extends Action{

    @SerializedName("points")
    private List<Point> points=new ArrayList<>();

    @SerializedName("dotted")
    private boolean dotted;

    @SerializedName("longDash")
    private boolean longDash;

    @SerializedName("size")
    private double size;

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public boolean isLongDash() {
        return longDash;
    }

    public void setLongDash(boolean longDash) {
        this.longDash = longDash;
    }

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }

    public boolean isDotted() {
        return dotted;
    }

    public void setDotted(boolean dotted) {
        this.dotted = dotted;
    }
}
