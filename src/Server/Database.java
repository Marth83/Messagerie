package Server;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class Database {
    public boolean addMessageToHistory(String sender, String receiver, String msg){
        String filePath = "database/history/";
        if(sender.compareTo(receiver) > 0){
            filePath += sender + "%" + receiver;
        }else{
            filePath += receiver + "%" + sender;
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true));
            writer.append(sender + "-" + receiver + " : " + msg + "\n");
            writer.close();

        }catch (Exception e){
            System.out.println("Erreur dans la persistance");
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
