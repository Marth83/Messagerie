package Server;

import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;
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

    public Hashtable<String,List<String>> getListGroups(){
        Hashtable<String,List<String>> groups = new Hashtable<String,List<String>>();
        File file = new File("database/group/groupList"); //<Nom du groupe>%<nom1>%<nom2>%<nom3>%...
        if(file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String [] temp = line.split("%");
                    List<String> listContact = new ArrayList<>();
                    for(int i = 1; i < temp.length; i++){
                        listContact.add(temp[i]);
                    }
                    groups.put(temp[0],listContact);
                }
            } catch (IOException e) {
                System.out.println("Erreur dans la lecture du fichier.");
                e.printStackTrace();
            }
        }
        return groups;
    }
    public boolean addGrpMessageToHistory(String sender, String receiver, String groupName, String msg){
        String filePath = "database/group/history/" + groupName + "%" + receiver;
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true));
            writer.append("[" + groupName + "]" + sender + " - " + msg + "\n");
            writer.close();

        }catch (Exception e){
            System.out.println("Erreur dans la persistance");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean addGrpMessageToHistory(String groupName, String receiver, String txt){
        String filePath = "database/group/history/" + groupName + "%" + receiver;
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true));
            writer.append(txt + "\n");
            writer.close();

        }catch (Exception e){
            System.out.println("Erreur dans la persistance");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean addGrpNewMessage(String sender, String receiver, String groupName, String msg){
        String filePath = "database/group/newMsg/" + groupName + "%" + receiver;
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true));
            writer.append("[" + groupName + "]" + sender + " - " + msg + "\n");
            writer.close();
        }catch (Exception e){
            System.out.println("Erreur dans la persistance du nv msg");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public List<String> getGrpTchatHistory(String sender, String groupName){
        List<String> history = new ArrayList();
        String filePath = "database/group/history/" + groupName + "%" + sender;
        File file = new File(filePath);
        if(!file.exists()){
            return new ArrayList<String>();
        }
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

    public List<String> getGrpNewMsg(String sender, String groupName){
        List<String> newMsg = new ArrayList();
        String filePath = "database/group/newMsg/" + groupName + "%" + sender;
        File file = new File(filePath);
        if(file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = br.readLine()) != null) {
                    newMsg.add(line);
                }
            } catch (IOException e) {
                System.out.println("Erreur dans la lecture du fichier.");
                e.printStackTrace();
            }
            file.delete();
        }
        return newMsg;
    }

    public boolean addGroup(List<String> newGroup){
        String filePath = "database/group/groupList";
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true));
            for (String var : newGroup) {
                writer.append(var + "%");
            }
            writer.append("\n");
            writer.close();
        }catch (Exception e){
            System.out.println("Erreur dans la persistence du nv msg");
            e.printStackTrace();
            return false;
        }
        return true;
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
