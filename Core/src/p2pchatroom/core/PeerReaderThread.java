package p2pchatroom.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

class PeerReaderThread extends Thread {
    private final Socket socket;
    private final Connection peerConnection;
    private BufferedReader reader;
    
    public PeerReaderThread(Socket socket, Connection peerConnection) throws IOException {
        this.socket = socket;
        this.peerConnection = peerConnection;
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }
    
    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                String message = reader.readLine();
                peerConnection.onMessageReceived(message);
            } catch (IOException e) {
                break;
            }
        }
    }
}
