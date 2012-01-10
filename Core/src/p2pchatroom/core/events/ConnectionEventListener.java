package p2pchatroom.core.events;

import p2pchatroom.core.Connection;

public interface ConnectionEventListener {
    void onMessageReceived(Connection peer, String message);
    void onPrivateMessageReceived(Connection peer, String message);
    void onNicknameReceived(Connection connection, String nickname);
}
