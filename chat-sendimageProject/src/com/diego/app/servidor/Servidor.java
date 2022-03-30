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
	//VOU FAZER O ENVIO DA MENSAGEM E NAO QUERO Q ESSA MENSAGEM SEJA ENVIADA DE VOLTA PARA O PROPRIO USUARIO QU MANDOU, AI VOU ADD TODOS OS CLIENNTES EM UMA LISTA, AI EU CONSIGO TESTAR PARA QUAIS CLIENTES EU ESTOU ENVIANDO!
	public Servidor() {//CONTRUCAO DO SOCKET
		try {
			
			serverSocket = new ServerSocket(5555);//PORTA NA QUAL O SOCKET VAI TRABALHAR
			System.out.println("Servidor ON!");
			
			while(true) { // VAI GARANTIR FIQUE SEMPRE FUNCIONANDO
				socket = serverSocket.accept(); // QUANDO O CLIENTE ENTRRA NO SERVIDOR CRIA UM OBJETO SOCKET
				
				
				new Thread(new ListenerSocket(socket)).start();     //ESSA TREAD VAI SER NOSSO OUVINTE NA PARTE DO SERVIDOR
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private class ListenerSocket implements Runnable {
		private ObjectOutputStream outputStream;//SERA RESPONSAVEL PELO ENVIO DAS MENSAGENS DO SERVER
		private ObjectInputStream inputStream; //QUEM RECEBE AS MENSAGENS
		public ListenerSocket (Socket socket) throws IOException{//INICIALIZA O NOSSOS OBEJTOS NO CONTRUTOR ATRAVEZ DOS SOCKETS!
			this.outputStream = new ObjectOutputStream(socket.getOutputStream());
			this.inputStream = new ObjectInputStream(socket.getInputStream());
		}
		public void run() {
			FileMessage message = null;//1 COISA E FAZER UMA VARIAVEL LOCAL
			try {
				
			while((message = (FileMessage) inputStream.readObject())!= null) {//UM CAST PARA TRANSFORMAR EM FILE MESSAGE PQ ELA VEM COMO OBEJETO
				
				streamMap.put(message.getClient(), outputStream);//COLOCAR O CLIENTE NA LISTA QUE SE CONECTOU!
				
				if(message.getFile()!=null) {//SE ELE FOR NULO E PQ FOI FEITO SO A CONEXAO, SE NAO FOR E PQ O CLIENTE ESTA ENVINADO UMA MENSAGEM!
					for(Map.Entry<String, ObjectOutputStream> kv : streamMap.entrySet()) {//
						if(!message.getClient().equals(kv.getKey())) {//NAO QUERO QUE ELE RECEBA A PROPRIA MENSAGEM, SE A MENSG ENVIADA CONTEM A MESMA CHAVE QUE ESTA NA LISTA, SE ESTIVER LA NAO ENVIA PRA ELE MESMO
							kv.getValue().writeObject(message);    //PEGA O OBJETO DE CADA CLIENTE QUE E O OUTPUTSTREAM
						}
					}
				}
				
			}
			
			}catch (Exception e) {
				streamMap.remove(message.getClient());
				System.out.println(message.getClient()+" Desconectou!");//EXCEPTION PARA QUANDO O CLIENTE DESCONECTAR
			}
		}
	}
	
	public static void main(String[] args) { //METEDO MAIN PARA INICIALIZAR O SERVER
		new Servidor();
	}
	
}
