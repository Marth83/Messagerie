package Server;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    public boolean addMessageToHistory(String sender, String receiver, String msg){
        String filePath = getFileName("history",sender, receiver);
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
        String filePath = getFileName("history",sender, receiver);
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

    public boolean addNewMessage(String sender, String receiver, String msg){
        String filePath = "database/toBeSend/" + sender + "%" + receiver;
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true));
            writer.append(sender + " says " + receiver + " : " + msg + "\n");
            writer.close();
        }catch (Exception e){
            System.out.println("Erreur dans la persistance du nv msg");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public List<String> getNewMsg(String sender, String receiver){
        List<String> newMsg = new ArrayList();
        String filePath = "database/toBeSend/" + receiver + "%" + sender; //Inverser car on attend les messages reçus
        File file = new File(filePath);
        if(file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = br.readLine()) != null) {
                    newMsg.add(line);
                    addMessageToHistory(sender, receiver, line);
                }
            } catch (IOException e) {
                System.out.println("Erreur dans la lecture du fichier.");
                e.printStackTrace();
            }
            file.delete();
        }
        return newMsg;
    }

    private String getFileName(String type, String sender, String receiver){
        //String filePath = "database/history/";
        String filePath = "database/" + type + "/";
        if(sender.compareTo(receiver) > 0){
            filePath += sender + "%" + receiver;
        }else{
            filePath += receiver + "%" + sender;
        }
        return filePath;
    }
}
