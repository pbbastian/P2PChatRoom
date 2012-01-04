package p2pchatroom.core;

import java.io.IOException;
import java.net.*;

public class DiscoveryBroadcaster {
    private int port;

    public DiscoveryBroadcaster(int port) {
        this.port = port;
    }

    public void broadcast() throws IOException, UnknownHostException {
        DatagramSocket socket = new DatagramSocket();
        //InetAddress local = InetAddress.getLocalHost();
        //NetworkInterface networkInterface = NetworkInterface.getByInetAddress(local);
        //InetAddress broadcastAddress = networkInterface.getInterfaceAddresses().get(0).getBroadcast();
        byte[] message = "P2PChatRoom".getBytes();
        DatagramPacket packet = new DatagramPacket(message, message.length, InetAddress.getByName("192.168.137.1"), port);
        socket.send(packet);
    }

    public static void main(String[] args) throws IOException, SocketException {
        DiscoveryBroadcaster broadcaster = new DiscoveryBroadcaster(1666);
        broadcaster.broadcast();
    }
}
