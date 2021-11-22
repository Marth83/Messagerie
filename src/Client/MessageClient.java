/***
 * EchoClient
 * Example of a TCP client
 * Date: 10/01/04
 * Authors:
 */
package Client;

import java.io.*;
import java.net.*;



public class MessageClient {


    /**
     *  main method
     *  accepts a connection, receives a message from client then sends an echo to the client
     **/
    public static void main(String[] args) throws IOException {

        Socket echoSocket = null;
        PrintStream socOut = null;
        BufferedReader stdIn = null;
        BufferedReader socIn = null;
        String sender = null;
        String receiver = null;

        if (args.length != 2) {
            System.out.println("Usage: java EchoClient <EchoServer host> <EchoServer port>");
            System.exit(1);
        }

        try {
            // creation socket ==> connexion
            echoSocket = new Socket(args[0],Integer.parseInt(args[1]));
            socIn = new BufferedReader(
                    new InputStreamReader(echoSocket.getInputStream())); //Ce qui vient du serveur
            socOut= new PrintStream(echoSocket.getOutputStream());
            stdIn = new BufferedReader(new InputStreamReader(System.in));
            ThreadClientListening tcl = new ThreadClientListening(socIn);
            tcl.start();
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host:" + args[0]);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for "
                    + "the connection to:"+ args[0]);
            System.exit(1);
        }

        String line;
        //Login
        sender=stdIn.readLine();
        socOut.println(sender);
        System.out.println("--- Bienvenue " + sender + "! ---");
        do {
            System.out.println("--- Qui voulez-vous contacter? ---");
            // Tester l'existence?
            receiver = stdIn.readLine();

            //Début de la conv
            String msg;
            while (true){
                msg = stdIn.readLine();
                if (msg.equals(".")) {
                    System.out.println("-- Fin de la conv --");
                    break;
                }
                socOut.println(sender + "%" + receiver + "%" + msg);
            }
            System.out.println("--- Fin de la conversation ---");
            System.out.println("--- Voulez-vous quitter? Entrer q pour quitter ---");
            line = stdIn.readLine();
        }while(!line.equals("q"));
        socOut.close();
        socIn.close();
        stdIn.close();
        echoSocket.close();
    }
}


