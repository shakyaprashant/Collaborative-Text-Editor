package client.controllers;

import client.Client;
import client.SceneController;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class AddNewCand {
    private SceneController sceneController;
    private Client client;
    @FXML
    private JFXTextField usernameField;

    @FXML
    private JFXTextField nameField;

    @FXML
    private JFXTextField emailField;

    @FXML
    private JFXPasswordField passwordField;

    @FXML
    private JFXPasswordField verifyPasswordField;

    @FXML
    void addButtonClicked(ActionEvent event) {
        client.sendMessageToServer("addCand " + usernameField.getText() + " " + nameField.getText() + " " +
                emailField.getText() + " " + passwordField.getText());
        String message = client.receiveMessageFromServer();
        String[] tokens = message.split(" ");
        if(tokens[0].equals("addCand")) {
            if (tokens[1].equals("success")) {
                sceneController.openIntDashboard();
            }
            else if(tokens[1].equals("fail")) {
                sceneController.openErrorView("Cannot Add User");
            }
        }
    }
    public void setSceneController(SceneController sceneController) {
        this.sceneController = sceneController;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
