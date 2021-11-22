package Client;

import java.io.BufferedReader;


/**
 * ClientThreadListening
 * Listens to the socket and prints its content
 * @author Tyefen, Mostapha, Mohammed
 */
public class ThreadClientListening extends Thread {

    private BufferedReader socIn ;

    ThreadClientListening(BufferedReader s) {
        this.socIn = s;
    }

    /**
     * Simple thread that listens to the socket and print lines from it
     */
    public void run() {
        try {
            while (true) {
                String msg = socIn.readLine();
                if (msg==null) break;
                System.out.println(msg);
            }
        } catch (Exception e) {
            System.err.println("Error in MessageServer:" + e);
        }
    }

}