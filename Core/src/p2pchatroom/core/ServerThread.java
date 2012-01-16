package p2pchatroom.core;

import p2pchatroom.core.events.ServerEventListener;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerThread extends Thread {
    private ServerSocket serverSocket;
    private ArrayList<ServerEventListener> eventListeners;

    public ServerThread(int port) throws IOException, BindException {
        serverSocket = new ServerSocket(port);
        eventListeners = new ArrayList<ServerEventListener>();
    }

    public void addEventListener(ServerEventListener eventListener) {
        eventListeners.add(eventListener);
    }
    
    @Override
    public void interrupt() {
        super.interrupt();
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                Socket socket = serverSocket.accept();
                connectionAccepted(socket);
            } catch (IOException e) {
                if (!isInterrupted()) {
                    ioError(e);
                } else {
                    return;
                }
            }
        }
    }

    private void connectionAccepted(Socket socket) {
        for (ServerEventListener eventListener : eventListeners) {
            eventListener.onConnectionAccepted(socket);
        }
    }

    private void ioError(IOException e) {
        for (ServerEventListener eventListener : eventListeners) {
            eventListener.onIOError(e);
        }
    }
}
