package interfaces;

import java.util.ArrayList;
import java.util.List;

import objetos.Arco;

public abstract class No implements RedeListener {
	private List<Arco> arcosDeEntrada;
	private List<Arco> arcosDeSaida;
	
	public No() {
		arcosDeEntrada = new ArrayList<Arco>();
		arcosDeSaida =  new ArrayList<Arco>();
	}
	
	public List<Arco> getArcosDeEntrada() {
		return arcosDeEntrada;
	}
	
	public List<Arco> getArcosDeSaida() {
		return arcosDeSaida;
	}
	
	public void addArcoDeEntrada(Arco arco) {
		arcosDeEntrada.add(arco);
	}
	
	public void addArcoDeSaida(Arco arco) {
		arcosDeSaida.add(arco);
	}
	
	@Override
	public void notificaDelecaoDeArco(Arco arco) {
		
	}

}
