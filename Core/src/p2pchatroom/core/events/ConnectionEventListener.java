package p2pchatroom.core.events;

import p2pchatroom.core.ConnectionTempName;

public interface ConnectionEventListener {
    void onMessageReceived(ConnectionTempName connection, String message);
    void onPrivateMessageReceived(ConnectionTempName connection, String message);
    void onNicknameReceived(ConnectionTempName connection, String nickname);
    void onConnectionClosed(ConnectionTempName connection);
}
