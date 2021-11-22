package Server;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    public boolean addMessageToHistory(String sender, String receiver, String msg){
        String filePath = getFileName(sender, receiver);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true));
            writer.append(sender + " says " + receiver + " : " + msg + "\n");
            writer.close();

        }catch (Exception e){
            System.out.println("Erreur dans la persistance");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public List<String> getTchatHistory(String sender, String receiver){
        List<String> history = new ArrayList();
        String filePath = getFileName(sender, receiver);
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null){
                history.add(line);
            }
        } catch (IOException e) {
            System.out.println("Erreur dans la lecture du fichier.");
            e.printStackTrace();
        }
        return history;
    }

    private String getFileName(String sender, String receiver){
        String filePath = "database/history/";
        if(sender.compareTo(receiver) > 0){
            filePath += sender + "%" + receiver;
        }else{
            filePath += receiver + "%" + sender;
        }
        return filePath;
    }
}
