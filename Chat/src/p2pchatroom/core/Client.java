package p2pchatroom.core;

/* This class handles the peers and provide the general API for any interface with the user.
 * 
 */

import p2pchatroom.core.events.*;

import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

@SuppressWarnings("ALL")
public class Client implements DiscoveryEventListener, ConnectionEventListener, ServerEventListener, IOExceptionEventListener {
    private String clientIdentifier;
    private ServerThread serverThread;
    private final ArrayList<Peer> peers;
    private String nickname;
    private InetAddress group;
    private int discoveryPort;
    private int connectionPort;
    private ArrayList<ClientEventListener> eventListeners;
    private DiscoveryListenerThread discoveryListenerThread;

    public Client(int discoveryPort, int connectionPort, String nickname, String clientIdentifier) throws UnknownHostException {
        this.peers = new ArrayList<Peer>();
        this.group = InetAddress.getByName("238.255.255.255");
        this.connectionPort = connectionPort;
        this.discoveryPort = discoveryPort;
        this.eventListeners = new ArrayList<ClientEventListener>();
        this.nickname = nickname;
        this.clientIdentifier = clientIdentifier;
        
        InetAddress localAddress = InetAddress.getLocalHost();
        Peer self = new Peer(localAddress, nickname);
        Connection fakeConnection = new FakeConnection(self, localAddress);
        fakeConnection.addEventListener(this);
        self.setConnection(fakeConnection);
        peers.add(self);
    }

    public void addEventListener(ClientEventListener eventListener) {
        eventListeners.add(eventListener);
    }

    public String getNickname() {
        return this.nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
        for (Peer peer : peers) {
            peer.getConnection().sendNickname(nickname);
        }
    }

    public ArrayList<Peer> getPeers() {
        return this.peers;
    }
    
    public void broadcast() throws IOException {
        DiscoveryBroadcasterThread discoveryBroadcasterThread = new DiscoveryBroadcasterThread(group, discoveryPort, clientIdentifier);
        discoveryBroadcasterThread.start();
    }

    public void startListeningForBroadcasts() throws IOException {
        discoveryListenerThread = new DiscoveryListenerThread(group,discoveryPort,clientIdentifier);
        discoveryListenerThread.addEventListener(this);
        discoveryListenerThread.start();
    }
    
    void stopListeningForBroadcasts() {
        discoveryListenerThread.interrupt();
    }

    public void startListeningForConnections() throws IOException, BindException{
        serverThread = new ServerThread(connectionPort);
        serverThread.addEventListener(this);
        serverThread.start();
    }
    
    void stopListeningForConnections() {
        serverThread.interrupt();
    }

    public void message(String message) {
        for (Peer peer : peers) {
            peer.getConnection().message(message);
        }
    }
    
    public void privateMessage(String nickname, String message) {
        for (Peer peer : peers) {
            if (peer.getNickname().equals(nickname)) {
                peer.getConnection().privateMessage(message);
            }
        }
    }

    public boolean closeConnections() {
        try {
            for (Peer peer : peers) {
                peer.getConnection().close();
            }
            peers.clear();
            stopListeningForBroadcasts();
            stopListeningForConnections();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public void onClientDiscovered(InetAddress address) {
        try {
            Peer peer = new Peer(address);
            PeerConnection connection = new PeerConnection(peer, address, connectionPort);
            connection.addEventListener(this);
            connection.open();
            peer.setConnection(connection);
            peers.add(peer);
            connection.sendNickname(nickname);
        } catch (IOException e) {
            errorOccurred(ErrorType.Connection, "An IO error occurred while establishing a connection to '" +
                address.toString() + "'.");
        }
    }

    @Override
    public void onIOError(IOException exception) {
        errorOccurred(ErrorType.IO, exception.getMessage());
    }

    @Override
    public void onMessageReceived(Connection connection, String message) {
        for (ClientEventListener eventListener : eventListeners) {
            eventListener.onMessageReceived(connection.getPeer(), message);
        }
    }

    @Override
    public void onPrivateMessageReceived(Connection connection, String message) {
        for (ClientEventListener eventListener : eventListeners) {
            eventListener.onPrivateMessageReceived(connection.getPeer(), message);
        }
    }

    @Override
    public void onNicknameReceived(Connection connection, String nickname) {
        String oldNickname = connection.getPeer().getNickname();
        connection.getPeer().setNickname(nickname);
        if (oldNickname == null) {
            connectionEstablished(connection.getPeer());
        } else {
            for (ClientEventListener eventListener : eventListeners) {
                eventListener.onNicknameChanged(connection.getPeer(), oldNickname);
            }
        }
    }

    @Override
    public void onConnectionClosed(Connection connection) {
        peers.remove(connection.getPeer());
        discoveryListenerThread.removeAddressFromKnownList(connection.getAddress().getAddress());
        for (ClientEventListener eventListener : eventListeners) {
            eventListener.onConnectionClosed(connection.getPeer());
        }
    }

    @Override
    public void onConnectionAccepted(Socket socket) {
        try {
            Peer peer = new Peer(socket.getInetAddress());
            PeerConnection connection = new PeerConnection(peer, socket);
            connection.addEventListener(this);
            connection.open();
            peer.setConnection(connection);
            peers.add(peer);
            connection.sendNickname(nickname);
        } catch (IOException e) {
            errorOccurred(ErrorType.Connection, "An IO error occurred while establishing a connection to '" +
                    socket.getInetAddress().toString() + "'.");
        }
    }
    
    private void errorOccurred(ErrorType type, String message) {
        for (ClientEventListener eventListener : eventListeners) {
            eventListener.onErrorOccurred(type, message);
        }
    }
    
    private void connectionEstablished(Peer peer) {
        for (ClientEventListener eventListener : eventListeners) {
            eventListener.onConnectionEstablished(peer);
        }
    }
}
