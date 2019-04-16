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
                String error;
                String option;

                Scanner serverIn = new Scanner(clientSocket.getInputStream());
                PrintStream serverOut = new PrintStream(clientSocket.getOutputStream());

                option = serverIn.nextLine();
                switch (option) {

                    // Registrar arquivos
                    case "1":
                        String nomeArquivos = serverIn.nextLine();
                        String[] arquivosParaRegistrar = nomeArquivos.split(",");
                        for (int i = 0; i < arquivosParaRegistrar.length; i++) {
                            registry(arquivosParaRegistrar[i]);
                        }
                        serverOut.println(option);
                        break;

                    // Localizar arquivo de determinado cliente
                    case "2":
                        String hostCliente = serverIn.nextLine();
                        ArrayList<String> arquivos = lookup(hostCliente);
                        String nomeDosArquivos = "";
                        try {

                            for (int i = 0; i < arquivos.size(); i++) {
                                nomeDosArquivos += i+1 + " " +arquivos.get(i) + " ";
                            }
                            serverOut.println(nomeDosArquivos);

                        } catch (Exception e) {
                            error = hostCliente + " não conectado";
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
    public void registry(String fileName) throws IOException {

        ArrayList<String> peerList = new ArrayList<String>();
        ArrayList<String> checkList = new ArrayList<String>();

        peerList.add(fileName);
        checkList = fileMap.get(clientSocket.getInetAddress().getHostAddress());

        if (checkList == null || checkList.isEmpty()) {
            fileMap.put(clientSocket.getInetAddress().getHostAddress(), peerList);
        } else {
            for (int i = 0; i < checkList.size(); i++) {
                if (checkList.get(i).equals(fileName)) {
                    checkList.remove(i);
                }
            }
            checkList.add(fileName);
            fileMap.put(clientSocket.getInetAddress().getHostAddress(), checkList);
        }
    }

    public ArrayList<String> lookup(String hostCliente) throws IOException {

        ArrayList<String> peerList = new ArrayList<String>();
        peerList = fileMap.get(hostCliente);
        return peerList;

    }

}