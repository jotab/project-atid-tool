/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import com.mxgraph.io.mxCodec;
import com.mxgraph.util.mxUtils;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import objetos.Rede;
import org.w3c.dom.Document;

/**
 *
 * @author User
 */
public class XMLEncoder {

    public XMLEncoder() {
    }

    public static String encode(Object obj) throws UnsupportedEncodingException,  IOException {
//        mxCodecRegistry.addPackage("objetos");
//        mxCodecRegistry.register(new mxObjectCodec(new objetos.AtividadeSimples()));

        mxCodec codec = new mxCodec();
        String xml = mxUtils.getXml(codec.encode(obj));
//        System.out.println("xml= " + xml);
        return xml;
//        Document document = mxUtils.parseXml(xml);
//        codec = new mxCodec(document);
//        Object rede = codec.decode(document.getDocumentElement(), obj);
//        return rede;
    }

//    public static Object decode(String xml){
////        mxCodec codec = new mxCodec();
////        String xml = mxUtils.getXml(codec.encode(obj));
////        return xml;

}
