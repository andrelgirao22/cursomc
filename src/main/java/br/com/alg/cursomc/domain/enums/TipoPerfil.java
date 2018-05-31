package br.com.alg.cursomc.domain.enums;

public enum TipoPerfil {

	ADMIN(1, "ROLE_ADMIN"),
	CLIENTE(2, "ROLE_CLIENTE");
	
	private int codigo;
	private String descricao;
	
	private TipoPerfil(int codigo, String descricao) {
		this.codigo = codigo;
		this.descricao = descricao;
	}
	
	public int getCodigo() {
		return codigo;
	}
	
	public String getDescricao() {
		return descricao;
	}
	
	public static TipoPerfil toEnum(Integer cod) {
		if(cod == null) {
			return null;
		}
		
		for(TipoPerfil tc: TipoPerfil.values()) {
			if(cod.equals(tc.getCodigo())) {
				return tc;
			}
		}
		
		throw new IllegalArgumentException("Id inválido " + cod);
	}
}
