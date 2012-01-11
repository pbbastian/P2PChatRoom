package p2pchatroom.Console;

import p2pchatroom.core.Client;
import p2pchatroom.core.Peer;
import p2pchatroom.core.events.DiscoveryEventListener;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

public class Application implements DiscoveryEventListener{
    private boolean getConsoleInput = true;
    private String programAndVersion;
    Client client;
    
    public Application(String program_andVersion) {
        this.programAndVersion = program_andVersion;
        introduction();
        client = new Client(null);
        while(getConsoleInput) {
            analyseConsoleInput(getConsoleInput());
        }
    }

    private void introduction() {
        largeSpacer();
        System.out.printf(" Welcome to %s, type '/help' for a list of commands\n", programAndVersion);
        largeSpacer();
    }

    private String getConsoleInput() {
        Scanner console = new Scanner(System.in);
        String consoleInput = "";
        System.out.print("> ");
        if(console.hasNextLine()) {
            consoleInput = console.nextLine();
        }
        if(consoleInput.equalsIgnoreCase("")) {
            return getConsoleInput();
        } else {
            return consoleInput;
        }
    }
    
    private void analyseConsoleInput(String consoleInput) {
        if(consoleInput.substring(0,1).equalsIgnoreCase("/") || consoleInput.substring(0,1).equalsIgnoreCase("@")) {
            //Checks input, to see if user was trying to type a command
            if(consoleInput.equalsIgnoreCase("/help") || consoleInput.equalsIgnoreCase("/?")
                    || consoleInput.equalsIgnoreCase("/commands")) {
                //Reports back a list of commands available to the user
                String[] commandList = new String[] {
                        "message",
                        "/list",
                        "@user <message>",
                        "/exit",
                        "/nick <nickname>"
                };
                String[] descriptionList = new String[] {
                        "Send message to chat",
                        "Lists online users",
                        "Send private message to user",
                        "Exit program",
                        "Set new nickname"
                };
                for (int i = 0; i < commandList.length; i++) {
                    System.out.printf("%-25s - %s\n",commandList[i], descriptionList[i]);
                }

            } else if(consoleInput.equalsIgnoreCase("/users")) {
                //Lists users online
                ArrayList<Peer> userlist = client.getPeers();
                //TEST STUFF-----------------------------------
                InetAddress address = null;
                try {
                    address = InetAddress.getByName("192.168.2.1");
                } catch (UnknownHostException e) {
                    System.out.println("Error occured: e");
                }
                Peer myself = new Peer(address, "Kristian");
                userlist.add(myself);
                //END TEST STUFF-------------------------------
                System.out.println("ONLINE USERS:");
                for(Peer peer : userlist) {
                    System.out.printf("@%-15s %s\n", peer.getNickname(), peer.getAddress().getHostAddress());
                }

            } else if(consoleInput.substring(0,1).equalsIgnoreCase("@")) {
                //Sends a private message
                String[] stringParts = consoleInput.split(" ", 2);
                String user = stringParts[0].replace("@", "");
                if(user.equalsIgnoreCase(client.getNickname()) || user.equalsIgnoreCase("Kristian")) {
                    System.out.println("ERROR: You wrote to yourself...über fail");
                } else {
                    String message = stringParts[1];
                    client.privateMessage(user, message);
                }

            } else if(consoleInput.equalsIgnoreCase("/exit")) {
                //Closes all connections and exits the program
                this.getConsoleInput = false;
                System.out.println("Closing connections and terminating program...");
                if (client.closeConnections()) {
                    client.message(""+client.getNickname()+" has exited the program");
                    System.exit(0);
                } else {
                    System.out.println("ERROR: Unable to close connect - Connection not found");
                    System.exit(1); //IOException occurs when Client cannot find the connection it is trying to close.
                }

            } else if(consoleInput.substring(0,5).equalsIgnoreCase("/nick")) {
                //Sets nickname of client
                String[] stringParts = consoleInput.split(" ");
                String nickname = stringParts[1];
                client.setNickname(nickname);
                System.out.printf("Nickname set to %s\n", nickname);

            } else {
                //Reached when command was not found
                System.out.println("Invalid command, type /help for a list of commands");
            }
        } else {
                //SEND MESSAGE
            }
    }
    private void largeSpacer() {
        System.out.println("-----------------------------------------------------------------");
    }

    //DISCOVERYLISTENERTHREAD INTERFACE///////////////////////////////////////
    @Override
     public void onClientDiscovered(InetAddress address) {
        System.out.println("Client Discovered @ "+address.getHostAddress());
    }

    //MAIN METHOD//////////////////////////////////////////////////////////////
    public static void main(String[] args) {
        Application application = new Application("P2PChatRoom v1.0");
    }

    @Override
    public void onIOError(IOException exception) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
