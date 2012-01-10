/* This class handles the peers and provide the general API for any interface with the user.
 * 
 */

package p2pchatroom.core;

import java.util.ArrayList;

public class Client {
    ArrayList<Peer> userList = new ArrayList<Peer>();

    public Client() {

    }
    public void sendMessage(String message) {
        
    }
    public void sendPrivateMessage(String user, String message) {
        
    }
    public void setNickname(String nickname) {
    }
    public void closeConnections() {
    }
    public void listUsers() {

    }
}
