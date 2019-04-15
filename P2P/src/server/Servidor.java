package server;

import criptografia.Criptografia;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.Scanner;


public class Servidor {

    public static void main(String[] args) {
        try {
            ServerSocket socket = new ServerSocket(5004);
            System.out.println("Servidor conectado!!!");
            while (true) {
                Socket serverSocket = socket.accept();
                Thread thread = new Thread(new PeerConnect(serverSocket));
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


class PeerConnect implements Runnable {

    public static HashMap<String, ArrayList<String>> fileMap = new HashMap<String, ArrayList<String>>();

    public Socket clientSocket;
    int count = 0;

    public PeerConnect(Socket socket) throws IOException {
        this.clientSocket = socket;
    }

    @Override
    public void run() {

        Boolean condition = true;
        String check = "";
        Scanner sc = new Scanner(System.in);
        Criptografia criptografia = new Criptografia();

        while (condition) {
            try {
                String peerId, fileName, searchResult, error, checkId;
                String option;

                String location = "";
                ArrayList<String> fileLocation = new ArrayList<String>();

                Scanner serverIn = new Scanner(clientSocket.getInputStream());
                PrintStream serverOut = new PrintStream(clientSocket.getOutputStream());

                peerId = serverIn.nextLine();

                option = serverIn.nextLine();
                switch (option) {

                    // Registrar arquivos
                    case "1":
                        File peerDirectory = new File(peerId);
                        File[] sharedFiles = peerDirectory.listFiles();

                        for (int i = 0; i < sharedFiles.length; i++) {
                            registry(peerId, criptografia.encriptar(sharedFiles[i].getName()));
                        }
                        serverOut.println(option);
                        break;

                    // Localizar arquivo
                    case "2":
                        fileName = serverIn.nextLine();
                        fileLocation = lookup(criptografia.encriptar(fileName));
                        try {
                            for (int i = 0; i < fileLocation.size(); i++) {
                                location += fileLocation.get(i) + " ";
                            }
                            searchResult = "O arquivo está presente na: " + location;
                            System.out.println(location);
                            serverOut.println(searchResult);

                        } catch (Exception e) {
                            error = "Arquivo não registrado";
                            serverOut.println(error);
                        }
                        break;

                    // Fazer download do arquivo
                    case "3":
                        serverOut.println(option);
                        break;

                    // Encerrar conexão
                    case "4":
                        System.out.println("Conexão encerrada!!!");
                        condition = false;
                        serverOut.println(option);
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /*
     * Register function - To register file present with each peer
     */
    public void registry(String peerId, String fileName) throws IOException {

        ArrayList<String> peerList = new ArrayList<String>();
        ArrayList<String> checkList = new ArrayList<String>();

        peerList.add(peerId);
        checkList = fileMap.get(fileName);

        if (checkList == null || checkList.isEmpty()) {
            fileMap.put(fileName, peerList);
        } else {
            for (int i = 0; i < checkList.size(); i++) {
                if (checkList.get(i).equals(peerId)) {
                    checkList.remove(i);
                }
            }
            checkList.add(peerId);
            fileMap.put(fileName, checkList);
        }
    }

    public ArrayList<String> lookup(String fileName) throws IOException {

        ArrayList<String> peerList = new ArrayList<String>();
        peerList = fileMap.get(fileName);
        return peerList;

    }

}