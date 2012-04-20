package editor;

import interfaces.AtividadeIF;
import interfaces.No;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import objetos.Arco;
import objetos.AtividadeBasica;
import objetos.AtividadeComposta;
import objetos.Celula;
import objetos.Rede;
import objetos.Transicao;
import sun.applet.AppletViewer;

import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxGraphActions;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.util.mxUndoManager;
import com.mxgraph.util.mxUndoableEdit;
import com.mxgraph.util.mxUndoableEdit.mxUndoableChange;
import com.mxgraph.view.mxGraph;

public class EditorRA extends JApplet implements KeyListener {

    private Rede redePrincipal, redeSalva;
    JButton botao = new JButton("Teste");
    JTextField campo = new JTextField();
    private Action remove;
    int coordenadaX, coordenadaY;
    private File currentFile;
    private mxUndoManager undoManager;
    protected boolean importado = false;
    /*
     *Flag para indicar se a rede atual foi modificada
     *
     */
    protected boolean modificado = false;
    protected mxIEventListener undoHandler = new mxIEventListener() {

        @Override
        public void invoke(Object source, mxEventObject evt) {
            undoManager.undoableEditHappened((mxUndoableEdit) evt.getProperty("edit"));
        }
    };
    protected mxIEventListener changeTracker = new mxIEventListener() {

        @Override
        public void invoke(Object source, mxEventObject evt) {
            setModificado(true);
        }
    };

    public EditorRA() {
    }

    private mxGraphComponent createGraph() {
        mxGraph graph = new mxGraph();
        mxGraphComponent graphComponent = new mxGraphComponent(graph);
        return graphComponent;
    }

    private JToolBar createToolBar() {
        final JToolBar toolbar = new JToolBar();
        EditorCellTemplate editorCell = new EditorCellTemplate();
        toolbar.setFloatable(false);

        //Inserir Atividade Simples
        URL inserirUrl = getClass().getClassLoader().getResource("org/jgraph/example/resources/insert.gif");
        ImageIcon atvSimplesIcon = new ImageIcon(inserirUrl);
//        editorCell.addTemplate("Vertice", insertIcon, "shape=image;image=/org/jgraph/example/resources/insert.gif", 32, 32, "", true, toolbar, redePrincipal);
        JLabel atvSimplesLabel = new JLabel(atvSimplesIcon);
        atvSimplesLabel.setToolTipText("Atividade Basica");
        atvSimplesLabel.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                addVertex(e.getX(), e.getY(), "Basica");
            }
        });
        toolbar.add(atvSimplesLabel);

        //Inserir Atividade Composta
        URL atvCompostaURL = getClass().getClassLoader().getResource("org/jgraph/example/resources/port.gif");
        ImageIcon atvCompostaIcon = new ImageIcon(atvCompostaURL);
//        editorCell.addTemplate("  Composta  ", atvCompostaIcon, "shape=image;image=/org/jgraph/example/resources/port.gif", 32, 32, "", true, toolbar, redePrincipal);
        JLabel atvCompostaLabel = new JLabel(atvCompostaIcon);
        atvCompostaLabel.setToolTipText("Atividade Composta");
        atvCompostaLabel.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
            	
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                addVertex(e.getX(), e.getY(), "Composta");
            }
        });
        toolbar.add(atvCompostaLabel);
        
        //Inserir transicao
        URL transicaoURL = getClass().getClassLoader().getResource("org/jgraph/example/resources/transition.png");
        ImageIcon transicaoIcon = new ImageIcon(transicaoURL);
        JLabel transicaoLabel = new JLabel(transicaoIcon);
        transicaoLabel.setToolTipText("Transicao");
        transicaoLabel.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
            	
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                addVertex(e.getX(), e.getY(), "Transicao");
            }
        });
        toolbar.add(transicaoLabel);


        //Remover
        URL removerUrl = getClass().getClassLoader().getResource("org/jgraph/example/resources/delete.gif");
        ImageIcon removerIcon = new ImageIcon(removerUrl);
        remove = (new AbstractAction("", removerIcon) {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!redePrincipal.getGraphComponent().getGraph().isSelectionEmpty()) {
                    Object[] cells = redePrincipal.getGraphComponent().getGraph().getSelectionCells();
                    for (int i = 0; i < cells.length; i++) {
                        // se a celula removida possuir arestas conectadas a ela,
                        // estas arestas tb serao removidas
                        Object[] arestas = redePrincipal.getGraphComponent().getGraph().getEdges(cells[i]);
                        if (arestas.length > 0) {
                            redePrincipal.getGraphComponent().getGraph().removeCells(cells, true);
                            if(!isImportado()){
                                redePrincipal.getElementos().remove(((mxCell)cells[i]).getValue());
                            }else{
                                redeSalva.getElementos().remove(((mxCell)cells[i]).getValue());
                            }
                            for (Object object : arestas) {
                                if(!isImportado()){
                                    redePrincipal.getElementos().remove(object);
                                }else{
                                    redeSalva.getElementos().remove(object);
                                }
                            }
                        } else {
                            redePrincipal.getGraphComponent().getGraph().getModel().remove(cells[i]);
                            if(!isImportado()){
                               redePrincipal.getElementos().remove(((mxCell)cells[i]).getValue());
                            }else{
                               redeSalva.getElementos().remove(((mxCell)cells[i]).getValue());
                            }
                        }


                    }

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
                mxGraph graph = redePrincipal.getGraphComponent().getGraph();
                Object[] cells = graph.getSelectionCells();
                if (cells.length == 2) {
                    Object source = cells[0];
                    Object target = cells[1];
                    if(!(((mxCell)source).getValue() instanceof AtividadeIF && ((mxCell)target).getValue() instanceof AtividadeIF)
                      || !(((mxCell)source).getValue() instanceof Transicao && ((mxCell)target).getValue() instanceof Transicao)){
                        mxCell aresta = (mxCell) graph.insertEdge(graph.getDefaultParent(), null, "aresta", source, target);
                        No fonte = ((No)((mxCell)source).getValue());
                        No destino = ((No)((mxCell)target).getValue());
                        Arco arco = new Arco(fonte, destino);
                        aresta.setValue(arco);
                        campo.setText("fonte-arcosSaida: " + fonte.getArcosDeSaida().size() + "  destino-arcosEntrada: " + destino.getArcosDeEntrada().size() );
                        redePrincipal.getElementos().add(aresta);
                    }else{
                      JOptionPane.showMessageDialog(rootPane, "Não é permitido conectar duas atividades", null, JOptionPane.WARNING_MESSAGE);
                    }
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
//                int indexDaRede = gerenciadorRedes.getRedes().indexOf(redePrincipal);
//                List<Object> elementos = gerenciadorRedes.getRedes().get(indexDaRede).getElementos();
//                if (elementos != null) {
//                    quantAtvCompostas = 0;
//                    for (Object object : elementos) {
//                        if (object instanceof AtividadeComposta) {
//                            quantAtvCompostas++;
//                        }
//                    }
                campo.setText("Atividades compostas: " + redePrincipal.getElementos().size());
//                }
//                if (((mxCell) redePrincipal.getRedeDeAtividades().getGraph().getSelectionCell()).isEdge()) {
//                    getAtributosArestas(redePrincipal.getRedeDeAtividades().getGraph().getSelectionCell());
//                } else {
//                    getDescendentes(redePrincipal.getRedeDeAtividades().getGraph().getSelectionCell());
//                }
            }
        });
        Action action;
        URL url;
        // Copiar
        action = javax.swing.TransferHandler.getCopyAction();
        url = getClass().getClassLoader().getResource(
                "org/jgraph/example/resources/copy.gif");
        toolbar.add(new EventRedirector(action, new ImageIcon(url)));
        // colar
        action = javax.swing.TransferHandler.getPasteAction();
        url = getClass().getClassLoader().getResource(
                "org/jgraph/example/resources/paste.gif");
        toolbar.add(new EventRedirector(action, new ImageIcon(url)));
        // salvar
        URL saveURL = getClass().getClassLoader().getResource("org/jgraph/example/resources/save.gif");
        ImageIcon saveIcon = new ImageIcon(saveURL);
        toolbar.add(vincularAcao("Save", new EditorDeAcoes.SaveAction(false), saveIcon));
        // salvar como
        URL saveAsURL = getClass().getClassLoader().getResource("org/jgraph/example/resources/saveas.gif");
        ImageIcon saveAsIcon = new ImageIcon(saveURL);
        toolbar.add(vincularAcao("SaveAs", new EditorDeAcoes.SaveAction(true), saveAsIcon));
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

        //open
        URL openURL = getClass().getClassLoader().getResource("org/jgraph/example/resources/open.gif");
        ImageIcon openIcon = new ImageIcon(openURL);
        toolbar.add(vincularAcao("Open", new EditorDeAcoes.OpenAction(), openIcon));


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

    private JPopupMenu createPopMenuAtvSimples() {
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
                String nome = txtField3.getText();
                if (!nome.equals("")) {
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
        menu.add(vincularAcao("Exibir subgrafo", mxGraphActions.getEnterGroupAction()));
        menu.addSeparator();
        //sair do grupo
        menu.add(vincularAcao("Sair do grupo", mxGraphActions.getExitGroupAction()));
        menu.addSeparator();
        //Selecionar tudo
        menu.add(vincularAcao("Selecionar Tudo", mxGraphActions.getSelectAllAction()));

        return menu;
    }

    private JPopupMenu createPopMenuAtvComposta(final Object object) {
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
                String nome = txtField3.getText();
                if (!nome.equals("")) {
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
        menu.add(vincularAcao("Exibir subgrafo", mxGraphActions.getEnterGroupAction()));
        menu.addSeparator();
        //sair do grupo
        menu.add(vincularAcao("Sair do grupo", mxGraphActions.getExitGroupAction()));
        menu.addSeparator();
        //Selecionar tudo
        menu.add(vincularAcao("Selecionar Tudo", mxGraphActions.getSelectAllAction()));

        // Editar atividade composta
        JMenuItem edit = menu.add("Editar Atividade Composta");
        edit.addActionListener(new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                new SubEditorRA((AtividadeComposta) object).show();
            }
        });

        return menu;
    }

    private void getDescendentes(final Object cell) {
        redePrincipal.getGraphComponent().getGraph().traverse(cell, true, new mxGraph.mxICellVisitor() {

            @Override
            public boolean visit(Object vertex, Object edge) {
                String saida = "";
                Object source = cell;
                saida += "edge=" + redePrincipal.getGraphComponent().getGraph().convertValueToString(edge)
                        + " vertex=" + redePrincipal.getGraphComponent().getGraph().convertValueToString(vertex)
                        + " quant. de filhos=" + ((mxCell) source).getChildCount();

                campo.setText(saida);

                return true;
            }
        });
    }

    private void getAtributosArestas(final Object cell) {
        redePrincipal.getGraphComponent().getGraph().traverse(cell, true, new mxGraph.mxICellVisitor() {

            @Override
            public boolean visit(Object vertex, Object edge) {
                mxCell aresta = (mxCell) cell;
                String saida = "";
                saida += "source=" + redePrincipal.getGraphComponent().getGraph().convertValueToString(aresta.getSource())
                        + " target=" + redePrincipal.getGraphComponent().getGraph().convertValueToString(aresta.getTarget());

                campo.setText(saida);
                return true;
            }
        });
    }

    private void addVertex(int x, int y, String tipo) {
        redePrincipal.getGraphComponent().getGraph().getModel().beginUpdate();
        Object parent = redePrincipal.getGraphComponent().getGraph().getDefaultParent();
        mxCell cell = null;
        mxCell celula = null;
        AtividadeIF atividade = null;
        Transicao transicao = null;
        if (tipo.equalsIgnoreCase("Basica")) {
            atividade = new AtividadeBasica();
            celula = new Celula(atividade);
            cell = (mxCell) redePrincipal.getGraphComponent().getGraph().insertVertex(parent, "", atividade, x, y, 32, 32);
            cell.setStyle("shape=image;image=/org/jgraph/example/resources/insert.gif");
            celula.setStyle(cell.getStyle());
        } else if (tipo.equalsIgnoreCase("Composta")) {
            atividade = new AtividadeComposta();
            celula = new Celula(atividade);
            cell = (mxCell) redePrincipal.getGraphComponent().getGraph().insertVertex(parent, "", atividade, x, y, 32, 32);
            cell.setStyle("shape=image;image=/org/jgraph/example/resources/port.gif");
            celula.setStyle(cell.getStyle());
        }else if(tipo.equalsIgnoreCase("Transicao")){
        	transicao = new Transicao();
        	cell = (mxCell) redePrincipal.getGraphComponent().getGraph().insertVertex(parent, "", transicao, x, y, 32, 32);
        	cell.setStyle("shape=image;image=/org/jgraph/example/resources/transition.png");
        }
        if(!isImportado()){
            redePrincipal.getElementos().add(cell.getValue());
        }else{
            redeSalva.getElementos().add(cell.getValue());
        }
        redePrincipal.getGraphComponent().getGraph().getModel().endUpdate();

    }

    protected void installListeners() {
        redePrincipal.getGraphComponent().getGraphControl().addKeyListener(this);
        redePrincipal.getGraphComponent().getGraphControl().addMouseListener(new TratadorDeEventosDoMouse());
        undoManager = createUndoManager();
        redePrincipal.getGraphComponent().getGraph().getModel().addListener(mxEvent.UNDO, undoHandler);
        redePrincipal.getGraphComponent().getGraph().getView().addListener(mxEvent.UNDO, undoHandler);
        // Atualiza as flags "modificado" e "importado" se o o graphModel for modificado ou um arquivo for importado
        redePrincipal.getGraphComponent().getGraph().getModel().addListener(mxEvent.CHANGE, changeTracker);
        redePrincipal.getGraphComponent().getGraph().setAllowDanglingEdges(false);
        redePrincipal.getGraphComponent().getGraph().setLabelsVisible(false);

        mxIEventListener undoHandler = new mxIEventListener() {

            @Override
            public void invoke(Object source, mxEventObject evt) {
                List<mxUndoableChange> changes = ((mxUndoableEdit) evt.getProperty("edit")).getChanges();
                redePrincipal.getGraphComponent().getGraph().setSelectionCells(redePrincipal.getGraphComponent().getGraph().getSelectionCellsForChanges(changes));
            }
        };

        undoManager.addListener(mxEvent.UNDO, undoHandler);
        undoManager.addListener(mxEvent.REDO, undoHandler);

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
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        setSize(800, 600);
        redePrincipal = new Rede(createGraph());
        installListeners();
        populateContentPane();
        getContentPane().add((redePrincipal.getGraphComponent()), BorderLayout.CENTER);
        System.out.println("arquivo atual: " + getArquivoAtual()
                + " modificado: " + isModificado() + " importado: " + isImportado());

    }

    public mxGraphComponent getGraphComponent() {
        return redePrincipal.getGraphComponent();
    }

    public File getArquivoAtual() {
        return currentFile;
    }

    private mxUndoManager createUndoManager() {
        return new mxUndoManager();
    }

    public void setArquivoAtual(File file) {
        File oldValue = currentFile;
        currentFile = file;

        firePropertyChange("currentFile", oldValue, file);

        if (oldValue != file) {
            atualizarTitulo();
        }
    }

    public mxUndoManager getUndoManager() {
        return undoManager;
    }

    public void atualizarTitulo() {
        AppletViewer frame = (AppletViewer) SwingUtilities.windowForComponent(this);

        if (frame != null) {
            String title = (currentFile != null) ? currentFile.getAbsolutePath() : "Novo Documento";

            if (isModificado()) {
                title += "*";
            }

            frame.setTitle(title);
        }
    }

    public class EventRedirector extends AbstractAction {

        protected Action action;

        public EventRedirector(Action a, ImageIcon icon) {
            super("", icon);
            this.action = a;
        }

        // Redirect the Actionevent
        @Override
        public void actionPerformed(ActionEvent e) {
            e = new ActionEvent(redePrincipal.getGraphComponent(), e.getID(), e.getActionCommand(), e.getModifiers());
            action.actionPerformed(e);
        }
    }

    public class TratadorDeEventosDoMouse extends MouseAdapter  {

        @Override
        public void mouseReleased(MouseEvent e) {
            if(!redePrincipal.getGraphComponent().getGraph().isSelectionEmpty()){
                mxCell cellSelecionada = (mxCell) redePrincipal.getGraphComponent().getGraph().getSelectionCell();
                if(cellSelecionada.isEdge()){
                     if (((mxCell) cellSelecionada).getSource().getValue() instanceof AtividadeIF
                        && ((mxCell) cellSelecionada).getTarget().getValue() instanceof AtividadeIF) {
                        redePrincipal.getGraphComponent().getGraph().removeCells();
                        JOptionPane.showMessageDialog(rootPane, "Não é permitido conectar duas atividades", null, JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
            
        }

           
        @Override
        public void mouseClicked(MouseEvent e) {
            JPopupMenu menu = null;
            List<Object> elementos = null;
            Iterator<Object> it = null;
            mxCell  cellSelecionada = (mxCell) redePrincipal.getGraphComponent().getGraph().getSelectionCell();
            if (!isImportado() ) {//currentFile == null || (!isModificado()))  {
                if (isRightButton(e) && (cellSelecionada != null)) {
                    elementos = redePrincipal.getElementos();
                    it = elementos.iterator();
                    while (it.hasNext()) {
                        Object object = it.next();
//                    if (cellSelecionada.getValue() instanceof AtividadeBasica
//                            && (((Celula) object).getAtividade() instanceof AtividadeBasica)
//                            && ((AtividadeBasica) ((Celula) object).getAtividade()).equals((AtividadeBasica) (cellSelecionada.getValue()))) {
//                        campo.setText(((AtividadeBasica) ((Celula) object).getAtividade()).getId() + " cell: " + ((AtividadeBasica) (cellSelecionada.getValue())).getId());
//                        menu = createPopMenuAtvSimples();
//                        break;
//                    } else if (cellSelecionada.getValue() instanceof AtividadeComposta
//                            && ((Celula) object).getAtividade() instanceof AtividadeComposta
//                            && ((AtividadeComposta) ((Celula) object).getAtividade()).getId().equals(((AtividadeComposta) (cellSelecionada.getValue())).getId())) {
//                        campo.setText(((AtividadeComposta) ((Celula) object).getAtividade()).getId() + " cell: " + ((AtividadeComposta) (cellSelecionada.getValue())).getId());
//                        menu = createPopMenuAtvComposta(object);
//                        break;
//                    }
                        if (cellSelecionada.getValue() instanceof AtividadeBasica
                                && ( object instanceof AtividadeBasica)
                                &&  ((AtividadeBasica)object).equals((AtividadeBasica) cellSelecionada.getValue())) {
                            campo.setText(((AtividadeBasica)object).getId() + " cell: " + ((AtividadeBasica) (cellSelecionada.getValue())).getId());
                            menu = createPopMenuAtvSimples();
                            break;
                        } else if (cellSelecionada.getValue() instanceof AtividadeComposta
                                && (object instanceof AtividadeComposta)
                                && ((AtividadeComposta)object).equals((AtividadeComposta) cellSelecionada.getValue())) {
                            campo.setText(((AtividadeComposta)object).getId() + " cell: " + ((AtividadeComposta) (cellSelecionada.getValue())).getId());
                            menu = createPopMenuAtvComposta(object);
                            break;
                        }

                    }
                    System.out.println("arquivo atual: " + getArquivoAtual()
                            + " modificado: " + isModificado() + "importado: " + isImportado());
//                    System.out.println("elementos.size(): " + elementos.size());
                }
                
            } else if (isImportado()|| (isImportado() && isModificado())) { // um arquivo foi importado
                if (isRightButton(e) && cellSelecionada!= null) {
                    elementos = redeSalva.getElementos();
                    System.out.println(elementos.size());
                    for (Object object : elementos) {
                        System.out.println(((AtividadeIF)object).getId());
                    }
                    System.out.println(cellSelecionada.getValue().getClass());
                    if(cellSelecionada.getValue() instanceof AtividadeBasica){
                         menu = createPopMenuAtvSimples();
                          campo.setText(((AtividadeBasica) (cellSelecionada.getValue())).getId());
                    }else if(cellSelecionada.getValue() instanceof AtividadeComposta){
                        menu = createPopMenuAtvComposta(cellSelecionada.getValue());
                         campo.setText(((AtividadeComposta) (cellSelecionada.getValue())).getId());
                    }
                    System.out.println("Segundo if =>" + "arquivo atual: " + getArquivoAtual()
                            + " modificado: " + isModificado() + " importado: " + isImportado());
                }
            }
            if (menu != null) {
                menu.show(redePrincipal.getGraphComponent(), e.getX(), e.getY());
            }

        }

        private boolean isRightButton(MouseEvent event) {
            return event.getButton() == MouseEvent.BUTTON3;
        }
        
    }

    public Rede getRedePrincipal() {
        return redePrincipal;
    }

    public void setRedePrincipal(Rede redePrincipal) {
        this.redePrincipal = redePrincipal;
    }

    public Rede getRedeSalva() {
        return redeSalva;
    }

    public void setRedeSalva(Rede redeSalva) {
        this.redeSalva = redeSalva;
    }

    public boolean isImportado() {
        return importado;
    }

    public void setImportado(boolean importado) {
        boolean oldValue = this.importado;
        this.importado = importado;
        firePropertyChange("importado", oldValue, importado);
    }

    void setModificado(boolean modificado) {
        boolean oldValue = this.modificado;
        this.modificado = modificado;

        firePropertyChange("modificado", oldValue, modificado);

        if (oldValue != modificado) {
            atualizarTitulo();
        }
    }

    public boolean isModificado() {
        return modificado;
    }
}
