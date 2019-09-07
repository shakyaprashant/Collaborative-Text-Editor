package client.handlers;

import client.Client;

import javax.swing.*;

@SuppressWarnings("unused")
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
    /**
     * Repaints the GUI after connecting with the server and the actions
     * have been completed.
     */
    @Override
    protected void done() {
        //client.getMainWindow().repaint();
        sent = false;

    }


}