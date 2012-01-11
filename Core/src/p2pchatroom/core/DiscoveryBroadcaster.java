package p2pchatroom.core;

import java.io.Closeable;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class DiscoveryBroadcaster extends Thread implements Closeable {
    private InetAddress group;
    private int port;
    private byte[] message;
    private DatagramPacket packet;
    private DatagramSocket socket;

    public DiscoveryBroadcaster(InetAddress group, int port, byte[] message) throws IOException {
        this.group = group;
        this.port = port;
        this.message = message;
    }

    public DiscoveryBroadcaster(String host, int port, byte[] message) throws IOException {
        this(InetAddress.getByName(host), port, message);
    }

    public DiscoveryBroadcaster(InetAddress group, int port, String message) throws IOException {
        this(group, port, message.getBytes());
    }

    public DiscoveryBroadcaster(String host, int port, String message) throws IOException {
        this(InetAddress.getByName(host), port, message.getBytes());
    }

    public void sendPackages(int numberOfPackages) throws IOException {
        for(int i = 0; i < numberOfPackages; i++) {
            socket.send(packet);
        }
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
            } catch (SocketException e) {
                System.out.println("ERROR: SocketException");
            }
            try{
                sendPackages(7);
            } catch (IOException e) {
                System.out.println("ERROR: IOException");
            }
            socket.close();
            try {
                this.wait(10000); //To make the thread repeat every 10000
            } catch (InterruptedException e) {
                System.out.println("ERROR: InterruptedException");
            }
        }
    }
}