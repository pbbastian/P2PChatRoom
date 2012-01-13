/* This class handles the peers and provide the general API for any interface with the user.
 * 
 */

package p2pchatroom.core;

import p2pchatroom.core.events.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Client implements DiscoveryEventListener, ConnectionEventListener, ServerEventListener, IOExceptionEventListener {
    private static final String clientIdentifier = "P2PChatRoom 0.1";
    private DiscoveryListenerThread discoveryListenerThread;
    private DiscoveryBroadcasterThread discoveryBroadcasterThread;
    private ServerThread serverThread;
    private ArrayList<Peer> peers;
    private String nickname;
    private InetAddress group;
    private int discoveryPort;
    private int connectionPort;
    private ArrayList<ClientEventListener> eventListeners;

    public Client() throws UnknownHostException {
        this.peers = new ArrayList<Peer>();
        this.group = InetAddress.getByName("239.255.255.255");
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
    
    public void broadcast() {
        try {
            discoveryBroadcasterThread = new DiscoveryBroadcasterThread(group, discoveryPort, clientIdentifier);
            discoveryBroadcasterThread.start();
        } catch (IOException e) {
            errorOccurred(ClientEventListener.ErrorType.Broadcast, "An IO error occurred while broadcasting.");
        }
    }

    public void listen() {
        try {
            discoveryListenerThread = new DiscoveryListenerThread(group,discoveryPort,clientIdentifier);
            discoveryListenerThread.start();
        } catch (IOException e) {
            System.out.println("Error occured: e");
        }
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
            Connection connection = new Connection(address, connectionPort);
            connection.addEventListener(this);
            connection.open();
            peer.setConnection(connection);
            peers.add(peer);
        } catch (IOException e) {
            
        }
    }

    @Override
    public void onIOError(IOException exception) {

    }

    @Override
    public void onMessageReceived(Connection peer, String message) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onPrivateMessageReceived(Connection peer, String message) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onNicknameReceived(Connection connection, String nickname) {
        for (Peer peer : peers) {
            if (peer.getConnection() == connection) {
                peer.setNickname(nickname);
                return;
            }
        }
    }

    @Override
    public void onConnectionAccepted(Socket socket) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
    
    private void errorOccurred(ClientEventListener.ErrorType type, String message) {
        for (ClientEventListener eventListener : eventListeners) {
            eventListener.onErrorOccurred(type, message);
        }
    }
}
