/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package interfaces;

import com.mxgraph.model.mxCell;
import java.io.Serializable;

/**
 *
 * @author User
 */
public interface AtividadeIF {

//    String getNome();
//    void setNome(String nome);
    mxCell getAtividade();
    void setAtividade(mxCell atividade);
//    String getCusto();
//    void setCusto(String custo);
//    int getTempo();
//    void setTempo(int tempo);
    String getId();
    void setId(String id);
    @Override
    boolean equals(Object obj);

}
