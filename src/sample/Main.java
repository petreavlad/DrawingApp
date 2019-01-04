package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader=new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root =loader.load();
        Controller controller=loader.getController();
        primaryStage.setTitle("DrawIt");
        Scene scene=new Scene(root, 600, 600);
        primaryStage.resizableProperty().setValue(Boolean.FALSE);
        primaryStage.setMaximized(true);
        primaryStage.setScene(scene);
        scene.setOnKeyPressed(controller.getKeyPressListener());
        scene.setOnKeyReleased(controller.getKeyReleaseListener());
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
