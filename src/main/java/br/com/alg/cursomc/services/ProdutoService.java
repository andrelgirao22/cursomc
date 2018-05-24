package br.com.alg.cursomc.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import br.com.alg.cursomc.domain.Categoria;
import br.com.alg.cursomc.domain.Produto;
import br.com.alg.cursomc.repositories.CategoriaRepository;
import br.com.alg.cursomc.repositories.ProdutoRepository;
import br.com.alg.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class ProdutoService {
	
	@Autowired
	private ProdutoRepository repository;
	
	@Autowired
	private CategoriaRepository categoriaRepository;
	
	public Produto find(Integer id) {
		Optional<Produto> produto = this.repository.findById(id);
		return produto.orElseThrow(() -> new ObjectNotFoundException("Objeto nao encontrado! Id: " + id
				+ ", Tipo: " + Produto.class.getName()));
	}
	
	
	public Page<Produto> search(String nome, List<Integer> ids, Integer page, Integer linesPerPage, String orderby, String direction) {
		
		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderby);
		List<Categoria> categorias = this.categoriaRepository.findAllById(ids);
		return this.repository.findDistinctByNomeContainingAndCategoriasIn(nome, categorias, pageRequest);
		
	}
	
	
	
}
