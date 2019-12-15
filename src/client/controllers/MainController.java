package client.controllers;

import client.Client;
import client.CompileAndRun.*;
import client.SceneController;
import client.handlers.MessageSwingWorker;
import client.utility.LineNumberingTextPane;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
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
import javax.swing.text.Document;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.ResourceBundle;
import client.utility.LinePainter;
import client.utility.SyntaxHighlight;
import client.utility.AutoSuggestion;
import client.utility.FindReplace;
import client.utility.Settings;

/**
 * Enum for Programming Languages
 */
enum Language {
    C,CPP,JAVA,PYTHON;
}

public class MainController implements Initializable{
    public boolean DEBUG = false;
    @FXML public TabPane editorTabPane;
    @FXML public TabPane bottomTabPane;
    @FXML public SplitPane verticalSplitPane;
    @FXML public SplitPane horizontalSplitPane;
    @FXML private JFXTextField convInput;
    @FXML private TextFlow convMessages;
    private TextFile textFile;
    private File file;
    private List<String > lines;
    private JTextPane textPane;
    private Tab tab1;
    private Tab newTab;
    private SwingNode swingNode ;
    private SceneController sceneController;
    private String documentName;
    private String documentText;
    private String username;
    TextDocumentListener documentListener;
    LineNumberingTextPane lineNumberingTextPane;
    AutoSuggestion autoSuggestion;
    Caret caret;
    Client client;
    private int currentVersion;
    private boolean sent = false;
    Language languageSelected;
    //chat
    private boolean isInterviewer = false;
    private NetworkConnection conversation = isInterviewer ? createConvServer() : createConvClient();
    // CompileAndRun
    private CompileAndRun compileAndRun;
    String stdin ;
    @FXML
    private TextFlow outputTextFlow;
    private SyntaxHighlight syntaxHighlight;

    /**
     * Creates a Swing textarea and Swing ScrollPane. To make it work inside JavaFx it creates a swingnode
     * and puts scrollpane inside it.
     * @param location
     * @param resources
     */
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
        textPane = new JTextPane();
        tab1 = new Tab("Untitled");
        tab1.setStyle(" -fx-font-weight: bold; ");
        setFontsize( "Arial" ,18);
        //textPane.setForeground(Color.DARK_GRAY);
        textPane.setBackground(Color.WHITE);
        //textPane.setTabSize(4);

        caret = new DefaultCaret();
        textPane.setCaret(caret);

        lineNumberingTextPane = new LineNumberingTextPane(textPane);

        JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setRowHeaderView(lineNumberingTextPane);

        documentListener = new TextDocumentListener();
        textPane.getDocument().addDocumentListener(documentListener);

        swingNode.setContent(scrollPane);
        tab1.setContent(swingNode);
        editorTabPane.getTabs().add(tab1);
        newTab = new Tab("+");
        newTab.setStyle(" -fx-font-weight: bold; ");
        editorTabPane.getTabs().add(newTab);

        LinePainter linePainter = new LinePainter(textPane);   // highlight current line  ///
        syntaxHighlight = new SyntaxHighlight(textPane);   // syntax  coloring //
        autoSuggestion = new AutoSuggestion(textPane);  // auto suggestion for C //


        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    textPane.getStyledDocument().insertString(0 ," " , null);
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
            }
        });

    }




    /**
     * Document Listener for the Text Area
     */
    private class TextDocumentListener implements DocumentListener {
        /**
         * Listenes for the insert updates in the document and Creates a change message to send to server.
         * @param e
         */
        @Override
        public void insertUpdate(DocumentEvent e) {
            lineNumberingTextPane.updateLineNumbers();
            syntaxHighlight.updateColor(e);
            autoSuggestion.checkAndShowSuggestion(e);
            synchronized (textPane) {
                int changeLength = e.getLength();
                int offset = e.getOffset();
                int insert = caret.getDot();
                String message;
                try {
                    String addedText = textPane.getDocument().getText(offset,changeLength);
                    String encodedText = Encoding.encode(addedText);
                    currentVersion = client.getVersion();
                    message = "change " + documentName + " " + username + " " + currentVersion + " insert " + encodedText + " " + insert;
                    if(DEBUG) System.out.println(message);
                    sent = true;
                    MessageSwingWorker worker = new MessageSwingWorker(client,
                            message, sent);
                    worker.execute();
                } catch (BadLocationException e1) {
                    e1.printStackTrace();
                }
            }
        }

        /**
         * Listens for removal of text in document and creates a change message to send to server.
         * @param e document event
         */
        @Override
        public void removeUpdate(DocumentEvent e) {
            lineNumberingTextPane.updateLineNumbers();
            syntaxHighlight.updateColor(e);
            autoSuggestion.removeSuggestion();
            synchronized (textPane) {
                int changeLength = e.getLength();
                currentVersion=client.getVersion();
                int offset = e.getOffset();
                int endPosition = offset + changeLength;
                String message = "change " + documentName +" "+username+" " +currentVersion+" remove " + offset
                        + " " + endPosition;

                if(DEBUG) System.out.println(message);

                sent = true;
                MessageSwingWorker worker = new MessageSwingWorker(client,
                        message, sent);
                client.updateVersion(currentVersion+1);
                worker.execute();
            }
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            lineNumberingTextPane.updateLineNumbers();
        }
    }

    /**
     * Moves the Caret position.
     * @param currentPos
     * @param pivotPosition
     * @param amount
     */
    private void manageCursor(int currentPos, int pivotPosition, int amount) {

        if(DEBUG) {
            System.out.println("first position: " + caret.getDot());
            System.out.println("pivot: " + pivotPosition);
            System.out.println("amount: " + amount);
        }


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

        if(DEBUG) System.out.println("caret moved to: "+caret.getDot());
    }

    /**
     * Updates the document according to the changes sent by the server.
     * @param updatedText
     * @param editPosition
     * @param editLength
     * @param username
     * @param version
     */
    public void updateDocument(String updatedText, int editPosition,
                               int editLength, String username, int version) {
        documentText = Encoding.decode(updatedText);
        int pos = caret.getDot();
        //System.out.println("--+--->  "+(editPosition));
        synchronized (textPane) {
            if(this.username!=null && !this.username.equals(username)){
                textPane.getDocument().removeDocumentListener(documentListener);
                textPane.setText(documentText);
                //Write method for update color and line number;
                updateColorAndLine(editPosition);


                textPane.getDocument().addDocumentListener(documentListener);
                manageCursor(pos, editPosition, editLength);
            }
            else if(this.username!=null && this.username.equals(username)) {
                //check if version matches up
                if(currentVersion<version-1){
                    textPane.getDocument().removeDocumentListener(documentListener);
                    textPane.setText(documentText);
                    //Write method for update color and line number;
                    updateColorAndLine(editPosition);

                    textPane.getDocument().addDocumentListener(documentListener);
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

        textPane.getDocument().removeDocumentListener(documentListener);
        textPane.setText("");
        tab1.setText(file.getName());
        for(String line : lines){
            append(line + "\n");
        }
        syntaxHighlight.syntaxColor();
        lineNumberingTextPane.updateLineNumbers();
        textPane.getDocument().addDocumentListener(documentListener);

    }

    @FXML
    private void onSave(  ){
//        List<String> newLines = Arrays.asList(textPane.getText().split("\n"));
//
//        try {
//            Files.write(file.toPath() , newLines , StandardOpenOption.TRUNCATE_EXISTING);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        //List<String> newLines = Arrays.asList(textPane.getText().split("\n"));

        // updated code....
        String newLines = textPane.getText();
        try {
            //Files.write(file.toPath() , newLines , StandardOpenOption.TRUNCATE_EXISTING)
//            for(String l: newLines){
//                System.out.println(l);
//            }

            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(newLines);
            fileWriter.close();
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

    /**
     * This method is executed when run menu option is clicked. Opens a new window for input of stdin.
     * @param actionEvent
     */
    @FXML
    public void onRun(ActionEvent actionEvent) {
        //Getting Standard Input
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/StdinInputWindow.fxml"));
        try {
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage inputStage = new Stage();
            inputStage.initOwner(sceneController.getStage());
            inputStage.setScene(scene);
            inputStage.showAndWait();
            stdin =  loader.<StdinInputController>getController().getStdin();
            System.out.println("Stdin is - " + stdin);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        //Running code
        if(languageSelected == Language.CPP)
        {
            compileAndRun = new CompileAndRunCPP(textPane.getText(),stdin);
            compileAndRun.run();
        }
        else if(languageSelected == Language.JAVA)
        {
            compileAndRun = new CompileAndRunJava(textPane.getText(),stdin);
            compileAndRun.run();
        }
        else if(languageSelected == Language.PYTHON)
        {
            compileAndRun = new CompileAndRunPython(textPane.getText(),stdin);
            compileAndRun.run();
        }
        else if(languageSelected == Language.C)
        {
            compileAndRun = new CompileAndRunC(textPane.getText(),stdin);
            compileAndRun.run();
        }
        System.out.println(compileAndRun.getCompileOutput());
        System.out.println(compileAndRun.getRunOutput());
        Text compileoutput = new Text(compileAndRun.getCompileOutput() + "\n");
        compileoutput.setFill(javafx.scene.paint.Color.RED);
        Text runoutput = new Text(compileAndRun.getRunOutput());
        outputTextFlow.getChildren().clear();
        outputTextFlow.getChildren().addAll(compileoutput,runoutput);
    }

    @FXML
    public void FindReplace(ActionEvent actionEvent) {
        SyntaxHighlight.STOP_FLAG = true;
        AutoSuggestion.STOP_FLAG = true;
        FindReplace findReplace = new FindReplace(textPane);

    }

    @FXML
    public void settings(ActionEvent actionEvent) {
        Settings settings = new Settings(this , lineNumberingTextPane);

    }

    @FXML
    private void onAbout(){

    }

    /**
     * Sets document programming language to C
     * @param event
     */
    @FXML
    void setLanguageToC(ActionEvent event) {
        languageSelected = Language.C;
    }
    /**
     * Sets document programming language to C++
     * @param event
     */
    @FXML
    void setLanguageToCPP(ActionEvent event) {
        languageSelected = Language.CPP;
    }
    /**
     * Sets document programming language to Java
     * @param event
     */
    @FXML
    void setLanguageToJava(ActionEvent event) {
        languageSelected = Language.JAVA;
    }
    /**
     * Sets document programming language to Python
     * @param event
     */
    @FXML
    void setLanguageToPython(ActionEvent event) {
        languageSelected = Language.PYTHON;
    }

    // Chat

    /**
     * method executes when user presses enters on conversation text field;
     * @param event
     */
    @FXML
    public void convTextFieldActionPerformed(ActionEvent event) {
        String message = isInterviewer ? "InterViewer: " : "Client: ";
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

    /**
     * Setter for document name.
     * @param documentName
     */
    public void setDocumentName(String documentName) {
        this.documentName = documentName;
        tab1.setText(documentName);
    }

    /**
     * Setter for document Text. Updates the Document text directly;
     * @param documentText
     */
    public void setDocumentText(String documentText) {
        this.documentText = Encoding.decode(documentText);
        textPane.getDocument().removeDocumentListener(documentListener);
        textPane.setText(this.documentText);
        textPane.getDocument().addDocumentListener(documentListener);
    }

    public void append(String s) {
        try {
            Document doc = textPane.getDocument();
            doc.insertString(doc.getLength(), s, null);
        } catch(BadLocationException exc) {
            exc.printStackTrace();
        }
    }

    public void updateColorAndLine(int pos){
        lineNumberingTextPane.updateLineNumbers();
        //syntaxHighlight.colorForRemoteUpdate(pos);
        syntaxHighlight.syntaxColor();
    }


    public void  setFontsize(String fontType ,   int fontsize){
        Font font = new Font(fontType, Font.PLAIN, fontsize);
        textPane.setFont(font);
    }



}