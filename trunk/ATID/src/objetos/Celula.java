/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package objetos;

import interfaces.AtividadeIF;

import java.io.Serializable;

import com.mxgraph.model.mxCell;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 *
 * @author User
 */

public class Celula extends mxCell implements Serializable{

    @XStreamAlias("atividade")
    private AtividadeIF atividade;

    public Celula(AtividadeIF atividade) {
//        setValue(atividade);
        this.atividade = atividade;
    }

    public Celula(){

    }

    public AtividadeIF getAtividade() {
        return atividade;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Celula)){
            return false;
        }
        Celula outro = (Celula)obj;
        return outro.getAtividade().equals(this.getAtividade());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + (this.atividade != null ? this.atividade.hashCode() : 0);
        return hash;
    }



}
