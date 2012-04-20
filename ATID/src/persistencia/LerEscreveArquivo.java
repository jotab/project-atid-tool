/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package persistencia;

import com.mxgraph.io.mxCodec;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxUtils;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import objetos.Rede;

/**
 *
 * @author Rodrigo,Pedro,Yuri
 */
public class LerEscreveArquivo {

    private static BufferedWriter writer;
    private static BufferedReader reader;
    private static XStream xstream = new XStream(new DomDriver("UTF-8"));

    /**
     * Salva uma List em um certo arquivo.
     * @param <T> O tipo dos objetos que a lista guarda.
     * @param rede Rede de objetos a ser salva no arquivo.
     * @param arquivo Arquivo onde a informacao vai ser salva.
     * @throws IOException
     */
    public static void salvarObjeto(Rede rede, File arquivo) throws IOException {
        try {
            writer = new BufferedWriter(new FileWriter(arquivo));
            mxCodec codec = new mxCodec();
            String xml = mxUtils.getXml(codec.encode(rede.getGraphComponent().getGraph().getModel()));
            String objetos = xstream.toXML(rede);
            objetos = trocarCaracteresInvalidos(objetos);
            writer.write(objetos);
        } catch (IOException ioex) {
            ioex.printStackTrace();
        } finally {
            writer.close();
        }
    }

    /**
     * Carrega (ler) todos os dados contidos em um certo arquivo.
     * @param <T> O tipo de objetos que a lista a ser retornada deve conter.
     * @param arquivo O arquivo a ser lido.
     * @return Uma lista que estava armazenada no arquivo.
     * @throws IOException
     */
    public static Rede lerArquivo(File arquivo) throws IOException {
        Rede rede = null;
        mxGraphModel model = null;
        try {
            reader = new BufferedReader(new FileReader(arquivo));
//            xstream.processAnnotations(Rede.class);
//            xstream.processAnnotations(Celula.class);
//            xstream.processAnnotations(AtividadeSimples.class);
//            xstream.processAnnotations(AtividadeComposta.class);
            xstream.alias("mxGraphModel", mxGraphModel.class);
            xstream.alias("mxGraphComponent", mxGraphComponent.class);
            xstream.alias("mxCell", mxCell.class);
            xstream.alias("Rede", objetos.Rede.class);
            xstream.alias("Array", Array.class);
            xstream.alias("AtividadeBasica", objetos.AtividadeBasica.class);
             xstream.alias("AtividadeBasica", objetos.AtividadeComposta.class);
//            xstream.alias("Celula", objetos.Celula.class);
            rede = (Rede) xstream.fromXML(reader);
//            model = (mxGraphModel) xstream.fromXML(reader);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ioex) {
            ioex.printStackTrace();
        } finally {
            reader.close();
        }
        return rede;
    }

    /**
     * Verifica se um arquivo ja existe.
     * @param arquivo O arquivo a ser verificado.
     * @return true: se o arquivo existir, false: caso contrario.
     */
    public static boolean existeXml(File arquivo) {
        return arquivo.exists();
    }

    private static String trocarCaracteresInvalidos(String in) {
        if (in == null || ("".equals(in))) {
            return null;
        }
        StringBuffer out = new StringBuffer(in);
        for (int i = 0; i < out.length(); i++) {
            if (out.charAt(i) == '&') {
                out.setCharAt(i, '-');
            }
        }
        return out.toString();
    }
}
