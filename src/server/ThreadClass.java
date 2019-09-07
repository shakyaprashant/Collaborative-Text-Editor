package server;

import server.handlers.Edit;
import server.handlers.Encoding;
import server.handlers.Edit.Type;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


/**
 *
 * The OurThreadClass takes care of making a new thread that handles one client
 * connection. While the thread is running, it could listens for the client
 * messages, handles the message, and send back the server message to the
 * client. For testing strategy, please see OurThreadClassTest.java.
 *
 */
public class ThreadClass extends Thread {

    private static final boolean DEBUG = true;
    final Socket socket;
    private boolean alive;
    private String username;
    private final Server server;
    private final String regex = "(bye)|(new [\\w\\d]+)|(look)|(open [\\w\\d]+)|(change .+)|(name [\\w\\d]+)";
    private final String error1 = "Error: Document already exists.";
    private final String error2 = "Error: No such document.";
    private final String error3 = "Error: No documents exist yet.";
    private final String error4 = "Error: Insert at invalid position.";
    private final String error5 = "Error: You must enter a name when creating a new document.";
    private final String error6 = "Error: Invalid arguments";
    private final String error7 = "Error: Username is not available";
    private final boolean sleep = false; // for debugging

    /**
     * Constructor for the thread object
     *
     * @param socket
     *            the socket to connect with the server
     * @param server
     *            the server that the client is trying to communicate with
     * @param alive
     *            a boolean that represents whether the client has disconnected
     */
    public ThreadClass(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
        this.alive = true;
    }

    /**
     * Run the server, listening for client connections and handling them. Never
     * returns unless an exception is thrown.
     *
     * @throws IOException
     *             if the main server socket is broken (IOExceptions from
     *             individual clients do *not* terminate serve()).
     */
    // thread object need to have "run" method
    public void run() {
        try {
            handleConnection(socket);
        } catch (IOException e) {
        }
    }


    /**
     * Handle a single client connection. Returns when client disconnects.
     *
     * Server-to-Client Message Protocol
     * message :== (Error|Alldocs |Newdocument | Opendocument | ChangeText| Name)
     * Error :== Error: .+
     * Alldocs:== "alldocs " DocumentName
     * Newdocument:=="new " DocumentName
     * Opendocument:=="open " DocumentName UserName Version DocumentText
     * ChangeText:=="change " DocumentName UserName Version ChangePosition ChangeLength DocumentText
     * Name:== name UserName
     * UserName :== Chars
     * Version :== Int+
     * ChangePosition :== Int+
     * ChangeLength:== -?Int+
     * DocumentName:==Chars
     * DocumentText:==.+
     * Chars:== [\\w\\d]
     * Int:==[0-9]
     *
     * @param socket
     *            socket where the client is connected
     * @throws IOException
     *             if connection has an error or terminates unexpectedly
     */
    private void handleConnection(Socket socket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(
                socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        try {
            for (String line = in.readLine(); line != null; line = in
                    .readLine()) {
                String output = handleRequest(line);
                // for debugging only:
                if (sleep) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                // If it's the bye message, terminate the connection
                if (output != null && output.equals("bye")) {
                    out.close();
                    in.close();
                    server.removeThread(this);

                }

                // if it's the ChangText message,return the message to all other
                // alive clients
                else if (output != null && output.startsWith("change")) {
                    server.returnMessageToEveryOtherClient(output, this);
                }

                // Otherwise, only send the message to the client that sends
                // this request.
                if (output != null) {
                    out.println(output);
                }
            }
        } finally {
            out.close();
            in.close();
        }
    }


    /**
     * handler for client input. Our grammar:
     * Message :== Edit | Open | New | Look| Bye |Name
     * Edit :== change DocumentName Username Version (Remove|Insert)
     * Remove :==remove Position Position
     * Insert :== insert Chars Position
     * Open:== open DocumentName
     * New :== new DocumentName
     * Look :== look
     * Bye::=="bye"
     * Name ::== name Username
     * Username ::== Chars
     * Chars:==.+
     * Position :== Int
     * DocumentName :== Chars
     * Chars ::== \\d\\w
     * Version :== [0-9]+
     * Int :== [0-9]
     *
     * make requested mutations on documenMap of the server if applicable, then
     * return appropriate message to the user.
     *
     * @param input
     *            the string that is the request coming from the client
     * @return the string that is the returning message to the user
     */

    public String handleRequest(String input) {
        if (!alive) {
            throw new RuntimeException(
                    "Should not get here since the client already disconnects.");
        }
        String returnMessage = "";
        input = input.trim();
        String[] tokens = input.split(" ");
        if (DEBUG) {
            System.out.println("Input message from the client is " + input);
        }
        if (!input.matches(regex)) {
            // for invalid input
            // empty documentName
            if (tokens.length == 1 && tokens[0].equals("new")) {
                return error5;
            } else {
                return error6;
            }
        } else {
            if (tokens[0].equals("bye")) {
                // 'bye' request
                alive = false;
                returnMessage = "bye";

            } else if (tokens[0].equals("new")) {
                // 'new' request, make a new document if the name is valid. else, return a error message.
                String documentName = tokens[1];
                if (DEBUG) {
                    System.out.println("creating new document");
                }
                if (server.getDocumentMap().containsKey(documentName)) {
                    returnMessage = error1;
                } else {
                    server.addNewDocument(documentName);
                    returnMessage = "new " + documentName;
                }
            }else if(tokens[0].equals("name")){
                if (DEBUG){System.out.println(tokens[1]);}
                if(server.nameIsAvailable(tokens[1])){
                    this.username = tokens[1];
                    server.addUsername(this, tokens[1]);
                    returnMessage = "name "+tokens[1];
                }
                else{
                    returnMessage = error7;
                }

            } else if (tokens[0].equals("look")) {
                // 'look' request,
                // if server does not have any documents, return error message
                // else, return a string of names separated by a space
                String result = "alldocs";
                if (server.documentMapisEmpty()) {
                    returnMessage = error3;
                } else {
                    result = result + server.getAllDocuments();
                    returnMessage = result;
                }

            } else if (tokens[0].equals("open")) {
                // 'open' request, must open a document if it exists on server
                String documentName = tokens[1];
                if (!server.getDocumentMap().containsKey(documentName)
                        || !server.getDocumentVersionMap().containsKey(
                        documentName)) {
                    returnMessage = error2;
                } else {
                    int version = server.getVersion(documentName);
                    String documentText = Encoding.encode(server
                            .getDocumentText(documentName));
                    returnMessage = "open " + documentName + " " + version
                            + " " + documentText;
                }

            } else if (tokens[0].equals("change")) {
                // 'change' request, must change the string stored on the server if applicable
                int version = Integer.parseInt(tokens[3]);
                int offset, changeLength;
                Edit edit;
                String documentName = tokens[1];
                String editType = tokens[4];
                String username = tokens[2];
                if (!server.getDocumentMap().containsKey(documentName) || !server.getDocumentVersionMap().containsKey(
                        documentName)) {
                    // if the server does not have the document
                    returnMessage = error2;
                } else {
                    Object lock = new Object();
                    // only one thread should be in below because as we change the
                    // version number, there might be race condition.
                    // I.e., a thread check the version number is correct, but in
                    // face that number is changed later on by another thread.
                    synchronized (lock) {
                        if (server.getVersion(documentName) != version) {
                            // the client's document version is out of date
                            //update the index relative to the previous inserts so that the change can be inserted
                            if(editType.equals("insert")){
                                offset = Integer.parseInt(tokens[6]);
                            }
                            else{offset = Integer.parseInt(tokens[5]);}
                            String updates = server.manageEdit(documentName,version, offset);
                            String[] updatedTokens = updates.split(" ");
                            version = Integer.parseInt(updatedTokens[1]);
                            offset = Integer.parseInt(updatedTokens[2]);
                        }
                        // then, the server could apply the (transformed) edit on document and return messages.
                        int length = server.getDocumentLength(documentName);
                        if (editType.equals("remove")) {
                            offset = Integer.parseInt(tokens[5]);
                            int endPosition = Integer.parseInt(tokens[6]);
                            // The server changes the document text:
                            server.delete(documentName, offset, endPosition);
                            changeLength = offset - endPosition; // negative
                            edit = new Edit(documentName, Type.REMOVE, "",
                                    version, offset, changeLength);
                            server.logEdit(edit);
                            // server updates version number:
                            server.updateVersion(documentName, version + 1);
                            returnMessage = createMessage(documentName, username,
                                    version + 1, offset, changeLength,
                                    Encoding.encode(server
                                            .getDocumentText(documentName)));// encode the message!
                        } else if (editType.equals("insert")) {
                            Type type = Type.INSERT;
                            offset = Integer.parseInt(tokens[6]);
                            String text = Encoding.decode(tokens[5]);
                            if (offset > length) {
                                returnMessage = error4;
                            } else {
                                // the server updates the document text:
                                server.insert(documentName, offset, text);
                                changeLength = text.length();
                                edit = new Edit(documentName, type, text,
                                        version, offset, changeLength);
                                server.logEdit(edit);
                                // the server updated the document version
                                server.updateVersion(documentName, version + 1);
                                returnMessage = createMessage(documentName, username,
                                        version + 1, offset, changeLength,
                                        Encoding.encode(server.getDocumentText(documentName)));
                            }
                        }
                    }
                }
            }
        }
        return returnMessage;
    }


    /**
     * Generate an return message from the arguments given according to the grammar.
     * @param documentName  a string that is the name of the document
     * @param version       an integer that is the version of the document
     * @param offset        an integer that is the starting position of the change
     * @param changeLength  an integer that is the length of the text client changes
     * @param documentText  the string that is the entire text of the document.
     * @return
     */
    private String createMessage(String documentName, String username, int version, int offset,
                                 int changeLength, String documentText) {
        String message = "change " + documentName + " " +username+" "+ version + " "
                + offset + " " + changeLength + " " + documentText;
        return message;
    }


    /**
     * return the socket of the client
     * @return socket, the socket of the client
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     *return the userName of the client for this thread
     * @return userName
     */
    public String getUsername() {
        return username;
    }


    /**
     * return private boolean field alive
     * @return alive, a boolean that represents if a client exits and
     *         disconnects.
     */
    public boolean getAlive() {
        return alive;
    }
}
