package p2pchatroom.core;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class PeerWriterThread extends Thread {
    private Socket socket;
    private ArrayList<String> queue;
    
    public PeerWriterThread(Socket socket) throws IOException {
        this.socket = socket;
        this.queue = new ArrayList<String>();
    }
    
    public void queueMessage(String message) {
        queue.add(message);
    }

    @Override
    public void run() {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            // TODO: Raise event
        }
        while (!isInterrupted()) {
            if (queue.size() > 0) {
                for (String message : queue) {
                    writer.println(message);
                }
                queue.clear();
                writer.flush();
            }
            try {
                sleep(100);
            } catch (InterruptedException e) {

            }
        }
    }
}
