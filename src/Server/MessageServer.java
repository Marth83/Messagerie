/***
 * EchoServer
 * Example of a TCP server
 * Date: 10/01/04
 * Authors:
 */

package Server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class MessageServer {

    /**
     * main method
     * @param MessageServer port
     *
     **/

    //Map login <-> socket (Hashmap + rapide, mais supporte mal les Thread
    public static Hashtable<String,Socket> sockets = new Hashtable<String,Socket>();
    public static Hashtable<String,List<String>> groups = new Hashtable<String,List<String>>(); //Nom du groupe / Liste de paxs
    public static Database db = new Database();

    public static void main(String args[]){
        ServerSocket listenSocket;

        if (args.length != 1) {
            System.out.println("Usage: java EchoServer <EchoServer port>");
            System.exit(1);
        }
        try {
            listenSocket = new ServerSocket(Integer.parseInt(args[0])); //port
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



    public static boolean sendMessageTo(String sender, String receiver, String message){
        Socket target = sockets.get(receiver);

        //Si la target n'est pas joignable...
        if(target == null || !target.isConnected()){
            target = sockets.get(sender);
            try {
                PrintStream out = new PrintStream(target.getOutputStream());
                out.println("(info) L'utilisateur n'est pas joignable -> Les messages seront transmis lors de sa future connexion");
                db.addNewMessage(sender, receiver, message);
                return true;
            }catch(Exception e){
                e.printStackTrace();
                return false;
            }
        }
        //Target bien trouvée
        try {
            PrintStream out = new PrintStream(target.getOutputStream());
            out.println(sender + " says " + message);
            db.addMessageToHistory(sender, receiver, message);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void getHistory(String sender, String receiver) throws IOException {
        Socket target = sockets.get(sender);
        PrintStream out = new PrintStream(target.getOutputStream());
        List<String> history = db.getTchatHistory(sender,receiver);
        out.print("--- Historique de votre conversation ---\n");
        if(history.isEmpty()) {
            out.println("Aucun historique à afficher");
        }
        else {
            for (String var : history) {
                out.println(var);
            }
        }
    }

    public static void getNewMsg(String sender, String receiver) throws IOException{
        Socket target = sockets.get(sender);
        PrintStream out = new PrintStream(target.getOutputStream());
        List<String> newMsg = db.getNewMsg(sender,receiver);
        out.print("--- Nouveau.x Message.s ---\n");
        if(newMsg.isEmpty()) {
            out.println("Aucun nouveau message\n");
        }
        else {
            for (String var : newMsg) {
                out.println(var);
            }
        }
    }

    public static void createGroup(List<String> newGroup){
        db.addGroup(newGroup);
    }

    public static List<String> getGroup(String name){
        return groups.get(name);
    }
}


