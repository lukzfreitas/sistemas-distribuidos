package cliente;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {

    public static void main(String[] args) {


        Scanner sc = new Scanner(System.in);
        String diretorio = ".";

        System.out.println("Insira o endereço ip do servidor");
        String ip = sc.nextLine();

        try {
            String option, check, fileSearch;
            Socket clientSocket = new Socket(ip, 5004);

            int peerId = Integer.parseInt("1100");
            sc = new Scanner(System.in);
            Scanner clientIn = new Scanner(clientSocket.getInputStream());

            Thread t1 = new Thread(new PeerFileDownload(diretorio, peerId));
            t1.start();

            do {
                System.out.println("****MENU****");
                System.out.println("1. Registrar arquivos");
                System.out.println("2. Procurar por arquivo em cliente host");
                System.out.println("3. Download de arquivo");
                System.out.println("4. Sair");

                option = sc.nextLine();

                PrintStream clientOut = new PrintStream(clientSocket.getOutputStream());

                switch (option) {

                    case "1": // Registrar arquivos
                        clientOut.println(option);

                        JFileChooser escolherDiretorio = new JFileChooser();
                        escolherDiretorio.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                        int res = escolherDiretorio.showOpenDialog(null);
                        diretorio = escolherDiretorio.getSelectedFile().getAbsolutePath();

                        File peerDirectory = new File(diretorio);
                        File[] sharedFiles = peerDirectory.listFiles();
                        String hashArquivos = "";
                        for (int i = 0; i < sharedFiles.length; i++) {
                            hashArquivos += sharedFiles[i].getAbsolutePath() + ",";
                        }
                        clientOut.println(hashArquivos);

                        System.out.println("Registrando arquivos. Aguarde!!!");
                        check = clientIn.nextLine();
                        System.out.println("Arquivos registrados!!!");
                        break;

                    case "2": // Localizar arquivo

                        clientOut.println(option);
                        System.out.println("digite o endereço do cliente: ");
                        fileSearch = sc.nextLine();
                        clientOut.println(fileSearch);
                        check = clientIn.nextLine();
                        System.out.println(check);
                        break;

                    case "3": // Realizar download de arquivo
                        clientOut.println(option);

                        System.out.println("digite o endereço do cliente:");
                        String host = sc.nextLine();

                        System.out.println("digite o nome do arquivo para download:");
                        String arquivoParaDownload = sc.nextLine();

                        System.out.println("digite o nome do arquivo para ser salvo:");
                        String nomeDoArquivoSalvo = sc.nextLine();

                        JFileChooser escolherPastaDownload = new JFileChooser();
                        escolherPastaDownload.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                        res = escolherPastaDownload.showOpenDialog(null);
                        String filePathDownload = escolherPastaDownload.getSelectedFile().getAbsolutePath();

                        retrieve(arquivoParaDownload, filePathDownload, "1100", host, nomeDoArquivoSalvo);
                        break;

                    case "4": // Sair

                        clientOut.println(option);
                        System.out.println("Cliente encerrado!!");
                        System.exit(0);
                        break;
                }
            } while (!(option.equals("4")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void retrieve(String fileName, String downPath, String clientId, String host, String nomeNovoArquivo) {

        //Make a connection with server to get file from
        int portNumber = Integer.parseInt(clientId);

        try {
            Socket peerClient = new Socket(host, portNumber);
            System.out.println("baixando o arquivo ...");

            //Input & Output for socket Communication
            Scanner peerIn = new Scanner(peerClient.getInputStream());
            PrintStream peerOut = new PrintStream(peerClient.getOutputStream());

            peerOut.println(fileName);
            peerOut.println(clientId);

            System.out.println("endereço do recurso baixado: " + peerIn.nextLine());

            long buffSize = peerIn.nextLong();
            int newBuffSize = (int) buffSize;

            byte[] b = new byte[newBuffSize];
            String filePath = downPath + "\\" + nomeNovoArquivo;
            //Write the file requested by the peer
            FileOutputStream writeFileStream = new FileOutputStream(filePath);

            writeFileStream.write(b);
            writeFileStream.close();

            System.out.println("Arquivo " + fileName +" baixado com sucesso");

            peerClient.close();

        } catch (FileNotFoundException ex) {
            System.out.println("FileNotFoundException : " + ex);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

 