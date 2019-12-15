package client;

import client.utility.ShowRating;
import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientActionListener extends Thread{
    private static boolean DEBUG = true;
    private Client client;
    private Socket socket;
    private BufferedReader in;
    private final String regex = "(Error: .+)|(change [\\w|\\d]+\\s[\\w|\\d]+\\s(\\d+)\\s(\\d+)\\s(-?\\d+)\\s?(.+)?)|(open [\\w|\\d]+\\s(\\d+)\\s?(.+)?)|(sendQues .+)|" +
            "(endInterview .+)|(quit)|(sendMessage .+)|(nothing)|(userScore .+)|(getCandScore .+)";
    private final int groupChangeVersion = 3;
    private final int groupChangePosition = 4;
    private final int groupChangeLength = 5;
    private final int groupChangeText = 6;
    private final int groupOpenVersion = 8;
    private final int groupOpenText = 9;
    private SceneController main;

    public ClientActionListener(Client client, Socket socket) {
        this.client = client;
        this.socket = socket;
        this.main = client.getSceneController();
    }

    @Override
    public void run(){

        try{
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            for (String line = in.readLine(); line != null; line = in.readLine()) {
                final String input = line;
                Platform.runLater(new Runnable() { // To allow run method to make changes in JavaFx main thread
                    @Override
                    public void run() {
                        handleMessageFromServer(input);
                    }
                });
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void handleMessageFromServer(String input) {
        input = input.trim();
        if(DEBUG){ System.out.println("Input message the client gets from the server is- " + input); }
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        if (!matcher.find()) {
            System.out.println("from CAL: regex failure " + input);
            //main.openErrorView("from CAL: regex failure");
        }
        String[] tokens = input.split(" ");

        if (tokens[0].equals("Error:")) {
            main.openErrorView(input);
        }
        else if(tokens[0].equals("sendQues")) {
            main.setQuestion(input);
        }
        else if(tokens[0].equals("sendMessage")) {
            main.setMessage(input);
        }
        else if(tokens[0].equals("userScore")) {
            if(tokens[1].equals("fail")) {
                main.openErrorView("User score not added");
            }
            else {
                main.openIntDashboard();
            }
        }
        else if(tokens[0].equals("getCandScore")) {
            ShowRating showRating = new ShowRating(tokens);
        }
        else if (tokens[0].equals("open")) {
            client.updateDocumentName(tokens[1]);
            //add for version:
            client.updateVersion(Integer.parseInt(matcher.group(groupOpenVersion)));
            String documentText = matcher.group(groupOpenText);
            client.updateText(documentText);
            if (DEBUG){System.out.println("The open message gets the document with text:" + documentText);}
            main.switchToDocumentView(tokens[1], documentText);
        }
        else if(tokens[0].equals("endInterview")) {
            client.sendMessageToServer("quit");
            main.close();
        }
        else if (tokens[0].equals("change")) {
            // first, need to check the documents are the same
            if(DEBUG){System.out.println("from CAL: updating document(in ClientActionListener.java)");}
            int version = Integer.parseInt(matcher.group(groupChangeVersion));
            if (client.getDocumentName()!=null) {
                if(client.getDocumentName().equals(tokens[1]) ){
                    // The document is changed, must update the back-end and front end
                    String username = tokens[2];
                    String documentText = matcher.group(groupChangeText);
                    if(DEBUG){System.out.println(documentText);}
                    int editPosition = Integer.parseInt(matcher.group(groupChangePosition));
                    int editLength = Integer.parseInt(matcher.group(groupChangeLength));
                    if(DEBUG){System.out.println(documentText);}
                    main.updateDocument(documentText, editPosition, editLength, username, version);
                    client.updateText(documentText);
                    client.updateVersion(version);

                }

            }
        }

    }
}
