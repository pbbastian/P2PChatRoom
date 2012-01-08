package p2pchatroom.core;

import java.io.IOException;
import java.net.*;

public class DiscoveryBroadcaster {
    private DatagramPacket packet;
    private DatagramSocket socket;
    private int numberOfPackages;

    public DiscoveryBroadcaster(InetAddress group, int port, byte[] message, int numberOfPackages) throws IOException {
        this.numberOfPackages = numberOfPackages;
        this.socket = new DatagramSocket(port);
        this.packet = new DatagramPacket(message, message.length, group, port);
    }

    public DiscoveryBroadcaster(String host, int port, byte[] message, int numberOfPackages) throws IOException {
        this(InetAddress.getByName(host), port, message, numberOfPackages);
    }

    public DiscoveryBroadcaster(InetAddress group, int port, String message, int numberOfPackages) throws IOException {
        this(group, port, message.getBytes(), numberOfPackages);
    }

    public DiscoveryBroadcaster(String host, int port, String message, int numberOfPackages) throws IOException {
        this(InetAddress.getByName(host), port, message.getBytes(), numberOfPackages);
    }

    public void sendPackets() throws IOException {
        for(int i = 0; i < numberOfPackages; i++) {
            socket.send(packet);
        }
    }
}