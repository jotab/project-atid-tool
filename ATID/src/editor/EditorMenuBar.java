package editor;


import java.awt.BorderLayout;
import java.awt.PopupMenu;
import javax.swing.JMenuBar;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author User
 */
public class EditorMenuBar extends JMenuBar  {

    private EditorRA editor;
    public EditorMenuBar(EditorRA editor) {
        this.editor = editor;
        JMenuBar menu = new JMenuBar();
        menu.add(new PopupMenu("Arquivo"));
        this.editor.getContentPane().add(menu,BorderLayout.NORTH);
    }



}
