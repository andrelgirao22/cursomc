package br.com.alg.cursomc.resources;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.com.alg.cursomc.domain.Categoria;
import br.com.alg.cursomc.dto.CategoriaDTO;
import br.com.alg.cursomc.services.CategoriaService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping(value="/categorias")
public class CategoriaResource {

	@Autowired
	private CategoriaService service;
	
	@GetMapping(value="/page")
	public ResponseEntity<Page<CategoriaDTO>> findPage(
			@RequestParam(value="page", defaultValue="0") Integer page, 
			@RequestParam(value="linesPerPage", defaultValue="24") Integer linesPerPage, 
			@RequestParam(value="orderby", defaultValue="nome") String orderby, 
			@RequestParam(value="direction", defaultValue="ASC") String direction) {
		Page<Categoria> categorias = 
				this.service.findPage(page, linesPerPage, orderby, direction);
		Page<CategoriaDTO> listDto = 
				categorias.map(obj -> new CategoriaDTO(obj));
		return ResponseEntity.ok(listDto);
	}
	
	@GetMapping
	public ResponseEntity<List<CategoriaDTO>> findAll() {
		List<Categoria> categorias = this.service.findAll();
		List<CategoriaDTO> listDto = 
				categorias.stream().map(obj -> new CategoriaDTO(obj)).collect(Collectors.toList());
		return ResponseEntity.ok(listDto);
	}
	
	@ApiOperation(value="Buca por Id")
	@GetMapping(value="/{id}")
	public ResponseEntity<Categoria> find(@PathVariable("id") Integer id) {
		
		Categoria categoria = this.service.find(id);
		
		return ResponseEntity.ok(categoria);
	}
	
	@PreAuthorize("hasAnyRole('ADMIN')")
	@PostMapping
	public ResponseEntity<Void> insert(@Valid @RequestBody CategoriaDTO objDTO) {
		Categoria obj = this.service.fromDTO(objDTO);
		obj = this.service.insert(obj);
		
		URI uri = 
				ServletUriComponentsBuilder
				.fromCurrentRequest().path("/{id}")
				.buildAndExpand(obj.getId()).toUri();
		
		return ResponseEntity.created(uri).build();
	}
	
	@PreAuthorize("hasAnyRole('ADMIN')")
	@PutMapping(value = "/{id}")
	public ResponseEntity<Void> update(@Valid @RequestBody CategoriaDTO objDTO, @PathVariable("id") Integer id) {
		
		Categoria obj = this.service.fromDTO(objDTO);
		
		obj.setId(id);
		obj = this.service.update(obj);
		
		return ResponseEntity.noContent().build();
	}
	
	@PreAuthorize("hasAnyRole('ADMIN')")
	@DeleteMapping("/{id}")
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "Não é possível excluir uma categoria que possui produtos"),  
			@ApiResponse(code = 404, message = "Código inexistente")
		}) 
	public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
		this.service.delete(id);
		return ResponseEntity.noContent().build();
	}
	
}
