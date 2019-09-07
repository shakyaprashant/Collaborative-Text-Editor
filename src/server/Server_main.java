package server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * The server_main class will start  to run a server that listens a specific
 * number of port(4444).
 *
 */
public class Server_main {
    private static final boolean DEBUG = true;
    private static final int defaultPort = 5555;

    /**
     * Entry point for starting a Collaborative Editor Server.
     * Starts the server on the specified port or the default port(4444)
     * if no port is specified or if arguments don't follow the usage
     *
     * Usage: Server_main -p PORT
     * PORT = desired port number for the server
     *
     * @param args
     */
    public static void main(String[] args) {
        int port;
        if (args.length == 2 && args[0].equals("-p")
                && args[1].matches("\\d\\d?\\d?\\d?\\d?")) {
            port = Integer.parseInt(args[1]);
        } else {
            port = defaultPort;
        }
        try {
            runServer(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Start a Server running on the specified port. Its map field is
     * initialized as a empty map as no clients have established connection with
     * the server yet.
     *
     * @param port
     *            The network port on which the server should listen, requires 0
     *            <= port <= 65535.
     */

    public static void runServer(int port) throws IOException {
        if (DEBUG) {
            System.out.println("I am at runServer().");
        }
        Map<String, StringBuffer> map = new HashMap<String, StringBuffer>();
        Map<String, Integer> versions = new HashMap<String, Integer>();
        Server server = new Server(port, map, versions);
        server.serve();
    }
}
