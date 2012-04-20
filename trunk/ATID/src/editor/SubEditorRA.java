package editor;


import editor.EditorCellTemplate;
import com.mxgraph.model.mxCell;
import java.awt.event.ActionEvent;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JToolBar;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxGraphActions;
import com.mxgraph.view.mxGraph;
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.List;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import objetos.AtividadeComposta;
import objetos.AtividadeBasica;
import objetos.Rede;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author User
 */
public class SubEditorRA extends JFrame {

    private AtividadeComposta atividadeComposta;
    private Action remove, group, ungroup, copy, paste;
    private Rede rede;
    JTextField campo = new JTextField();

    public SubEditorRA(AtividadeComposta atividadeComposta) {
        this.atividadeComposta = atividadeComposta;
        rede = new Rede(createGraph());
        this.atividadeComposta.setRedeDeAtividades(rede);
        init();
    }

    public AtividadeComposta getAtividadeComposta() {
        return atividadeComposta;
    }

    public void setAtividadeComposta(AtividadeComposta atividadeComposta) {
        this.atividadeComposta = atividadeComposta;
    }

    private void init() {
        this.setSize(600, 600);
        this.setResizable(false);
        populateContentPane();
        atividadeComposta.getRedeDeAtividades().getGraphComponent().getGraph().setAllowDanglingEdges(false);
        rede.getGraphComponent().getGraph().setLabelsVisible(false);
        installListeners(rede.getGraphComponent());


    }

    private void installListeners(final mxGraphComponent graphComponent) {
        graphComponent.getGraphControl().addMouseListener(new TratadorDeEventosDoMouse());


    }

    private void populateContentPane() {
        getContentPane().add(new JPanel());
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(criarToolBar(), BorderLayout.NORTH);
        getContentPane().add(campo, BorderLayout.SOUTH);
        getContentPane().add((atividadeComposta.getRedeDeAtividades().getGraphComponent()), BorderLayout.CENTER);

    }

    private JToolBar criarToolBar() {
        final JToolBar toolbar = new JToolBar();
        EditorCellTemplate editorCell = new EditorCellTemplate();
        toolbar.setFloatable(false);

        //Inserir Atividade Simples
        URL inserirUrl = getClass().getClassLoader().getResource("org/jgraph/example/resources/insert.gif");
        final ImageIcon atvSimplesIcon = new ImageIcon(inserirUrl);
//        editorCell.addTemplate("Vertice", insertIcon, "shape=image;image=/org/jgraph/example/resources/insert.gif", 32, 32, "", true, toolbar, atividadeComposta.getRedeDeAtividades());
            JLabel atvSimplesLabel = new JLabel(atvSimplesIcon);
        atvSimplesLabel.setToolTipText("Atividade Simples");
        atvSimplesLabel.setText("Simples");
        atvSimplesLabel.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                addVertex(e.getX(), e.getY(), "Simples");
            }
        });
        toolbar.add(atvSimplesLabel);


        //Inserir Atividade Composta
        URL atvCompostaURL = getClass().getClassLoader().getResource("org/jgraph/example/resources/port.gif");
        final ImageIcon atvCompostaIcon = new ImageIcon(atvCompostaURL);
//        editorCell.addTemplate("  Composta  ", atvCompostaIcon, "shape=image;image=/org/jgraph/example/resources/port.gif", 32, 32, "", true, toolbar, atividadeComposta.getRedeDeAtividades());
        JLabel atvCompostaLabel = new JLabel(atvCompostaIcon);
        atvCompostaLabel.setToolTipText("Atividade Composta");
        atvCompostaLabel.setText("Composta");
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


        //Remover
        URL removerUrl = getClass().getClassLoader().getResource("org/jgraph/example/resources/delete.gif");
        ImageIcon removerIcon = new ImageIcon(removerUrl);
        remove = (new AbstractAction("", removerIcon) {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!atividadeComposta.getRedeDeAtividades().getGraphComponent().getGraph().isSelectionEmpty()) {
                    Object[] cells = atividadeComposta.getRedeDeAtividades().getGraphComponent().getGraph().getSelectionCells();
                    for (int i = 0; i < cells.length; i++) {
                        // se a celula removida possuir arestas conectadas a ela,
                        // estas arestas tb serao removidas
                        if (atividadeComposta.getRedeDeAtividades().getGraphComponent().getGraph().getEdges(cells[i]).length > 0) {
                            atividadeComposta.getRedeDeAtividades().getGraphComponent().getGraph().removeCells(cells, true);
                        } else {
                            atividadeComposta.getRedeDeAtividades().getGraphComponent().getGraph().getModel().remove(cells[i]);
                        }


                    }

                }

            }
        });
        toolbar.add(remove);

        //  conecta arestas.
        URL connectUrl = getClass().getClassLoader().getResource(
                "org/jgraph/example/resources/connecton.gif");
        ImageIcon connectIcon = new ImageIcon(connectUrl);
        toolbar.add(new AbstractAction("", connectIcon) {

            @Override
            public void actionPerformed(ActionEvent e) {
                mxGraph graph = atividadeComposta.getRedeDeAtividades().getGraphComponent().getGraph();
                Object[] cells = graph.getSelectionCells();
                if (cells.length == 2) {
                    Object source = cells[0];
                    Object target = cells[1];
                    mxCell aresta = (mxCell) graph.insertEdge(graph.getDefaultParent(), null, "aresta", source, target);
                    atividadeComposta.getRedeDeAtividades().getElementos().add(aresta);
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
                campo.setText("Atividades compostas: " + atividadeComposta.getRedeDeAtividades().getElementos().size());
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

        return toolbar;
    }

    private void addVertex(int x, int y, String tipo) {
        rede.getGraphComponent().getGraph().getModel().beginUpdate();
        Object parent = rede.getGraphComponent().getGraph().getDefaultParent();
        mxCell cell = null;
        if(tipo.equalsIgnoreCase("Simples")){
            AtividadeBasica atividadeSimples = new AtividadeBasica();
            cell = (mxCell) rede.getGraphComponent().getGraph().insertVertex(parent, "", atividadeSimples, x, y, 32, 32);
            cell.setStyle("shape=image;image=/org/jgraph/example/resources/insert.gif");
        }else if(tipo.equalsIgnoreCase("Composta")){
            AtividadeComposta atividadeComposta = new AtividadeComposta();
            cell = (mxCell) rede.getGraphComponent().getGraph().insertVertex(parent, "", atividadeComposta, x, y, 32, 32);
            cell.setStyle("shape=image;image=/org/jgraph/example/resources/port.gif");
        }
        rede.getElementos().add(cell);
        rede.getGraphComponent().getGraph().getModel().endUpdate();

    }

    private mxGraphComponent createGraph() {
        mxGraph graph = new mxGraph();
        mxGraphComponent graphComponent = new mxGraphComponent(graph);
        return graphComponent;
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
            e = new ActionEvent(atividadeComposta.getRedeDeAtividades(), e.getID(), e.getActionCommand(), e.getModifiers());
            action.actionPerformed(e);
        }
    }

    public Action vincularAcao(String name, final Action action, ImageIcon icon) {
        return new AbstractAction(name, icon) {

            @Override
            public void actionPerformed(ActionEvent e) {
                action.actionPerformed(new ActionEvent(atividadeComposta.getRedeDeAtividades(), e.getID(), e.getActionCommand()));
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
                new SubEditorRA((AtividadeComposta) ((mxCell) object).getValue()).show();
            }
        });

        return menu;
    }
     
    public class TratadorDeEventosDoMouse extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            mxCell cellSelecionada = (mxCell) rede.getGraphComponent().getGraph().getSelectionCell();
            if (isRightButton(e) && (cellSelecionada != null)) {
                JPopupMenu menu = null;
                List<Object> elementos = rede.getElementos();
                Iterator<Object> it = elementos.iterator();
                while (it.hasNext()) {
                    Object object = it.next();
                    if (cellSelecionada.getValue() instanceof AtividadeBasica
                            && (((mxCell) object).getValue() instanceof AtividadeBasica)
                            && ((AtividadeBasica) ((mxCell) object).getValue()).equals((AtividadeBasica) (cellSelecionada.getValue()))) {
                        campo.setText(((AtividadeBasica) ((mxCell) object).getValue()).getId() + " cell: " + ((AtividadeBasica) (cellSelecionada.getValue())).getId());
                        menu = createPopMenuAtvSimples();
                        break;
                    }else if (cellSelecionada.getValue() instanceof AtividadeComposta
                            &&((mxCell) object).getValue() instanceof AtividadeComposta
                            && ((AtividadeComposta) ((mxCell) object).getValue()).getId().equals(((AtividadeComposta) (cellSelecionada.getValue())).getId())) {
                        campo.setText(((AtividadeComposta) ((mxCell) object).getValue()).getId() + " cell: " + ((AtividadeComposta) (cellSelecionada.getValue())).getId());
                        menu = createPopMenuAtvComposta(object);
                        break;
                    }
                }
                System.out.println("elementos.size(): " + elementos.size());
                if (menu != null) {
                    menu.show(rede.getGraphComponent(), e.getX(), e.getY());
                }

            }

        }

        private boolean isRightButton(MouseEvent event) {
            return event.getButton() == MouseEvent.BUTTON3;
        }
    }
}
