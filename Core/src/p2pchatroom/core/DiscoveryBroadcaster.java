package p2pchatroom.core;

import java.io.IOException;
import java.net.*;

public class DiscoveryBroadcaster {
    private String host = "239.255.255.255";
    private int port = 1667;
    private int nrOfPackets = 7;
    private String message = "P2PChatRoom";
    private DatagramPacket packet;
    private DatagramSocket socket;

    public DiscoveryBroadcaster() throws IOException{
        byte[] buffer = message.getBytes();
        InetAddress group = InetAddress.getByName(host);
        socket = new DatagramSocket(port);
        packet = new DatagramPacket(buffer, buffer.length, group, port);

    }
    public void sendPackets() {
        for(int i = 0; i < nrOfPackets; i++) {
            try {
                socket.send(packet);
            } catch (IOException e) {
                System.out.println("Error occured: e");
            }
        }
    }

    
    public void setHost(String newHost) {
        this.host = newHost;
    }

    public void setPort(int newPort) {
        this.port = newPort;
    }

    public void setNrOfPackets(int newNrOfPackets){
        this.nrOfPackets = newNrOfPackets;
    }
    
    public void setMessage(String newMessage) {
        this.message = newMessage;
    }

    public static void main(String[] args) throws IOException, SocketException {
        new Thread() {
            public void run() {
                DiscoveryListener listener = new DiscoveryListener();
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
                DiscoveryBroadcaster broadcaster = null;
                try {
                    broadcaster = new DiscoveryBroadcaster();
                } catch (IOException e) {
                    System.out.println("Error occured: e");
                }
            }
        }.start();
    }
}