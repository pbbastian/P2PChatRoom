package p2pchatroom.core.events;

import p2pchatroom.core.ErrorType;
import p2pchatroom.core.Peer;

public interface ClientEventListener {
    void onMessageReceived(Peer peer, String message);
    void onPrivateMessageReceived(Peer peer, String message);
    void onNicknameChanged(Peer peer, String oldNickname);
    void onErrorOccurred(ErrorType type, String message);
    void onConnectionEstablished(Peer peer);
    void onConnectionClosed(Peer peer);
}
