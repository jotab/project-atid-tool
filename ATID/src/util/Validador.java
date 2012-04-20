package util;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author User
 */
public class Validador {

    /* Verifica se uma String so contem digitos.
     * @param texto A string a ser verificadada e validada.
     * @return true: Se a string apenas possui digitos.
     * 			false: caso a String possua alguma letra.
     */
    public static boolean validaStringSemLetras(String texto) {
        try {
            Double.parseDouble(texto);
            return true;
        } catch (NumberFormatException nfex) {
            return false;
        }
    }
}
