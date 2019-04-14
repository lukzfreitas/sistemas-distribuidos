package cliente;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Cliente {

	public static void main(String[] args){

		JFileChooser escolherDiretorio = new JFileChooser();
		escolherDiretorio.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int res = escolherDiretorio.showOpenDialog(null);
		String diretorio = escolherDiretorio.getSelectedFile().getAbsolutePath();

		try{
			String option, check, fileSearch, fileDownload, clientId;
			Socket clientSocket = new Socket("localhost", 5004);

			int peerId = Integer.parseInt("1100");
			Scanner sc = new Scanner(System.in);
			Scanner clientIn = new Scanner(clientSocket.getInputStream());

			Thread t1 = new Thread(new PeerFileDownload(diretorio, peerId));
			t1.start();
			
			do{
				System.out.println("****MENU****");
				System.out.println("1. Registrar arquivos");
				System.out.println("2. Procurar por arquivo");
				System.out.println("3. Download de arquivo");
				System.out.println("4. Sair");
				
				option = sc.nextLine();
				
				PrintStream clientOut = new PrintStream(clientSocket.getOutputStream());
				clientOut.println(diretorio);
				
				switch(option){
				
				case "1": // Registrar arquivos
					
					clientOut.println(option);
					System.out.println("Registrando arquivos. Aguarde!!!");
					check = clientIn.nextLine();
					System.out.println("Arquivos registrados!!!");
					break;
					
				case "2": // Localizar arquivo
					
					clientOut.println(option);
					System.out.println("digite o nome do arquivo: ");
					fileSearch = sc.nextLine();
					clientOut.println(fileSearch);
					check = clientIn.nextLine();
					System.out.println(check);
					break;
					
				case "3": // Realizar download de arquivo
					
					clientOut.println(option);
					System.out.println("digite o nome do arquivo para download:");
					fileDownload = sc.nextLine();

					JFileChooser escolherPastaDownload = new JFileChooser();
					escolherPastaDownload.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					res = escolherPastaDownload.showOpenDialog(null);
					String filePathDownload = escolherPastaDownload.getSelectedFile().getAbsolutePath();



//					System.out.println("Insira o nome da pasta do arquivo: ");
//					clientId = sc.nextLine();
					//double startTime = System.currentTimeMillis();
					retrieve(fileDownload,filePathDownload,"1100");
					//double endTime = System.currentTimeMillis();
					//System.out.println("Time taken to download: "+(endTime-startTime));
					check = clientIn.nextLine();
					break;
					
				case "4": // exit case
					
					clientOut.println(option);
					System.out.println("Cliente encerrado!!");
					System.exit(0);
					break;
				}
			}while(!(option.equals("4")));
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	/*
	 * Retrieve function -  invoked by a peer to download a file from another peer
	 */
	public static void retrieve(String fileName, String downPath, String clientId){

		//Make a connection with server to get file from
		int portNumber = Integer.parseInt(clientId);
		
		try {
			Socket peerClient = new Socket("localhost",portNumber);
			System.out.println("baixando o arquivo ...");

			//Input & Output for socket Communication
			Scanner peerIn = new Scanner(peerClient.getInputStream());
			PrintStream peerOut = new PrintStream(peerClient.getOutputStream());
	
			peerOut.println(fileName);
			peerOut.println(clientId);	
	
			long buffSize = peerIn.nextLong();
			int newBuffSize = (int) buffSize;
	
			byte[] b = new byte[newBuffSize];
			String filePath = downPath + "\\" + fileName;
			//Write the file requested by the peer
			FileOutputStream writeFileStream = new FileOutputStream(filePath);

			writeFileStream.write(b);
			writeFileStream.close();

			System.out.println("Arquivo baixado com sucesso");
			System.out.println("Display file " + fileName);

			peerClient.close();

		} 
		catch (FileNotFoundException ex){
			System.out.println("FileNotFoundException : " + ex);
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
}
/*
 * Exceptions that are left
 * while downloading check if the peer given is correct or not
 * and if the peer is up and running
 * */
 