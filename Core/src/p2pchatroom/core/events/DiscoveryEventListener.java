package p2pchatroom.core.events;

import java.net.InetAddress;

public interface DiscoveryEventListener {
    void onClientDiscovered(InetAddress address);
}
