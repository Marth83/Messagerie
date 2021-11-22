/***
 * EchoServer
 * Example of a TCP server
 * Date: 10/01/04
 * Authors:
 */

package Server;

import java.io.*;
import java.net.*;
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
    public static Database db = new Database();

    public static void main(String args[]){
        ServerSocket listenSocket;

        if (args.length != 1) {
            System.out.println("Usage: java EchoServer <EchoServer port>");
            System.exit(1);
        }
        try {
            listenSocket = new ServerSocket(Integer.parseInt(args[0])); //port
            System.out.println("Server ready...");
            while (true) {
                Socket clientSocket = listenSocket.accept();
                System.out.println("Connexion from:" + clientSocket.getInetAddress());
                ClientThread ct = new ClientThread(clientSocket);
                ct.start();
                FileWriter myWriter = new FileWriter("filename.txt");
                myWriter.write("Files in Java might be tricky, but it is fun enough!");
                myWriter.close();
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
                out.println("Error : l'utilisateur n'est pas joignable");
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

    public static List<String> getHistory(String sender, String receiver){

        return db.getTchatHistory(sender, receiver);
    }
}


