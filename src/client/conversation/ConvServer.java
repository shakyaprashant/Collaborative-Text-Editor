package client.conversation;

import java.io.Serializable;
import java.util.function.Consumer;

public class ConvServer extends NetworkConnection {

    private int port;

    public ConvServer(int port, Consumer<Serializable> onReceiveCallback) {
        super(onReceiveCallback);
        this.port = port;
    }

    @Override
    protected boolean isServer() {
        return true;
    }

    @Override
    protected String getIP() {
        return null;
    }

    @Override
    protected int getPort() {
        return port;
    }
}