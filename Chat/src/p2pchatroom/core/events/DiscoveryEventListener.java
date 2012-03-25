package p2pchatroom.core.events;

import java.net.InetAddress;

public interface DiscoveryEventListener extends IOExceptionEventListener {
    void onClientDiscovered(InetAddress address);
}
