package sample;

import com.google.gson.annotations.SerializedName;

public class ImageAction extends ShapeAction {

    @SerializedName("image")
    private byte[] image;

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
