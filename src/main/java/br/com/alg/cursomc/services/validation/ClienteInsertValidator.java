package br.com.alg.cursomc.services.validation;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import br.com.alg.cursomc.domain.enums.TipoCliente;
import br.com.alg.cursomc.dto.ClienteNewDTO;
import br.com.alg.cursomc.resources.exceptions.FieldMessage;
import br.com.alg.cursomc.services.validation.utils.BR;

public class ClienteInsertValidator implements ConstraintValidator<ClienteInsert, ClienteNewDTO>{

	public void initialize(ClienteInsert clienteInsert) {
		
	}
	
	@Override
	public boolean isValid(ClienteNewDTO objDTO, ConstraintValidatorContext context) {
		List<FieldMessage> list = new ArrayList<>();
		
		if(objDTO.getTipo() == null) {
			list.add(new FieldMessage("tipo", "Tipo do Cliente deve ser informado"));
		}
		
		if(objDTO.getTipo() == null || objDTO.getTipo().equals(TipoCliente.PESSOA_FISICA.getCodigo()) && !BR.isValidCPF(objDTO.getCpfOuCnpj())) {
			list.add(new FieldMessage("cpfOuCnpj", "CPF inválido"));
		}
		
		if(objDTO.getTipo() == null || objDTO.getTipo().equals(TipoCliente.PESSOA_JURIDICA.getCodigo()) && !BR.isValidCNPJ(objDTO.getCpfOuCnpj())) {
			list.add(new FieldMessage("cpfOuCnpj", "CNPJ inválido"));
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
