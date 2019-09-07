package client.controllers;

import client.SceneController;
import client.handlers.WelcomeViewThread;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import client.Client;
import client.SceneController;
//import client.handlers.WelcomeViewThread;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;

public class WelcomeView {
    private Client client;
    private SceneController sceneController;
    @FXML
    JFXTextField documentName;
    @FXML
    JFXButton openButton,createButton;

    @FXML
    public void onCreateClicked(ActionEvent e) {
        System.out.println("WelcomeView Create Button Clicked");
        String newDocumentName = documentName.getText().trim();
        if (newDocumentName.matches("[\\w\\d]+")) {
            WelcomeViewThread thread = new WelcomeViewThread(client, "new " + newDocumentName);
            thread.start();
        } else {
            Alert a1 = new Alert(Alert.AlertType.ERROR);
            a1.setTitle("Invalid Arguments");
            a1.setContentText("I\"Document name cannot be empty and must only contain letters and digits.Invalid document name\"");
            a1.setHeaderText(null);
            a1.showAndWait();
        }
    }
    @FXML
    public void openDocumentClicked(ActionEvent e) {
        client.sendMessageToServer("look");
    }

    public void setClient(Client client) {
        this.client = client;
    }
    public void setSceneController(SceneController sceneController) {
        this.sceneController = sceneController;
    }
    public Client getClient() {
        return client;
    }

}
