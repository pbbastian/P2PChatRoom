package p2pchatroom.core;

import java.io.Closeable;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class DiscoveryBroadcaster implements Closeable {
    private DatagramPacket packet;
    private DatagramSocket socket;

    public DiscoveryBroadcaster(InetAddress group, int port, byte[] message) throws IOException {
        this.socket = new DatagramSocket(port);
        this.packet = new DatagramPacket(message, message.length, group, port);
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
        this.socket.close();
    }
}