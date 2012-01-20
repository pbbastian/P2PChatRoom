package p2pchatroom.core;

import p2pchatroom.core.events.DiscoveryEventListener;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.Arrays;

class DiscoveryListenerThread extends Thread {
    private final InetAddress address;
    private final int port;
    private MulticastSocket socket;
    private final String programName;
    private ArrayList<byte[]> knownAddresses;
    private ArrayList<DiscoveryEventListener> eventListeners;
    
    public DiscoveryListenerThread(InetAddress address, int port, String programName) throws IOException {
        this.address = address;
        this.port = port;
        this.programName = programName;
        
        socket = new MulticastSocket(port);
        socket.joinGroup(address);

        knownAddresses = new ArrayList<byte[]>();
        eventListeners = new ArrayList<DiscoveryEventListener>();
        
        knownAddresses.add(InetAddress.getLocalHost().getAddress());
    }

    public DiscoveryListenerThread(String host, int port, String programName) throws IOException {
        this(InetAddress.getByName(host), port, programName);
    }
    
    public void addEventListener(DiscoveryEventListener eventListener) {
        eventListeners.add(eventListener);
    }

    @Override
    public void interrupt() {
        super.interrupt();
        socket.close();
    }

    public void run() {
        byte[] buffer = programName.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        while (!isInterrupted()) {
            try {
                socket.receive(packet);
                InetAddress packetAddress = packet.getAddress();
                boolean matchingAddressFound = false;
                final ArrayList<byte[]> knownAddresses = this.knownAddresses;
                for (byte[] knownAddress : knownAddresses) {
                    if (Arrays.equals(knownAddress, packetAddress.getAddress())) {
                        matchingAddressFound = true;
                    }
                }
                if (!matchingAddressFound) {
                    clientDiscovered(packetAddress);
                    knownAddresses.add(packetAddress.getAddress());
                }
            } catch (IOException e) {
                if (!isInterrupted()) {
                    ioError(e);
                } else {
                    return;
                }
            }
        }
    }
    
    public void removeAddressFromKnownList(byte[] address) {
        for (byte[] knownAddress : knownAddresses) {
            if (Arrays.equals(knownAddress,  address)) {
                knownAddresses.remove(knownAddress);
            }
        }
    }
    
    private void clientDiscovered(InetAddress address) {
        for (DiscoveryEventListener eventListener : eventListeners) {
            eventListener.onClientDiscovered(address);
        }
    }

    private void ioError(IOException exception) {
        for (DiscoveryEventListener eventListener : eventListeners) {
            eventListener.onIOError(exception);
        }
    }
}
