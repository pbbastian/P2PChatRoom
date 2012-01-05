package p2pchatroom.core;

import java.io.IOException;
import java.net.*;
import java.util.HashSet;

public class DiscoveryListener {
    private MulticastSocket multicastSocket;
    private int serverPort = 1666;
    private String host = "239.255.255.255";
    private InetAddress group;
    private String program_name;
    public boolean listening;
    private HashSet<String> clients;

    public DiscoveryListener() { //Syntax DiscoveryListener(<BROADCAST_IP>, <PORT>)
        this.program_name = "P2PChatRoom";
        try {
            this.group = InetAddress.getByName(host);
            multicastSocket = new MulticastSocket(serverPort);
            multicastSocket.joinGroup(group);
            System.out.println("TTL: "+multicastSocket.getTimeToLive());
        } catch (Exception e) {
            System.out.println("Exception occured");
        }
    }
    public void listen() {
        byte[] name = program_name.getBytes();
        byte[] buffer = new byte[name.length];
        System.out.println("--Bytes generated");

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        listening = true;
        while(listening) {
            try {
                multicastSocket.receive(packet);
                System.out.println("--Packet received");
                clients.add(packet.getAddress().getHostAddress());
            } catch (IOException e) {
                System.out.println("Exception occured");
            }
            String msg = new String(buffer, 0, packet.getLength());
            System.out.println("MESSAGE BELOW:::");
            System.out.println(packet.getAddress().getHostName() + ": " + msg);
            packet.setLength(buffer.length);
        }
    }
    public void stopListen() {
        try {
            listening = false;
            multicastSocket.leaveGroup(group);
            multicastSocket.close();
        } catch (Exception e) {
            System.out.println("Exception occured");
        }
    }
    
    public void setProgram_name(String program_name) {
        this.program_name = program_name;
    }
    
    public void setHost(String newHost) {
        this.host = newHost;
    }

    public void setPort(int newServerPort) {
        this.serverPort = newServerPort;
    }
    
    public HashSet<String> getClients() {
        return clients;
    }

    // TEST MAIN PROGRAM-----------
    public static void main(String [] args) {
        DiscoveryListener listener = new DiscoveryListener();
        listener.listen();
    }
    //END MAIN PROGRAM-------------
}