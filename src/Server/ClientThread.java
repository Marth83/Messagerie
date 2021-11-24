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
import java.util.HashMap;
import java.util.Hashtable;
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
            boolean connected = false;
            socOut.println("Vous êtes connectés au serveur, bienvenue!");
            do {
                socOut.println("--- Veuillez rentrer votre login ---");
                socOut.println("waitForLogin");
                //Reçoit le login
                sender = socIn.readLine();
                Hashtable<String, String> usersList = MessageServer.getUserList();
                if (usersList.containsKey(sender)) {
                    socOut.println("--- Veuillez rentrer votre mdp ---");
                    String mdp = socIn.readLine();
                    if (!usersList.get(sender).equals(mdp)) {
                        socOut.println("--- Erreur : le mdp est erroné ---");
                    }else{
                        connected = true;
                        socOut.println("connected");
                    }
                }
            }while(!connected);
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
                    case "broadcast" :
                        broadcast(socIn,socOut, sender);
                        break;
                    case "quit" :
                        active = false;
                        socOut.println(".");
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
            if (line.equals(".")) {
                return;
            }
            MessageServer.sendMessageTo(sender,receiver,line);
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
        }else if(!MessageServer.getGroup(groupName).contains(sender)){ //Test si appartient bien au groupe
            socOut.println("--- Vous n'avez pas accès à ce groupe. Appuyez sur . pour quitter ---");
            while(true){
                if(socIn.readLine().equals(".")){
                    return;
                }
                socOut.println("--- Aucun groupe selectionne. Appuyez sur . pour quitter ---");
            }
        }else{
            MessageServer.getGroupHistory(sender, groupName);
            MessageServer.getGroupNewMessage(sender, groupName);
            while (true) {
                String line = socIn.readLine();
                if(!line.equals("\n"))
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
        Hashtable<String,String> listUsers =  MessageServer.getUserList();
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
                if(listUsers.containsKey(tmp)) {
                    newGroup.add(tmp);
                    socOut.println("* " + tmp + " a bien été ajouté.");
                }else{
                    socOut.println("--- Cet utilisateur n'existe pas!");
                }
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

    public static boolean broadcast(BufferedReader socIn, PrintStream socOut, String sender) throws IOException {
        String msg = socIn.readLine();
        if(sender.equals("admin")){
            MessageServer.broadcastMsg(msg);
            socOut.println("--- Message envoye");
        }else{
            socOut.println("--- Erreur -> vous n'etes pas admin");
            return false;
        }
        return true;
    }

}


