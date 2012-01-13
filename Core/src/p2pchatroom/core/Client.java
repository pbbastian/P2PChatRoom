/* This class handles the peers and provide the general API for any interface with the user.
 * 
 */

package p2pchatroom.core;

import p2pchatroom.core.events.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class Client implements DiscoveryEventListener, ConnectionEventListener, ServerEventListener, IOExceptionEventListener {
    private static final String clientIdentifier = "P2PChatRoom 0.1";
    private ServerThread serverThread;
    private ArrayList<Peer> peers;
    private String nickname;
    private InetAddress group;
    private int discoveryPort;
    private int connectionPort;
    private ArrayList<ClientEventListener> eventListeners;
    private DiscoveryListenerThread discoveryListenerThread;

    public Client(InetAddress group, int discoveryPort, int connectionPort) {
        this.peers = new ArrayList<Peer>();
        this.group = group;
        this.connectionPort = connectionPort;
        this.discoveryPort = discoveryPort;
        this.eventListeners = new ArrayList<ClientEventListener>();
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
    
    public void stopListeningForBroadcasts() {
        discoveryListenerThread.interrupt();
    }

    public void startListeningForConnections() throws IOException {
        serverThread = new ServerThread(connectionPort);
        serverThread.addEventListener(this);
        serverThread.start();
    }
    
    public void stopListeningForConnections() {
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
                peers.remove(peer);
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public void onClientDiscovered(InetAddress address) {
        try {
            Peer peer = new Peer(address);
            Connection connection = new Connection(peer, address, connectionPort);
            connection.addEventListener(this);
            connection.open();
            peer.setConnection(connection);
            peers.add(peer);
            connectionEstablished(peer);
        } catch (IOException e) {
            errorOccurred(ClientEventListener.ErrorType.Connection, "An IO error occurred while establishing a connection to '" +
                address.toString() + "'.");
        }
    }

    @Override
    public void onIOError(IOException exception) {
        errorOccurred(ClientEventListener.ErrorType.IO, exception.getMessage());
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
        for (ClientEventListener eventListener : eventListeners) {
            eventListener.onNicknameChanged(connection.getPeer(), oldNickname);
        }
    }

    @Override
    public void onConnectionAccepted(Socket socket) {
        try {
            Peer peer = new Peer(socket.getInetAddress());
            Connection connection = new Connection(peer, socket);
            connection.addEventListener(this);
            connection.open();
            peer.setConnection(connection);
            peers.add(peer);
            connectionEstablished(peer);
        } catch (IOException e) {
            errorOccurred(ClientEventListener.ErrorType.Connection, "An IO error occurred while establishing a connection to '" +
                    socket.getInetAddress().toString() + "'.");
        }
    }
    
    private void errorOccurred(ClientEventListener.ErrorType type, String message) {
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
