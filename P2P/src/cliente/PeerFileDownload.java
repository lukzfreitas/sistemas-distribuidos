package cliente;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class PeerFileDownload implements Runnable
{
	String fileDownloadPath;
	int portNumber;
	Socket peerSocket;
	Scanner in;
	PrintStream out;

	public PeerFileDownload(String filePath,int userInpPort){
		this.portNumber = userInpPort;
		this.fileDownloadPath = filePath;
	}

	public void run(){
		try{
			ServerSocket downloadSocket = new ServerSocket(portNumber);
			System.out.println("cliente Iniciado!!!");
			
			while(true){
				peerSocket = downloadSocket.accept();
				System.out.println("Cliente conectado para compartilhar arquivos ...");

				out = new PrintStream(peerSocket.getOutputStream());
				in = new Scanner(peerSocket.getInputStream());

				String fileName = in.nextLine();

				PrintStream peerOut = new PrintStream(peerSocket.getOutputStream());
				peerOut.println(peerSocket.getInetAddress().getHostAddress());

				File checkFile = new File(fileName);

				FileInputStream fin = new FileInputStream(checkFile);
				BufferedInputStream buffReader = new BufferedInputStream(fin);

				if (!checkFile.exists()){
					System.out.println("Arquivo não existe");
					buffReader.close();
					return;
				}

				int size = (int) checkFile.length();
				byte[] buffContent = new byte[size];

				out.println(size);

				int startRead = 0;
				int numOfRead = 0;

				while (startRead < buffContent.length && (numOfRead = buffReader.read(buffContent, startRead, buffContent.length - startRead)) >= 0) 
				{
					startRead = startRead + numOfRead;
				}

				if (startRead < buffContent.length){
					System.out.println("Leitura do arquivo incompleta" + checkFile.getName());
				}
				out.println(buffContent);
				buffReader.close();
			}
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
}
