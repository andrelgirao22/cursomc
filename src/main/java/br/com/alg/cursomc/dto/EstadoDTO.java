package br.com.alg.cursomc.dto;

import java.io.Serializable;

import br.com.alg.cursomc.domain.Estado;

public class EstadoDTO implements Serializable {


	private static final long serialVersionUID = 1L;
	private Integer id;
	private String name;

	public EstadoDTO(Estado estado) {
		this.id = estado.getId();
		this.name = estado.getName();
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
}
