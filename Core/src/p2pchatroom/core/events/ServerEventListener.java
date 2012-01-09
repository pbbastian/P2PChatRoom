package p2pchatroom.core.events;

import java.net.Socket;

public interface ServerEventListener extends IOExceptionEventListener {
    void onConnectionAccepted(Socket socket);
}
