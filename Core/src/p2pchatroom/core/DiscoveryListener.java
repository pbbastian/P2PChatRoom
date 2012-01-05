package p2pchatroom.core;

import java.io.IOException;
import java.net.*;

public class DiscoveryListener {
    private MulticastSocket multicastSocket;
    private int server_port;
    private InetAddress group;
    private String program_name;
    private boolean listening;

    public DiscoveryListener(String host, int server_port) { //Syntax DiscoveryListener(<BROADCAST_IP>, <PORT>)
        this.server_port = server_port;
        this.program_name = "P2PChatRoom";
        try {
            this.group = InetAddress.getByName(host);
            multicastSocket = new MulticastSocket(server_port);
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
    public void setPort(int server_port) {
        this.server_port = server_port;
    }
    public void setProgram_name(String program_name) {
        this.program_name = program_name;
    }

    // TEST MAIN PROGRAM-----------
    public static void main(String [] args) {
//        InetAddress local = null;
//        try {
//            local = InetAddress.getLocalHost();
//        } catch (UnknownHostException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }
//        NetworkInterface networkInterface = null;
//        try {
//            networkInterface = NetworkInterface.getByInetAddress(local);
//        } catch (SocketException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }
//        for (InterfaceAddress address : networkInterface.getInterfaceAddresses()) {
//            System.out.println(address.getAddress() + " - " + address.getNetworkPrefixLength());
//        }

        DiscoveryListener listener = new DiscoveryListener("239.255.255.255", 1666);
        listener.listen();
    }
    //END MAIN PROGRAM-------------
}