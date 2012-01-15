import net.miginfocom.swing.MigLayout;
import p2pchatroom.core.Client;
import p2pchatroom.core.Peer;
import p2pchatroom.core.events.ClientEventListener;
import p2pchatroom.core.events.ErrorType;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ApplicationGUI implements ActionListener, ClientEventListener{
    private Client client;
    private String nickname;
    private ArrayList<Peer> peers;
    
    private ChatLogPanel chatLog;
    private JTextField chatInput;
    private JList<String> userList;
    private JButton send;
    
    public ApplicationGUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        ///////////////////////////////////////////////////////////////GUI START
        JFrame frame = new JFrame("TEST");
        MigLayout layout = new MigLayout("fill, wrap 3");
        JPanel panel = new JPanel(layout);
        frame.getContentPane().add(panel);

        chatLog= new ChatLogPanel();
        userList = new JList<String>();
        chatInput = new JTextField();
        send = new JButton("Send");
        
        chatInput.addActionListener(this);
        send.addActionListener(this);

        userList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        userList.setLayoutOrientation(JList.VERTICAL);
        userList.setVisibleRowCount(-1);

        panel.add(chatLog, "cell 1 0 3 1, grow, push, gapx 8 10, gapy 10 0");
        panel.add(userList, "dock east, gapx 0 10, gapy 10 10, width 90!");
        panel.add(chatInput, "cell 1 3 2 1, growx, pushx, gapx 10 0, gapy 0 10, h 26!");
        panel.add(send, "cell 3 3, gapx 0 10");

        frame.pack();
        frame.setSize(640, 480);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        this.nickname = JOptionPane.showInputDialog(null, "Nickname");
        ////////////////////////////////////////////////////////////////GUI END

        try {
            client = new Client("238.255.255.255", 9010, 9011,this.nickname);
            client.addEventListener(this);
            client.startListeningForBroadcasts();
            client.startListeningForConnections();
            client.broadcast();
        } catch (Exception e) {
            chatLog.addErrorMessage("Error occured: " + e);
        }
        
        this.peers = new ArrayList<Peer>(client.getPeers());

    }
    private void updateUserList() {
        DefaultListModel<String> model = new DefaultListModel<String>();
        for(Peer list : client.getPeers()) {
            model.addElement("@"+list.getNickname());
        }
        userList.setModel(model);
    }

    private void analyseInput(String textInput) {
        if(textInput.substring(0,1).equalsIgnoreCase("/") || textInput.substring(0,1).equalsIgnoreCase("@")) {
            //Checks input, to see if user was trying to type a command
            if(textInput.equalsIgnoreCase("/help") || textInput.equalsIgnoreCase("/?")
                    || textInput.equalsIgnoreCase("/commands")) {
                //Reports back a list of commands available to the user
                String[] commandList = new String[] {
                        "message",
                        "/users",
                        "@user <message>",
                        "/nick <nickname>"
                };
                String[] descriptionList = new String[] {
                        "Send message to chat",
                        "Lists online users",
                        "Send private message to user",
                        "Set new nickname"
                };
                for (int i = 0; i < commandList.length; i++) {
                    //TODO: Add the following text output to chatlog.
                    //System.out.printf("%-25s - %s\n",commandList[i], descriptionList[i]);
                }

            } else if(textInput.equalsIgnoreCase("/users")) {
                //Lists users online
                ArrayList<Peer> peers = new ArrayList<Peer>(client.getPeers());
                chatLog.addPeerList(peers);

            } else if(textInput.substring(0,1).equalsIgnoreCase("@")) {
                //Sends a private message
                String[] stringParts = textInput.split(" ", 2);
                String user = stringParts[0].replace("@", "");
                if(user.equalsIgnoreCase(client.getNickname()) || user.equalsIgnoreCase(client.getNickname())) {
                    JOptionPane.showMessageDialog(null, "You can't send a Private Message to yourself");
                } else {
                    String message = stringParts[1];
                    // TODO: Simplify this in some way
                    chatLog.addPrivateMessage(new Peer(null, client.getNickname()), message);
                    client.privateMessage(user, message);
                }

            } else if(textInput.substring(0,5).equalsIgnoreCase("/nick")) {
                //Sets nickname of client
                String[] stringParts = textInput.split(" ");
                String nickname = stringParts[1];
                chatLog.addNicknameChangeMessage(new Peer(null, client.getNickname()), nickname);
                client.setNickname(nickname);

            } else {
                chatLog.addErrorMessage("Invalid command, type /help for a list of commands");
            }
        } else {
            chatLog.addMessage(new Peer(null, client.getNickname()), textInput);
            client.message(textInput);
        }
    }

    public static void main(String[] args) {
        ApplicationGUI applicationGUI = new ApplicationGUI();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == send || e.getSource() == chatInput) {
            if(!chatInput.getText().equals(null)) {
                analyseInput(chatInput.getText());
            }
        }
    }

    @Override
    public void onMessageReceived(Peer peer, String message) {
        chatLog.addMessage(peer, message);
    }

    @Override
    public void onPrivateMessageReceived(Peer peer, String message) {
        chatLog.addPrivateMessage(peer, message);
    }

    @Override
    public void onNicknameChanged(Peer peer, String oldNickname) {
        chatLog.addNicknameChangeMessage(peer, oldNickname);
        updateUserList();
    }

    @Override
    public void onErrorOccurred(ErrorType type, String message) {
        chatLog.addErrorMessage(message);
    }

    @Override
    public void onConnectionEstablished(Peer peer) {
        chatLog.addJoinMessage(peer);
        updateUserList();
    }

    @Override
    public void onConnectionClosed(Peer peer) {
        chatLog.addLeftMessage(peer);
        updateUserList();
    }
}
