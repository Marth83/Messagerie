/***
 * ClientThread
 * Example of a TCP server
 * Date: 14/12/08
 * Authors:
 */

package Server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

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
                    case "multicast" :
                        multicast(socIn,socOut);
                        break;
                    case "create" :
                        createGroup(socIn, socOut, sender);
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
        String receiver = socIn.readLine();
        MessageServer.getHistory(sender, receiver);
        MessageServer.getNewMsg(sender, receiver);
        while (true) {
            String line = socIn.readLine();
            System.out.println("Reçu :  " + line);
            String[] tab = line.split("%",3);
            if (tab[2].equals(".")) {
                return;
            }
            MessageServer.sendMessageTo(tab[0],tab[1],tab[2]);
        }
    }

    public void multicast(BufferedReader socIn, PrintStream socOut) throws IOException {
        System.out.println(sender + " passe en mode multicast");
        //Teste existence du groupe
        String groupName = socIn.readLine();
        if (MessageServer.getGroup(groupName) == null) {
            socOut.println("--- Ce groupe n'existe pas. Appuyez sur . pour quitter ---");
            while(true){
                if(socIn.readLine().equals(".")){
                    return;
                }
                socOut.println("--- Aucun groupe selectionne. Appuyez sur . pour quitter ---");
            }
        } /*else if(MessageServer.getGroup(groupName).get(groupName) == null){ //Test si appartient bien au groupe
            socOut.println("--- Vous n'avez pas accès à ce groupe. Appuyez sur . pour quitter ---");
            while(true){
                if(socIn.readLine().equals(".")){
                    return;
                }
                socOut.println("--- Aucun groupe selectionne. Appuyez sur . pour quitter ---");
            }
        } */else{
            while (true) {
                String line = socIn.readLine();
                System.out.println("[Multicast] Reçu :  " + line);
                if (line.equals(".")) {
                    return;
                }
                MessageServer.sendMessageToGroup(sender,groupName,line);
            }

        }
    }

    public static boolean createGroup(BufferedReader socIn, PrintStream socOut, String sender) throws IOException {
        List<String> newGroup = new ArrayList<>();
        boolean active = true;
        //Récupère le nom du groupe
        String groupName = socIn.readLine();
        newGroup.add(groupName);
        newGroup.add(sender);
        do{
            String tmp = socIn.readLine();
            if(tmp.equals(".")){
                active = false;
            }else{
                newGroup.add(tmp);
                socOut.println("* " + tmp + " a bien été ajouté.");
            }
        }while(active);
        if(newGroup.size() <= 3){ //Si on a que 2 personnes + le nom du groupe...
            socOut.println("--- Le groupe ne peut pas contenir moins de 3 personnes -> création annulée");
            return false;
        }else{
            MessageServer.createGroup(newGroup);
        }
        return true;
    }

}


