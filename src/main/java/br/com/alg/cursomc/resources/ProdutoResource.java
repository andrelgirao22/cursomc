package br.com.alg.cursomc.resources;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.alg.cursomc.domain.Produto;
import br.com.alg.cursomc.dto.ProdutoDTO;
import br.com.alg.cursomc.resources.utils.URL;
import br.com.alg.cursomc.services.ProdutoService;

@RestController
@RequestMapping(value="/produtos")
public class ProdutoResource {

	@Autowired
	private ProdutoService service;
	
	@GetMapping(value="/{id}")
	public ResponseEntity<Produto> find(@PathVariable("id") Integer id) {
		
		Produto produto = this.service.find(id);
		
		return ResponseEntity.ok(produto);
	}
	
	@GetMapping
	public ResponseEntity<Page<ProdutoDTO>> findPage(
			@RequestParam(value="nome", defaultValue="") String nome,
			@RequestParam(value="categorias", defaultValue="") String categorias,
			@RequestParam(value="page", defaultValue="0") Integer page, 
			@RequestParam(value="linesPerPage", defaultValue="24") Integer linesPerPage, 
			@RequestParam(value="orderby", defaultValue="nome") String orderby, 
			@RequestParam(value="direction", defaultValue="ASC") String direction) {
		List<Integer> ids  = URL.decodeIntList(categorias);
		String nomeDecode = URL.decodeParam(nome);
		Page<Produto> produtos = 
				this.service.search(nomeDecode, ids, page, linesPerPage, orderby, direction);
		Page<ProdutoDTO> listDto = 
				produtos.map(obj -> new ProdutoDTO(obj));
		return ResponseEntity.ok(listDto);
	}
	
	
	
}
