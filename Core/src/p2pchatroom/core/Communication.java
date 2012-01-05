package p2pchatroom.core;

import java.io.IOException;
import java.util.HashSet;

public class Communication {

    private HashSet<String> clients;
    private int server_port;


    public Communication() {
        DiscoveryListener listener = new DiscoveryListener();
        DiscoveryBroadcaster broadcaster = null;
        try {
            broadcaster = new DiscoveryBroadcaster();
        } catch (IOException e) {
            System.out.println("Error occured: e");
        }
        broadcaster.sendPackets();
        while(listener.listening) {
            this.clients = listener.getClients();
        }

    }
}
