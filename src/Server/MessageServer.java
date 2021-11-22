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

public class MessageServer {

    /**
     * main method
     * @param MessageServer port
     *
     **/

    //Map login <-> socket (Hashmap + rapide, mais supporte mal les Thread
    public static Hashtable<String,Socket> sockets = new Hashtable<String,Socket>();

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
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}


