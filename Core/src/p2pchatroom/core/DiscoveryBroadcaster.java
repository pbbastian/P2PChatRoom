package p2pchatroom.core;

import java.io.IOException;
import java.net.*;

public class DiscoveryBroadcaster {
    private int port;

    public DiscoveryBroadcaster(int port) {
        this.port = port;
    }

    public void broadcast() throws IOException, UnknownHostException {
        byte[] message = "P2PChatRoom".getBytes();
        InetAddress group = InetAddress.getByName("224.0.0.1");
        MulticastSocket socket = new MulticastSocket(port);
        socket.joinGroup(group);
        DatagramPacket packet = new DatagramPacket(message, message.length, group, port);
        socket.send(packet);
    }

    public static void main(String[] args) throws IOException, SocketException {
        new Thread() {
            public void run() {
                DiscoveryListener listener = new DiscoveryListener(1667);
                listener.listen();
            }
        }.start();
        new Thread() {
            public void run() {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                DiscoveryBroadcaster broadcaster = new DiscoveryBroadcaster(1667);
                try {
                    broadcaster.broadcast();
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }.start();
        
    }
}
