package br.com.alg.cursomc.services;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import br.com.alg.cursomc.domain.Cidade;
import br.com.alg.cursomc.domain.Cliente;
import br.com.alg.cursomc.domain.Endereco;
import br.com.alg.cursomc.domain.enums.TipoCliente;
import br.com.alg.cursomc.dto.ClienteDTO;
import br.com.alg.cursomc.dto.ClienteNewDTO;
import br.com.alg.cursomc.repositories.ClienteRepository;
import br.com.alg.cursomc.repositories.EnderecoRepository;
import br.com.alg.cursomc.services.exceptions.DataIntegrityException;
import br.com.alg.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class ClienteService {
	
	@Autowired
	private ClienteRepository repository;
	
	@Autowired
	private EnderecoRepository enderecoRepository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private S3Service s3Service;
	
	public Cliente find(Integer id) {
		Optional<Cliente> categoria = this.repository.findById(id);
		return categoria.orElseThrow(() -> new ObjectNotFoundException("Objeto nao encontrado! Id: " + id
				+ ", Tipo: " + Cliente.class.getName()));
	}

	public Cliente update(Cliente obj) {
		Cliente newObj = find(obj.getId());
		updateDate(newObj, obj);
		return repository.save(newObj);
	}

	private void updateDate(Cliente newObj, Cliente obj) {
		newObj.setNome(obj.getNome());
		newObj.setEmail(obj.getEmail());
	}

	public void delete(Integer id) {
		this.find(id);
		try {			
			this.repository.deleteById(id);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityException("Não é possível excluir porque há pedidos relacionados");
		}
		
	}

	public List<Cliente> findAll() {
		return this.repository.findAll();
	}
	
	public Page<Cliente> findPage(Integer page, Integer linesPerPage, String orderby, String direction) {
		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderby);
		return repository.findAll(pageRequest);
	}
	
	public Cliente fromDTO(ClienteDTO clienteTO) {
		return new Cliente(clienteTO.getId(), clienteTO.getNome(), clienteTO.getEmail(), null, null, null);
	}

	public Cliente fromDTO(ClienteNewDTO objDTO) {
		
		Cliente cli = new Cliente(null, objDTO.getNome(), objDTO.getEmail(), objDTO.getCpfOuCnpj(), TipoCliente.toEnum(objDTO.getTipo()), passwordEncoder.encode(objDTO.getSenha()));
		Cidade cid = new Cidade(objDTO.getCidadeId(), null, null);
		Endereco endereco = 
				new Endereco(null, objDTO.getLogradouro(), objDTO.getNumero(), objDTO.getComplemento(), objDTO.getBairro(), objDTO.getCep(), cli, cid);
		
		
		cli.getEnderecos().add(endereco);
		cli.getTelefones().add(objDTO.getTelefone1());
		if(objDTO.getTelefone2() != null) {
			cli.getTelefones().add(objDTO.getTelefone2());
		}
		
		if(objDTO.getTelefone3() != null) {
			cli.getTelefones().add(objDTO.getTelefone3());
		}
		
		return cli; 
	}

	@Transactional
	public Cliente insert(Cliente obj) {
		obj.setId(null);
		obj =  repository.save(obj);
		enderecoRepository.saveAll(obj.getEnderecos());
		return obj;
	}

	public Cliente findByEmail(String email) {
		return this.repository.findByEmail(email);
	}
	
	public URI uploadProfilePicture(MultipartFile multipartFile) {
		return this.s3Service.uploadFile(multipartFile);
	}
	
}
