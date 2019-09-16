package client.handlers;

import client.Client;

import javax.swing.*;


public class MessageSwingWorker extends SwingWorker<Void, Void> {
    private Client client;
    private String message;
    private boolean sent;

    public MessageSwingWorker(Client client, String message, boolean sent){
        this.client = client;
        this.message = message;
        this.sent = sent;
    }


    protected Void doInBackground() {
        client.sendMessageToServer(message);
        done();
        return null;
    }

    @Override
    protected void done() {
        sent = false;

    }


}