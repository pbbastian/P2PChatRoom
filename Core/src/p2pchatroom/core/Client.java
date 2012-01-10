/* This class handles the peers and provide the general API for any interface with the user.
 * 
 */

package p2pchatroom.core;

import p2pchatroom.core.events.DiscoveryEventListener;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;

public class Client implements DiscoveryEventListener {

    private String nickname;
    private DiscoveryBroadcaster discoveryBroadcaster;
    private DiscoveryListenerThread discoveryListenerThread;
    private ServerThread serverThread;
    private ArrayList<Peer> peers;

    public Client(InetAddress group) {
        peers = new ArrayList<Peer>();
        discoveryBroadcaster = new DiscoveryBroadcaster()
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
    
    public String getNickname() {
        return this.nickname;
    }
    
    public void setNickname(String nickname) {
        this.nickname = nickname;
        for (Peer peer : peers) {
            peer.getConnection().sendNickname(nickname);
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
    
    public ArrayList<Peer> getPeers() {
        return this.peers;
    }

    @Override
    public void onClientDiscovered(InetAddress address) {

    }

    @Override
    public void onIOError(IOException exception) {

    }
}
