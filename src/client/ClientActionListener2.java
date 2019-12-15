package client;

import client.SceneController;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Listens for the update from the server, and handles the message from the
 * server.
 *
 */
public class ClientActionListener2 extends Thread{

    private static boolean DEBUG = true;
    private Client client;
    private Socket socket;
    private BufferedReader in;
    private final String regex = "(Error: .+)|"
            + "(alldocs [\\w|\\d]+)|(new [\\w|\\d]+)|(open [\\w|\\d]+\\s(\\d+)\\s?(.+)?)|"
            + "(change [\\w|\\d]+\\s[\\w|\\d]+\\s(\\d+)\\s(\\d+)\\s(-?\\d+)\\s?(.+)?)|(name [\\d\\w]+)";
    private final int groupChangeVersion = 8;
    private final int groupChangePosition = 9;
    private final int groupChangeLength = 10;
    private final int groupChangeText = 11;
    private final int groupOpenVersion = 5;
    private final int groupOpenText = 6;
    private SceneController main;

    /**
     * Creates a new ClientActionListener with a client and a socket
     *
     * @param client
     * @param socket
     */
    public ClientActionListener2(Client client, Socket socket) {
        this.client = client;
        this.socket = socket;
        this.main = client.getSceneController();
    }

    /**
     * listens for server updates and handle the message
     */
    @Override
    public void run(){

        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            for (String line = in.readLine(); line != null; line = in.readLine()) {
                final String input = line;
                Platform.runLater(new Runnable() { // To allow run method to make changes in JavaFx main thread
                    @Override
                    public void run() {
                        handleMessageFromServer(input);
                    }
                });
                //System.out.println("Server Sent-" + line);
            }
           // System.out.println("Out");
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

    /**
     *
     * Handle the message from the server by updating the GUI and the nameOfDocument, textOfDocument as well
     *
     * Server-to-Client Message Protocol
     * message :== (Error| alldocs | Newdocument | Opendocument | ChangeText)
     * Error :== error [1-6] .+
     * Alldocs :== "alldocs " DocumentName
     * Newdocument:=="new " DocumentName
     * Opendocument:=="open " DocumentName Version DocumentText
     * ChangeText :=="change " DocumentName Username Version ChangePosition ChangeLength DocumentText
     * Version :== Int+
     * ChangePosition :== Int+
     * ChangeLength :== -?Int+
     * DocumentName:==[\\d\\w]+
     * DocumentText:==(Chars*\n)*
     * Username :==[\\d\\w]+
     * Chars:== .+
     * Int:== [0-9]
     */
    public void handleMessageFromServer(String input) {
        input = input.trim();
        if(DEBUG){ System.out.println("Input message the client gets from the server is- " + input);}
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        if (!matcher.find()) {
            // invalid input
            main.openErrorView("from CAL: regex failure");
        }
        String[] tokens = input.split(" ");

        // 'error' message , only update the front-end
        if (tokens[0].equals("Error:")) {
            main.openErrorView(input);
        }

        // "alldocs" message, only update the front-end
        else if (tokens[0].equals("alldocs")) {
            ArrayList<String> names = new ArrayList<String>();
            for (int i = 1; i < tokens.length; i++) {
                names.add(tokens[i]);
            }
            main.displayOpenDocuments(names);

        }
        else if (tokens[0].equals("name")){
            client.setUsername(tokens[1]);


        }

        // "Create" a document with valid name, needs to update the front and
        // back ends
        else if (tokens[0].equals("new")) {
            main.switchToDocumentView(tokens[1], "");
            client.updateDocumentName(tokens[1]);
            // add for version: set the version to 1
            client.updateVersion(1);
        }

        // "Open the document", update both front and end
        else if (tokens[0].equals("open")) {
            client.updateDocumentName(tokens[1]);
            //add for version:
            client.updateVersion(Integer.parseInt(matcher.group(groupOpenVersion)));
            String documentText = matcher.group(groupOpenText);
            client.updateText(documentText);
            if (DEBUG){System.out.println("The open message gets the document with text:" + documentText);}
            main.switchToDocumentView(tokens[1], documentText);



        }

        // Change the document.
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
