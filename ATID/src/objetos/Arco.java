package objetos;

import interfaces.No;

public class Arco {
	
	private No fonte;
	private No destino;
	private final double CAPACIDADE_INICIAL = Double.MAX_VALUE;
	private double capacidade;
	
	public Arco(No fonte, No destino) {
		this.capacidade = CAPACIDADE_INICIAL;
		this.fonte = fonte;
		this.destino = destino;
		atualizaFonteEDestino();
	}

	public No getFonte() {
		return fonte;
	}

	public void setFonte(No fonte) {
		this.fonte = fonte;
	}

	public No getDestino() {
		return destino;
	}

	public void setDestino(No destino) {
		this.destino = destino;
	}
	
	public void atualizaFonteEDestino(){
		fonte.addArcoDeSaida(this);
		destino.addArcoDeEntrada(this);
	}
	
	public enum DirecaoDoArco {
		ATIVIDADE_TRANSICAO,
		TRANSICAO_ATIVIDADE
		
		
	}

	
}
