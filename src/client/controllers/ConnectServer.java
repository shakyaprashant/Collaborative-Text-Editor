package client.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import client.Client;
import client.SceneController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.net.Socket;

public class ConnectServer {
    @FXML
    JFXButton connectButton;
    @FXML
    JFXTextField serverIPfield,portfield;

    Socket s;
    private Client client;
    private SceneController sceneController;
    private boolean DEBUG = true;

    /**
     * This method runs when the user clicks on connect button.
     * @param event
     * @throws IOException
     */
    @FXML
    private void onClickConnect(ActionEvent event) throws IOException {
        System.out.println("Connect Button Clicked");
        String hostInput = serverIPfield.getText().trim();
        String portInput = portfield.getText().trim();
        String portRegex = "\\d\\d?\\d?\\d?\\d?";
        if(hostInput.length() != 0 && portInput.matches(portRegex)) {
            try {
                client = new Client(Integer.parseInt(portInput),hostInput,sceneController);
                sceneController.setClient(client);
            }
            catch (NumberFormatException e1) {
                Alert a1 = new Alert(Alert.AlertType.ERROR);
                a1.setTitle("Invalid Arguments");
                a1.setContentText("Invalid Arguments");
                a1.setHeaderText(null);
                a1.showAndWait();

            }
            client.setSceneController(sceneController);
            if(DEBUG) {
                System.out.println("set client scenechanger connectserver");
            }

//            ConnectServerThread thread = new ConnectServerThread(this);
//            thread.start();

            //Have to Change this After
            try {
                client.start();
            }
            catch (Exception e) {
                e.printStackTrace();
            }

        }
        else {
            Alert a1 = new Alert(Alert.AlertType.ERROR);
            a1.setTitle("Invalid Arguments");
            a1.setContentText("Invalid Arguments");
            a1.setHeaderText(null);
            a1.showAndWait();
        }

    }

    /**
     * Setter for Scene Controller
     * @param sceneController
     */
    public void setSceneController(SceneController sceneController) {
        this.sceneController = sceneController;
    }

    /**
     * Getter for client
     * @return
     */
    public Client getClient() {
        return client;
    }
}
