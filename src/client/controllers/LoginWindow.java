package client.controllers;

import client.Client;
import client.SceneController;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class LoginWindow {

    @FXML
    private JFXTextField intUsername;

    @FXML
    private JFXPasswordField intPassword;

    @FXML
    private JFXTextField candUsername;

    @FXML
    private JFXPasswordField candPassword;

    @FXML
    private JFXButton intLogin;

    @FXML
    private JFXButton candLogin;

    @FXML
    private JFXButton intSignUp;
    private SceneController sceneController;
    private Client client;
    private String int_ID;
    @FXML
    void candLoginPressed(ActionEvent event) {
        client.sendMessageToServer("candLogin " + candUsername.getText() + " " + candPassword.getText());
        String message = client.receiveMessageFromServer();
        String[] tokens = message.trim().split(" ");
        if(tokens[0].equals("candLogin")) {
            if (tokens[1].equals("success")) {
                System.out.println("candLogin Successfull");
                sceneController.setUsername(candUsername.getText());
                sceneController.setIsInterviewer(false);
                String documentName,int_IP;
                int_ID = tokens[2];
                sceneController.setIntID(int_ID);
                documentName = tokens[3];
                if(!documentName.equals("null"))
                {
                    int_IP = tokens[4];
                    sceneController.setIntIP(int_IP);
                    System.out.println("Interviewer has opened- " + documentName + " int_id " + int_ID + " IntIP " + int_IP);
                    client.sendMessageToServer("open " + documentName);
                    client.startClientActionListener();
                } else {
                    sceneController.openErrorView("Interviewer has not opened Document");
                }
            }
            else if(tokens[1].equals("fail")) {
                sceneController.openErrorView("Incorrect username or password");
            }
        }
        else {
            sceneController.openErrorView("Invalid message from server ");
        }
    }

    @FXML
    void intLoginPressed(ActionEvent event) {
        client.sendMessageToServer("intLogin " + intUsername.getText() + " " + intPassword.getText());
        String message = client.receiveMessageFromServer();
        String[] tokens = message.trim().split(" ");
        if(tokens[0].equals("intLogin")) {
            if (tokens[1].equals("success")) {
                System.out.println("IntLogin Successfull");
                sceneController.setUsername(intUsername.getText());
                sceneController.setIsInterviewer(true);
                sceneController.openIntDashboard();
                sceneController.setIntID(tokens[2]);
            }
            else if(tokens[1].equals("fail")) {
                sceneController.openErrorView("Incorrect username or password");
            }
        }
        else {
            sceneController.openErrorView("Invalid message from server ");
        }
    }

    @FXML
    void intSignUpPressed(ActionEvent event) {
        sceneController.openIntSignUp();
    }

    public void setSceneController(SceneController sceneController) {
        this.sceneController = sceneController;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
