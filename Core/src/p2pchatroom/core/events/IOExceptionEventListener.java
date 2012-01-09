package p2pchatroom.core.events;

import java.io.IOException;

public interface IOExceptionEventListener {
    void onIOError(IOException exception);
}
