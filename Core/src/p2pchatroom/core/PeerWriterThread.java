package p2pchatroom.core;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

class PeerWriterThread extends Thread {
    private final Socket socket;
    private final ArrayList<String> queue;
    
    public PeerWriterThread(Socket socket) {
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
                    if (writer != null) {
                        writer.println(message);
                    }
                }
                queue.clear();
                if (writer != null) {
                    writer.flush();
                }
            }
            try {
                sleep(100);
            } catch (InterruptedException e) {

            }
        }
    }
}
