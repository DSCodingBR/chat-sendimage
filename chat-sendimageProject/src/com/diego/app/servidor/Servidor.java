package com.diego.app.servidor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import com.diego.app.bean.FileMessage;

public class Servidor { // NOSSO SERVIDOR 
	private ServerSocket serverSocket;
	private Socket socket;
	private Map<String,ObjectOutputStream> streamMap = new HashMap<String,ObjectOutputStream>(); //A CHAVE STRING VAI CONTER O NOME DO CLIENTE //E O ObjectOutputStream VAI CONTER O FLUXO DAS MENSAGENS
	
	public Servidor() {
		try {
			
			serverSocket = new ServerSocket(5555);
			System.out.println("Servidor ON!");
			
			while(true) { // VAI GARANTIR FIQUE SEMPRE FUNCIONANDO
				socket = serverSocket.accept(); // QUANDO O CLIENTE ENTRRA NO SERVIDOR CRIA UM OBJETO SOCKET
				
				
				new Thread(new ListenerSocket(socket)).start();     //ESSA TREAD VAI SER NOSSO OUVINTE NA PARTE DO NSERVIDOR
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private class ListenerSocket implements Runnable {
		private ObjectOutputStream outputStream;//SERA RESPONSAVEL PELO ENVIO DAS MENSAGENS DO SERVER
		private ObjectInputStream inputStream; //QUEM RECEBE AS MENSAGENS
		public ListenerSocket (Socket socket) throws IOException{
			this.outputStream = new ObjectOutputStream(socket.getOutputStream());
			this.inputStream = new ObjectInputStream(socket.getInputStream());
		}
		public void run() {
			FileMessage message = null;
			try {
				
			while((message = (FileMessage) inputStream.readObject())!= null) {//UM CAST PARA TRANSFORMAR EM FILE MESSAGE PQ ELA VEM COMO OBEJETO
				
				streamMap.put(message.getClient(), outputStream);//COLOCAR O CLIENTE NA LISTA
				
				if(message.getFile()!=null) {
					for(Map.Entry<String, ObjectOutputStream> kv : streamMap.entrySet()) {
						if(!message.getClient().equals(kv.getKey())) {//SE O CLIENTE QUE ENVIOU A MENSAGEM VAI RECEBER OU NAO A PROPRIA MENSAGEM*NAO QUERO QUE ELE VEJA
							kv.getValue().writeObject(message);    //PEGA O OBJETO DE CADA CLIENTE QUE E O OUTPUTSTREAM
						}
					}
				}
				
			}
			
			}catch (Exception e) {
				streamMap.remove(message.getClient());
				System.out.println(message.getClient()+" Desconectou!");
			}
		}
	}
	
	public static void main(String[] args) { //PARA INICIALIZAR O SERVER
		new Servidor();
	}
	
}
