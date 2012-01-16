import net.miginfocom.swing.MigLayout;
import p2pchatroom.core.Peer;

import javax.swing.*;
import java.awt.*;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Calendar;

public class ChatLogPanel extends JPanel {
    private final MigLayout layout;
    private boolean isFirstMessage = true;

    public ChatLogPanel() {
        layout = new MigLayout("fillx, aligny bottom, wrap 3", "[align right]10[grow]10[align right]", "5[align top]5[]");
        setLayout(layout);
        setBackground(Color.WHITE);
    }

    public void addMessage(Peer peer, String message) {
        addLine("<html><p><b>" + peer.getNickname() + "</b></p></html>", "<html><p>" + message + "</p></html>");
    }
    
    public void addPrivateMessage(Peer peer, String message) {
        addLine("<html><p><b>" + peer.getNickname() + "</b></p></html>", "<html><p>" + message + "</p></html>", null, Color.getHSBColor((float) 0.3, (float) 1.0, (float) 0.5));
    }
    
    public void addJoinMessage(Peer peer) {
        addLine("*", String.format("<html><b>%s</b> has joined.</html>", peer.getNickname()), null, Color.getHSBColor((float) 0.6, (float) 1.0, (float) 0.5));
    }

    public void addLeftMessage(Peer peer) {
        addLine("*", String.format("<html><b>%s</b> has left.</html>", peer.getNickname()), null, Color.getHSBColor((float)0.6, (float)1.0, (float)0.5));
    }
    
    public void addSystemMessage(String message) {
        addLine("*", message, null, Color.getHSBColor((float)0.6, (float)1.0, (float)0.5));
    }

    public void addErrorMessage(String message) {
        addLine("*", message, null, Color.getHSBColor((float)0.6, (float)1.0, (float)0.5));
    }
    
    public void addNicknameChangeMessage(Peer peer, String oldNickname) {
        addLine("*", String.format("<html><b>%s</b> has changed nickname to <b>%s</b>.</html>", oldNickname, peer.getNickname()), null, Color.getHSBColor((float)0.6, (float)1.0, (float)0.5));
    }
    
    public void addPeerList(ArrayList<Peer> peers) {
        addLine("*", "Online users:", null, Color.getHSBColor((float)0.6, (float)1.0, (float)0.5));
        for (Peer peer : peers) {
            addLine("", String.format("<html><b>%s</b> at <b>%s</b>", peer.getNickname(), peer.getAddress()), null, Color.getHSBColor((float)0.6, (float)1.0, (float)0.5), false);
        }
    }
    
    private void addLine(String sender, String message, Color senderForeground, Color messageForeground, boolean useSeparator) {
        if (!isFirstMessage && useSeparator) {
            JSeparator separator = new JSeparator(JSeparator.HORIZONTAL);
            separator.setForeground(Color.getHSBColor((float)0, (float)0, (float)0.9));
            add(separator, "span 3, grow");
        } else if (isFirstMessage) {
            isFirstMessage = false;
        }

        JLabel senderLabel = new JLabel(sender);
        JLabel messageLabel = new JLabel(message);
        JLabel timestampLabel = new JLabel(String.format("%1$tH:%1$tM:%1$tS", Calendar.getInstance()));
        timestampLabel.setForeground(Color.getHSBColor((float)0, (float)0, (float)0.5));

        if (senderForeground != null) {
            senderLabel.setForeground(senderForeground);
        }
        if (messageForeground != null) {
            messageLabel.setForeground(messageForeground);
        }

        add(senderLabel);
        add(messageLabel);
        add(timestampLabel);

        updateUI();
    }
    
    private void addLine(String sender, String message, Color senderForeground, Color messageForeground) {
        addLine(sender, message, null, messageForeground, true);
    }

    private void addLine(String sender, String message, Color senderForeground) {
        addLine(sender, message, senderForeground, null, true);
    }

    private void addLine(String sender, String message, boolean useSeparator) {
        addLine(sender, message, null, null, useSeparator);
    }

    private void addLine(String sender, String message) {
        addLine(sender, message, null, null, true);
    }
    
    public static void main(String[] args) throws Exception {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JFrame frame = new JFrame("Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ChatLogPanel chat = new ChatLogPanel();
        chat.addMessage(new Peer(null, "izym"), "Dette er en besked!");
        chat.addMessage(new Peer(null, "Systemic33"), "Dette er en besked!");
        chat.addPrivateMessage(new Peer(null, "Systemic33"), "Dette er en besked! asdf asdf asdf asdf asdf asdf");
        chat.addMessage(new Peer(null, "izym"), "Dette er en besked!");
        ArrayList<Peer> peers = new ArrayList<Peer>(5);
        for (int i = 0; i < 5; i++) {
            peers.add(new Peer(InetAddress.getByName("192.168.1.19"), "izym"+i));
        }
        chat.addPeerList(peers);
        frame.setContentPane(chat);
        frame.setSize(500, 500);
        frame.setVisible(true);
        Thread.sleep(200);
        chat.addMessage(new Peer(null, "izym"), "Dette er en besked!");
        Thread.sleep(200);
        chat.addPrivateMessage(new Peer(null, "Systemic33"), "Dette er en besked! asdf asdf asdf asdf asdf asdf");
        Thread.sleep(200);
        chat.addJoinMessage(new Peer(null, "Systemic33"));
        Thread.sleep(200);
        chat.addMessage(new Peer(null, "izym"), "Dette er en besked!");
    }
}
