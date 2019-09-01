package interCode.controller;

import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.nio.file.*;
import interCode.utility.LineNumberingTextArea;
import interCode.utility.LinePainter;

public class MainController implements Initializable{

    @FXML public TabPane editorTabPane;
    @FXML public TabPane bottomTabPane;
    @FXML public SplitPane verticalSplitPane;
    @FXML public SplitPane horizontalSplitPane;
    private TextFile textFile;
    private File file;
    private List<String > lines;
    private JTextArea textArea;
    private Tab tab;
    private Tab newTab;
    private SwingNode swingNode ;

    @Override
    public void initialize(URL location , ResourceBundle resources){

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

        LineNumberingTextArea lineNumberingTextArea = new LineNumberingTextArea(textArea);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setRowHeaderView(lineNumberingTextArea);
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                lineNumberingTextArea.updateLineNumbers();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                lineNumberingTextArea.updateLineNumbers();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                lineNumberingTextArea.updateLineNumbers();
            }
        });

        LinePainter linePainter = new LinePainter(textArea);   // highlight current line  ///

        swingNode.setContent(scrollPane);
        tab.setContent(swingNode);
        editorTabPane.getTabs().add(tab);
        newTab = new Tab("+");
        newTab.setStyle(" -fx-font-weight: bold; ");
        editorTabPane.getTabs().add(newTab);

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
}
