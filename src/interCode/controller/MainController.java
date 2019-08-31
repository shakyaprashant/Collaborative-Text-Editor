package interCode.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.nio.file.*;

public class MainController implements Initializable{

    @FXML public TabPane editorTabPane;
    @FXML public TabPane bottomTabPane;
    private TextFile textFile;
    private File file;
    private List<String > lines;
    private TextArea textArea;
    private Tab tab;

    @Override
    public void initialize(URL location , ResourceBundle resources){


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

        textArea = new TextArea();
        for(String line : lines){
            System.out.println(line);
            textArea.appendText(line + "\n");
        }

        textArea.setStyle(" -fx-text-fill : blue; -fx-font-size: 16px; ");
        tab = new Tab(file.getName());
        tab.setContent(textArea);

        editorTabPane.getTabs().add(tab);


    }

    @FXML
    private void onSave(  ){
        List<String> newLines = Arrays.asList(textArea.getText().split("\n"));

        for(String line : newLines){
            System.out.println(line);
        }

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

    }

    @FXML
    private void onAbout(){

    }

}
