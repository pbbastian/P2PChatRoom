import net.miginfocom.swing.MigLayout;
import p2pchatroom.core.Peer;

import javax.swing.*;
import java.awt.*;

public class ChatLogPanel extends JPanel {
    private MigLayout layout;
    private boolean isFirstMessage = true;

    public ChatLogPanel() {
        layout = new MigLayout("fillx, aligny bottom, wrap 2", "[align right]10[grow]", "5[align top]5[]");
        setLayout(layout);
        setBackground(Color.WHITE);
    }

    public void addMessage(Peer peer, String message) {
        addLine("<html><p><b>" + peer.getNickname() + "</b></p></html>", "<html><p>" + message + "</p></html>", null, null);
    }
    
    public void addPrivateMessage(Peer peer, String message) {
        addLine("<html><p><b>" + peer.getNickname() + "</b></p></html>", "<html><p>" + message + "</p></html>", null, Color.getHSBColor((float)0.3, (float)1.0, (float)0.5));
    }
    
    public void addJoinMessage(Peer peer) {
        addLine("*", "<html><b>" + peer.getNickname() + "</b> has joined.</html>", null, Color.getHSBColor((float)0.6, (float)1.0, (float)0.5));
    }

    public void addLeftMessage(Peer peer) {
        addLine("*", "<html><b>" + peer.getNickname() + "</b> has left.</html>", null, Color.getHSBColor((float)0.6, (float)1.0, (float)0.5));
    }
    
    private void addLine(String sender, String message, Color senderForeground, Color messageForeground) {
        if (!isFirstMessage) {
            JSeparator separator = new JSeparator(JSeparator.HORIZONTAL);
            separator.setForeground(Color.getHSBColor((float)0, (float)0, (float)0.9));
            add(separator, "span 2, grow");
        } else {
            isFirstMessage = false;
        }

        JLabel senderLabel = new JLabel(sender);
        JLabel messageLabel = new JLabel(message);

        if (senderForeground != null) {
            senderLabel.setForeground(senderForeground);
        }
        if (messageForeground != null) {
            messageLabel.setForeground(messageForeground);
        }

        add(senderLabel);
        add(messageLabel);

        updateUI();
    }
    
    public static void main(String[] args) throws InterruptedException {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        JFrame frame = new JFrame("Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ChatLogPanel chat = new ChatLogPanel();
        chat.addMessage(new Peer(null, "izym"), "Dette er en besked!");
        chat.addMessage(new Peer(null, "Systemic33"), "Dette er en besked!");
        chat.addPrivateMessage(new Peer(null, "Systemic33"), "Dette er en besked! asdf asdf asdf asdf asdf asdf");
        chat.addMessage(new Peer(null, "izym"), "Dette er en besked!");
        frame.setContentPane(chat);
        frame.setSize(500, 500);
        frame.setVisible(true);
        Thread.sleep(1000);
        chat.addJoinMessage(new Peer(null, "Systemic33"));
        chat.addPrivateMessage(new Peer(null, "Systemic33"), "Dette er en besked! asdf asdf asdf asdf asdf asdf");
        Thread.sleep(1000);
        chat.addMessage(new Peer(null, "izym"), "Dette er en besked!");
    }
}
