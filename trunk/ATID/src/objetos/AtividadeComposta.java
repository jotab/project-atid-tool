/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package objetos;

import interfaces.AtividadeIF;
import interfaces.No;

import java.io.Serializable;

import com.mxgraph.model.mxCell;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 *
 * @author User
 */
public class AtividadeComposta extends No implements AtividadeIF, Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -3675041524784303269L;
	
	private Rede redeDeAtividades;
    private mxCell atividade;
    private static int contador;
    
    @XStreamAlias("id")
    private String id;

    
    public AtividadeComposta(Rede redeDeAtividades) {
        this.redeDeAtividades = redeDeAtividades;
    }

    public AtividadeComposta(mxCell atividade) {
        this.atividade = atividade;
      //  this.atividade.setId("COMP" + (++contador));
    }

    public AtividadeComposta() {
        this.id = "COMP" + (++contador);
    }

    public Rede getRedeDeAtividades() {
        return redeDeAtividades;
    }

    public void setRedeDeAtividades(Rede redeDeAtividades) {
        this.redeDeAtividades = redeDeAtividades;
    }

    @Override
    public mxCell getAtividade() {
        return atividade;
    }

    @Override
    public void setAtividade(mxCell atividade) {
        this.atividade = atividade;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
       if(!(obj instanceof AtividadeComposta)){
           return false;
       }
       AtividadeComposta outro = (AtividadeComposta)obj;
       return outro.getId().equals(this.getId());
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 31 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }


	
     

}
