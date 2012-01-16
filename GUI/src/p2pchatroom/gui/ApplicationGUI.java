package p2pchatroom.gui;

import net.miginfocom.swing.MigLayout;
import p2pchatroom.core.Client;
import p2pchatroom.core.ErrorType;
import p2pchatroom.core.Peer;
import p2pchatroom.core.events.ClientEventListener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.IOException;
import java.net.BindException;
import java.util.ArrayList;
import java.util.Scanner;

public class ApplicationGUI implements ActionListener, ClientEventListener {
    //Core-related
    private static final String programVersion = "P2P LAN Chat v1.0";
    private Client client;
    private final String nickname;
    private final ArrayList<Peer> peers;
    private final String[] commands = {"help","commands","userlist","users","list","nick","nickname","@","setports"};

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
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); //Makes use of Windows 7 looks
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
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

        JScrollPane scrollPane = new JScrollPane(chatLog);
        scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            public void adjustmentValueChanged(AdjustmentEvent e) {
                e.getAdjustable().setValue(e.getAdjustable().getMaximum());
            }});
        
        chatInput.addActionListener(this);
        send.addActionListener(this);

        userList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        userList.setLayoutOrientation(JList.VERTICAL);
        userList.setVisibleRowCount(-1);

        panel.add(scrollPane, "cell 1 0 3 1, grow, push, gapx 8 10, gapy 10 0");
        panel.add(userList, "dock east, gapx 0 10, gapy 10 10, width 90!");
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
        this.peers = new ArrayList<Peer>(client.getPeers());

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
            input = input.substring(1,input.length());
            Scanner inputScan = new Scanner(input);
            int keywordSize = 3;
            String keyword[] = new String[keywordSize];
            for (int i = 0; i < keywordSize; i++) {
                if(inputScan.hasNext()) {
                    keyword[i] = inputScan.next();
                }
            }
            for(String command : commands) {
                if(command.equals(keyword[0])) {
                    executeCommand(command, keyword[1], keyword[2]);
                    found = true;
                }
            }
            if(!found) {
                chatLog.addErrorMessage("Invalid command, type /help for a list of commands");
            }
        } else if (input.startsWith("@")) { // If Input is a Private Message
            boolean found = false;
            for(Peer peer : peers) {
                if(input.substring(1,(peer.getNickname().length())).equals(peer.getNickname())) {
                    String message = input.substring(peer.getNickname().length()+2,input.length());
                    chatLog.addPrivateMessage(peer, message);
                    client.privateMessage(peer.getNickname(), message);
                    found = true;
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
        if(command.equals("help") || command.equals("commands")) {
            chatLog.addSystemMessage("This command is currently not working");

        } else if(command.equals("nick") || command.equals("nickname")){
            chatLog.addNicknameChangeMessage(new Peer(null, parameter1), client.getNickname());
            client.setNickname(parameter1);

        } else if(command.equals("userlist") || command.equals("users") || command.equals("list")) {
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
                chatLog.addMessage(new Peer(null, client.getNickname()), input);
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
