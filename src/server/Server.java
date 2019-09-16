package server;

import server.handlers.Edit;
import server.handlers.EditManager;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;


public class Server {
    private static final boolean DEBUG = true;
    private final Map<String, StringBuffer> documentMap;
    private final Map<String, Integer> documentVersionMap;
    private ServerSocket serverSocket;
    private ArrayList<ThreadClass> threadList;
    private ArrayList<String> usernameList;
    private final EditManager editManager;

    /**
     * Constructor for Server Class. Creates Server Socket on the specified port.
     * @param port
     * @param documents
     * @param version
     */
    public Server(int port, Map<String, StringBuffer> documents,
                  Map<String, Integer> version) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server created. Listening on port " + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        documentMap = Collections.synchronizedMap(documents);
        threadList = new ArrayList<ThreadClass>();
        documentVersionMap = Collections.synchronizedMap(version);
        usernameList = new ArrayList<String>();
        editManager = new EditManager();
    }


    public void serve() {
        while (true) {
            try {
                // block until a client connects
                Socket socket = serverSocket.accept();
                // handle the client by making a new OurThreadClass thread
                // running for that client,
                // also add that thread to the threadList so that the server
                // could send the message to the client
                ThreadClass t = new ThreadClass(socket, this);
                threadList.add(t);
                t.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized Map<String, StringBuffer> getDocumentMap() {
        return documentMap;

    }

    public synchronized boolean nameIsAvailable(String name){
        return !usernameList.contains(name);
    }

    public synchronized void addUsername(ThreadClass t, String name){
        usernameList.add(name);
    }


    public synchronized Map<String, Integer> getDocumentVersionMap() {
        return documentVersionMap;

    }


    public synchronized String getAllDocuments() {
        String documentNames = "";
        for (String key : documentMap.keySet()) {
            documentNames += " " + key;
        }
        return documentNames;
    }


    public synchronized String manageEdit(String documentName, int version,
                                          int offset) {
        return editManager.manageEdit(documentName, version, offset);
    }


    public synchronized boolean documentMapisEmpty() {
        return documentMap.isEmpty();
    }

    public synchronized boolean versionMapisEmpty() {
        return documentVersionMap.isEmpty();
    }

    public synchronized void logEdit(Edit edit) {
        editManager.logEdit(edit);
    }


    public synchronized void removeThread(ThreadClass t) {
        if (DEBUG) {
            System.out.println("removing thread from threadlist");
        }
        usernameList.remove(t.getUsername());
        threadList.remove(t);
    }


    public synchronized void addNewDocument(String documentName) {
        documentMap.put(documentName, new StringBuffer());
        documentVersionMap.put(documentName, 1);
        editManager.createNewlog(documentName);

    }

    public synchronized void updateVersion(String documentName, int version) {
        documentVersionMap.put(documentName, version);
    }


    public synchronized int getVersion(String documentName) {
        return documentVersionMap.get(documentName);
    }


    public synchronized void delete(String documentName, int offset,
                                    int endPosition) {
        if (offset < 0 || endPosition < 1) {
            throw new RuntimeException("invalid args");
        }
        documentMap.get(documentName).delete(offset, endPosition);
    }


    public synchronized void insert(String documentName, int offset, String text) {
        documentMap.get(documentName).insert(offset, text);
    }


    public synchronized String getDocumentText(String documentName) {
        String document = "";
        document = documentMap.get(documentName).toString();
        return document;
    }


    public synchronized int getDocumentLength(String documentName) {
        return documentMap.get(documentName).length();
    }

    /**
     * sends message from server to every client except client thread.
     * @param message
     * @param thread
     */
    public void returnMessageToEveryOtherClient(String message,
                                                ThreadClass thread) {
        for (ThreadClass t : threadList) {
            if (!thread.equals(t) && !t.getSocket().isClosed()) {
                // if the thread is still alive and it's not the one that sends
                // the request, send message
                PrintWriter out;
                if (t.getSocket().isConnected()) {
                    synchronized (t) {
                        try {
                            // for those threads, open a printWriter and write
                            // message to its socket.
                            out = new PrintWriter(t.getSocket()
                                    .getOutputStream(), true);
                            out.println(message);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

}
