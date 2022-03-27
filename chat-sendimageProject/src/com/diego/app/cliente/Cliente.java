package com.diego.app.cliente;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.channels.FileChannel;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JFileChooser;

import com.diego.app.bean.FileMessage;

public class Cliente {
	
	private Socket socket;
	private ObjectOutputStream outputStream;
	
	public Cliente() throws IOException {
		this.socket = new Socket("localhost", 5555); //CONECTAR MEU SOCKET A PORTA
		this.outputStream = new ObjectOutputStream(socket.getOutputStream());
		
		new Thread (new ListenerSocket(socket)).start();; // AQ SERA NOSSO OUVINTE
		
		menu();
		
	}
	private void menu() throws IOException {//USAR PARA ENVIAR MENSAGEM
		Scanner scanner = new Scanner (System.in);
		
		System.out.print("Digite Seu nome: "); // SERA O NOME DO CLIENTE
		
		String nome = scanner.nextLine();// VAI RECEBER O NOME DIGITADO DO CLIENTE
		
		this.outputStream.writeObject(new FileMessage(nome));// ADD NO OUTPUTSTREAM UM OBJETO DO TIPO FILE MESSAGE PASSANDO O NOME DO MEU CLIENTE
		//ASSSIM GARANTIMOS QUE O CLIENTE ESTA SE CONECTANDO
		
		int option = 0;
		
		while(option != 1) {// ENQUANTO O OPTION FOI DIFERENTE DE -1 EU MANTENHO O PROCESSO DENTRO DESSE WHILE
			
			System.out.print("1- SAIR  | 2- ENVIAR: ");
			option = scanner.nextInt();
			
			if(option == 2) {//FACO UM TESTE
				send(nome); //CHAR O METODO SEND PASSANDO O NOME DO CLIENTE COM PARAMETRO
			}else if(option==1) { // CASO OPSTION SER = 1 FORCAMOS UMA SAIDA
				System.exit(0);
			}
		}
		
	}
	
	private void send(String nome) throws IOException {
		FileMessage filemessage = new FileMessage();
		
		JFileChooser fileChooser = new JFileChooser();	// VAMOS USAR PARA SELECIONAR O ARQUIVO QUE QUEREMOS ENVIAR
		
		int opt = fileChooser.showOpenDialog(null);// VAI ABRIR A JANELA PARA SELECIONAR O NOSSO ARQUIVO
		
		if(opt == JFileChooser.APPROVE_OPTION){ //AGORA O RETORNO CASO O CLIENTE ACEITE A NOSSA MENSAGEM YEAH! OU CANCELE AAAHHHHH :c
			File file = fileChooser.getSelectedFile(); //VAI SER A VAIAVEL QUE VAI ARMAZENAR O ARQUIVO SELECIONADO ATRAVEZ DO METODO getSelectedFile()
			this.outputStream.writeObject(new FileMessage(nome, file));;//AGORA EU ENVIO ESSA MENSAGEM ATRAVEZ DO OUTPUTSTREAM E DO METODO WRITEOBJECT PASSANDO A INSTANCIA DA MINHA CLASSE FileMessage, ONDE EU ACIONO O NOME DO CLIENTE E O ARQUIVO QUE ESTA EM FILE
		}
	}

	private class ListenerSocket implements Runnable {//USAR PARA RECEBER MENSAGEM ENVIADAS PELOS OUTROS CLIENTES //CLASSE DO OUVINTE
		
		private ObjectInputStream inputStream;
		
		
		public ListenerSocket (Socket socket) throws IOException {
			this.inputStream = new ObjectInputStream(socket.getInputStream());
		}
		
		public void run() {
			
			FileMessage message = null;

			try {
			while((message = (FileMessage) inputStream.readObject())!= null) {
				//METODO QUE VAI LER E RECEBER MENSAGEM, LER O ARQUIVO E EXIBIR ELE, O SEU CONTEUDO
				System.out.println("\n VOCE REBEU UM ARQUIVO DE: " + message.getClient());// VAMOS EXIBIR NO CONSELE UMA MENSAGEM, QUE PEGA O NOME DO CLIENTE QUE ENVIOU A MENSAGEM
				System.out.println("O ARQUIVO É: " + message.getFile().getName());//NOME DO ARQUIVO
				
				//imprime(message);//METODO QUE VAI IMPRIMIR NO CONSOLE O CONTEUDO QUE RECEBEMOS DO ARQUIVO
				
				salvar(message);// METODO QUE VAI SALVAR O ARQUIVO!
				
				
				System.out.print("1- SAIR  | 2- ENVIAR: ");
			}
			
			}catch (Exception e) {
				e.printStackTrace();
			}
			
		}

		private void salvar(FileMessage message) {// METODO QUE VAI SALVAR O ARQUIVO!
			try {
				
				Thread.sleep(new Random().nextInt(1000));// PRA GARANTIR UM TEMPO DIFERENTE NESSE SLEEP PARA CADA EXECUCAO//A CADA 1000 VALE 1 SEGUNDO, ENTRE 0 e 1 SEG
				
				long time = System.currentTimeMillis();//VOU PEGAR O TEMPO, O HORARIO DA EXECUCAO DA LINHA
				
				
				FileInputStream fileInputStream = new FileInputStream(message.getFile());//VAI RECER O ARQUIVO DA MENSAGEM
				FileOutputStream fileOutputStream = new FileOutputStream("C:\\z\\" + time + "_" + message.getFile().getName());//VAI INDICAR ONDE EU QUERO QUE SALVE O ARQUIVO
				FileChannel fin = fileInputStream.getChannel();//CRIO UM CANAL PARA PASSAR O ARQUIVO NO IN CHEGANDO
				FileChannel fout = fileOutputStream.getChannel();//POR ONDE VAI SAIR O AQUIVO QUE QUERO SALVAR
				
				long size = fin.size(); //VAI PEGAR TAMANHO DO ARQUIVO
				
				fin.transferTo(0, size, fout);//AQUI EU PASSO O TAMANHO DO ARQUIVO E O QUE EU QUERO SALVAR
				
			} catch (FileNotFoundException e) {				
				e.printStackTrace();
			}catch (IOException e) {
				e.printStackTrace();
			}catch (Exception e) {
				e.printStackTrace();
			}
			
		}

		private void imprime(FileMessage message) {//METODO QUE VAI IMPRIMIR NO CONSOLE O CONTEUDO QUE RECEBEMOS DO ARQUIVO NAO VAI MAIS POS SUBISTITUI PLEO SALVAR
			try {
				FileReader fileReader = new FileReader(message.getFile());//COMECARMOS A LEITURA DO ARQUIVO
				BufferedReader bufferedReader = new BufferedReader(fileReader);
				
				String linha;
				
				while((linha=bufferedReader.readLine()) != null) {//PARA LER O NOSSO BUFFEREDREADER E ADD NA LINHA O CONTEUDO DE CADA LINHA LIDA DO ARQUIVO
					System.out.println(linha);
				}
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	public static void main(String[] args) {
		try {
			new Cliente();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
