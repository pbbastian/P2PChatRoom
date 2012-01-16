package p2pchatroom.core;

import p2pchatroom.core.events.ConnectionEventListener;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class PeerConnection implements Connection {
    private final Socket socket;
    private InetAddress address;
    private PeerReaderThread readerThread;
    private PeerWriterThread writerThread;
    private final ArrayList<ConnectionEventListener> eventListeners;
    private Peer peer;

    public PeerConnection(Peer peer, Socket socket) {
        this.peer = peer;
        this.socket = socket;
        this.address = socket.getInetAddress();
        this.eventListeners = new ArrayList<ConnectionEventListener>();
    }

    public PeerConnection(Peer peer, InetAddress address, int port) throws IOException {
        this(peer, new Socket(address, port));
    }

    public void addEventListener(ConnectionEventListener eventListener) {
        eventListeners.add(eventListener);
    }

    public void open() throws IOException {
        readerThread = new PeerReaderThread(socket, this);
        writerThread = new PeerWriterThread(socket);

        readerThread.start();
        writerThread.start();
    }

    public void message(String message) {
        writerThread.queueMessage("MSG " + message);
    }

    public void privateMessage(String message) {
        writerThread.queueMessage("PM " + message);
    }

    public void sendNickname(String nickname) {
        writerThread.queueMessage("NICK " + nickname);
    }

    void onMessageReceived(String message) {
        if (message == null)
            return;

        String[] messageParts = message.split(" ", 2);
        String command = messageParts[0];

        if (command.equals("MSG")) {
            for (ConnectionEventListener eventListener : eventListeners) {
                eventListener.onMessageReceived(this, messageParts[1]);
            }
        } else if (command.equals("PM")) {
            for (ConnectionEventListener eventListener : eventListeners) {
                eventListener.onPrivateMessageReceived(this, messageParts[1]);
            }
        } else if (command.equals("NICK")) {
            for (ConnectionEventListener eventListener : eventListeners) {
                eventListener.onNicknameReceived(this, messageParts[1]);
            }
        } else if (command.equals("EXIT")) {
            for (ConnectionEventListener eventListener : eventListeners) {
                eventListener.onNicknameReceived(this, messageParts[1]);
            }
        }
    }

    @Override
    public void close() throws IOException {
        writerThread.queueMessage("EXIT");
        readerThread.interrupt();
        writerThread.interrupt();
        socket.close();
        readerThread = null;
        writerThread = null;
    }

    public InetAddress getAddress() {
        return address;
    }

    public Peer getPeer() {
        return peer;
    }

    public void setPeer(Peer peer) {
        this.peer = peer;
    }
}
