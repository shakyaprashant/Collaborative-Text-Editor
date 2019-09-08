package client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    private final int port;
    private final String host;
    private SceneController sceneController;
    private Socket socket;
    private boolean DEBUG = true;
    private PrintWriter out;
    private String username;
    private ClientActionListener actionListener;
    private int versionOfDocument;
    private String nameOfDocument;
    private String textOfDocument;

    public Client(int port, String host, SceneController main) {
        this.port = port;
        this.host = host;
        sceneController = main;
    }

    public void start() throws IOException {
        System.out.println("Trying to open a socket");
        socket = new Socket(host,port);
        sceneController.openUsernameDialog();
        System.out.println("Client connected to the server. ");
        actionListener = new ClientActionListener(this, socket);
        actionListener.start();
        //out = new PrintWriter(socket.getOutputStream());
    }

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

    public void setSceneController(SceneController sceneController) {
        this.sceneController = sceneController;
    }

    public SceneController getSceneController() {
        return sceneController;
    }

    public void setUsername(String username) {
        System.out.println("setting username");
        this.username = username;
        sceneController.setUsername(username);
        sceneController.switchToWelcomeView();
        System.out.println("Switched to Welcome View");
    }

    public void updateVersion(int newVersion) {
        versionOfDocument = newVersion;
    }

    public int getVersion(){
        return versionOfDocument;
    }

    public void updateDocumentName(String name) {
        System.out.println("updating documentName");
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
