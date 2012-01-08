package p2pchatroom.core.events;

import java.io.IOException;
import java.net.InetAddress;

public interface DiscoveryEventListener {
    void onClientDiscovered(InetAddress address);
    void onIOError(IOException exception);
}
