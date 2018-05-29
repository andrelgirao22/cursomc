package br.com.alg.cursomc.services;

import org.springframework.mail.SimpleMailMessage;

import br.com.alg.cursomc.domain.Pedido;

public interface EmailService {

	void sendOrderConfirmationEmail(Pedido pedido);
	
	void sendEmail(SimpleMailMessage msg);
	
}
