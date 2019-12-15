package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    private final int port;
    private final String host;
    private SceneController sceneController;
    private Socket socket;
    private boolean DEBUG = true;
    private PrintWriter out;
    private BufferedReader in;
    private String username;
    private ClientActionListener actionListener;
    private int versionOfDocument;
    private String nameOfDocument;
    private String textOfDocument;

    /**
     * Constructor for Client Class
     * @param port
     * @param host
     * @param main SceneController Object
     */
    public Client(int port, String host, SceneController main) {
        this.port = port;
        this.host = host;
        sceneController = main;
    }

    /**
     * Creates a new Client Socket and opens Username Dialog.
     * Starts the Client Action Listener For listening to updates from server.
     * @throws IOException
     */
    public void start() throws IOException {
        System.out.println("Trying to open a socket");
        socket = new Socket(host,port);
        //sceneController.openUsernameDialog();
        sceneController.openLoginWindow();
        System.out.println("Client connected to the server. ");
//        actionListener = new ClientActionListener(this, socket);
//        actionListener.start();
//        out = new PrintWriter(socket.getOutputStream());
    }
    public void startClientActionListener() {
        actionListener = new ClientActionListener(this,socket);
        actionListener.start();
    }
    /**
     * Sends the message to Server
     * @param message
     */
    public void sendMessageToServer(String message) {
        if (DEBUG) {System.out.println("we got to Client Send message");}
        try {
            out = new PrintWriter(socket.getOutputStream());
            if (DEBUG) {System.out.println("socket is" + socket.getLocalPort());}
            out.write(message + "\n");
            out.flush();
        } catch (IOException e) {
            sceneController.openErrorView(e.getMessage());
        }
    }

    public String receiveMessageFromServer() {
        String message = null;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            message = in.readLine();
        } catch (Exception e) {
            System.out.println(e);
        }
        if (DEBUG) {
            System.out.println("got message from server - " + message);
        }
        return message;
    }

    /**
     * Setter for Scene Controller
     * @param sceneController
     */
    public void setSceneController(SceneController sceneController) {
        this.sceneController = sceneController;
    }

    /**
     * Getter for Scene Controller
     * @return
     */
    public SceneController getSceneController() {
        return sceneController;
    }

    /**
     * Setter for username. opens Welcome View.
     * @param username
     */
    public void setUsername(String username) {
        System.out.println("setting username");
        this.username = username;
        sceneController.setUsername(username);
        sceneController.switchToWelcomeView();
        System.out.println("Switched to Welcome View");
    }

    /**
     * Updates document Version
     * @param newVersion
     */
    public void updateVersion(int newVersion) {
        versionOfDocument = newVersion;
    }

    /**
     * Getter for Version
     * @return
     */
    public int getVersion(){
        return versionOfDocument;
    }

    /**
     * Update Document Name
     * @param name
     */
    public void updateDocumentName(String name) {
        //System.out.println("updating documentName");
        nameOfDocument = name;
    }

    public void updateText(String text) {
        textOfDocument = text;
    }

    public String getDocumentName() {
        return nameOfDocument;
    }
    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

}
