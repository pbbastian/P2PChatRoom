package p2pchatroom.core;

import p2pchatroom.core.events.ConnectionEventListener;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;

public interface Connection extends Closeable {
    void open() throws IOException;
    void addEventListener(ConnectionEventListener eventListener);
    void message(String message);
    void privateMessage(String message);
    void sendNickname(String nickname);
    Peer getPeer();
    void setPeer(Peer peer);
    public InetAddress getAddress();
}
