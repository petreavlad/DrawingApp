package sample;

import com.google.gson.annotations.SerializedName;
import javafx.scene.paint.Color;

public class Action {

    @SerializedName("type")
    private int type;
    @SerializedName("red")
    private double red;
    @SerializedName("green")
    private double green;
    @SerializedName("blue")
    private double blue;
    @SerializedName("opacity")
    private double opacity;
    @SerializedName("name")
    private String nameOfSender;

    public String getNameOfSender() {
        return nameOfSender;
    }

    public void setNameOfSender(String nameOfSender) {
        this.nameOfSender = nameOfSender;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Color getColor() {
        return new Color(red,green,blue,opacity);
    }

    public void setColor(Color color) {
        red=color.getRed();
        blue=color.getBlue();
        green=color.getGreen();
        opacity=color.getOpacity();
    }
}
