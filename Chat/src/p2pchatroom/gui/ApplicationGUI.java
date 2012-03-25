package p2pchatroom.gui;

import net.miginfocom.swing.MigLayout;
import p2pchatroom.core.Client;
import p2pchatroom.core.ErrorType;
import p2pchatroom.core.Peer;
import p2pchatroom.core.events.ClientEventListener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.BindException;
import java.util.ArrayList;

public class ApplicationGUI implements ActionListener, ClientEventListener {
    //Core-related
    private static final String programVersion = "P2P LAN Chat v1.0";
    private Client client;
    private final String nickname;
    private final ArrayList<Peer> peers;
    private final String[] commands = {
            "help",
            "userlist",
            "nick",
            "@",
            "setports"};
    private final String[] descriptions = {
            "List available commands",
            "Lists online users",
            "Set new nickname",
            "Send private message to user",
            "Set the ports to connect to",
    };

    //GUI-related
    private final ChatLogPanel chatLog;
    private final JTextField chatInput;
    private final JList<String> userList;
    private final JButton send;
    
    private ApplicationGUI() {
        ///////////////////////////////////////////////////////////////GUI START
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JFrame frame = new JFrame(programVersion);
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

        JScrollPane scrollPane = new JScrollPane(userList);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        panel.add(chatLog.getScrollPane(), "cell 1 0 3 1, grow, push, gapx 8 10, gapy 10 0");
        panel.add(scrollPane, "dock east, gapx 0 10, gapy 10 10, width 90!");
        panel.add(chatInput, "cell 1 3 2 1, growx, pushx, gapx 10 0, gapy 0 10, h 26!");
        panel.add(send, "cell 3 3, gapx 0 10");

        frame.pack();
        frame.setSize(640, 480);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        
        this.nickname = JOptionPane.showInputDialog(null, "Nickname");
        
        chatInput.requestFocus();
        ////////////////////////////////////////////////////////////////GUI END

        startClient(9010, 9011);
        this.peers = new ArrayList<Peer>();
        updateUserList();
    }
    private void startClient(int discoveryPort, int connectionPort) {
        try {
            client = new Client(discoveryPort, connectionPort,this.nickname, programVersion);
            client.addEventListener(this);
            client.startListeningForBroadcasts();
            client.startListeningForConnections();
            client.broadcast();
        } catch (BindException e) {
            discoveryPort = discoveryPort+2;
            connectionPort = connectionPort+2;
            chatLog.addErrorMessage("Ports already in use, another instance of this program may be running");
            chatLog.addSystemMessage(String.format("Switching to ports %d and %d", discoveryPort, connectionPort));
            startClient(discoveryPort,connectionPort);
        } catch (IOException e) {
            chatLog.addErrorMessage("Error occured: " + e);
        }
    }
    private void updateUserList() {
        DefaultListModel<String> model = new DefaultListModel<String>();
        for(Peer list : client.getPeers()) {
            model.addElement("@"+list.getNickname());
        }
        userList.setModel(model);
    }
    private void interpretInput(String input) {
        if (input.startsWith("/")) { //If Input is a command
            boolean found = false;
            String[] parts = input.split(" ", 3);
            String inputtedCommand = parts[0].substring(1);
            /*input = input.substring(1,input.length());
            Scanner inputScan = new Scanner(input);
            int keywordSize = 3;
            String keyword[] = new String[keywordSize];
            for (int i = 0; i < keywordSize; i++) {
                if(inputScan.hasNext()) {
                    keyword[i] = inputScan.next();
                }
            }*/
            for(String command : commands) {
                if(command.equals(inputtedCommand)) {
                    executeCommand(command, parts[1], parts[2]);
                    found = true;
                    break;
                }
            }
            if(!found) {
                chatLog.addErrorMessage("Invalid command, type /help for a list of commands");
            }
        } else if (input.startsWith("@")) { // If Input is a Private Message
            String[] parts = input.split(" ", 2);
            String nickname = parts[0].substring(1);
            String message = parts[1];
            boolean found = false;
            for(Peer peer : peers) {
                if(nickname.equals(peer.getNickname())) {
                    client.privateMessage(peer.getNickname(), message);
                    found = true;
                    break;
                }
            }
            if(!found) {
                chatLog.addErrorMessage("Could not find a user by that name");
            }
        } else { //Message is interpreted as a regular message now
            client.message(input);
        }
    }
    
    private void executeCommand(String command, String parameter1, String parameter2) {
        if(command.equals("help")) {
            
        } else if(command.equals("nick")){
            client.setNickname(parameter1);

        } else if(command.equals("userlist")) {
            ArrayList<Peer> peers = new ArrayList<Peer>(client.getPeers());
            chatLog.addPeerList(peers);

        } else if(command.equals("setports")) {
            if (parameter1 != null || parameter2 != null) {
                startClient(Integer.parseInt(parameter1), Integer.parseInt(parameter2));
            } else {
                chatLog.addErrorMessage("Invalid port numbers, choose an integer between 1024 and 49151");
            }
        }
    }
    
    
    
    
    
    
    public static void main(String[] args) {
        ApplicationGUI applicationGUI = new ApplicationGUI();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == send || e.getSource() == chatInput) {
            String input = chatInput.getText();
            if(!chatInput.getText().equals("")) {
                chatInput.setText("");
                interpretInput(input);
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
