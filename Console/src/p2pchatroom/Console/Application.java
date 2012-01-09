package p2pchatroom.Console;

import p2pchatroom.core.events.DiscoveryEventListener;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Scanner;

public class Application implements DiscoveryEventListener{
    private boolean getConsoleInput = true;
    private String programAndVersion;
    
    public Application(String program_andVersion) {
        this.programAndVersion = program_andVersion;
        introduction();
        while(getConsoleInput) {
            analyseConsoleInput(getConsoleInput());
        }
    }

    private void introduction() {
        spacer();
        System.out.printf("> Welcome to %s, type '/help' for a list of commands\n", programAndVersion);
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
            if(consoleInput.equalsIgnoreCase("/help") || consoleInput.equalsIgnoreCase("/?")) {
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
                spacer();
                for (int i = 0; i < commandList.length; i++) {
                    System.out.printf("> %-25s - %s\n",commandList[i], descriptionList[i]);
                }
                spacer();

            } else if(consoleInput.equalsIgnoreCase("/list")) {
                //LIST USERS

            } else if(consoleInput.substring(0,1).equalsIgnoreCase("@")) {
                //SEND PM | Recipient = user & message = stringParts[1]
                //String[] stringParts = consoleInput.split(" ", 2);
                //String user = stringParts[0].replace("@", "");

            } else if(consoleInput.equalsIgnoreCase("/exit")) {
                this.getConsoleInput = false;
                System.out.println("Closing connections and terminating program...");
                //CLOSE SOCKETS AND EXIT PROGRAM

            } else if(consoleInput.substring(0,5).equalsIgnoreCase("/nick")) {
                //SET NICKNAME
                String[] stringParts = consoleInput.split(" ");
                System.out.printf("> Nickname set to %s\n", stringParts[1]);

            } else {
                System.out.println("Invalid command, type /help for a list of commands");
            }
        } else {
                //SEND MESSAGE
            }
    }
    private void spacer() {
        System.out.println("> -----------------------------------------------------------------");
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
