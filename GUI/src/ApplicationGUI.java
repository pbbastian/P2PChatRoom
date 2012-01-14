import net.miginfocom.swing.MigLayout;
import p2pchatroom.core.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.UnknownHostException;
import java.security.PublicKey;

public class ApplicationGUI implements ActionListener{
    private Client client;
    private String nickname;
    
    private JTextArea chatLog;
    private JTextArea userList;
    private JTextField chatInput;
    private JButton send;
    
    public ApplicationGUI() {
        ///////////////////////////////////////////////////////////////GUI START
        JFrame frame = new JFrame("TEST");
        MigLayout layout = new MigLayout("fill, wrap 3");
        JPanel panel = new JPanel(layout);
        frame.getContentPane().add(panel);

        chatLog= new JTextArea("CHAT LOG PLACEHOLDER");
        userList = new JTextArea("USERLIST PLACEHOLDER");
        chatInput = new JTextField("CHAT INPUT");
        send = new JButton("Send");
        send.addActionListener(this);

        panel.add(chatLog, "cell 1 0 3 1, grow, push, gapx 8 10, gapy 10 0");
        panel.add(userList, "dock east, gapx 0 10, gapy 10 10, width 90!");
        panel.add(chatInput, "cell 1 3 2 1, growx, pushx, gapx 10 0, gapy 0 10, h 26!");
        panel.add(send, "cell 3 3, gapx 0 10");

        frame.pack();
        frame.setSize(640, 480);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.nickname = JOptionPane.showInputDialog(null, "Nickname");
        ////////////////////////////////////////////////////////////////GUI END

        try {
            client = new Client("238.255.255.255", 9010, 9011,this.nickname);
        } catch (UnknownHostException e) {
            System.out.println("Error occured: e");
        }
    }
    public static void main(String[] args) {
        ApplicationGUI applicationGUI = new ApplicationGUI();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == send) {
            
        }
    }
}
