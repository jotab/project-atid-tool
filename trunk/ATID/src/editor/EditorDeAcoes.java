package editor;


import java.util.logging.Level;
import java.util.logging.Logger;
import util.DefaultFileFilter;
import com.mxgraph.canvas.mxICanvas;
import com.mxgraph.canvas.mxSvgCanvas;
import com.mxgraph.io.mxCodec;
import com.mxgraph.io.mxGdCodec;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxGraphTransferable;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxCellRenderer.CanvasFactory;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.util.mxResources;
import com.mxgraph.util.mxUtils;
import com.mxgraph.util.png.mxPngEncodeParam;
import com.mxgraph.util.png.mxPngImageEncoder;
import com.mxgraph.util.png.mxPngTextDecoder;
import com.mxgraph.view.mxGraph;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceAdapter;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import javax.swing.filechooser.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.rmi.server.ExportException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import objetos.AtividadeComposta;
import objetos.AtividadeBasica;
import objetos.Rede;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import persistencia.LerEscreveArquivo;
import util.XMLEncoder;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author User
 */
public class EditorDeAcoes {

    protected static JLabel selectedEntry = null;
    protected static mxEventSource eventSource = null;

    /**
     *
     * @param e
     * @return Returns the graph for the given action event.
     */
    public static final EditorRA getEditor(ActionEvent e) {
        if (e.getSource() instanceof Component) {
            Component component = (Component) e.getSource();

            while (component != null
                    && !(component instanceof EditorRA)) {
                component = component.getParent();
            }

            return (EditorRA) component;
        }

        return null;
    }

    public static class SaveAction extends AbstractAction {

        /**
         *
         */
        protected boolean showDialog;
        /**
         *
         */
        protected String lastDir = null;

        /**
         *
         */
        public SaveAction(boolean showDialog) {
            this.showDialog = showDialog;
        }

        /**
         * Saves XML+PNG format.
         */
        protected void saveXmlPng(EditorRA editor, String filename,
                Color bg) throws IOException, Exception {
            File arquivo = new File(filename.replace(".png", "") + ".xml");
            mxGraphComponent graphComponent = editor.getGraphComponent();
            mxGraph graph = graphComponent.getGraph();

            // Creates the image for the PNG file
            BufferedImage image = mxCellRenderer.createBufferedImage(graph,
                    null, 1, bg, graphComponent.isAntiAlias(), null,
                    graphComponent.getCanvas());
            if (image == null) {
//                 JOptionPane.showMessageDialog(graphComponent,
//                          "Não há imagem para salvar."  /*mxResources.get("noImageData")*/);
//                 return;
                throw new ExportException("Não há imagem para salvar.");
            }
            // Creates the URL-encoded XML data
//            if(!editor.isImportado()){
            if(!editor.isImportado()){
                LerEscreveArquivo.salvarObjeto(editor.getRedePrincipal(), arquivo);// salva a rede no formato XML
                editor.setRedeSalva(editor.getRedePrincipal()); // quando a rede eh salva pela primeira vez ela é igual a rede local do editor
            }else{
                Rede rede = LerEscreveArquivo.lerArquivo(new File(editor.getArquivoAtual().getName().replace(".png", "") + ".xml"));
                editor.setRedeSalva(rede);
                LerEscreveArquivo.salvarObjeto(editor.getRedeSalva(), arquivo);
            }
//            }else{
//                 Rede rede = LerEscreveArquivo.lerArquivo(editor.getArquivoAtual());
//                 editor.setRedeSalva(rede);
//                 LerEscreveArquivo.salvarObjeto(rede, arquivo);
//            }
            mxCodec codec = new mxCodec();
            String xml = URLEncoder.encode(
                    mxUtils.getXml(codec.encode(graph.getModel())), "UTF-8");
            mxPngEncodeParam param = mxPngEncodeParam.getDefaultEncodeParam(image);
            param.setCompressedText(new String[]{"mxGraphModel", xml});

            // Saves as a PNG file
            FileOutputStream outputStream = new FileOutputStream(new File(
                    filename));
            try {
                mxPngImageEncoder encoder = new mxPngImageEncoder(outputStream,
                        param);

                if (image != null) {
                    encoder.encode(image);

                    editor.setModificado(false);
                    editor.setArquivoAtual(new File(filename));
                } else {
                    JOptionPane.showMessageDialog(graphComponent,
                            "Não há imagem para salvar.");
                }
            } finally {
                outputStream.close();
            }
        }

        /**
         *
         */
        public void actionPerformed(ActionEvent e) {
            EditorRA editor = getEditor(e);

            if (editor != null) {
                mxGraphComponent graphComponent = editor.getGraphComponent();
                mxGraph graph = graphComponent.getGraph();
                FileFilter selectedFilter = null;
                DefaultFileFilter xmlPngFilter = new DefaultFileFilter(".png",
                        "PNG+XML " + "Arquivo" + " (.png)");
                FileFilter vmlFileFilter = new DefaultFileFilter(".html",
                        "VML " + "Arquivo" + " (.html)");
                String filename = null;
                boolean dialogShown = false;

                if (showDialog || editor.getArquivoAtual() == null) {
                    String wd;
                    if (lastDir != null) {
                        wd = lastDir;
                    } else if (editor.getArquivoAtual() != null) {
                        wd = editor.getArquivoAtual().getParent();
                    } else {
                        wd = System.getProperty("user.dir");
                    }

                    JFileChooser fc = new JFileChooser(wd);

                    // Adds the default file format
                    FileFilter defaultFilter = xmlPngFilter;
                    fc.addChoosableFileFilter(defaultFilter);

                    // Adds special vector graphics formats and HTML
                    fc.addChoosableFileFilter(new DefaultFileFilter(".mxe",
                            "mxGraph Editor " + "Arquivo"
                            + " (.mxe)"));
                    fc.addChoosableFileFilter(new DefaultFileFilter(".txt",
                            "Graph Drawing " +"Arquivo"
                            + " (.txt)"));
                    fc.addChoosableFileFilter(new DefaultFileFilter(".svg",
                            "SVG " + "Arquivo" + " (.svg)"));
                    fc.addChoosableFileFilter(vmlFileFilter);
                    fc.addChoosableFileFilter(new DefaultFileFilter(".html",
                            "HTML " + "Arquivo" + " (.html)"));

                    // Adds a filter for each supported image format
                    Object[] imageFormats = ImageIO.getReaderFormatNames();

                    // Finds all distinct extensions
                    HashSet<String> formats = new HashSet<String>();

                    for (int i = 0; i < imageFormats.length; i++) {
                        String ext = imageFormats[i].toString().toLowerCase();
                        formats.add(ext);
                    }

                    imageFormats = formats.toArray();

                    for (int i = 0; i < imageFormats.length; i++) {
                        String ext = imageFormats[i].toString();
                        fc.addChoosableFileFilter(new DefaultFileFilter("."
                                + ext, ext.toUpperCase() + " "
                                + "Arquivo" + " (." + ext + ")"));
                    }

                    // Adds filter that accepts all supported image formats
                    fc.addChoosableFileFilter(new DefaultFileFilter.ImageFileFilter(
                            mxResources.get("allImages")));
                    fc.setFileFilter(defaultFilter);
                    int rc = fc.showDialog(null, "Salvar");
                    dialogShown = true;

                    if (rc != JFileChooser.APPROVE_OPTION) {
                        return;
                    } else {
                        lastDir = fc.getSelectedFile().getParent();
                    }

                    filename = fc.getSelectedFile().getAbsolutePath();
                    selectedFilter = fc.getFileFilter();

                    if (selectedFilter instanceof DefaultFileFilter) {
                        String ext = ((DefaultFileFilter) selectedFilter).getExtension();

                        if (!filename.toLowerCase().endsWith(ext)) {
                            filename += ext;
                        }
                    }

                    if (new File(filename).exists()
                            && JOptionPane.showConfirmDialog(graphComponent,
                            "Arquivo já exisitente. Deseja substituí-lo?"/*mxResources.get("overwriteExistingFile")*/) != JOptionPane.YES_OPTION) {
                        return;
                    }
                } else {
                    filename = editor.getArquivoAtual().getAbsolutePath();
                }

                try {
                    String ext = filename.substring(filename.lastIndexOf('.') + 1);

                    if (ext.equalsIgnoreCase("svg")) {
                        mxSvgCanvas canvas = (mxSvgCanvas) mxCellRenderer.drawCells(graph, null, 1, null,
                                new CanvasFactory() {

                                    public mxICanvas createCanvas(
                                            int width, int height) {
                                        mxSvgCanvas canvas = new mxSvgCanvas(
                                                mxUtils.createSvgDocument(
                                                width, height));
                                        canvas.setEmbedded(true);

                                        return canvas;
                                    }
                                });
                        if (canvas == null) {
                            JOptionPane.showMessageDialog(graphComponent, "Não há imagem para salvar", "Aviso",
                                    JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                        mxUtils.writeFile(mxUtils.getXml(canvas.getDocument()),
                                filename);
                    } else if (selectedFilter == vmlFileFilter) {
                        String vmlDocument = mxUtils.getXml(mxCellRenderer.createVmlDocument(graph, null, 1, null, null).getDocumentElement());
                        mxUtils.writeFile(vmlDocument, filename);
                    } else if (ext.equalsIgnoreCase("html")) {
                        String htmlDocument = mxUtils.getXml(mxCellRenderer.createHtmlDocument(graph, null, 1, null, null).getDocumentElement());
                        mxUtils.writeFile(htmlDocument, filename);
                    } else if (ext.equalsIgnoreCase("mxe")
                            || ext.equalsIgnoreCase("xml")) {
                        mxCodec codec = new mxCodec();
                        String xml = mxUtils.getXml(codec.encode(graph.getModel()));
                        if (xml == null) {
                            JOptionPane.showMessageDialog(graphComponent, "Não há imagem para salvar", "Aviso",
                                    JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                        mxUtils.writeFile(xml, filename);

//                        editor.setModificado(false);
                        editor.setArquivoAtual(new File(filename));
                    } else if (ext.equalsIgnoreCase("txt")) {
                        String content = mxGdCodec.encode(graph).getDocumentString();

                        mxUtils.writeFile(content, filename);
                    } else {
                        Color bg = null;

                        if ((!ext.equalsIgnoreCase("gif") && !ext.equalsIgnoreCase("png"))
                                || JOptionPane.showConfirmDialog(
                                graphComponent, "Background transparente?" /*mxResources.get("transparentBackground")*/) != JOptionPane.YES_OPTION) {
                            bg = graphComponent.getBackground();
                        }

                        if (selectedFilter == xmlPngFilter
                                || (editor.getArquivoAtual() != null
                                && ext.equalsIgnoreCase("png") && !dialogShown)) {
                            try {
                                saveXmlPng(editor, filename, bg);
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(graphComponent, ex.getMessage(), "Aviso",
                                        JOptionPane.WARNING_MESSAGE);
                            }
                        } else {
                            BufferedImage image = mxCellRenderer.createBufferedImage(graph, null, 1, bg,
                                    graphComponent.isAntiAlias(), null,
                                    graphComponent.getCanvas());

                            if (image != null) {
                                ImageIO.write(image, ext, new File(filename));
                            } else {
                                JOptionPane.showMessageDialog(graphComponent,
                                        "Não há nenhuma imagem para salvar", "Aviso", JOptionPane.WARNING_MESSAGE);
                            }
                        }
                    }
                } catch (Throwable ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(graphComponent,
                            ex.toString(), mxResources.get("error"),
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    public static class HistoryAction extends AbstractAction {

        /**
         *
         */
        protected boolean undo;

        /**
         *
         */
        public HistoryAction(boolean undo) {
            this.undo = undo;
        }

        /**
         *
         */
        public void actionPerformed(ActionEvent e) {
            EditorRA editor = getEditor(e);

            if (editor != null) {
                if (undo) {
                    editor.getUndoManager().undo();
                } else {
                    editor.getUndoManager().redo();
                }
            }
        }
    }

    public static class DragDropAction {

        private static int contAtvSimples;

        public static void addDragDropListener(final JLabel entry, final mxCell cell, JToolBar toolBar, final Rede redePrincipal) {
            mxRectangle bounds = (mxGeometry) cell.getGeometry().clone();
            final mxGraphTransferable t = new mxGraphTransferable(
                    new Object[]{cell}, bounds);
            entry.addMouseListener(new MouseListener() {

                /*
                 * (non-Javadoc)
                 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
                 */
                @Override
                public void mousePressed(MouseEvent e) {
                    setSelectionEntry(entry, t);

                }

                /*
                 * (non-Javadoc)
                 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
                 */
                public void mouseClicked(MouseEvent e) {
                }

                /*
                 * (non-Javadoc)
                 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
                 */
                public void mouseEntered(MouseEvent e) {
                }

                /*
                 * (non-Javadoc)
                 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
                 */
                public void mouseExited(MouseEvent e) {
                }

                /*
                 * (non-Javadoc)
                 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
                 */
                public void mouseReleased(MouseEvent e) {
                }

                public void setSelectionEntry(JLabel entry, mxGraphTransferable t) {
                    JLabel previous = selectedEntry;
                    eventSource = new mxEventSource(this);
                    selectedEntry = entry;

                    if (previous != null) {
                        previous.setBorder(null);
                        previous.setOpaque(false);
                    }

                    if (selectedEntry != null) {
//			selectedEntry.setBorder(ShadowBorder.getSharedInstance());
                        selectedEntry.setOpaque(true);
                    }

                    eventSource.fireEvent(new mxEventObject(mxEvent.SELECT, "entry",
                            selectedEntry, "transferable", t, "previous", previous));
                }
            });

            // Install the handler for dragging nodes into a graph
            DragGestureListener dragGestureListener = new DragGestureListener() {

                private ArrayList<Object> elementosCopia = new ArrayList<Object>();

                @Override
                public void dragGestureRecognized(DragGestureEvent e) {
                    e.startDrag(null, mxConstants.EMPTY_IMAGE, new Point(),
                            t, null);
                    if (entry.getText().equals("Vertice")) {
                        Node atv = null;
                        mxCodec codec = new mxCodec();
                        AtividadeBasica atvSimples = new AtividadeBasica(cell);
                        atv = codec.encode(atvSimples);
                        cell.setValue(atv);
                        ((Element) cell.getValue()).setAttribute("id", "SIMP" + (++contAtvSimples));
                    } else if (entry.getText().equals("  Composta  ")) {
                        cell.setValue(new AtividadeComposta());

                    }

                    redePrincipal.getElementos().add(cell);
                    elementosCopia.add(cell);
                    Iterator<Object> it = redePrincipal.getElementos().iterator();
                    while (it.hasNext()) {
                        Object object = it.next();
                        //((Element)((mxCell) object).getValue()).setAttribute("id","SIMP" + (++contAtvSimples));
                        System.out.println(((Element) ((mxCell) object).getValue()).getAttribute("id"));
//                        System.out.println(((AtividadeSimples) object).getAtividade().getId());

                    }
                }
            };

            DragSource dragSource = new DragSource();
            dragSource.createDefaultDragGestureRecognizer(entry,
                    DnDConstants.ACTION_COPY, dragGestureListener);
            dragSource.addDragSourceListener(new DragSourceAdapter() {

                @Override
                public void dragDropEnd(DragSourceDropEvent dsde) {
//                if (entry.getText().equals("Vertice")) {
//                    try {
//                        mxCell atv = (mxCell) XMLEncoder.encode(cell);
//                        System.out.println(((AtividadeSimples) atv.getValue()).getId());
//                    } catch (UnsupportedEncodingException ex) {
//                        Logger.getLogger(EditorDeAcoes.class.getName()).log(Level.SEVERE, null, ex);
//                    } catch (IOException ex) {
//                        Logger.getLogger(EditorDeAcoes.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//                } else if (entry.getText().equals("  Composta  ")) {
//                    try {
//                        mxCell atv = (mxCell) XMLEncoder.encode(cell);
//                        System.out.println(((AtividadeComposta) atv.getValue()).getId());
//                    } catch (UnsupportedEncodingException ex) {
//                        Logger.getLogger(EditorDeAcoes.class.getName()).log(Level.SEVERE, null, ex);
//                    } catch (IOException ex) {
//                        Logger.getLogger(EditorDeAcoes.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//                }
                }
            });
            toolBar.add(entry);
        }
    }

    public static class OpenAction extends AbstractAction {

        /**
         *
         */
        protected String lastDir;

        protected void resetEditor(EditorRA editor) {
            editor.setModificado(false);
            editor.getUndoManager().clear();
            editor.getGraphComponent().zoomAndCenter();
        }

        /**
         * Reads XML+PNG format.
         */
        protected void openXmlPng(EditorRA editor, File file) throws IOException {
            String fileName =  file.getName().toLowerCase();
            if(fileName.endsWith(".xml")){
                Rede rede = LerEscreveArquivo.lerArquivo(file);
//            mxGraphModel graph =  LerEscreveArquivo.lerArquivo(file);
//            System.out.println(rede.getId() + " elementos.size(): " + rede.getElementos().size() );
                String XMLDaRede = XMLEncoder.encode(rede.getGraphComponent().getGraph().getModel());
//            String XMLDaRede = XMLEncoder.decode(rede);
                Document document = mxUtils.parseXml(XMLDaRede);
                mxCodec codec = new mxCodec(document);
                codec.decode(document.getDocumentElement(), editor.getGraphComponent().getGraph().getModel());
                editor.setImportado(true); // altera o flag para true indicado que um arquivo foi importado
                editor.setArquivoAtual(file);
                editor.setRedeSalva(rede);
//                editor.setRedePrincipal(rede);
//               editor.getRedePrincipal().getGraphComponent().getGraph().setModel(rede.getGraphComponent().getGraph().getModel());
//                resetEditor(editor);
            } else if (fileName.endsWith(".png")) {
                Rede rede = LerEscreveArquivo.lerArquivo(new File(fileName.replace(".png", "") + ".xml"));
                Map<String, String> text = mxPngTextDecoder.decodeCompressedText(new FileInputStream(file));
                if (text != null) {
                    String value = text.get("mxGraphModel");
                    if (value != null) {
                        Document document = mxUtils.parseXml(URLDecoder.decode(
                                value, "UTF-8"));
                        mxCodec codec = new mxCodec(document);
                        codec.decode(document.getDocumentElement(), editor.getGraphComponent().getGraph().getModel());
                        editor.setArquivoAtual(file);
                        editor.setRedeSalva(rede);
                        editor.setImportado(true);
                        editor.setModificado(false);
//                        editor.getRedePrincipal().getGraphComponent().getGraph().setModel(rede.getGraphComponent().getGraph().getModel());
//                        editor.setRedePrincipal(rede);
//                        resetEditor(editor);
                        return;
                    }
                }
            }
            JOptionPane.showMessageDialog(editor,
                    mxResources.get("imageContainsNoDiagramData"));
        }

        public void actionPerformed(ActionEvent e) {
            EditorRA editor = getEditor(e);

            if (editor != null) {
                if (JOptionPane.showConfirmDialog(editor,
                        "Deseja abrir um novo arquivo e perder as alterações feitas?") == JOptionPane.YES_OPTION) {
                    mxGraph graph = editor.getGraphComponent().getGraph();

                    if (graph != null) {
                        String wd = (lastDir != null) ? lastDir : System.getProperty("user.dir");

                        JFileChooser fc = new JFileChooser(wd);

                        // Adds file filter for supported file format
                        DefaultFileFilter defaultFilter = new DefaultFileFilter(
                                ".mxe", mxResources.get("allSupportedFormats")
                                + " (.mxe, .png, .vdx)") {

                            public boolean accept(File file) {
                                String lcase = file.getName().toLowerCase();

                                return super.accept(file)
                                        || lcase.endsWith(".png")
                                        || lcase.endsWith(".vdx");
                            }
                        };
                        fc.addChoosableFileFilter(defaultFilter);

                        fc.addChoosableFileFilter(new DefaultFileFilter(".mxe",
                                "mxGraph Editor " + mxResources.get("file")
                                + " (.mxe)"));
                        fc.addChoosableFileFilter(new DefaultFileFilter(".png",
                                "PNG+XML  " + mxResources.get("file")
                                + " (.png)"));

                        // Adds file filter for VDX import
                        fc.addChoosableFileFilter(new DefaultFileFilter(".vdx",
                                "XML Drawing  " + mxResources.get("file")
                                + " (.vdx)"));

                        // Adds file filter for GD import
                        fc.addChoosableFileFilter(new DefaultFileFilter(".txt",
                                "Graph Drawing  " + mxResources.get("file")
                                + " (.txt)"));

                        fc.setFileFilter(defaultFilter);

                        int rc = fc.showDialog(null,
                                mxResources.get("openFile"));

                        if (rc == JFileChooser.APPROVE_OPTION) {
                            lastDir = fc.getSelectedFile().getParent();

                            try {
                                if (fc.getSelectedFile().getAbsolutePath().toLowerCase().endsWith(".png")||
                                     fc.getSelectedFile().getAbsolutePath().toLowerCase().endsWith(".xml") ) {
                                    openXmlPng(editor, fc.getSelectedFile());
//                                } else if (fc.getSelectedFile().getAbsolutePath().toLowerCase().endsWith(".txt")) {
//                                    mxGdDocument document = new mxGdDocument();
//                                    document.parse(mxUtils.readFile(fc.getSelectedFile().getAbsolutePath()));
//                                    openGD(editor, fc.getSelectedFile(),
//                                            document);
//                                } else {
//                                    Document document = mxUtils.parseXml(mxUtils.readFile(fc.getSelectedFile().getAbsolutePath()));
//
//                                    if (fc.getSelectedFile().getAbsolutePath().toLowerCase().endsWith(".vdx")) {
//                                        openVdx(editor, fc.getSelectedFile(),
//                                                document);
//                                    } else {
//                                        mxCodec codec = new mxCodec(document);
//                                        codec.decode(
//                                                document.getDocumentElement(),
//                                                graph.getModel());
//                                        editor.setArquivoAtual(fc.getSelectedFile());
//                                    }
//
//                                    resetEditor(editor);
                                }
                            } catch (IOException ex) {
                                ex.printStackTrace();
                                JOptionPane.showMessageDialog(
                                        editor.getGraphComponent(),
                                        ex.toString(),
                                        mxResources.get("error"),
                                        JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                }
            }
        }
    }
}
