package p2pchatroom.core;

import p2pchatroom.core.events.ConnectionEventListener;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class Connection implements Closeable {
    private Socket socket;
    private InetAddress address;
    private PeerReaderThread readerThread;
    private PeerWriterThread writerThread;
    private ArrayList<ConnectionEventListener> eventListeners;

    public Connection(Socket socket) {
        this.socket = socket;
        this.address = socket.getInetAddress();
    }
    
    public Connection(InetAddress address, int port) throws IOException {
        this(new Socket(address, port));
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

    public void onMessageReceived(String message) {
        String[] messageParts = message.split(" ", 2);
        String command = messageParts[0];

        if (command.equalsIgnoreCase("MSG")) {
            for (ConnectionEventListener eventListener : eventListeners) {
                eventListener.onMessageReceived(this, messageParts[1]);
            }
        } else if (command.equalsIgnoreCase("PM")) {
            for (ConnectionEventListener eventListener : eventListeners) {
                eventListener.onPrivateMessageReceived(this, messageParts[1]);
            }
        } else if (command.equalsIgnoreCase("NICK")) {
            for (ConnectionEventListener eventListener : eventListeners) {
                eventListener.onNicknameReceived(this, messageParts[1]);
            }
        }
    }

    @Override
    public void close() throws IOException {
        readerThread.interrupt();
        writerThread.interrupt();
        socket.close();
    }

    public InetAddress getAddress() {
        return address;
    }
}
