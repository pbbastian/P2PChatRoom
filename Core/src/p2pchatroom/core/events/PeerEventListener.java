package p2pchatroom.core.events;

import p2pchatroom.core.Peer;

public interface PeerEventListener {
    void onMessageReceived(Peer peer, String message);
    void onPrivateMessageReceived(Peer peer, String message);
    void onNicknameReceived(Peer peer, String nickname);
}
