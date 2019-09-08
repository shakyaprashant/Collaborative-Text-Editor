package client.conversation;

import java.io.Serializable;
import java.util.function.Consumer;

public class ConvClient extends NetworkConnection {

    private String ip;
    private int port;

    public ConvClient(String ip, int port, Consumer<Serializable> onReceiveCallback) {
        super(onReceiveCallback);
        this.ip = ip;
        this.port = port;
    }

    @Override
    protected boolean isServer() {
        return false;
    }

    @Override
    protected String getIP() {
        return ip;
    }

    @Override
    protected int getPort() {
        return port;
    }
}
