package interCode.controller;

import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.fxml.Initializable;
import javafx.stage.FileChooser;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.nio.file.*;
import interCode.utility.LineNumberingTextArea;
import interCode.utility.LinePainter;
import interCode.utility.SyntexHighlight;

public class MainController implements Initializable{

    @FXML public TabPane editorTabPane;
    @FXML public TabPane bottomTabPane;
    @FXML public SplitPane verticalSplitPane;
    @FXML public SplitPane horizontalSplitPane;
    private TextFile textFile;
    private File file;
    private List<String > lines;
    private JTextPane textPane;
    private Tab tab;
    private Tab newTab;
    private SwingNode swingNode ;
    private SyntexHighlight syntexHighlight ;

    @Override
    public void initialize(URL location , ResourceBundle resources){

        // initializing pane
        verticalSplitPane.setDividerPosition(0 , 0.8);
        horizontalSplitPane.setDividerPosition(0 , 0.2);

        swingNode = new SwingNode();
        textPane = new JTextPane();
        tab = new Tab("Untitled");
        tab.setStyle(" -fx-font-weight: bold; ");
        Font font = new Font("Serif", Font.PLAIN, 18);
        textPane.setFont(font);
        textPane.setForeground(Color.DARK_GRAY);
        textPane.setBackground(Color.WHITE);
        //textPane.

        LineNumberingTextArea lineNumberingTextArea = new LineNumberingTextArea(textPane);

        JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setRowHeaderView(lineNumberingTextArea);

        textPane.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                lineNumberingTextArea.updateLineNumbers();
                syntexHighlight.updateColor(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                lineNumberingTextArea.updateLineNumbers();
                syntexHighlight.updateColor(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                lineNumberingTextArea.updateLineNumbers();
                //myDocumentListener.updateColor(e);
            }
        });

        LinePainter linePainter = new LinePainter(textPane);   // highlight current line  ///

        syntexHighlight = new SyntexHighlight(textPane);   // systex coloring //

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

        textPane.setText("");
        tab.setText(file.getName());
        for(String line : lines){
            append(line + "\n");
        }

        syntexHighlight.syntexColor();   // applying coloring /
    }

    @FXML
    private void onSave(  ){
        //List<String> newLines = Arrays.asList(textPane.getText().split("\n"));
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

    @FXML
    public void onCompile(ActionEvent actionEvent) {

    }

    @FXML
    public void onRun(ActionEvent actionEvent) {

    }

    @FXML
    private void onAbout(){

    }


    public void append(String s) {
        try {
            Document doc = textPane.getDocument();
            doc.insertString(doc.getLength(), s, null);
        } catch(BadLocationException exc) {
            exc.printStackTrace();
        }
    }
}
