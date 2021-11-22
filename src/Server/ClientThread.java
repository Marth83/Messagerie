/***
 * ClientThread
 * Example of a TCP server
 * Date: 14/12/08
 * Authors:
 */

package Server;

import java.io.*;
import java.net.*;

public class ClientThread
        extends Thread {

    private Socket clientSocket;
    private String sender;
    private String receiver;

    ClientThread(Socket s) {
        this.clientSocket = s;
    }

    /**
     * receives a request from client then sends an echo to the client
     *
     **/
    public void run() {
        try {
            BufferedReader socIn = null;
            socIn = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            PrintStream socOut = new PrintStream(clientSocket.getOutputStream());
            //Averti l'utilisateur de sa connexion
            socOut.println("Vous êtes connectés au serveur!");
            socOut.println("--- Veuillez rentrer votre login ---");

            //Reçoit le login
            sender = socIn.readLine();
            MessageServer.sockets.put(sender,clientSocket);
            boolean active = true;
            while(active){
                String line = socIn.readLine();
                switch(line){
                    case "unicast" :
                        unicast(socIn);
                        break;
                    case "quit" :
                        active = false;
                        break;
                }
            }

        } catch (Exception e) {
            System.err.println("Error in EchoServer:" + e);
        }
    }

    public void unicast(BufferedReader socIn) throws IOException {
        System.out.println(sender + " passe en mode unicast");
        while (true) {
            String line = socIn.readLine();
            System.out.println("Reçu :  " + line);
            String[] tab = line.split("%",3);
            MessageServer.sendMessageTo(tab[0],tab[1],tab[2]);
        }
    }

}


