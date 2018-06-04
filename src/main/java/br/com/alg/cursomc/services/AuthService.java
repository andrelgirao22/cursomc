package br.com.alg.cursomc.services;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.alg.cursomc.domain.Cliente;
import br.com.alg.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class AuthService {

	@Autowired
	private ClienteService clienteService;
	
	@Autowired
	private BCryptPasswordEncoder pe;
	
	@Autowired
	private EmailService emailService;
	
	private Random random = new Random();
	
	public void sendNewPassword(String email) {
		
		Cliente cliente = this.clienteService.findByEmail(email);
		if(cliente == null) {
			throw new ObjectNotFoundException("Email não encontrado");
		}
		
		String newPass = newPassword();
		cliente.setSenha(this.pe.encode(newPass));
		
		this.clienteService.update(cliente);
		
		this.emailService.sendNewPasswordEmail(cliente, newPass);
	}

	private String newPassword() {
		char [] vet = new char [10];
		for(int i = 0; i< 10; i++) {
			vet[i] = randomChar();
		}
		return new String(vet);
	}

	private char randomChar() {
		int opt = random.nextInt(3);
		if(opt == 0) { //gerar um digito
			return (char) (random.nextInt(10) + 48);
		} else if(opt == 1){ //gera letra maiuscula 
			return (char) (random.nextInt(26) + 65);
		} else { //gera letra minuscula
			return (char) (random.nextInt(26) + 97);
		}
	}
	
}
