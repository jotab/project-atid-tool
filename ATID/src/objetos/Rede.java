/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package objetos;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import interfaces.RedeListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.naming.spi.ObjectFactory;

/**
 *
 * @author User
 */
public class Rede implements Serializable{

   
	private static final long serialVersionUID = -6337947431836533525L;
	
	private mxGraphComponent graphComponent;
//    @XStreamAlias("Array")
    private  List<Object> elementos ;
    private List<RedeListener> listeners;
    private mxGraph graph;
    private static int contador;
//    @XStreamAlias("id")
    private String id ;

    public Rede(){
       
    }
    public Rede(mxGraphComponent redeDeAtividades) {
        setGraphComponent(redeDeAtividades);
        elementos = new ArrayList<Object>();
        listeners = new ArrayList<RedeListener>();
        gerarId();
    }

    public mxGraph getGraph() {
        return graph;
    }

    public void setGraph(mxGraph graph) {
        this.graph = graph;
    }

    public List<Object> getElementos() {
        return elementos;
    }

    public void setElementos(List<Object> elementos) {
        this.elementos = elementos;
    }
    
    public mxGraphComponent getGraphComponent() {
        return graphComponent;
    }

    public void setGraphComponent(mxGraphComponent graphComponent) {
        this.graphComponent = graphComponent;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Rede)){
            return false;
        }
        Rede outra = (Rede)obj;
        return outra.getId().equals(this.getId());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

   

    private void gerarId() {
        id = "REDE" + (++contador);
    }

    public String getId() {
        return id;
    }
    
    public void addListener(RedeListener listener){
    	if(!listeners.contains(listener)){
    		listeners.add(listener);
    	}
    }
    
    public void disparaNotificacaoDelecaoNaRede(Arco arco){
    	for (RedeListener l : listeners) {
			l.notificaDelecaoDeArco(arco);
		}
    }
	


    


}
