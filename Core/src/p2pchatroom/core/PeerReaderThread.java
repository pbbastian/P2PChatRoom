package p2pchatroom.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class PeerReaderThread extends Thread {
    private Socket socket;
    private Peer peer;
    private BufferedReader reader;
    
    public PeerReaderThread(Socket socket, Peer peer) throws IOException {
        this.socket = socket;
        this.peer = peer;
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }
    
    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                String message = reader.readLine();
                peer.onMessageReceived(message);
            } catch (IOException e) {
                if (!isInterrupted())
                    e.printStackTrace();
                else
                    System.out.println("PeerReaderThread interrupted");
            }
        }
    }
}
