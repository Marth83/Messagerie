/***
 * EchoServer
 * Example of a TCP server
 * Date: 10/01/04
 * Authors:
 */

package Server;

import java.io.*;
import java.net.*;
import java.util.*;

public class MessageServer {

    /**
     * Main method
     *
     * @param MessageServer port
     * @author Martin, Yasmine, Claire
     **/

    //Map login <-> socket (Hashmap + rapide, mais supporte mal les Thread
    public static Hashtable<String, Socket> sockets = new Hashtable<String, Socket>();
    public static Hashtable<String, List<String>> groups = new Hashtable<String, List<String>>(); //Nom du groupe / Liste de paxs
    public static Hashtable<String, String> users = new Hashtable<String, String>(); //Login / pwd
    public static Database db = new Database();

    public static void main(String args[]) {
        ServerSocket listenSocket;

        if (args.length != 1) {
            System.out.println("Usage: java EchoServer <EchoServer port>");
            System.exit(1);
        }
        try {
            listenSocket = new ServerSocket(Integer.parseInt(args[0])); //port
            users = db.loadUsers();
            groups = db.getListGroups();
            System.out.println("Server ready...");
            while (true) {
                Socket clientSocket = listenSocket.accept();
                System.out.println("Connexion from:" + clientSocket.getInetAddress());
                ClientThread ct = new ClientThread(clientSocket);
                ct.start();
            }
        } catch (Exception e) {
            System.err.println("Error in EchoServer:" + e);
        }
    }


    public static boolean sendMessageTo(String sender, String receiver, String message) {
        Socket target = sockets.get(receiver);

        //Si la target n'est pas joignable...
        if (target == null || !target.isConnected()) {
            target = sockets.get(sender);
            try {
                PrintStream out = new PrintStream(target.getOutputStream());
                out.println("(info) L'utilisateur n'est pas joignable -> Les messages seront transmis lors de sa future connexion");
                db.addNewMessage(sender, receiver, message);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        //Target bien trouvée
        try {
            PrintStream out = new PrintStream(target.getOutputStream());
            out.println(sender + " - " + message);
            db.addMessageToHistory(sender, receiver, message);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean sendMessageToGroup(String sender, String groupName, String message) {
        List<String> listPax = getGroup(groupName); //Liste des destinataires
        List<Socket> sockPax = new ArrayList<>(); //Liste des sockets à remplir
        for (String paxName : listPax) {
            sockPax.add(sockets.get(paxName));
        }
        sockPax.remove(sockets.get(sender));
        int i = 1; //Indice pour recuperer le nom
        db.addGrpMessageToHistory(sender, sender, groupName, message); //Ajout à son historique
        for (Socket pax : sockPax) {
            if (pax == null || !pax.isConnected()) {
                String receiver = listPax.get(i);
                db.addGrpNewMessage(sender, receiver, groupName, message);
            } else {
                try {
                    PrintStream out = new PrintStream(pax.getOutputStream());
                    out.println("[" + groupName + "] " + sender + " - " + message);
                    String receiver = getNameOfSocket(pax);
                    db.addGrpMessageToHistory(sender, receiver, groupName, message);
                    //Traitement sauvegarde historique
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
            i++;
        }
        return true;
    }

    public static void getHistory(String sender, String receiver) throws IOException {
        Socket target = sockets.get(sender);
        PrintStream out = new PrintStream(target.getOutputStream());
        List<String> history = db.getTchatHistory(sender, receiver);
        out.print("--- Historique de votre conversation ---\n");
        if (history.isEmpty()) {
            out.println("Aucun historique à afficher");
        } else {
            for (String var : history) {
                out.println(var);
            }
        }
    }

    public static void getNewMsg(String sender, String receiver) throws IOException {
        Socket target = sockets.get(sender);
        PrintStream out = new PrintStream(target.getOutputStream());
        List<String> newMsg = db.getNewMsg(sender, receiver);
        out.print("--- Nouveau.x Message.s ---\n");
        if (newMsg.isEmpty()) {
            out.println("Aucun nouveau message\n");
        } else {
            for (String var : newMsg) {
                out.println(var);
            }
        }
    }

    public static void getGroupHistory(String sender, String groupName) throws IOException {
        Socket target = sockets.get(sender);
        PrintStream out = new PrintStream(target.getOutputStream());
        List<String> history = db.getGrpTchatHistory(sender, groupName);
        out.print("--- Historique de votre conversation ---\n");
        if (history.isEmpty()) {
            out.println("Aucun historique à afficher");
        } else {
            for (String var : history) {
                out.println(var);
            }
        }
    }

    public static void getGroupNewMessage(String sender, String groupName) throws IOException {
        Socket target = sockets.get(sender);
        PrintStream out = new PrintStream(target.getOutputStream());
        List<String> newMsg = db.getGrpNewMsg(sender, groupName);
        out.print("--- Nouveau.x Message.s ---\n");
        if (newMsg.isEmpty()) {
            out.println("Aucun nouveau message\n");
        } else {
            for (String var : newMsg) {
                db.addGrpMessageToHistory(groupName, sender, var);
                out.println(var);
            }
        }
    }

    public static void createGroup(List<String> newGroup) {
        db.addGroup(newGroup);
        groups = db.getListGroups();
    }

    public static void broadcastMsg(String message){
        for(String receiver : users.keySet())
            sendMessageTo("admin", receiver, message);
    }

    public static List<String> getGroup(String name) { //renvoie nom des membres
        return groups.get(name);
    }

    public static String getNameOfSocket(Socket target) {
        if (sockets.containsValue(target)) {
            for (Map.Entry<String, Socket> entry : sockets.entrySet()) {
                if (Objects.equals(entry.getValue(), target)) {
                    return entry.getKey();
                }
            }
        }
        return "Somebody";
    }

    public static Hashtable<String,String> getUserList(){
        return users;
    }
    public static Hashtable<String, List<String>> getGroups(){ return groups;}
}


