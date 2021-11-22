/***
 * EchoClient
 * Example of a TCP client
 * Date: 10/01/04
 * Authors:
 */
package Client;

import java.io.*;
import java.net.*;
import java.util.concurrent.TimeUnit;


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
        boolean exit = false;
        do {
            System.out.println("--- Menu ---");
            System.out.println("- 1 -> Envoyer un msg privé");
            System.out.println("- 2 -> Envoyer un msg groupé");
            System.out.println("- q -> Quitter");
            System.out.print("Faites votre choix : ");
            line = stdIn.readLine();
            switch (line){
                case "1" :
                    sendUnicast(sender, stdIn, socOut, socIn);
                    break;
                case "2" :
                    //sendMulticast(send, stdIn, stdOut);
                    break;
                case "q" :
                    exit = true;
            }
        }while(!exit);
        socOut.close();
        socIn.close();
        stdIn.close();
        echoSocket.close();
    }

    private static void sendUnicast(String sender, BufferedReader stdIn, PrintStream socOut, BufferedReader socIn ) throws IOException {
        socOut.println("unicast");
        System.out.println("--- Qui voulez-vous contacter? ---");
        String receiver = stdIn.readLine();
        socOut.println(receiver);

        System.out.println("--- Historique de votre conversation ---");
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
    }
}


