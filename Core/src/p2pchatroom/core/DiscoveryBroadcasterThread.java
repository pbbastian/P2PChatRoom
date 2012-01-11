package p2pchatroom.core;

import java.io.Closeable;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class DiscoveryBroadcasterThread extends Thread implements Closeable {
    private InetAddress group;
    private int port;
    private byte[] message;
    private DatagramPacket packet;
    private DatagramSocket socket;
    private boolean keepBroadcasting = false;
    private int interval;

    public DiscoveryBroadcasterThread(InetAddress group, int port, byte[] message) throws IOException {
        this.group = group;
        this.port = port;
        this.message = message;
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

    public void sendPackages(int numberOfPackages) throws IOException {
        for(int i = 0; i < numberOfPackages; i++) {
            socket.send(packet);
        }
    }

    public void setKeepBroadcasting(boolean keepBroadcasting, int interval) {
        this.keepBroadcasting = keepBroadcasting;
        this.interval = interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
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
            if(keepBroadcasting) {
                try{
                    sendPackages(7);
                    this.close();
                    this.wait(interval);
                } catch (Exception e) {
                    System.out.println("ERROR: Interval broadcast stopped");
                }
            }
        }
    }
}