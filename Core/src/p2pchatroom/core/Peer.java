package p2pchatroom.core;

import p2pchatroom.core.events.PeerEventListener;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class Peer {
    private Socket socket;
    private PeerReaderThread readerThread;
    private PeerWriterThread writerThread;
    private String nickname;
    private String address;
    private ArrayList<PeerEventListener> eventListeners;

    Peer(Socket socket) throws IOException {
        this.socket = socket;
        
        readerThread = new PeerReaderThread(socket, this);
        writerThread = new PeerWriterThread(socket);
        
        readerThread.start();
        writerThread.start();
    }

    public void sendMessage(String message) {
        writerThread.queueMessage("MSG " + message);
    }
    
    public void sendPrivateMessage(String message) {
        writerThread.queueMessage("PM " + message);
    }

    public void sendNickname(String nickname) {
        writerThread.queueMessage("NICK " + nickname);
    }

    void closeConnection() throws IOException {
        readerThread.interrupt();
        writerThread.interrupt();

        while (writerThread.isAlive()) {}

        socket.close();

        readerThread = null;
        writerThread = null;
    }

    void onMessageReceived(String message) {
        String[] messageParts = message.split(" ", 2);
        String command = messageParts[0];

        if (command.equalsIgnoreCase("MSG")) {
            for (PeerEventListener eventListener : eventListeners) {
                eventListener.onMessageReceived(this, messageParts[1]);
            }
        } else if (command.equalsIgnoreCase("PM")) {
            for (PeerEventListener eventListener : eventListeners) {
                eventListener.onPrivateMessageReceived(this, messageParts[1]);
            }
        } else if (command.equalsIgnoreCase("NICK")) {
            for (PeerEventListener eventListener : eventListeners) {
                eventListener.onNicknameReceived(this, messageParts[1]);
            }
        }
    }
}
