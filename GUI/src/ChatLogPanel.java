import net.miginfocom.swing.MigLayout;
import p2pchatroom.core.Peer;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Calendar;

public class ChatLogPanel {
    private final MigLayout layout;
    private boolean isFirstMessage = true;

    public JPanel getPanel() {
        return panel;
    }

    private JPanel panel;

    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    private JScrollPane scrollPane;

    public ChatLogPanel() {
        layout = new MigLayout("fillx, aligny bottom, wrap 3", "[align right]10[grow]10[align right]", "5[align top]5[]");
        panel = new JPanel(layout);
        scrollPane = new JScrollPane(panel);

        panel.setLayout(layout);
        panel.setBackground(Color.WHITE);
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
        // The value  represents the very start of the viewable area, whereas the maximum represents the very end
        //   of the entire pane.
        JScrollBar scrollBar = scrollPane.getVerticalScrollBar();
        int height = scrollBar.getHeight();
        int lowerCurrent = scrollBar.getValue() + height;
        int maximum = scrollBar.getMaximum();
        final boolean adjustScrollPane = lowerCurrent == maximum || lowerCurrent == 0 ? true : false;
        
        if (!isFirstMessage && useSeparator) {
            JSeparator separator = new JSeparator(JSeparator.HORIZONTAL);
            separator.setForeground(Color.getHSBColor((float)0, (float)0, (float)0.9));
            panel.add(separator, "span 3, grow");
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

        panel.add(senderLabel);
        panel.add(messageLabel);
        panel.add(timestampLabel);
        
        panel.updateUI();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (adjustScrollPane) {
                    scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
                }
            }
        });
    }
    
    private void addLine(String sender, String message, Color senderForeground, Color messageForeground) {
        addLine(sender, message, senderForeground, messageForeground, true);
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
}
