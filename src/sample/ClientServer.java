package sample;

import com.google.gson.Gson;
import javafx.application.Platform;

import javax.swing.text.TextAction;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.stream.Stream;

public class ClientServer extends Thread {

    private BufferedReader in;
    private PrintWriter out;
    private String ip;
    private String name;
    private OnItemDrawnListener listener;


    ClientServer(String ip, String name, OnItemDrawnListener listener) {
        this.ip = ip;
        this.name = name;
        this.listener = listener;
    }

    @Override
    public void run() {
        // Make connection and initialize streams
        String serverAddress = ip;
        Socket socket;
        try {
            socket = new Socket(serverAddress, 8061);
            in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            Platform.exit();
            System.exit(0);
            e.printStackTrace();
        }

        while (true) {
            // Process all messages from server, according to the protocol.
            Stream<String> lines = in.lines();
            if (lines == null) {
                Platform.exit();
                System.exit(0);
            }
            lines.forEach(line -> {
                if (line.startsWith("SUBMITNAME"))
                    out.println(name);
                if (!line.startsWith(name + ":")&&!line.startsWith("NAMEACCEPTED")&&!line.startsWith("SUBMITNAME")) {
                    Gson gson = new Gson();
                    System.out.println(line);
                    Action action = gson.fromJson(line, Action.class);
                    if(!action.getNameOfSender().equals(name)) {
                        switch (action.getType()) {
                            case ActionsService.DRAWING:
                                action = gson.fromJson(line, DrawingAction.class);
                                break;
                            case ActionsService.CIRCLE:
                                action = gson.fromJson(line, ShapeAction.class);
                                break;
                            case ActionsService.RECTANGLE:
                                action = gson.fromJson(line, ShapeAction.class);
                                break;
                            case ActionsService.INSERT_IMAGE:
                                action = gson.fromJson(line, ImageAction.class);
                                break;
                            case ActionsService.DRAW_TEXT:
                                action=gson.fromJson(line,WrittingAction.class);
                                break;
                        }
                        listener.onItemAction(action);
                    }
                }
            });
        }
    }

    public void printMessage(String text) {
        out.println(text);
    }


}
