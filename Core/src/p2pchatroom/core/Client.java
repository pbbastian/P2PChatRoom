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
    ArrayList<Peer> peers;

    public Client() {
        peers = new ArrayList<Peer>();
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
    
    public void setNickname(String nickname) {
        this.nickname = nickname;
        for (Peer peer : peers) {
            peer.getConnection().sendNickname(nickname);
        }
    }
    
    public void closeConnections() throws IOException {
        for (Peer peer : peers) {
            peer.getConnection().close();
            peers.remove(peer);
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
