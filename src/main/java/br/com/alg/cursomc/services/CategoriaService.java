package br.com.alg.cursomc.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import br.com.alg.cursomc.domain.Categoria;
import br.com.alg.cursomc.repositories.CategoriaRepository;
import br.com.alg.cursomc.services.exceptions.DataIntegrityException;
import br.com.alg.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class CategoriaService {
	
	@Autowired
	private CategoriaRepository repository;
	
	public Categoria find(Integer id) {
		Optional<Categoria> categoria = this.repository.findById(id);
		return categoria.orElseThrow(() -> new ObjectNotFoundException("Objeto nao encontrado! Id: " + id
				+ ", Tipo: " + Categoria.class.getName()));
	}

	public Categoria insert(Categoria obj) {
		obj.setId(null);
		return repository.save(obj);
	}

	public Categoria update(Categoria obj) {
		find(obj.getId());
		return repository.save(obj);
	}

	public void delete(Integer id) {
		this.find(id);
		try {			
			this.repository.deleteById(id);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityException("Não é possível excluir uma categoria que possui produtos");
		}
		
	}

	public List<Categoria> findAll() {
		return this.repository.findAll();
	}
	
}
