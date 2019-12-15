package client.controllers;

import com.jfoenix.controls.JFXTextArea;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class StdinInputController {
    @FXML
    private JFXTextArea stdinTextArea;
    String stdin;
    boolean cancelButtonClicked = false;

    @FXML
    void onCancel(ActionEvent event) {
        cancelButtonClicked = true;
        stdinTextArea.getScene().getWindow().hide();
    }

    @FXML
    void onRunButtonClicked(ActionEvent event) {
        cancelButtonClicked = false;
        stdin = stdinTextArea.getText();
        stdinTextArea.getScene().getWindow().hide();
    }
    public String getStdin() { return stdin; }
    public  boolean getCancelStatus() {
        return cancelButtonClicked;
    }

}

