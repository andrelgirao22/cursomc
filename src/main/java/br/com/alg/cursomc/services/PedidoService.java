package br.com.alg.cursomc.services;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.alg.cursomc.domain.Categoria;
import br.com.alg.cursomc.domain.Cliente;
import br.com.alg.cursomc.domain.ItemPedido;
import br.com.alg.cursomc.domain.PagamentoComBoleto;
import br.com.alg.cursomc.domain.Pedido;
import br.com.alg.cursomc.domain.enums.EstadoPagamento;
import br.com.alg.cursomc.repositories.ItemPedidoRepository;
import br.com.alg.cursomc.repositories.PagamentoRepository;
import br.com.alg.cursomc.repositories.PedidoRepository;
import br.com.alg.cursomc.security.UserSS;
import br.com.alg.cursomc.services.exceptions.AuthorizationException;
import br.com.alg.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class PedidoService {
	
	@Autowired
	private PedidoRepository repository;
	
	@Autowired
	private ClienteService clienteService;
	
	@Autowired
	private BoletoService boletoService;
	
	@Autowired
	private PagamentoRepository pagamentoRepository;
	
	@Autowired
	private ProdutoService produtoService;
	
	@Autowired
	private ItemPedidoRepository itemPedidoRepository;
	
	public Pedido find(Integer id) {
		Optional<Pedido> pedido = this.repository.findById(id);
		return pedido.orElseThrow(() -> new ObjectNotFoundException("Objeto nao encontrado! Id: " + id
				+ ", Tipo: " + Pedido.class.getName()));
	}

	@Autowired
	private EmailService emailService;
	
	@Transactional
	public Pedido insert(Pedido obj) {
		
		obj.setId(null);
		obj.setInstante(new Date());
		
		obj.setCliente(this.clienteService.find(obj.getCliente().getId()));
		
		obj.getPagamento().setEstado(EstadoPagamento.PENDENTE);
		obj.getPagamento().setPedido(obj);
		if(obj.getPagamento() instanceof PagamentoComBoleto) {
			PagamentoComBoleto pagto = (PagamentoComBoleto) obj.getPagamento();
			boletoService.preencherPagamentoComBoleto(pagto, obj.getInstante());
		}
		
		obj = repository.save(obj);
		
		this.pagamentoRepository.save(obj.getPagamento());
		for(ItemPedido item: obj.getItens()) {
			item.setDesconto(0.0);
			item.setProduto(this.produtoService.find(item.getProduto().getId()));
			Double preco = item.getProduto().getPreco();
			item.setPreco(preco);
			item.setPedido(obj);
		}
		
		this.itemPedidoRepository.saveAll(obj.getItens());
		this.emailService.sendOrderConfirmationHtmlEmail(obj);
		
		return obj;
	}
	
	public Page<Pedido> findPage(Integer page, Integer linesPerPage, String orderby, String direction) {
		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderby);
		
		UserSS user = UserService.authenticated();
		if(user == null ) {
			throw new AuthorizationException("Acesso negado");
		}
		Cliente cliente = this.clienteService.find(user.getId());
		
		return this.repository.findByCliente(cliente, pageRequest);
		
	}
	
}
