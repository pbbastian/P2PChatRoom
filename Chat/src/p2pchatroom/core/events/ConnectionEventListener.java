package p2pchatroom.core.events;

import p2pchatroom.core.Connection;

public interface ConnectionEventListener {
    void onMessageReceived(Connection connection, String message);
    void onPrivateMessageReceived(Connection connection, String message);
    void onNicknameReceived(Connection connection, String nickname);
    void onConnectionClosed(Connection connection);
}
