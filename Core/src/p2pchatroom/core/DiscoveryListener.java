package p2pchatroom.core;

import java.io.IOException;
import java.net.*;

public class DiscoveryListener {
    private DatagramSocket datagramsocket;
    private int server_port;
    private String program_name;

    public DiscoveryListener(int server_port) { //Server listens to UDP port XXXX (over 1024)
        this.server_port = server_port;
        this.program_name = "P2PChatRoom";
        try {
            datagramsocket = new DatagramSocket(this.server_port);
        } catch (SocketException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
    public void listen() {
        byte[] name = program_name.getBytes();
        byte[] buffer = new byte[name.length];
        System.out.println("--Bytes generated");

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        
        while(true) {
            try {
                datagramsocket.receive(packet);
                System.out.println("--Packet received");
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            String msg = new String(buffer, 0, packet.getLength());
            System.out.println("MESSAGE BELOW:::");
            System.out.println(packet.getAddress().getHostName() + ": " + msg);
            packet.setLength(buffer.length);
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

        DiscoveryListener listener = new DiscoveryListener(1666);
        listener.listen();
    }
    //END MAIN PROGRAM-------------
}