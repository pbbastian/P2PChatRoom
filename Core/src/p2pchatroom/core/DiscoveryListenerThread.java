package p2pchatroom.core;

import p2pchatroom.core.events.DiscoveryEventListener;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;

public class DiscoveryListenerThread extends Thread {
    private InetAddress address;
    private int port;
    private MulticastSocket socket;
    private String programName;
    private ArrayList<DiscoveryEventListener> eventListeners;
    
    public DiscoveryListenerThread(InetAddress address, int port, String programName) throws IOException {
        this.address = address;
        this.port = port;
        this.programName = programName;
        
        socket = new MulticastSocket(port);
        socket.joinGroup(address);
        
        eventListeners = new ArrayList<DiscoveryEventListener>();
    }
    
    public DiscoveryListenerThread(String host, int port, String programName) throws IOException {
        this(InetAddress.getByName(host), port, programName);
    }
    
    public void addEventListener(DiscoveryEventListener eventListener) {
        eventListeners.add(eventListener);
    }

    public void interrupt() {
        super.interrupt();
        socket.close();
    }

    public void run() {
        byte[] buffer = programName.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        while (true) {
            try {
                socket.receive(packet);
                clientDiscovered(packet.getAddress());
            } catch (InterruptedIOException e) {
                Thread.currentThread().interrupt();
                System.out.println("Interrupted via InterruptedIOException");
            } catch (IOException e) {
                if (!isInterrupted()) {
                    e.printStackTrace();
                } else {
                    System.out.println("Interrupted");
                }
            }
        }
    }
    
    private void clientDiscovered(InetAddress address) {
        for (DiscoveryEventListener eventListener : eventListeners) {
            eventListener.onClientDiscovered(address);
        }
    }
    
    public static void main(String[] args) {

    }
}
