package br.com.alg.cursomc.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.alg.cursomc.domain.Cliente;
import br.com.alg.cursomc.repositories.ClienteRepository;
import br.com.alg.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class ClienteService {
	
	@Autowired
	private ClienteRepository repository;
	
	public Cliente find(Integer id) {
		Optional<Cliente> categoria = this.repository.findById(id);
		return categoria.orElseThrow(() -> new ObjectNotFoundException("Objeto nao encontrado! Id: " + id
				+ ", Tipo: " + Cliente.class.getName()));
	}
	
}
