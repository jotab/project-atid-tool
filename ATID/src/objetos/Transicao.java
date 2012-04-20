/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package objetos;

import interfaces.AtividadeIF;
import interfaces.No;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author User
 */
public class Transicao extends No implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7585134029186619809L;
	private boolean disparada = false;
	private AtividadeIF source ;
	private AtividadeIF target; 
	List<Arco> arcosEntrada = new ArrayList<Arco>();
	List<Arco> arcosSaida = new ArrayList<Arco>();
	
	public Transicao(){
		
	}

	public AtividadeIF getSource() {
		return source;
	}

	public void setSource(AtividadeIF source) {
		this.source = source;
	}

	public AtividadeIF getTarget() {
		return target;
	}

	public void setTarget(AtividadeIF target) {
		this.target = target;
	}

	public void setDisparar(boolean disparar) {
		this.disparada = disparar;
	}

	public boolean isDisparada() {
		return disparada;
	}

	
	

}
