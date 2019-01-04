package sample;

import com.google.gson.annotations.SerializedName;

public class WrittingAction extends Action{

    @SerializedName("x")
    private double x;
    @SerializedName("y")
    private double y;
    @SerializedName("font_name")
    private String fontName;
    @SerializedName("font_size")
    private double fontSize;
    @SerializedName("text")
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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

    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public double getFontSize() {
        return fontSize;
    }

    public void setFontSize(double fontSize) {
        this.fontSize = fontSize;
    }
}
