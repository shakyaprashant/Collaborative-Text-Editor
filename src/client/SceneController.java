package client;

import client.controllers.ConnectServer;
import client.controllers.MainController;
import client.controllers.WelcomeView;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

public class SceneController extends Application {

    private Client client;
    private String username;
    private Stage window;
    private MainController documentView;

    @Override
    public void start(Stage primaryStage) throws Exception{
        window = primaryStage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("views/ConnectServer.fxml"));
        Parent root = loader.load();
        //Get Controller
        ConnectServer connectServer = loader.getController();
        connectServer.setSceneController(this);
        window.setTitle("Connect to Server");
        window.setScene(new Scene(root));
        window.show();
    }

    public void openUsernameDialog() {
        System.out.println("Trying to Open Username Dialog");
        TextInputDialog dialog = new TextInputDialog("Username");

        dialog.setTitle("Enter Username");
        dialog.setHeaderText("Enter your Username:");

        Optional<String> result = dialog.showAndWait();
        dialog.getDialogPane().lookupButton(ButtonType.CANCEL).setDisable(true);
        if(result.isPresent()) {
            username = result.get();
            client.sendMessageToServer("name " + username);
        }
        else {
            System.out.println("Username Not Entered");
        }
    }
    public void switchToWelcomeView() {
        //window.hide();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("views/WelcomeView.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        WelcomeView welcomeView = loader.getController();
        welcomeView.setSceneController(this);
        welcomeView.setClient(client);
        //window.close();
        //Stage window = new Stage();
        window.setTitle("Welcome to InterCode");
        window.setScene(new Scene(root));
        //window.showAndWait();
        window.show();
    }

    public void openErrorView(String error) {
        Alert a1 = new Alert(Alert.AlertType.ERROR);
        a1.setContentText(error);
        a1.setHeaderText(null);
        a1.showAndWait();
    }
    public static void main(String[] args) {
        launch(args);
    }
    
    public void setClient(Client client) {
        this.client = client;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void displayOpenDocuments(ArrayList<String> documentNames) {
        System.out.println("switching to open existing document view");
        if(documentNames == null) {
            System.out.println("There is no document on the server yet");
        }
        ChoiceDialog<String> choiceDialog = new ChoiceDialog<>(documentNames.get(0),documentNames);
        choiceDialog.setHeaderText("Open Document");

        // set content text
        choiceDialog.setContentText("Select a document to open");

        // show the dialog
        Optional<String> result = choiceDialog.showAndWait();
        result.ifPresent(s -> client.sendMessageToServer("open " + s));
    }

    public void switchToDocumentView(String documentName, String documentText) {
        System.out.println("Trying to Switch to Document View");
        window.hide();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("views/mainWindow.fxml"));
            Parent root = loader.load();
            documentView = loader.getController();
            documentView.setSceneController(this);
            documentView.setDocumentName(documentName);
            documentView.setDocumentText(documentText);
            window.setScene(new Scene(root));
            window.setTitle("InterCode");
            window.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Switching to Document View");
    }

    public void updateDocument(String documentText,int editPosition,
                               int editLength, String username,int version)
    {
        System.out.println("updating document");
        if(documentView !=null)
            documentView.updateDocument(documentText,editPosition, editLength,
                    username,version);
    }
    public String getUsername() {
        return username;
    }

    public Client getClient() {
        return client;
    }
}
