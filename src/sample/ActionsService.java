package sample;

import com.google.gson.Gson;
import javafx.scene.paint.Color;

public class ActionsService {

    private static ActionsService instance;

    private int actionType = NO_ACTION;
    private ClientServer server;

    public static final int NO_ACTION = -1;
    public static final int DRAWING = 0;
    public static final int CIRCLE = 1;
    public static final int RECTANGLE = 2;
    public static final int INSERT_IMAGE = 4;
    public static final int DRAW_TEXT=5;
    public static final int CUT=6;

    private Color color = Color.BLACK;
    private double strokeColor = 1;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static ActionsService getInstance() {
        if (instance == null) {
            instance = new ActionsService();
        }

        return instance;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getAction() {
        return actionType;
    }

    public void setAction(int action) {
        actionType = action;
    }

    public double getStrokeColor() {
        return strokeColor;
    }

    public void setStrokeColor(double strokeColor) {
        this.strokeColor = strokeColor;
    }

    public void startServer(String ip, String name,OnItemDrawnListener listener) {
        this.name=name;
        server = new ClientServer(ip, name,listener);
        server.start();
    }

    public void onActionMade(Action action) {
        Gson gson = new Gson();
        server.printMessage(gson.toJson(action));
    }
}
