package client.controllers;

import client.Client;
import client.SceneController;
import client.handlers.MessageSwingWorker;
import client.utility.LineNumberingTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import server.handlers.Encoding;
import client.conversation.ConvClient;
import client.conversation.ConvServer;
import client.conversation.NetworkConnection;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable{

    @FXML public TabPane editorTabPane;
    @FXML public TabPane bottomTabPane;
    @FXML public SplitPane verticalSplitPane;
    @FXML public SplitPane horizontalSplitPane;
    @FXML private JFXTextField convInput;
    @FXML private TextFlow convMessages;
    private TextFile textFile;
    private File file;
    private List<String > lines;
    private JTextArea textArea;
    private Tab tab;
    private Tab newTab;
    private SwingNode swingNode ;
    private SceneController sceneController;
    private String documentName;
    private String documentText;
    private String username;
    TextDocumentListener documentListener;
    LineNumberingTextArea lineNumberingTextArea;
    Caret caret;
    Client client;
    private int currentVersion;
    private boolean sent = false;
    //chat
    private boolean isInterviewer = false;
    private NetworkConnection conversation = isInterviewer ? createConvServer() : createConvClient();

    @Override
    public void initialize(URL location , ResourceBundle resources){
        //Chat
        try {
            conversation.startConnection();
        }
        catch (Exception e) {
            System.out.println("Unable to start Conversation Connection");
            e.printStackTrace();
        }
        // initializing pane
        verticalSplitPane.setDividerPosition(0 , 0.8);
        horizontalSplitPane.setDividerPosition(0 , 0.2);

        swingNode = new SwingNode();
        textArea = new JTextArea();
        tab = new Tab("Untitled");
        tab.setStyle(" -fx-font-weight: bold; ");
        Font font = new Font("Serif", Font.PLAIN, 18);
        textArea.setFont(font);
        textArea.setForeground(Color.DARK_GRAY);
        textArea.setBackground(Color.WHITE);
        textArea.setTabSize(4);

        caret = new DefaultCaret();
        textArea.setCaret(caret);

        lineNumberingTextArea = new LineNumberingTextArea(textArea);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setRowHeaderView(lineNumberingTextArea);
        documentListener = new TextDocumentListener();
        textArea.getDocument().addDocumentListener(documentListener);

        swingNode.setContent(scrollPane);
        tab.setContent(swingNode);
        editorTabPane.getTabs().add(tab);
        newTab = new Tab("+");
        newTab.setStyle(" -fx-font-weight: bold; ");
        editorTabPane.getTabs().add(newTab);

    }
    private class TextDocumentListener implements DocumentListener {

        @Override
        public void insertUpdate(DocumentEvent e) {
            lineNumberingTextArea.updateLineNumbers();
            synchronized (textArea) {
                int changeLength = e.getLength();
                int offset = e.getOffset();
                int insert = caret.getDot();
                String message;
                try {
                    String addedText = textArea.getDocument().getText(offset,changeLength);
                    String encodedText = Encoding.encode(addedText);
                    currentVersion = client.getVersion();
                    message = "change " + documentName + " " + username + " " + currentVersion + " insert " + encodedText + " " + insert;
                    System.out.println(message);
                    sent = true;
                    MessageSwingWorker worker = new MessageSwingWorker(client,
                            message, sent);
                    worker.execute();
                } catch (BadLocationException e1) {
                    e1.printStackTrace();
                }
            }
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            lineNumberingTextArea.updateLineNumbers();
            synchronized (textArea) {
                int changeLength = e.getLength();
                currentVersion=client.getVersion();
                int offset = e.getOffset();
                int endPosition = offset + changeLength;
                String message = "change " + documentName +" "+username+" " +currentVersion+" remove " + offset
                        + " " + endPosition;


                System.out.println(message);

                sent = true;
                MessageSwingWorker worker = new MessageSwingWorker(client,
                        message, sent);
                client.updateVersion(currentVersion+1);
                worker.execute();
            }
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            lineNumberingTextArea.updateLineNumbers();
        }
    }

    private void manageCursor(int currentPos, int pivotPosition, int amount) {

            System.out.println("first position: "+caret.getDot());
            System.out.println("pivot: "+pivotPosition);
            System.out.println("amount: "+amount);

        if (currentPos >= pivotPosition) {
            if (currentPos <= pivotPosition + Math.abs(amount)) {
                caret.setDot(pivotPosition);
            } else {
                caret.setDot(amount+currentPos);
            }
        }
        else{
            caret.setDot(currentPos);
        }

        System.out.println("caret moved to: "+caret.getDot());
    }

    public void updateDocument(String updatedText, int editPosition,
                               int editLength, String username, int version) {
        documentText = Encoding.decode(updatedText);
        int pos = caret.getDot();
        synchronized (textArea) {
            if(this.username!=null && !this.username.equals(username)){
                textArea.getDocument().removeDocumentListener(documentListener);
                textArea.setText(documentText);
                textArea.getDocument().addDocumentListener(documentListener);
                manageCursor(pos, editPosition, editLength);
            }
            else if(this.username!=null && this.username.equals(username)) {
                //check if version matches up
                if(currentVersion<version-1){
                    textArea.getDocument().removeDocumentListener(documentListener);
                    textArea.setText(documentText);
                    textArea.getDocument().addDocumentListener(documentListener);
                    caret.setDot(editPosition+editLength);
                }

            }

        }
    }

    @FXML
    private void onOpen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("./"));
        file = fileChooser.showOpenDialog(null);
        if(file != null){
            try {
                lines = Files.readAllLines(file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        textArea.setText("");
        tab.setText(file.getName());
        for(String line : lines){
            textArea.append(line+"\n");
        }

    }

    @FXML
    private void onSave(  ){
        List<String> newLines = Arrays.asList(textArea.getText().split("\n"));

        try {
            Files.write(file.toPath() , newLines , StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onClose(){
        System.exit(0);
    }

    @FXML
    private void onDelete(){
        file.deleteOnExit();
    }

    @FXML
    public void onCompile(ActionEvent actionEvent) {
    }

    @FXML
    public void onRun(ActionEvent actionEvent) {
    }

    @FXML
    private void onAbout(){

    }

    // Chat
    @FXML
    public void convTextFieldActionPerformed(ActionEvent event) {
        String message = isInterviewer ? "InterViewer: " : "Client";
        message += convInput.getText();
        convInput.clear();
        Text text1 = new Text(message + "\n");
        convMessages.getChildren().add(text1);

        try {
            conversation.send(message);
        }
        catch (Exception e) {
            convMessages.getChildren().add(new Text("Failed to Send"));
        }
    }
    private ConvClient createConvClient() {
        return new ConvClient("localhost",5554,data -> {
            Platform.runLater(() -> {
                convMessages.getChildren().add(new Text(data.toString() + "\n"));
            });
        });
    }
    private ConvServer createConvServer() {
        return new ConvServer(5554, data -> {
            Platform.runLater(() -> {
                convMessages.getChildren().add(new Text(data.toString() + "\n"));
            });
        });
    }


    public void setSceneController(SceneController sceneController) {
        this.sceneController = sceneController;
        this.username = sceneController.getUsername();
        this.client = sceneController.getClient();
    }
    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }
    public void setDocumentText(String documentText) {
        this.documentText = Encoding.decode(documentText);
        textArea.setText(this.documentText);
    }
}
