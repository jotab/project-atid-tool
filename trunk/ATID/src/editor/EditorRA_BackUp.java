package editor;


import editor.EditorDeAcoes;
import com.mxgraph.model.mxCell;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxGraphActions;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.util.mxUndoManager;
import com.mxgraph.util.mxUndoableEdit;
import com.mxgraph.util.mxUndoableEdit.mxUndoableChange;
import com.mxgraph.view.mxGraph;
import javax.swing.Action;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import objetos.AtividadeBasica;

public class EditorRA_BackUp extends JApplet implements KeyListener {

    private mxGraph graph;
    private mxGraphComponent graphComponent;
    JButton botao = new JButton("Teste");
    JTextField campo = new JTextField();
    private int contadorVertices;
    private Action remove, group, ungroup, copy, paste;
    int coordenadaX, coordenadaY;
    private File currentFile;
    private mxUndoManager undoManager;
    protected mxIEventListener undoHandler = new mxIEventListener() {

        @Override
        public void invoke(Object source, mxEventObject evt) {
            undoManager.undoableEditHappened((mxUndoableEdit) evt.getProperty("edit"));
        }
    };

    public EditorRA_BackUp() {
    }

    private mxGraphComponent createGraph() {
        graph = new mxGraph();
        mxGraphComponent graphComponent = new mxGraphComponent(graph);
        return graphComponent;
    }

    private JToolBar createToolBar() {
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);

        //Inserir
        URL inserirUrl = getClass().getClassLoader().getResource("org/jgraph/example/resources/insert.gif");
        final ImageIcon insertIcon = new ImageIcon(inserirUrl);
        final JLabel labelIcon = new JLabel(insertIcon);
        labelIcon.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                labelIcon.setIcon(new ImageIcon(getClass().getClassLoader().
                        getResource("org/jgraph/example/resources/insert_clicked.gif")));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                int coordenadaX = e.getX();
                int coordenadaY = e.getY();
                addVertex(coordenadaX, coordenadaY);
                labelIcon.setIcon(insertIcon);
                campo.setText("posX= " + coordenadaX + " posy= " + coordenadaY);
            }
        });
        toolbar.add(labelIcon);

        //Remover
        URL removerUrl = getClass().getClassLoader().getResource("org/jgraph/example/resources/delete.gif");
        ImageIcon removerIcon = new ImageIcon(removerUrl);
        remove = (new AbstractAction("", removerIcon) {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!graph.isSelectionEmpty()) {
                    Object[] cells = graph.getSelectionCells();
                    for (int i = 0; i < cells.length; i++) {
                        // se a celula removida possuir arestas conectadas a ela,
                        // estas arestas tb serao removidas
                        if (graph.getEdges(cells[i]).length > 0) {
                            graph.removeCells(cells, true);
                        } else {
                            graph.getModel().remove(cells[i]);
                        }

                    }

//                    mxCell cell = (mxCell) graph.getSelectionCell();
//                    graph.getModel().remove(cell);
                }

            }
        });
        toolbar.add(remove);

//        // Ativa/desativa conexao de arestas.
//        URL connectUrl = getClass().getClassLoader().getResource(
//                "org/jgraph/example/resources/connecton.gif");
//        ImageIcon connectIcon = new ImageIcon(connectUrl);
//        toolbar.add(new AbstractAction("", connectIcon) {
//
//            public void actionPerformed(ActionEvent e) {
//                URL connectUrl;
//                graphComponent.setConnectable(!graphComponent.isConnectable());
//                if (graphComponent.isConnectable()) {
//                    connectUrl = getClass().getClassLoader().getResource(
//                            "org/jgraph/example/resources/connecton.gif");
//                } else {
//                    connectUrl = getClass().getClassLoader().getResource(
//                            "org/jgraph/example/resources/connectoff.gif");
//                }
//                ImageIcon connectIcon = new ImageIcon(connectUrl);
//                putValue(SMALL_ICON, connectIcon);
//
//            }
//        });
        //  conecta arestas.
        URL connectUrl = getClass().getClassLoader().getResource(
                "org/jgraph/example/resources/connecton.gif");
        ImageIcon connectIcon = new ImageIcon(connectUrl);
        toolbar.add(new AbstractAction("", connectIcon) {

            @Override
            public void actionPerformed(ActionEvent e) {
                Object[] cells = graph.getSelectionCells();
                if (cells.length == 2) {
                    Object source = cells[0];
                    Object target = cells[1];
                    graph.insertEdge(graph.getDefaultParent(), null, "aresta", source, target);
                }
            }
        });
        // agrupa vertices
        URL groupUrl = getClass().getClassLoader().getResource(
                "org/jgraph/example/resources/group.gif");
        ImageIcon groupIcon = new ImageIcon(groupUrl);
        toolbar.add(vincularAcao("Group", mxGraphActions.getGroupAction(), groupIcon));
        // Ungroup
        URL ungroupUrl = getClass().getClassLoader().getResource(
                "org/jgraph/example/resources/ungroup.gif");
        ImageIcon ungroupIcon = new ImageIcon(ungroupUrl);
        toolbar.add(vincularAcao("Group", mxGraphActions.getUngroupAction(), ungroupIcon));
        // ver filhos
        URL childrenURL = getClass().getClassLoader().getResource(
                "org/jgraph/example/resources/jgraph.gif");
        ImageIcon childrenIcon = new ImageIcon(childrenURL);
        toolbar.add(new AbstractAction("", childrenIcon) {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (((mxCell) graph.getSelectionCell()).isEdge()) {
                    getAtributosArestas(graph.getSelectionCell());
                } else {
                    getDescendentes(graph.getSelectionCell());
                }
            }
        });
        Action action;
        URL url;
        // Copy
        action = javax.swing.TransferHandler.getCopyAction();
        url = getClass().getClassLoader().getResource(
                "org/jgraph/example/resources/copy.gif");
        toolbar.add(copy = new EventRedirector(action, new ImageIcon(url)));
        // Paste
        action = javax.swing.TransferHandler.getPasteAction();
        url = getClass().getClassLoader().getResource(
                "org/jgraph/example/resources/paste.gif");
        toolbar.add(paste = new EventRedirector(action, new ImageIcon(url)));
        // save
        URL saveURL = getClass().getClassLoader().getResource("org/jgraph/example/resources/saveas.gif");
        ImageIcon saveIcon = new ImageIcon(saveURL);
        toolbar.add(vincularAcao("Save", new EditorDeAcoes.SaveAction(true), saveIcon));
        //undo
        URL undoURL = getClass().getClassLoader().getResource("org/jgraph/example/resources/undo.gif");
        ImageIcon undoIcon = new ImageIcon(undoURL);
        toolbar.add(vincularAcao("Undo", new EditorDeAcoes.HistoryAction(true),
                undoIcon));
        //redo
        URL redoURL = getClass().getClassLoader().getResource("org/jgraph/example/resources/redo.gif");
        ImageIcon redoIcon = new ImageIcon(redoURL);
        toolbar.add(vincularAcao("Redo", new EditorDeAcoes.HistoryAction(false),
                redoIcon));


        return toolbar;
    }

    /*
     * vincula um icon a uma Action ja existente
     */
    public Action vincularAcao(String name, final Action action, ImageIcon icon) {
        return new AbstractAction(name, icon) {

            @Override
            public void actionPerformed(ActionEvent e) {
                action.actionPerformed(new ActionEvent(getGraphComponent(), e.getID(), e.getActionCommand()));
            }
        };
    }

    public Action vincularAcao(String name, final Action action) {
        return vincularAcao(name, action, null);
    }

    private JPopupMenu createPopMenu() {
        final JPopupMenu menu = new JPopupMenu();
         //Add nome
        final JTextField txtField3 = new JTextField();
        menu.add("Nome:");
        menu.add(txtField3);
        JButton botaoOK3 = new JButton("OK");
        menu.add(botaoOK3);
        botaoOK3.addActionListener(new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String tempo = txtField3.getText();
                if (!tempo.equals("")) {
                    txtField3.setEditable(false);
                }
            }
        });
        menu.addSeparator();
        //Add tempo
        final JTextField txtField = new JTextField();
        menu.add("Tempo:");
        menu.add(txtField);
        JButton botaoOK = new JButton("OK");
        menu.add(botaoOK);
        botaoOK.addActionListener(new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String tempo = txtField.getText();
                if (!tempo.equals("")) {
                    txtField.setEditable(false);
                }
            }
        });
        menu.addSeparator();
        //Add custo
        final JTextField txtField2 = new JTextField();
        menu.add("Custo:");
        menu.add(txtField2);
        JButton botaoOK2 = new JButton("OK");
        menu.add(botaoOK2);
        botaoOK2.addActionListener(new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String custo = txtField2.getText();
                if (!custo.equals("")) {
                    txtField2.setEditable(false);
                }
            }
        });
        menu.addSeparator();
        //entrar no grupo
        menu.add(vincularAcao("Exibir subgrafo", mxGraphActions
				.getEnterGroupAction())
				);
        menu.addSeparator();
        //sair do grupo
        menu.add(vincularAcao("Sair do grupo", mxGraphActions
				.getExitGroupAction()));
        menu.addSeparator();
        //Selecionar tudo
        menu.add(vincularAcao("Selecionar Tudo", mxGraphActions.getSelectAllAction()));
        return menu;
    }

 
    private void getDescendentes(final Object cell) {
        graph.traverse(cell, true, new mxGraph.mxICellVisitor() {

            @Override
            public boolean visit(Object vertex, Object edge) {
                String saida = "";
                Object source = cell;
                saida += "edge=" + graph.convertValueToString(edge)
                        + " vertex=" + graph.convertValueToString(vertex)
                        + " quant. de filhos=" + ((mxCell) source).getChildCount();

                campo.setText(saida);

                return true;
            }
        });
    }

    private void getAtributosArestas(final Object cell) {
        graph.traverse(cell, true, new mxGraph.mxICellVisitor() {

            @Override
            public boolean visit(Object vertex, Object edge) {
                mxCell aresta = (mxCell) cell;
                String saida = "";
                saida += "source=" + graph.convertValueToString(aresta.getSource())
                        + " target=" + graph.convertValueToString(aresta.getTarget());

                campo.setText(saida);
                return true;
            }
        });
    }

    private void addVertex(int x, int y) {
        mxCell vertex = new mxCell();
        graph.getModel().beginUpdate();
        Object parent = graph.getDefaultParent();
        vertex = (mxCell)graph.insertVertex(parent, null, "Vertice " + contadorVertices++, x, y, 100, 50);
        AtividadeBasica atividadeSimples = new AtividadeBasica(vertex);
        //graph.addCell(vertex);
        graph.getModel().endUpdate();
    }

    protected void installListeners(final mxGraphComponent graphComponent) {
        graphComponent.getGraphControl().addKeyListener(this);
        graphComponent.getGraphControl().addMouseListener(new TratadorDeEventosDoMouse());

        graphComponent.getGraphControl().addMouseMotionListener(new MouseMotionListener() {

            @Override
            public void mouseMoved(MouseEvent e) {
                mouseDragged(e);

            }

            @Override
            public void mouseDragged(MouseEvent e) {
                campo.setText("x: " + e.getX() + " y: " + e.getY());
            }
        });




    }

    private void populateContentPane() {
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(createToolBar(), BorderLayout.NORTH);
        getContentPane().add(campo, BorderLayout.SOUTH);

    }

    @Override
    public void keyTyped(KeyEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // Listen for Delete Key Press
        if (e.getKeyCode() == KeyEvent.VK_DELETE) // Execute Remove Action on Delete Key Press
        {
            remove.actionPerformed(null);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void init() {
        setSize(800, 600);
        graphComponent = createGraph();
        installListeners(graphComponent);
        populateContentPane();
        getContentPane().add((graphComponent), BorderLayout.CENTER);
        undoManager = createUndoManager();
        graph.getModel().addListener(mxEvent.UNDO, undoHandler);
        graph.getView().addListener(mxEvent.UNDO, undoHandler);

        mxIEventListener undoHandler = new mxIEventListener() {

            @Override
            public void invoke(Object source, mxEventObject evt) {
                List<mxUndoableChange> changes = ((mxUndoableEdit) evt.getProperty("edit")).getChanges();
                graph.setSelectionCells(graph.getSelectionCellsForChanges(changes));
            }
        };

        undoManager.addListener(mxEvent.UNDO, undoHandler);
        undoManager.addListener(mxEvent.REDO, undoHandler);

    }

    public mxGraphComponent getGraphComponent() {
        return graphComponent;
    }

    public File getCurrentFile() {
        return currentFile;
    }

    private mxUndoManager createUndoManager() {
        return new mxUndoManager();
    }

    public void setCurrentFile(File file) {
        File oldValue = currentFile;
        currentFile = file;

        firePropertyChange("currentFile", oldValue, file);

//        if (oldValue != file) {
//            updateTitle();
//        }
    }

    public mxUndoManager getUndoManager() {
        return undoManager;
    }

//    public void updateTitle() {
//        JFrame frame = (JFrame) SwingUtilities.windowForComponent(this);
//
//        if (frame != null) {
//            String title = (currentFile != null) ? currentFile.getAbsolutePath() : mxResources.get("newDiagram");
//
//            if (modified) {
//                title += "*";
//            }
//
//            frame.setTitle(title + " - " + appTitle);
//        }
//    }
    public class EventRedirector extends AbstractAction {

        protected Action action;

        public EventRedirector(Action a, ImageIcon icon) {
            super("", icon);
            this.action = a;
        }

        // Redirect the Actionevent
        @Override
        public void actionPerformed(ActionEvent e) {
            e = new ActionEvent(graphComponent, e.getID(), e.getActionCommand(), e.getModifiers());
            action.actionPerformed(e);
        }
    }

    public class TratadorDeEventosDoMouse extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (isRightButton(e) && (graph.getSelectionCell() != null)) {
                JPopupMenu menu = createPopMenu();
                menu.show(graphComponent, e.getX(), e.getY());
            }

        }

        private boolean isRightButton(MouseEvent event) {
            return event.getButton() == MouseEvent.BUTTON3;
        }
    }
}
