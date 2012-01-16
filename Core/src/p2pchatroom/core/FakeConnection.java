package p2pchatroom.core;

import p2pchatroom.core.events.ConnectionEventListener;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;

public class FakeConnection implements Connection {
    private final InetAddress address;
    private Peer peer;
    private final ArrayList<ConnectionEventListener> eventListeners;
    
    public FakeConnection(Peer peer, InetAddress address) {
        this.peer = peer;
        this.address = address;
        this.eventListeners = new ArrayList<ConnectionEventListener>();
    }

    @Override
    public void open() throws IOException {
    }

    @Override
    public void addEventListener(ConnectionEventListener eventListener) {
        this.eventListeners.add(eventListener);
    }

    @Override
    public void message(String message) {
        for (ConnectionEventListener eventListener : eventListeners) {
            eventListener.onMessageReceived(this, message);
        }
    }

    @Override
    public void privateMessage(String message) {
        for (ConnectionEventListener eventListener : eventListeners) {
            eventListener.onPrivateMessageReceived(this, message);
        }
    }

    @Override
    public void sendNickname(String nickname) {
        for (ConnectionEventListener eventListener : eventListeners) {
            eventListener.onNicknameReceived(this, nickname);
        }
    }

    @Override
    public Peer getPeer() {
        return peer;
    }

    @Override
    public void setPeer(Peer peer) {
        this.peer = peer;
    }

    @Override
    public InetAddress getAddress() {
        return this.address;
    }

    @Override
    public void close() throws IOException {
    }
}
