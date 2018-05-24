package br.com.alg.cursomc.services.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerMapping;

import br.com.alg.cursomc.domain.Cliente;
import br.com.alg.cursomc.dto.ClienteDTO;
import br.com.alg.cursomc.repositories.ClienteRepository;
import br.com.alg.cursomc.resources.exceptions.FieldMessage;

public class ClienteUpdateValidator implements ConstraintValidator<ClienteUpdate, ClienteDTO>{

	@Autowired
	private ClienteRepository clienteRepository;
	
	@Autowired
	private HttpServletRequest request;
	
	public void initialize(ClienteUpdate clienteUpdate) {
		
	}
	
	@Override
	public boolean isValid(ClienteDTO objDTO, ConstraintValidatorContext context) {
		
		@SuppressWarnings("unchecked")
		Map<String, String> map = 
				(Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		Integer uriId = Integer.parseInt(map.get("id"));
		
		List<FieldMessage> list = new ArrayList<>();
		
		Cliente cliAux = clienteRepository.findByEmail(objDTO.getEmail());
		if(cliAux != null && !cliAux.getId().equals(uriId)) {
			list.add(new FieldMessage("email", "Email já existente"));
		}
		
		for(FieldMessage fm: list) {
			context.disableDefaultConstraintViolation();
			context
			.buildConstraintViolationWithTemplate(fm.getMessage())
			.addPropertyNode(fm.getFieldName())
			.addConstraintViolation();
		}
		
		return list.isEmpty();
	}

	
}
