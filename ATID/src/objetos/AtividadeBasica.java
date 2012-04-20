/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package objetos;

import com.mxgraph.model.mxCell;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import interfaces.AtividadeIF;
import interfaces.No;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author User
 */
public class AtividadeBasica extends No implements AtividadeIF,Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -2314603040457259619L;
	private mxCell atividade;
    private String custo;
    private int tempo;
    private String nome;

    @XStreamAlias("id")
    private String id;
    
    private static int contador;

    public AtividadeBasica(mxCell atividade)  {
        this.atividade = atividade;
//        this.nome= "ATV" + ++contador;
        this.atividade.setId("SIMP" + (++contador));
        this.atividade.setVertex(true);
    }

    public AtividadeBasica() {
        this.id= "ATV" + (++contador);
    }
   
    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public mxCell getAtividade() {
        return atividade;
    }

    public void setAtividade(mxCell atividade) {
        this.atividade = atividade;
    }

    public String getCusto() {
        return custo;
    }

    public void setCusto(String custo) {
        this.custo = custo;
    }

    public int getTempo() {
        return tempo;
    }

    public void setTempo(int tempo) {
        this.tempo = tempo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    

    @Override
    public boolean equals(Object obj) {
         if(!(obj instanceof AtividadeBasica)){
           return false;
       }
       AtividadeBasica outro = (AtividadeBasica)obj;
       return outro.getId().equals(this.getId());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }


}
