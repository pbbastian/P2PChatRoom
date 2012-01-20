package p2pchatroom.core;

import p2pchatroom.core.events.IOExceptionEventListener;

import java.io.Closeable;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

class DiscoveryBroadcasterThread extends Thread implements Closeable {
    private final InetAddress group;
    private final int port;
    private final byte[] message;
    private DatagramPacket packet;
    private DatagramSocket socket;
    private final ArrayList<IOExceptionEventListener> eventListeners;

    private DiscoveryBroadcasterThread(InetAddress group, int port, byte[] message) {
        this.group = group;
        this.port = port;
        this.message = message;
        this.eventListeners = new ArrayList<IOExceptionEventListener>();
    }

    public DiscoveryBroadcasterThread(String host, int port, byte[] message) throws IOException {
        this(InetAddress.getByName(host), port, message);
    }

    public DiscoveryBroadcasterThread(InetAddress group, int port, String message) throws IOException {
        this(group, port, message.getBytes());
    }

    public DiscoveryBroadcasterThread(String host, int port, String message) throws IOException {
        this(InetAddress.getByName(host), port, message.getBytes());
    }

    public void addEventListener(IOExceptionEventListener eventListener) {
        eventListeners.add(eventListener);
    }

    @Override
    public void close() throws IOException {
        this.interrupt();
        socket.close();
    }

    public void run() {
        while (!isInterrupted()) {
            packet = new DatagramPacket(message, message.length, group, port);
            try {
                socket = new DatagramSocket(port);
                for(int i = 0; i < 7; i++) {
                    socket.send(packet);
                }
            } catch (IOException e) {
                ioError(e);
            } finally {
                try {
                    this.close();
                } catch (IOException e) {
                    // The connection might already have been closed, but that doesn't really matter in this case.
                }
            }
        }
    }

    private void ioError(IOException e) {
        for (IOExceptionEventListener eventListener : eventListeners) {
            eventListener.onIOError(e);
        }
    }
}