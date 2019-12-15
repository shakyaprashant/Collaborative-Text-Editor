package client.controllers;

import client.Client;
import client.SceneController;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class RateCandWindow implements Initializable {
    private SceneController sceneController;
    @FXML
    private JFXComboBox<String> commRating;

    @FXML
    private JFXComboBox<String> techRating;

    @FXML
    private JFXComboBox<String> attitRating;

    String message ;

    @FXML
    private JFXTextField overallScore;
    ObservableList<String> ratingList = FXCollections.
            observableArrayList("1","2","3","4","5");
    private Client client;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        commRating.setItems(ratingList);
        techRating.setItems(ratingList);
        attitRating.setItems(ratingList);
    }

    @FXML
    void submitButtonClicked(ActionEvent event) {
        message = "userScore " + sceneController.getIntID() + " ";
        message += commRating.getValue() + " " + techRating.getValue() + " " + attitRating.getValue() + " " + overallScore.getText();
        client.sendMessageToServer(message);
    }
    @FXML
    void attitClicked(ActionEvent event) {
    }

    @FXML
    void commClicked(ActionEvent event) {
    }
    @FXML
    void techClicked(ActionEvent event) {
    }

    public void setSceneController(SceneController sceneController) {
        this.sceneController = sceneController;
    }


    public void setClient(Client client) {
        this.client = client;
    }
}
