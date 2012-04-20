package editor;


import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JToolBar;
import objetos.Rede;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author User
 */
public class EditorCellTemplate {

    

        public void addTemplate(final String name, ImageIcon icon, String style,
            int width, int height, Object value, boolean isDragDrop, JToolBar toolBar, Rede redePrincipal) {

        mxCell cell = new mxCell(value, new mxGeometry(0, 0, width, height),
                style);
        cell.setVertex(true);
        addTemplate(name, icon,cell, isDragDrop, toolBar, redePrincipal);

    }

    private  void addTemplate(String name, ImageIcon icon,mxCell cell ,boolean isDragDrop, JToolBar toolBar, Rede redePrincipal) {
        
       

        // Scales the image if it's too large for the library
        if (icon != null) {
            if (icon.getIconWidth() > 32 || icon.getIconHeight() > 32) {
                icon = new ImageIcon(icon.getImage().getScaledInstance(32, 32,
                        0));
            }
        }

        final JLabel entry = new JLabel(icon);
        entry.setPreferredSize(new Dimension(50, 50));
        entry.setFont(new Font(entry.getFont().getFamily(), 0, 10));
        entry.setBackground(toolBar.getBackground().brighter());

        entry.setVerticalTextPosition(JLabel.BOTTOM);
        entry.setHorizontalTextPosition(JLabel.CENTER);
        entry.setIconTextGap(0);

        entry.setToolTipText(name);
        entry.setText(name);
        if(isDragDrop){
            EditorDeAcoes.DragDropAction.addDragDropListener(entry,cell,toolBar, redePrincipal);

        }else{
            toolBar.add(entry);
        }
        
    }

   
}
