package client.controllers;

import client.Client;
import client.SceneController;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class InterviewerDashboard {
    private SceneController sceneController;
    private Client client;
    @FXML
    private JFXTextField documentName;

    @FXML
    void addNewCandClicked(ActionEvent event) {
        sceneController.openAddNewCandWindow();
    }

    @FXML
    void createDocumentClicked(ActionEvent event) {
        String docName = documentName.getText();
        client.sendMessageToServer("new " + docName);
        String message = client.receiveMessageFromServer();
        String[] tokens = message.split(" ");
        if(tokens[0].equals("new"))
        {
            if (tokens[1].equals("success")) {
                System.out.println("document added");
                client.startClientActionListener();
                sceneController.switchToDocumentView(docName,"");

                client.updateDocumentName(docName);
                client.updateVersion(1);
            }
            else
                sceneController.openErrorView("Document Already exists");
        }
        else if(tokens[0].equals("Error")) {
            sceneController.openErrorView(message);
        }
        else
            sceneController.openErrorView("Some Error Happened");
    }
    @FXML
    void seeCandScoreClicked(ActionEvent event) {
        client.sendMessageToServer("getCandScore " + sceneController.getIntID());
    }

    public void setSceneController(SceneController sceneController) {
        this.sceneController = sceneController;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}