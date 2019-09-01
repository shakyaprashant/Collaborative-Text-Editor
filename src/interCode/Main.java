package interCode;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("views/mainWindow.fxml"));
        primaryStage.setTitle("interCode");
        primaryStage.setScene(new Scene(root, 1000, 600));
        primaryStage.setMinHeight(558);
        primaryStage.setMinWidth(854);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
