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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.com.alg.cursomc.domain.Cliente;
import br.com.alg.cursomc.dto.ClienteDTO;
import br.com.alg.cursomc.dto.ClienteNewDTO;
import br.com.alg.cursomc.services.ClienteService;

@RestController
@RequestMapping(value="/clientes")
public class ClienteResource {

	@Autowired
	private ClienteService service;
	
	@GetMapping(value="/{id}")
	public ResponseEntity<Cliente> find(@PathVariable("id") Integer id) {
		
		Cliente cliente = this.service.find(id);
		
		return ResponseEntity.ok(cliente);
	}
	
	@PreAuthorize("hasAnyHole('ADMIN')")
	@GetMapping(value="/page")
	public ResponseEntity<Page<ClienteDTO>> findPage(
			@RequestParam(value="page", defaultValue="0") Integer page, 
			@RequestParam(value="linesPerPage", defaultValue="24") Integer linesPerPage, 
			@RequestParam(value="orderby", defaultValue="nome") String orderby, 
			@RequestParam(value="direction", defaultValue="ASC") String direction) {
		Page<Cliente> Clientes = 
				this.service.findPage(page, linesPerPage, orderby, direction);
		Page<ClienteDTO> listDto = 
				Clientes.map(obj -> new ClienteDTO(obj));
		return ResponseEntity.ok(listDto);
	}
	
	@GetMapping
	public ResponseEntity<List<ClienteDTO>> findAll() {
		List<Cliente> Clientes = this.service.findAll();
		List<ClienteDTO> listDto = 
				Clientes.stream().map(obj -> new ClienteDTO(obj)).collect(Collectors.toList());
		return ResponseEntity.ok(listDto);
	}
	
	
	@PutMapping(value = "/{id}")
	public ResponseEntity<Void> update(@Valid @RequestBody ClienteDTO objDTO, @PathVariable("id") Integer id) {
		
		Cliente obj = this.service.fromDTO(objDTO);
		
		obj.setId(id);
		obj = this.service.update(obj);
		
		return ResponseEntity.noContent().build();
	}
	
	@PreAuthorize("hasAnyHole('ADMIN')")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
		this.service.delete(id);
		return ResponseEntity.noContent().build();
	}
	
	@PostMapping
	public ResponseEntity<Void> insert(@Valid @RequestBody ClienteNewDTO objDTO) {
		Cliente obj = this.service.fromDTO(objDTO);
		obj = this.service.insert(obj);
		
		URI uri = 
				ServletUriComponentsBuilder
				.fromCurrentRequest().path("/{id}")
				.buildAndExpand(obj.getId()).toUri();
		
		return ResponseEntity.created(uri).build();
	}
	
	@PostMapping("/picture")
	public ResponseEntity<Void> uploadProfilePicture(@RequestParam(name="file") MultipartFile file) {
		URI uri = this.service.uploadProfilePicture(file);
		return ResponseEntity.created(uri).build();
	}
	
}
