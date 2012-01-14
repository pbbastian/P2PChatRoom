import net.miginfocom.swing.MigLayout;
import p2pchatroom.core.Peer;

import javax.swing.*;
import java.awt.*;

public class ChatLogPanel extends JPanel {
    private MigLayout layout;

    public ChatLogPanel() {
        layout = new MigLayout("fillx, aligny bottom, wrap 2", "[align right]10[fill]", "[]10[]");
        setLayout(layout);
        setBackground(Color.WHITE);
        
        addMessage(new Peer(null, "izym"), "Dette er en besked!");
        addMessage(new Peer(null, "Systemic33"), "Dette er en besked!");
        addMessage(new Peer(null, "Systemic33"), "Dette er en besked!asdfasdf");
        addMessage(new Peer(null, "izym"), "Dette er en besked!");
    }

    public void addMessage(Peer peer, String message) {
        JLabel usernameLabel = new JLabel("<html><b>" + peer.getNickname() + "</b>");
        JLabel messageLabel = new JLabel(message);
        add(usernameLabel);
        add(messageLabel);
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InstantiationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IllegalAccessException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        JFrame frame = new JFrame("Test");
        ChatLogPanel chat = new ChatLogPanel();
        frame.setContentPane(chat);
        frame.pack();
        frame.setVisible(true);
    }
}
