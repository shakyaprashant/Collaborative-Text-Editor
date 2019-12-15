package client.controllers;

import client.Client;
import client.SceneController;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class IntSignUp {

    @FXML
    private JFXTextField usernameField;

    @FXML
    private JFXPasswordField passwordField;

    @FXML
    private JFXPasswordField verifyPasswordField;
    @FXML
    private Label usernameNotAvailLabel;

    @FXML
    private Label passwordNotMatchLabel;

    private SceneController sceneController;
    private Client client;

    @FXML
    void registerButtonClicked(ActionEvent event) {
        if(checkPassword() && checkUsername())
        {
            client.sendMessageToServer("addInt " + usernameField.getText() + " " + passwordField.getText());
            String message = client.receiveMessageFromServer(), tokens[];
            tokens = message.split(" ");
            if(tokens[0].equals("addInt")) {
                if(tokens[1].equals("success")) {
                    sceneController.openLoginWindow();
                }
                else if(tokens[1].equals("fail")) {
                    sceneController.openErrorView("Error in Registering");
                }
            }
            else
                sceneController.openErrorView("Invalid message from server");
        }
    }

    private boolean checkPassword() {
        if(!passwordField.getText().trim().equals(verifyPasswordField.getText().trim()))
        {
            passwordNotMatchLabel.setVisible(true);
            System.out.println("Password do not match " + passwordField.getText() + " " + verifyPasswordField.getText());
            return false;
        }
        return true;
    }

    private boolean checkUsername() {
        client.sendMessageToServer("intUsernameCheck " + usernameField.getText());
        String message = client.receiveMessageFromServer().trim();
        String tokens[] = message.split(" ");
        if(tokens[0].equals("intUsernameCheck"))
        {
            if(tokens[1].equals("success"))
                return true;
            else if(tokens[1].equals("fail")) {
                usernameNotAvailLabel.setVisible(true);
                return true;
            }
        }
        else
            sceneController.openErrorView("Invalid message from server from checkUsername");
        return false;
    }

    public void setSceneController(SceneController sceneController) {
        this.sceneController = sceneController;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}