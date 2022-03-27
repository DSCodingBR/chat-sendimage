package com.diego.app.bean;

import java.io.File;
import java.io.Serializable;

public class FileMessage implements Serializable {	// classe responsavel pelo conteudo da mensagem, IMPORTNATO Serializable POIS IREI PRECISAR Q MEU OBJETO SEJA SERIALIZADO
	private String client;// QUE SERA O NOME DO CLIENTE QUE ESTA ENVIANDO A MENSAGEM
	private File file;// QUE IRA CONTER O ARQUIVO QUE SERA ENVIADO
	
	public FileMessage(String client, File file) {
		this.client = client;
		this.file = file;
		
	}

	public FileMessage(String client) {
		this.client = client;
	}

	public FileMessage() {
		
	}

	public String getClient() {
		return client;
	}

	public File getFile() {
		return file;
	}
	
	
	
}
