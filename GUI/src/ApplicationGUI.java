import net.miginfocom.swing.MigLayout;
import p2pchatroom.core.*;
import p2pchatroom.core.events.ClientEventListener;
import p2pchatroom.core.events.ErrorType;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.UnknownHostException;
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
        ///////////////////////////////////////////////////////////////GUI START
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

        JFrame frame = new JFrame("TEST");
        MigLayout layout = new MigLayout("fill, wrap 3");
        JPanel panel = new JPanel(layout);
        frame.getContentPane().add(panel);

        chatLog= new ChatLogPanel();
        userList = new JList<String>();
        chatInput = new JTextField("CHAT INPUT");
        send = new JButton("Send");
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
        } catch (UnknownHostException e) {
            System.out.println("Error occured: "+e);
        }
        this.peers = client.getPeers();

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
                ArrayList<Peer> userlist = client.getPeers();
                System.out.println("ONLINE USERS:");
                for(Peer peer : userlist) {
                    //TODO: Add the following text output to chatlog.
                    //System.out.printf("@%-15s %s\n", peer.getNickname(), peer.getAddress().getHostAddress());
                }

            } else if(textInput.substring(0,1).equalsIgnoreCase("@")) {
                //Sends a private message
                String[] stringParts = textInput.split(" ", 2);
                String user = stringParts[0].replace("@", "");
                if(user.equalsIgnoreCase(client.getNickname()) || user.equalsIgnoreCase(client.getNickname())) {
                    JOptionPane.showMessageDialog(null, "You can't send a Private Message to yourself");
                } else {
                    //TODO: Add the sent PM to chatlog
                    String message = stringParts[1];
                    client.privateMessage(user, message);
                }

            } else if(textInput.substring(0,5).equalsIgnoreCase("/nick")) {
                //Sets nickname of client
                String[] stringParts = textInput.split(" ");
                String nickname = stringParts[1];
                client.setNickname(nickname);
                //TODO: Notify chatlog of name change
                //System.out.printf("Nickname set to %s\n", nickname);

            } else {
                //Reached when command was not found
                //TODO: Notify chatlog of an uknown command
                //System.out.println("Invalid command, type /help for a list of commands");
            }
        } else {
            //TODO: Add message to chatlog
            client.message(textInput);
        }
    }

    public static void main(String[] args) {
        ApplicationGUI applicationGUI = new ApplicationGUI();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == send) {
            if(!chatInput.getText().equals(null)) {
                analyseInput(chatInput.getText());
            }
        }
    }

    @Override
    public void onMessageReceived(Peer peer, String message) {
        //TODO: Notify chatlog
    }

    @Override
    public void onPrivateMessageReceived(Peer peer, String message) {
        //TODO: Notify chatlog
    }

    @Override
    public void onNicknameChanged(Peer peer, String oldNickname) {
        //TODO: Notify chatlog
        updateUserList();
    }

    @Override
    public void onErrorOccurred(ErrorType type, String message) {
        //TODO: Notify chatlog
    }

    @Override
    public void onConnectionEstablished(Peer peer) {
        //TODO: Notify chatlog
        updateUserList();
    }

    @Override
    public void onConnectionClosed(Peer peer) {
        //TODO: Notify chatlog
        updateUserList();
    }
}
