package uk.ac.gla.student._2074245k.cde.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.NumberFormatter;

import uk.ac.gla.student._2074245k.cde.components.Component;
import uk.ac.gla.student._2074245k.cde.components.ConcreteComponent;
import uk.ac.gla.student._2074245k.cde.components.GateComponent;
import uk.ac.gla.student._2074245k.cde.components.GateComponent.GateType;
import uk.ac.gla.student._2074245k.cde.components.HingeComponent;
import uk.ac.gla.student._2074245k.cde.components.LineSegmentComponent;
import uk.ac.gla.student._2074245k.cde.util.GraphicsGenerator;
import uk.ac.gla.student._2074245k.cde.util.LoadingResult;
import uk.ac.gla.student._2074245k.cde.util.ProjectPersistenceUtilities;


/**
* The MainFrame class provides a custom frame which contains the main 
* panel of the editor (@see MainCanvas).  
*
* @author  Alexios Koukoulas
* @version 0.40
* @since   2/2/2017 
*/
public final class MainFrame extends JFrame
{       
    public static final String WINDOW_TITLE               = "C.D.E (Circuit Design Editor)"; 
    private static final long serialVersionUID            = 7475614725428306744L;    
    private static final String DEFAULT_LAF_CLASS_NAME    = "javax.swing.plaf.metal.MetalLookAndFeel";
    private static final Dimension DEFAULT_WINDOW_MIN_DIM = new Dimension(500, 500);
    private static final Dimension DEFAULT_WINDOW_DIM     = new Dimension((int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth() * 0.75f),
                                                                          (int)(Toolkit.getDefaultToolkit().getScreenSize().getHeight() * 0.75f));    
    private static final Dimension DEFAULT_CANVAS_DIM     = new Dimension((int)(DEFAULT_WINDOW_DIM.getWidth() * 0.75f),
                                                                          (int)(DEFAULT_WINDOW_DIM.getHeight() * 0.75f));
    private JPanel masterPanel;    
    private JPanel menuPanel;
    private MainCanvas canvasPanel;
    private Color lastChosenColor;
    
    public MainFrame()
    {
        super(WINDOW_TITLE);
        init(DEFAULT_CANVAS_DIM, DEFAULT_LAF_CLASS_NAME);        
    }
        
    private void init(final Dimension canvasDimension, final String lookAndFeelClassName)
    {
        try 
        {             
            lastChosenColor = null;            
            setContentPane(createComponents(this, canvasDimension));
            changeLookAndFeel(lookAndFeelClassName);
            setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            setSize(DEFAULT_WINDOW_DIM);
            setMinimumSize(DEFAULT_WINDOW_MIN_DIM);
            setLocationRelativeTo(null);
            setVisible(true);   
            addWindowListener(new WindowAdapter()
            {
                @Override
                public void windowClosing(final WindowEvent __)
                {        
                    if (canvasPanel.hasTakenActionSinceLastSave())
                    {                        
                        int selOption = JOptionPane.showConfirmDialog (null, "The program is exiting, would you like to save your progress?", "Exiting Option", JOptionPane.YES_NO_CANCEL_OPTION);
                        if (selOption == JOptionPane.YES_OPTION)
                        {                        
                            displaySaveProjectDialog();                            
                            if (ProjectPersistenceUtilities.TEMP_FILE.exists()) ProjectPersistenceUtilities.TEMP_FILE.delete();
                            System.exit(0);
                        }
                        else if (selOption == JOptionPane.NO_OPTION)
                        {                            
                            if (ProjectPersistenceUtilities.TEMP_FILE.exists()) ProjectPersistenceUtilities.TEMP_FILE.delete();
                            System.exit(0);                            
                        }    
                        else if (selOption == JOptionPane.CANCEL_OPTION)
                        {
                            return;
                        }
                    }
                    System.exit(0);
                }
            });
        }
        catch (IOException e) 
        {
            JOptionPane.showMessageDialog(null, "Error populating main frame", "IO Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
    
    private JComponent createComponents(final JFrame frame, final Dimension canvasDimension) throws IOException 
    {   
        // Build Menu
        JMenuBar menuBar = new JMenuBar();
        
        JMenu fileMenuTab = new JMenu("File");
        fileMenuTab.setMnemonic(KeyEvent.VK_F);
        fileMenuTab.getAccessibleContext().setAccessibleDescription("File related actions");
        
        JMenuItem createNewCanvasItem = new JMenuItem("Create new Canvas", KeyEvent.VK_1);
        createNewCanvasItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        createNewCanvasItem.getAccessibleContext().setAccessibleDescription("Creates a new Canvas");
        
        JMenuItem openProjectItem = new JMenuItem("Open project", KeyEvent.VK_2);
        openProjectItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        openProjectItem.getAccessibleContext().setAccessibleDescription("Open an existing project");
        
        JMenuItem saveProjectItem = new JMenuItem("Save Project", KeyEvent.VK_3);
        saveProjectItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        saveProjectItem.getAccessibleContext().setAccessibleDescription("Saves the project to disk");
        
        JMenuItem saveAsProjectItem = new JMenuItem("Save Project As..", KeyEvent.VK_4);
        saveAsProjectItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK));
        saveAsProjectItem.getAccessibleContext().setAccessibleDescription("Saves the project to disk at a specified location");
        
        JMenuItem resizeCanvasItem = new JMenuItem("Resize Canvas", KeyEvent.VK_5);
        resizeCanvasItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
        resizeCanvasItem.getAccessibleContext().setAccessibleDescription("Resizes the current canvas to the specified dimension");
        
        JMenuItem exportProjectItem = new JMenuItem("Export to SVG", KeyEvent.VK_6);
        exportProjectItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
        exportProjectItem.getAccessibleContext().setAccessibleDescription("Export project to scalable vector graphics (svg) format");
        
        fileMenuTab.add(createNewCanvasItem);
        fileMenuTab.add(openProjectItem);
        fileMenuTab.add(saveProjectItem);
        fileMenuTab.add(saveAsProjectItem);
        fileMenuTab.add(resizeCanvasItem);
        fileMenuTab.add(exportProjectItem);
        
        JMenu editTab = new JMenu("Edit");
        editTab.setMnemonic(KeyEvent.VK_E);
        editTab.getAccessibleContext().setAccessibleDescription("Edit actions");
        
        JMenuItem undoItem = new JMenuItem("Undo", KeyEvent.VK_1);
        undoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
        undoItem.getAccessibleContext().setAccessibleDescription("Undo last action");
        
        JMenuItem redoItem = new JMenuItem("Redo", KeyEvent.VK_2);
        redoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK));
        redoItem.getAccessibleContext().setAccessibleDescription("Redo last undone action");
        
        JMenuItem selectAllMenuItem = new JMenuItem("Select All Components", KeyEvent.VK_3);
        selectAllMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
        selectAllMenuItem.getAccessibleContext().setAccessibleDescription("Selects all components in the canvas");
        
        JMenuItem copyItem = new JMenuItem("Copy", KeyEvent.VK_4);
        copyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        copyItem.getAccessibleContext().setAccessibleDescription("Copy the selected components");
        
        JMenuItem pasteItem = new JMenuItem("Paste", KeyEvent.VK_5);
        pasteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
        pasteItem.getAccessibleContext().setAccessibleDescription("Paste the copied selection");
        
        JMenuItem deleteItem = new JMenuItem("Delete", KeyEvent.VK_6);
        deleteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        deleteItem.getAccessibleContext().setAccessibleDescription("Delete the selected component(s)");
        
        JMenuItem zoomInItem = new JMenuItem("Zoom In", KeyEvent.VK_7);
        zoomInItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, ActionEvent.CTRL_MASK));
        zoomInItem.getAccessibleContext().setAccessibleDescription("Zoom in canvas");
        
        JMenuItem zoomOutItem = new JMenuItem("Zoom out", KeyEvent.VK_8);
        zoomOutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, ActionEvent.CTRL_MASK));
        zoomOutItem.getAccessibleContext().setAccessibleDescription("Zoom out from canvas");
        
        JMenuItem opacityItem = new JMenuItem("Toggle Opacity", KeyEvent.VK_8);
        opacityItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.SHIFT_MASK));
        opacityItem.getAccessibleContext().setAccessibleDescription("Toggles the opacity of the selected white box component(s)");
        
        JMenuItem createWhiteBoxItem = new JMenuItem("Create White Box", KeyEvent.VK_9);
        createWhiteBoxItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.SHIFT_MASK));
        createWhiteBoxItem.getAccessibleContext().setAccessibleDescription("Creates a white box out of the selected component(s)");
        
        editTab.add(undoItem);
        editTab.add(redoItem);
        editTab.add(selectAllMenuItem);
        editTab.add(copyItem);
        editTab.add(pasteItem);
        editTab.add(deleteItem);
        editTab.add(zoomInItem);
        editTab.add(zoomOutItem);
        editTab.add(opacityItem);
        editTab.add(createWhiteBoxItem);
        
        JMenu libraryTab = new JMenu("Library");
        libraryTab.setMnemonic(KeyEvent.VK_L);
        libraryTab.getAccessibleContext().setAccessibleDescription("Library related options");
        
        JMenuItem exportProjectAsLibraryItem = new JMenuItem("Save Project as library", KeyEvent.VK_1);
        exportProjectAsLibraryItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.SHIFT_MASK));
        exportProjectAsLibraryItem.getAccessibleContext().setAccessibleDescription("Saves the entre project as library");
        
        JMenuItem exportSelectionAsLibraryItem = new JMenuItem("Save Selection as library", KeyEvent.VK_2);
        exportSelectionAsLibraryItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.SHIFT_MASK | ActionEvent.CTRL_MASK));
        exportSelectionAsLibraryItem.getAccessibleContext().setAccessibleDescription("Saves the current selection as library");
        
        JMenuItem loadLibraryItem = new JMenuItem("Load Library", KeyEvent.VK_3);
        loadLibraryItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, ActionEvent.SHIFT_MASK));
        loadLibraryItem.getAccessibleContext().setAccessibleDescription("Loads a library into the project");
        
        libraryTab.add(exportProjectAsLibraryItem);
        libraryTab.add(exportSelectionAsLibraryItem);
        libraryTab.add(loadLibraryItem);
        
        JMenu windowTab = new JMenu("Window");
        windowTab.setMnemonic(KeyEvent.VK_W);
        windowTab.getAccessibleContext().setAccessibleDescription("Window related options");
        
        JMenuItem changeLF = new JMenuItem("Change look and feel", KeyEvent.VK_1);
        changeLF.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.SHIFT_MASK | ActionEvent.ALT_MASK));
        changeLF.getAccessibleContext().setAccessibleDescription("Open an existing project");
        
        windowTab.add(changeLF);
        
        menuBar.add(fileMenuTab);
        menuBar.add(editTab);
        menuBar.add(libraryTab);
        menuBar.add(windowTab);
        frame.setJMenuBar(menuBar);
                
        menuPanel = new JPanel(new WrapLayout());        
                        
        final Image bbImage      = ImageIO.read(getClass().getResourceAsStream("/icons/blackbox_icon.png"));
        final Image gateImage    = ImageIO.read(getClass().getResourceAsStream("/icons/lg_icon.png"));
        final Image wiresImage   = ImageIO.read(getClass().getResourceAsStream("/icons/wire_icon.png"));
        final Image nubImage     = ImageIO.read(getClass().getResourceAsStream("/icons/nub_icon.png"));
        final Image opacityImage = ImageIO.read(getClass().getResourceAsStream("/icons/opacity_icon.png"));
        final Image textboxImage = ImageIO.read(getClass().getResourceAsStream("/icons/textbox_icon.png"));
        final Image paletteImage = ImageIO.read(getClass().getResourceAsStream("/icons/palette_icon.png"));
        
        JButton bbButton = new JButton(new ImageIcon(bbImage));
        bbButton.setBorder(BorderFactory.createLineBorder(Colors.DEFAULT_COLOR));
        bbButton.setToolTipText("Specify and create a black box component");
        
        JButton gateButton = new JButton(new ImageIcon(gateImage));
        gateButton.setBorder(BorderFactory.createLineBorder(Colors.DEFAULT_COLOR));
        gateButton.setToolTipText("Select and create a gate from the predefined gates");
                
        JButton wireButton = new JButton(new ImageIcon(wiresImage));
        wireButton.setBorder(BorderFactory.createLineBorder(Colors.DEFAULT_COLOR));
        wireButton.setToolTipText("Create a mutable wire segment");
        
        JButton nubButton = new JButton(new ImageIcon(nubImage));
        nubButton.setBorder(BorderFactory.createLineBorder(Colors.DEFAULT_COLOR));
        nubButton.setToolTipText("Drag and drop a nub denoting electrical connection between components");
        
        JButton opacityButton = new JButton(new ImageIcon(opacityImage));
        opacityButton.setBorder(BorderFactory.createLineBorder(Colors.DEFAULT_COLOR));
        opacityButton.setToolTipText("Toggles on and off the opacity of the selected white box(es)");
        
        JButton textboxButton = new JButton(new ImageIcon(textboxImage));
        textboxButton.setBorder(BorderFactory.createLineBorder(Colors.DEFAULT_COLOR));
        textboxButton.setToolTipText("Drag and drop an editable textbox");
        
        JButton paletteButton = new JButton(new ImageIcon(paletteImage));
        paletteButton.setBorder(BorderFactory.createLineBorder(Colors.DEFAULT_COLOR));
        paletteButton.setToolTipText("Select a custom color for the selected component(s)");
                
        // Component Movement Panel        
        JRadioButton componentAxisResMove = new JRadioButton("Axis Res.");               
        JRadioButton componentUnrestrictedMove    = new JRadioButton("Unrestricted");
        componentUnrestrictedMove.setSelected(true);
        
        ButtonGroup bg = new ButtonGroup();
        bg.add(componentAxisResMove);
        bg.add(componentUnrestrictedMove);
        
        JLabel componentMovementLabel = new JLabel("Black box/Gate Movement");
        componentMovementLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel componentMovementPanel = new JPanel(new BorderLayout(0,31));
        componentMovementPanel.add(componentMovementLabel, BorderLayout.NORTH);
        componentMovementPanel.add(componentAxisResMove, BorderLayout.EAST);
        componentMovementPanel.add(componentUnrestrictedMove, BorderLayout.WEST);
        componentMovementPanel.setBorder(BorderFactory.createLineBorder(Colors.DEFAULT_COLOR));
        
        // Wire Movement Panel        
        JRadioButton wireAxisResMove = new JRadioButton("Axis Res.");                              
        JRadioButton wireFreeMove    = new JRadioButton("Unrestricted");           
        wireFreeMove.setSelected(true);
        
        ButtonGroup bg2 = new ButtonGroup();
        bg2.add(wireAxisResMove);
        bg2.add(wireFreeMove);
        
        JLabel wireMovementLabel = new JLabel("Wire Movement");
        wireMovementLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel wireMovementPanel = new JPanel(new BorderLayout(0, 31));
        wireMovementPanel.add(wireMovementLabel, BorderLayout.NORTH);
        wireMovementPanel.add(wireAxisResMove, BorderLayout.EAST);
        wireMovementPanel.add(wireFreeMove, BorderLayout.WEST);
        wireMovementPanel.setBorder(BorderFactory.createLineBorder(Colors.DEFAULT_COLOR));
        
        // Hinge movement panel        
        JRadioButton moveHingeButtonUnrestricted    = new JRadioButton("Move Hinge Unrestricted");        
        JRadioButton segmentSpawnButtonUnrestricted = new JRadioButton("New Line Segment Unrestricted");
        JRadioButton moveHingeButtonAxisRes         = new JRadioButton("Move Hinge Axis Res.");
        JRadioButton segmentSpawnButtonAxisRes      = new JRadioButton("New Line Segment Axis Res.");        
        moveHingeButtonUnrestricted.setSelected(true);
        
        JPanel moveHingePanel = new JPanel(new BorderLayout());
        moveHingePanel.add(moveHingeButtonUnrestricted, BorderLayout.NORTH);
        moveHingePanel.add(moveHingeButtonAxisRes, BorderLayout.SOUTH);
        
        JPanel segmentSpawnPanel = new JPanel(new BorderLayout());
        segmentSpawnPanel.add(segmentSpawnButtonUnrestricted, BorderLayout.NORTH);
        segmentSpawnPanel.add(segmentSpawnButtonAxisRes, BorderLayout.SOUTH);
        
        ButtonGroup bg3 = new ButtonGroup();
        bg3.add(moveHingeButtonUnrestricted);
        bg3.add(segmentSpawnButtonUnrestricted);
        bg3.add(moveHingeButtonAxisRes);
        bg3.add(segmentSpawnButtonAxisRes);
        
        JLabel hingeDraggingLabel = new JLabel("Hinge Movement");
        hingeDraggingLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel hingeMovementPanel = new JPanel(new BorderLayout(0, 8));
        hingeMovementPanel.add(hingeDraggingLabel, BorderLayout.NORTH);
        hingeMovementPanel.add(moveHingePanel, BorderLayout.WEST);
        hingeMovementPanel.add(segmentSpawnPanel, BorderLayout.EAST);
        
        hingeMovementPanel.setBorder(BorderFactory.createLineBorder(Colors.DEFAULT_COLOR));
                
        // Alignment checkbox
        JCheckBox alignmentCheckbox = new JCheckBox("Alignment Cues");
        alignmentCheckbox.setSelected(true);
        
        // Hinge visibility checkbox
        JCheckBox hingeVisibilityCheckbox = new JCheckBox("Hinge Visibility");
        hingeVisibilityCheckbox.setSelected(true);
        
        // Port Arrow Visibility checkbox
        JCheckBox arrowVisibilityCheckbox = new JCheckBox("Port Arrow Visibility");
        arrowVisibilityCheckbox.setSelected(true);
        
        JPanel checkBoxPanel = new JPanel(new BorderLayout());
        checkBoxPanel.add(alignmentCheckbox, BorderLayout.NORTH);
        checkBoxPanel.add(hingeVisibilityCheckbox, BorderLayout.CENTER);
        checkBoxPanel.add(arrowVisibilityCheckbox, BorderLayout.SOUTH);
        
        // Add components to the menu panel
        menuPanel.add(bbButton);        
        menuPanel.add(gateButton);
        menuPanel.add(wireButton); 
        menuPanel.add(nubButton);    
        menuPanel.add(opacityButton);
        menuPanel.add(textboxButton);
        menuPanel.add(paletteButton);
        menuPanel.add(componentMovementPanel);
        menuPanel.add(wireMovementPanel);        
        menuPanel.add(hingeMovementPanel);
        menuPanel.add(checkBoxPanel);        
                
        canvasPanel = new MainCanvas(this);            
        canvasPanel.setPreferredSize(canvasDimension);
        canvasPanel.setOriginalSize(canvasDimension.width, canvasDimension.height);
        canvasPanel.setFocusable(true);
              
        JPanel wrapperPanel = new JPanel(new GridBagLayout());
        wrapperPanel.add(canvasPanel, new GridBagConstraints());
        
        JScrollPane canvasScrollPane = new JScrollPane(wrapperPanel, 
                                                       JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                                       JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);                
        canvasPanel.setScrollPane(canvasScrollPane);        
        canvasScrollPane.getVerticalScrollBar().setUnitIncrement(15);
        canvasScrollPane.getHorizontalScrollBar().setUnitIncrement(15);
        
        // Create the master panel
        masterPanel = new JPanel(new BorderLayout());
        masterPanel.add(menuPanel, BorderLayout.NORTH);
        masterPanel.add(canvasScrollPane, BorderLayout.CENTER);
        
        // Add functionality to components
        createNewCanvasItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent __) 
            {
                JDialog jDialog = new JDialog(frame, "New canvas specification", ModalityType.APPLICATION_MODAL);
                
                NumberFormatter dimensionsFormatter = new NumberFormatter(NumberFormat.getInstance());
                dimensionsFormatter.setValueClass(Integer.class);
                dimensionsFormatter.setMinimum(0);
                dimensionsFormatter.setCommitsOnValidEdit(false);
                
                JLabel widthLabel = new JLabel("Canvas Width:  ");
                JFormattedTextField widthField = new JFormattedTextField(dimensionsFormatter);        
                widthField.setValue(canvasPanel.getWidth());
                widthField.setColumns(10);
                widthField.addFocusListener(new SelectAllFocusListener(widthField));                        
                            
                JLabel heightLabel = new JLabel("Canvas Height:  ");
                JFormattedTextField heightField = new JFormattedTextField(dimensionsFormatter);        
                heightField.setValue(canvasPanel.getHeight());
                heightField.setColumns(10);
                heightField.addFocusListener(new SelectAllFocusListener(heightField));        
                
                JPanel widthPanel = new JPanel();
                widthPanel.add(widthLabel);
                widthPanel.add(widthField);
                
                JPanel heightPanel = new JPanel();
                heightPanel.add(heightLabel);
                heightPanel.add(heightField);
                
                JPanel newCanvasSpecsPanel = new JPanel();
                newCanvasSpecsPanel.setLayout(new BoxLayout(newCanvasSpecsPanel, BoxLayout.Y_AXIS));
                newCanvasSpecsPanel.add(widthPanel);
                newCanvasSpecsPanel.add(heightPanel);    
                
                JButton createButton = new JButton("Create");
                createButton.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent __) 
                    {
                        int selOption = JOptionPane.showConfirmDialog (null, "Creating new canvas, would you like to save your progress?", "New canvas option", JOptionPane.YES_NO_OPTION);
                        if (selOption == JOptionPane.YES_OPTION)
                        {                        
                            displaySaveProjectDialog();       
                        }      
                        canvasPanel.init(null);
                        canvasPanel.setPreferredSize(new Dimension((int)widthField.getValue(), (int)heightField.getValue()));
                        canvasPanel.setOriginalSize((int)widthField.getValue(), (int)heightField.getValue());
                        jDialog.dispose();                        
                    }            
                });
                
                JButton cancelButton = new JButton("Cancel");
                cancelButton.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent __) 
                    {
                        jDialog.dispose();
                    }            
                });
                
                JPanel optionsPanel = new JPanel();
                optionsPanel.add(createButton);
                optionsPanel.add(cancelButton);
                
                JPanel newCanvasOptionsPanel = new JPanel(new BorderLayout());
                newCanvasOptionsPanel.add(optionsPanel, BorderLayout.EAST);  
                
                JPanel lfPanel = new JPanel(new BorderLayout());
                lfPanel.add(newCanvasSpecsPanel, BorderLayout.NORTH);
                lfPanel.add(newCanvasOptionsPanel, BorderLayout.SOUTH);
                
                jDialog.setContentPane(lfPanel);
                jDialog.getRootPane().setDefaultButton(createButton);
                jDialog.pack();                
                jDialog.setResizable(false);
                jDialog.setLocationRelativeTo(frame);
                jDialog.setVisible(true);  
            }
        });
        
        openProjectItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent __) 
            {
                JFileChooser fc = new JFileChooser(".");
                FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("Circuit Design Editor file (*.cde)", "cde");
                fc.setFileFilter(fileFilter);
                
                int choice = fc.showOpenDialog(menuPanel);
                if (choice == JFileChooser.APPROVE_OPTION) 
                {    
                    canvasPanel.openProjectFromFile(fc.getSelectedFile());                    
                }
            }
        });
        
        saveProjectItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent __)
            {
                if (canvasPanel.getLastSaveLocation() == null)
                {
                    displaySaveProjectDialog();
                }
                else
                {
                    canvasPanel.saveProject();
                }
            }
        });
        
        saveAsProjectItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent __) 
            {
                displaySaveProjectDialog();
            }
        });
        
        resizeCanvasItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent __) 
            {
                JDialog jDialog = new JDialog(frame, "Resize canvas", ModalityType.APPLICATION_MODAL);
                
                NumberFormatter dimensionsFormatter = new NumberFormatter(NumberFormat.getInstance());
                dimensionsFormatter.setValueClass(Integer.class);
                dimensionsFormatter.setMinimum(0);
                dimensionsFormatter.setCommitsOnValidEdit(false);
                
                JLabel widthLabel = new JLabel("Canvas Width:  ");
                JFormattedTextField widthField = new JFormattedTextField(dimensionsFormatter);        
                widthField.setValue(canvasPanel.getWidth());
                widthField.setColumns(10);
                widthField.addFocusListener(new SelectAllFocusListener(widthField));                        
                            
                JLabel heightLabel = new JLabel("Canvas Height:  ");
                JFormattedTextField heightField = new JFormattedTextField(dimensionsFormatter);        
                heightField.setValue(canvasPanel.getHeight());
                heightField.setColumns(10);
                heightField.addFocusListener(new SelectAllFocusListener(heightField));        
                
                JPanel widthPanel = new JPanel();
                widthPanel.add(widthLabel);
                widthPanel.add(widthField);
                
                JPanel heightPanel = new JPanel();
                heightPanel.add(heightLabel);
                heightPanel.add(heightField);
                
                JPanel resizeCanvasSpecsPanel = new JPanel();
                resizeCanvasSpecsPanel.setLayout(new BoxLayout(resizeCanvasSpecsPanel, BoxLayout.Y_AXIS));
                resizeCanvasSpecsPanel.add(widthPanel);
                resizeCanvasSpecsPanel.add(heightPanel);                                    
                
                JButton resizeButton = new JButton("Resize");                  
                resizeButton.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent __) 
                    {                        
                        canvasPanel.setPreferredSize(new Dimension((int)widthField.getValue(), (int)heightField.getValue()));
                        canvasPanel.setOriginalSize((int)widthField.getValue(), (int)heightField.getValue());
                        canvasPanel.revalidate();
                        canvasPanel.repaint();
                        jDialog.dispose();
                    }            
                });
                
                JButton cancelButton = new JButton("Cancel");
                cancelButton.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent __) 
                    {
                        jDialog.dispose();
                    }            
                });
                
                JPanel optionsPanel = new JPanel();
                optionsPanel.add(resizeButton);
                optionsPanel.add(cancelButton);
                
                JPanel resizeCanvasOptionsPanel = new JPanel(new BorderLayout());
                resizeCanvasOptionsPanel.add(optionsPanel, BorderLayout.EAST);  
                
                JPanel lfPanel = new JPanel(new BorderLayout());
                lfPanel.add(resizeCanvasSpecsPanel, BorderLayout.NORTH);
                lfPanel.add(resizeCanvasOptionsPanel, BorderLayout.SOUTH);
                
                jDialog.setContentPane(lfPanel);
                jDialog.getRootPane().setDefaultButton(resizeButton);                
                jDialog.pack();                
                jDialog.setResizable(false);
                jDialog.setLocationRelativeTo(frame);
                jDialog.setVisible(true);              
                jDialog.setFocusable(true);                          
            }
        });
        
        exportProjectItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent __)
            {                
                JFileChooser fc = new JFileChooser(".");
                FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("Scalable Vector Graphics file (*.svg)", "svg");
                fc.setFileFilter(fileFilter);
                
                int choice = fc.showSaveDialog(menuPanel);
                if (choice == JFileChooser.APPROVE_OPTION) 
                {                
                    File selFile = fc.getSelectedFile();
                    File adjustedFile = selFile;
                    
                    if (!selFile.getName().endsWith(".svg"))
                    {
                        adjustedFile = new File(selFile.getAbsolutePath() + ".svg");
                    }
                    
                    if (adjustedFile.exists())
                    {                        
                        int selOption = JOptionPane.showConfirmDialog (null, "Overwrite existing file?", "Export Option", JOptionPane.YES_NO_OPTION);
                        if (selOption == JOptionPane.YES_OPTION)
                        {                                                            
                            canvasPanel.exportToSVG(adjustedFile);
                        }
                    }
                    else
                    {
                        canvasPanel.exportToSVG(adjustedFile);
                    }
                }
            }
        });
        
        undoItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent __) 
            {
                canvasPanel.undo();
            }
            
        });
        
        redoItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent __) 
            {
                canvasPanel.redo();
            }    
        });
        
        selectAllMenuItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent __)
            {
                canvasPanel.selectAll();
            }
        });
        
        copyItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent __)
            {
                canvasPanel.copy();
            }
        });
        
        pasteItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent __)
            {
                canvasPanel.paste();
            }
        });
        
        deleteItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent __)
            {
                canvasPanel.delete();
            }
        });
        
        zoomInItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent __)
            {                
                canvasPanel.zoomIn();
            }
        });
        
        zoomOutItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent __)
            {
                
                canvasPanel.zoomOut();
            }
        });
        
        opacityItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent __)
            {
                if (canvasPanel.getNumberOfSelectedComponents() == 0)
                {    
                    JOptionPane.showMessageDialog(null, "No components have been selected!\nPlease select at least one white box component in order to toggle their opacity.", "Opacity Selection Error", JOptionPane.ERROR_MESSAGE);
                }
                else
                {                                                    
                    canvasPanel.toggleOpacity();
                }                                
            }
        });
        
        createWhiteBoxItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent __)
            {                
                if (canvasPanel.getNumberOfSelectedComponents() > 0)
                {                                        
                    canvasPanel.addChildrenAndParentsToSelection();
                    JDialog jDialog = new JDialog(frame, "White Box specification", ModalityType.APPLICATION_MODAL);
                    jDialog.setResizable(true);
                    
                    ConcreteComponentBuilderPanel builderPanel = new ConcreteComponentBuilderPanel(canvasPanel, true, canvasPanel.getSelectedComponentsIterator(), jDialog);
                    builderPanel.subscribeToConcreteComponentCreationEvent(canvasPanel);                
                    JScrollPane scrollPane = new JScrollPane(builderPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);                 
                    scrollPane.getVerticalScrollBar().setUnitIncrement(15);
                    
                    jDialog.setIconImage(bbImage);
                    jDialog.setContentPane(scrollPane);                
                    jDialog.pack();
                    jDialog.setLocationRelativeTo(frame);            
                    jDialog.setVisible(true);
                }
                else
                {    
                    JOptionPane.showMessageDialog(null, "No components have been selected!\nPlease select at least one component in order to create a white box.", "White box creation error", JOptionPane.ERROR_MESSAGE);
                }                                    
            }
        });
        
        exportProjectAsLibraryItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent __) 
            {
                // Make sure there are components in the canvas
                if (canvasPanel.getComponentsIterator().hasNext())
                {        
                    Iterator<Component> compsIter = canvasPanel.getComponentsIterator();
                    Set<Component> components = new HashSet<Component>();
                    while (compsIter.hasNext()) components.add(compsIter.next());                    
                    exportLibraryModal(components);        
                }
                else
                {
                    JOptionPane.showMessageDialog(null, "No components found in canvas!\nPlease create at least one component in order to export the project as a library.", "Library creation error", JOptionPane.ERROR_MESSAGE);
                }
            }
            
        });
        
        exportSelectionAsLibraryItem.addActionListener(new ActionListener()                
        {
            @Override
            public void actionPerformed(ActionEvent __) 
            {
                if (canvasPanel.getNumberOfSelectedComponents() != 0)
                {
                    canvasPanel.addChildrenAndParentsToSelection();
                    Iterator<Component> selCompsIter = canvasPanel.getSelectedComponentsIterator();
                    Set<Component> components = new HashSet<Component>();
                    while (selCompsIter.hasNext()) components.add(selCompsIter.next());
                    
                    exportLibraryModal(components);
                }
                else
                {
                    JOptionPane.showMessageDialog(null, "No components have been selected!\nPlease select at least one component in order to export as a library.", "Library creation error", JOptionPane.ERROR_MESSAGE);                    
                }
            }
        });
        
        loadLibraryItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent __) 
            {
                if (!ProjectPersistenceUtilities.LIB_DIR.exists() || ProjectPersistenceUtilities.LIB_DIR.listFiles().length == 0)
                {
                    JOptionPane.showMessageDialog(null, "No libraries were found!", "Library loading error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                JDialog jDialog = new JDialog(frame, "Library selection", ModalityType.APPLICATION_MODAL);
                JPanel libraryListPanel = new JPanel();                
                
                File[] libFiles = ProjectPersistenceUtilities.LIB_DIR.listFiles();
                String[] libFileNames = new String[libFiles.length];
                
                for (int i = 0; i < libFiles.length; ++i)
                {
                    libFileNames[i] = libFiles[i].getName();
                }
                
                Arrays.sort(libFileNames);
                
                JComboBox<String> cb = new JComboBox<String>(libFileNames);
                
                libraryListPanel.add(cb);                                          
                cb.setFont(cb.getFont().deriveFont(15.0f));                
                
                JButton loadLibraryButton = new JButton("Load Library");
                loadLibraryButton.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent arg0) 
                    {
                        File libraryFile = new File(ProjectPersistenceUtilities.LIB_DIR.getAbsolutePath() + "/" + (String)cb.getSelectedItem());
                        LoadingResult lr = ProjectPersistenceUtilities.openProject(libraryFile, true, canvasPanel);
                        
                        for (Component comp: lr.loadedComponents)
                        {
                            canvasPanel.addComponentToCanvas(comp);                            
                        }
                                                
                        jDialog.dispose();
                    }            
                });
                
                JButton cancelButton = new JButton("Cancel");
                cancelButton.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent __) 
                    {
                        jDialog.dispose();
                    }            
                });
                
                JPanel optionsPanel = new JPanel();
                optionsPanel.add(loadLibraryButton);
                optionsPanel.add(cancelButton);
                
                JPanel libraryOptionsPanel = new JPanel(new BorderLayout());
                libraryOptionsPanel.add(optionsPanel, BorderLayout.EAST);                
                
                JPanel modalPanel = new JPanel(new BorderLayout());                
                modalPanel.setPreferredSize(new Dimension(200, 200));                
                modalPanel.add(libraryListPanel, BorderLayout.NORTH);                
                modalPanel.add(libraryOptionsPanel, BorderLayout.SOUTH);
                                
                jDialog.setContentPane(modalPanel);
                jDialog.getRootPane().setDefaultButton(loadLibraryButton);
                jDialog.pack();
                jDialog.setIconImage(gateImage);
                jDialog.setResizable(false);
                jDialog.setLocationRelativeTo(frame);
                jDialog.setVisible(true);     
            }
        });
        
        changeLF.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent __)
            {
                JDialog jDialog = new JDialog(frame, "Change Look & Feel", ModalityType.APPLICATION_MODAL);
                
                JPanel lfiPanel = new JPanel();                 
                JComboBox<String> cb = new JComboBox<String>();
                
                LookAndFeelInfo[] lfInfo = UIManager.getInstalledLookAndFeels();                
                for (LookAndFeelInfo lfi: lfInfo)
                    cb.addItem(lfi.getClassName());
                lfiPanel.add(cb, BorderLayout.CENTER);                
                
                
                JButton okButton = new JButton("OK");
                okButton.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent __) 
                    {
                        changeLookAndFeel((String)cb.getSelectedItem());        
                        jDialog.dispose();
                    }    
                });
                
                JButton cancelButton = new JButton("Cancel");
                cancelButton.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent __) 
                    {
                        jDialog.dispose();
                    }            
                });
                
                JButton applyButton = new JButton("Apply");
                applyButton.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent __) 
                    {
                        changeLookAndFeel((String)cb.getSelectedItem());                                         
                    }            
                });
                
                JPanel optionsPanel = new JPanel();
                optionsPanel.add(okButton);
                optionsPanel.add(cancelButton);
                optionsPanel.add(applyButton);
                
                JPanel lfOptionsPanel = new JPanel(new BorderLayout());
                lfOptionsPanel.add(optionsPanel, BorderLayout.EAST);  
                
                JPanel lfPanel = new JPanel(new BorderLayout());
                lfPanel.add(lfiPanel, BorderLayout.NORTH);
                lfPanel.add(lfOptionsPanel, BorderLayout.SOUTH);
                
                jDialog.setContentPane(lfPanel);
                jDialog.getRootPane().setDefaultButton(okButton);
                jDialog.pack();                
                jDialog.setResizable(false);
                jDialog.setLocationRelativeTo(frame);
                jDialog.setVisible(true);   
            }
        });
        
        bbButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent __) 
            {
                boolean shouldBuildWhiteBox = false;
                if (canvasPanel.getNumberOfSelectedComponents() > 0)
                {                    
                    int selOption = JOptionPane.showConfirmDialog (null, "Build White Box with selected components?", "White Box Option", JOptionPane.YES_NO_CANCEL_OPTION);                    
                    if (selOption == JOptionPane.YES_OPTION)
                    {
                        canvasPanel.addChildrenAndParentsToSelection();
                        shouldBuildWhiteBox = true;
                    }                    
                    else if (selOption == JOptionPane.CANCEL_OPTION)
                    {
                        return;
                    }
                }
                
                JDialog jDialog = new JDialog(frame, (shouldBuildWhiteBox ? "White" : "Black") + " Box specification", ModalityType.APPLICATION_MODAL);
                jDialog.setResizable(true);
                
                ConcreteComponentBuilderPanel builderPanel = new ConcreteComponentBuilderPanel(canvasPanel, shouldBuildWhiteBox, canvasPanel.getSelectedComponentsIterator(), jDialog);
                builderPanel.subscribeToConcreteComponentCreationEvent(canvasPanel);                
                JScrollPane scrollPane = new JScrollPane(builderPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);                 
                scrollPane.getVerticalScrollBar().setUnitIncrement(15);
                
                jDialog.setIconImage(bbImage);
                jDialog.setContentPane(scrollPane);                
                jDialog.pack();
                jDialog.setLocationRelativeTo(frame);            
                jDialog.setVisible(true);
                
            }
        });
                
        gateButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent __) 
            {            
                JDialog jDialog = new JDialog(frame, "Gate selection", ModalityType.APPLICATION_MODAL);
                JPanel gateTypesPanel = new JPanel();                
                String[] gateTypeNames = Arrays.toString(GateComponent.GateType.values()).replaceAll("^.|.$", "").split(", ");
                JComboBox<String> cb = new JComboBox<String>(gateTypeNames);
                
                gateTypesPanel.add(cb);
                
                JPanel gateDisplayPanel = new JPanel()
                {                    
                    private static final long serialVersionUID = 4290139864214174415L;

                    @Override
                    public void paintComponent(final Graphics g)
                    {
                        g.setColor(Colors.CANVAS_BKG_COLOR);
                        g.fillRect(20, 20, 160, 110);
                        
                        // Hardcoded representation of ports and labels
                        GraphicsGenerator gen = new GraphicsGenerator((Graphics2D)g);
                        gen.setStroke(Strokes.THIN_STROKE);
                        gen.setColor(Colors.DEFAULT_COLOR);
                        gen.drawLine(20, 56, 74, 56);
                        gen.drawLine(20, 88, 74, 88);
                        gen.drawLine(141, 72, 178, 72);                    
                        gen.drawString("a", 85, 60);
                        gen.drawString("b", 85, 90);
                        gen.drawString("out", 118, 75);    
                        gen.drawGate(GateType.valueOf((String)cb.getSelectedItem()), 
                                     new Rectangle(getWidth()/2 - GateComponent.DEFAULT_SIZE/2, 
                                                   getHeight()/2 - GateComponent.DEFAULT_SIZE/2, 
                                                   GateComponent.DEFAULT_SIZE, 
                                                   GateComponent.DEFAULT_SIZE), 
                                     Colors.DEFAULT_COLOR);                                                                    
                    }
                };                
                
                cb.setFont(cb.getFont().deriveFont(15.0f));
                cb.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent arg) 
                    {
                        gateDisplayPanel.repaint();
                    }                    
                });
                
                JButton createButton = new JButton("Add Gate");
                createButton.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent arg0) 
                    {
                        int targetX = 0;
                        int targetY = 0;
                        
                        if (canvasPanel.getWidth() < canvasScrollPane.getViewportBorderBounds().width)
                        {
                            targetX = canvasPanel.getWidth()/2;
                        }
                        else
                        {
                            targetX = canvasScrollPane.getViewport().getViewPosition().x + canvasScrollPane.getViewportBorderBounds().width/2; 
                        }
                        
                        if (canvasPanel.getHeight() < canvasScrollPane.getViewportBorderBounds().height)
                        {
                            targetY = canvasPanel.getHeight()/2;
                        }
                        else
                        {
                            targetY = canvasScrollPane.getViewport().getViewPosition().y + canvasScrollPane.getViewportBorderBounds().height/2;
                        }
                        
                        targetX -= GateComponent.DEFAULT_SIZE/2;
                        targetY -= GateComponent.DEFAULT_SIZE/2;
                        
                        GateComponent gateToAdd = new GateComponent(canvasPanel,
                                                                    GateComponent.GateType.valueOf((String)cb.getSelectedItem()),
                                                                    true, 
                                                                    targetX,
                                                                    targetY);
                        gateToAdd.constructPortsAutomatically();                        
                        canvasPanel.addComponentToCanvas(gateToAdd);
                        jDialog.dispose();
                    }            
                });
                
                JButton cancelButton = new JButton("Cancel");
                cancelButton.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent __) 
                    {
                        jDialog.dispose();
                    }            
                });
                                    
                JPanel optionsPanel = new JPanel();
                optionsPanel.add(createButton);
                optionsPanel.add(cancelButton);
                
                JPanel gateOptionsPanel = new JPanel(new BorderLayout());
                gateOptionsPanel.add(optionsPanel, BorderLayout.EAST);                
                
                JPanel modalPanel = new JPanel(new BorderLayout());                
                modalPanel.setPreferredSize(new Dimension(200, 200));                
                modalPanel.add(gateTypesPanel, BorderLayout.NORTH);
                modalPanel.add(gateDisplayPanel, BorderLayout.CENTER);
                modalPanel.add(gateOptionsPanel, BorderLayout.SOUTH);
                                
                jDialog.setContentPane(modalPanel);
                jDialog.getRootPane().setDefaultButton(createButton);
                jDialog.pack();
                jDialog.setIconImage(gateImage);
                jDialog.setResizable(false);
                jDialog.setLocationRelativeTo(frame);
                jDialog.setVisible(true);                                
            }
        });
        
        wireButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(final ActionEvent __)
            {                                                
                int targetX = 0;
                int targetY = 0;
                
                if (canvasPanel.getWidth() < canvasScrollPane.getViewportBorderBounds().width)
                {
                    targetX = canvasPanel.getWidth()/2;
                }
                else
                {
                    targetX = canvasScrollPane.getViewport().getViewPosition().x + canvasScrollPane.getViewportBorderBounds().width/2; 
                }
                
                if (canvasPanel.getHeight() < canvasScrollPane.getViewportBorderBounds().height)
                {
                    targetY = canvasPanel.getHeight()/2;
                }
                else
                {
                    targetY = canvasScrollPane.getViewport().getViewPosition().y + canvasScrollPane.getViewportBorderBounds().height/2;
                }
                
                canvasPanel.finalizeWirePosition(targetX, targetY);                
            }
        });
        
        nubButton.addMouseMotionListener(new MouseMotionListener()
        {

            @Override
            public void mouseDragged(MouseEvent e) 
            {                
                canvasPanel.setNubCreationPosition(canvasScrollPane.getViewport().getViewPosition().x -canvasPanel.getLocation().x + e.getX() + nubButton.getLocation().x,
                                                   canvasScrollPane.getViewport().getViewPosition().y -canvasPanel.getLocation().y + e.getY() + nubButton.getLocation().y - menuPanel.getHeight());
            }

            @Override
            public void mouseMoved(MouseEvent __) { }        
        });
        
        nubButton.addMouseListener(new MouseListener()
        {

            @Override
            public void mouseClicked(MouseEvent __) { }                            

            @Override
            public void mouseEntered(MouseEvent __) { }

            @Override
            public void mouseExited(MouseEvent __) { }

            @Override
            public void mousePressed(MouseEvent e) 
            {
                canvasPanel.startCreatingNub(-canvasPanel.getLocation().x + e.getX() + nubButton.getLocation().x,
                                             -canvasPanel.getLocation().y + e.getY() + nubButton.getLocation().y - menuPanel.getHeight());
            }                                 

            @Override
            public void mouseReleased(MouseEvent e) 
            {
                canvasPanel.finalizeNubPosition(-canvasPanel.getLocation().x + e.getX() + nubButton.getLocation().x,
                                                -canvasPanel.getLocation().y + e.getY() + nubButton.getLocation().y - menuPanel.getHeight());
            }
        
        });
        
        opacityButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent __)
            {
                if (canvasPanel.getNumberOfSelectedComponents() == 0)
                {    
                    JOptionPane.showMessageDialog(null, "No components have been selected!\nPlease select at least one white box component in order to toggle their opacity.", "Opacity Selection Error", JOptionPane.ERROR_MESSAGE);
                }
                else
                {                                                    
                    canvasPanel.toggleOpacity();
                }                                
            }
        });
        
        textboxButton.addMouseMotionListener(new MouseMotionListener()
        {
            @Override
            public void mouseDragged(MouseEvent e) 
            {                
                canvasPanel.setTextboxCreationPosition(canvasScrollPane.getViewport().getViewPosition().x -canvasPanel.getLocation().x + e.getX() + textboxButton.getLocation().x,
                                                       canvasScrollPane.getViewport().getViewPosition().y -canvasPanel.getLocation().y + e.getY() + textboxButton.getLocation().y - menuPanel.getHeight());
            }

            @Override
            public void mouseMoved(MouseEvent __) { } 
        });
        
        textboxButton.addMouseListener(new MouseListener()
        {

            @Override
            public void mouseClicked(MouseEvent __) { }                            

            @Override
            public void mouseEntered(MouseEvent __) { }

            @Override
            public void mouseExited(MouseEvent __) { }

            @Override
            public void mousePressed(MouseEvent e) 
            {
                canvasPanel.startCreatingTextbox(-canvasPanel.getLocation().x + e.getX() + textboxButton.getLocation().x,
                                                 -canvasPanel.getLocation().y + e.getY() + textboxButton.getLocation().y - menuPanel.getHeight());
            }                                 

            @Override
            public void mouseReleased(MouseEvent e) 
            {
                canvasPanel.finalizeTextboxPosition(-canvasPanel.getLocation().x + e.getX() + textboxButton.getLocation().x,
                                                    -canvasPanel.getLocation().y + e.getY() + textboxButton.getLocation().y - menuPanel.getHeight());
            }
        
        });
        
        paletteButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent __)
            {
                if (canvasPanel.getNumberOfSelectedComponents() == 0)
                {    
                    JOptionPane.showMessageDialog(null, "No components have been selected!\nPlease select one or more components in order to color them.", "Color Selection Error", JOptionPane.ERROR_MESSAGE);
                }
                else
                {                                                    
                    lastChosenColor = JColorChooser.showDialog(frame, "Choose custom color", lastChosenColor != null ? lastChosenColor : Colors.DEFAULT_COLOR);
                    if (lastChosenColor != null)
                    {
                        canvasPanel.colorSelectedComponents(lastChosenColor);
                    }
                }                                
            }
        });
        
        componentAxisResMove.addActionListener(new ActionListener() 
        { 
            public void actionPerformed(ActionEvent __) 
            {
                ConcreteComponent.globalConcreteComponentMovementType = Component.MovementType.AXIS_RESTRICTED;
            }
           });
        
        componentUnrestrictedMove.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent __)
            {
                ConcreteComponent.globalConcreteComponentMovementType = Component.MovementType.FREE;
            }
        });
        
        wireAxisResMove.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent __)
            {
                LineSegmentComponent.globalLineSegmentMovementType = Component.MovementType.AXIS_RESTRICTED;
            }
        });
        
        wireFreeMove.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent __)
            {
                LineSegmentComponent.globalLineSegmentMovementType = Component.MovementType.FREE;
            }
        });
        
        segmentSpawnButtonUnrestricted.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent __)
            {
                HingeComponent.globalHingeDraggingMode = Component.HingeDraggingMode.SPAWN_SEGMENT_UNRES; 
                
            }
        });
        
        segmentSpawnButtonAxisRes.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent __)
            {
                HingeComponent.globalHingeDraggingMode = Component.HingeDraggingMode.SPAWN_SEGMENT_AXIS_RES;
            }
        });
        
        moveHingeButtonUnrestricted.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent __)
            {
                HingeComponent.globalHingeDraggingMode = Component.HingeDraggingMode.MOVE_UNRES;
            }
        });
        
        moveHingeButtonAxisRes.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent __)
            {
                HingeComponent.globalHingeDraggingMode = Component.HingeDraggingMode.MOVE_AXIS_RES;
            }
        });
           
        alignmentCheckbox.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent __)
            {
                Component.globalAlignmentEnabled = !Component.globalAlignmentEnabled;
            }
        });        
        
        hingeVisibilityCheckbox.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent __)
            {
                HingeComponent.globalHingeVisibility = !HingeComponent.globalHingeVisibility;
            }
        });                
        
        arrowVisibilityCheckbox.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent __)
            {
                HingeComponent.arrowVisibility = !HingeComponent.arrowVisibility;
            }
        });        
        
        try 
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(frame);
        }
        catch (ClassNotFoundException | 
               InstantiationException | 
               IllegalAccessException |
               UnsupportedLookAndFeelException e1) 
        {        
            JOptionPane.showMessageDialog(null, "Error while chaning the look and feel of the window", "IO Error", JOptionPane.ERROR_MESSAGE);
        }
        
        return masterPanel;
    }
    
    private void displaySaveProjectDialog()
    {
        JFileChooser fc = new JFileChooser(".");
        FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("Circuit Design Editor file (*.cde)", "cde");
        fc.setFileFilter(fileFilter);
        
        int choice = fc.showSaveDialog(menuPanel);
        if (choice == JFileChooser.APPROVE_OPTION) 
        {    
            File selFile = fc.getSelectedFile();
            File adjustedFile = selFile;
            
            if (!selFile.getName().endsWith(".cde"))
            {
                adjustedFile = new File(selFile.getAbsolutePath() + ".cde");
            }
            
            if (adjustedFile.exists())
            {                        
                int selOption = JOptionPane.showConfirmDialog (null, "Overwrite existing file?", "Save Option", JOptionPane.YES_NO_OPTION);
                if (selOption == JOptionPane.YES_OPTION)
                {                        
                    canvasPanel.saveProjectToFile(adjustedFile);                    
                }
            }
            else
            {
                canvasPanel.saveProjectToFile(adjustedFile);
            }
        }
    }
    
    private void exportLibraryModal(final Set<Component> components)
    {
        JDialog jDialog = new JDialog(this, "Export as Library", ModalityType.APPLICATION_MODAL);
                
        JPanel exportLibSpecsPanel = new JPanel();
        exportLibSpecsPanel.setLayout(new BoxLayout(exportLibSpecsPanel, BoxLayout.Y_AXIS));
        
        JLabel libNameLabel = new JLabel("Library Name: ");
        
        JTextField libNameField = new JTextField();        
        libNameField.setName("Enter a name here");
        libNameField.setColumns(20);
        libNameField.addFocusListener(new SelectAllFocusListener(libNameField));        
        
        JLabel libExtLabel = new JLabel(".lib");
        
        JPanel libNamePanel = new JPanel();
        libNamePanel.add(libNameLabel);
        libNamePanel.add(libNameField);
        libNamePanel.add(libExtLabel);
        exportLibSpecsPanel.add(libNamePanel);
        
        JButton exportButton = new JButton("Export");                  
        exportButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent __) 
            {        
                if (!ProjectPersistenceUtilities.LIB_DIR.exists())
                {
                    ProjectPersistenceUtilities.LIB_DIR.mkdirs();
                }
                
                File libFile = new File(ProjectPersistenceUtilities.LIB_DIR.getAbsolutePath() + "/" + libNameField.getText() + ".lib");
                
                if (libFile.exists())
                {
                    int selOption = JOptionPane.showConfirmDialog (null, "Overwrite existing library file?", "Export Option", JOptionPane.YES_NO_OPTION);
                    if (selOption == JOptionPane.NO_OPTION)
                    {                                                            
                        jDialog.dispose();
                        return;                                    
                    }
                }
                else
                
                ProjectPersistenceUtilities.saveAsLibrary(libFile, components, canvasPanel.getSize());
                JOptionPane.showMessageDialog(null, "Exported library: " + libNameField.getText() + ".lib" + " successfully");                                
                jDialog.dispose();
            }            
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent __) 
            {
                jDialog.dispose();
            }            
        });
        
        JPanel optionsPanel = new JPanel();
        optionsPanel.add(exportButton);
        optionsPanel.add(cancelButton);
        
        JPanel exportLibOptionsPanel = new JPanel(new BorderLayout());
        exportLibOptionsPanel.add(optionsPanel, BorderLayout.EAST);  
                            
        JPanel lfPanel = new JPanel(new BorderLayout());
        lfPanel.add(exportLibSpecsPanel, BorderLayout.NORTH);
        lfPanel.add(exportLibOptionsPanel, BorderLayout.SOUTH);
        
        jDialog.setContentPane(lfPanel);
        jDialog.getRootPane().setDefaultButton(exportButton);                
        jDialog.pack();                
        jDialog.setResizable(false);
        jDialog.setLocationRelativeTo(this);
        jDialog.setVisible(true);              
        jDialog.setFocusable(true); 
    }
    
    private void changeLookAndFeel(final String lookAndFeelClassName)
    {
        try 
        {
            UIManager.setLookAndFeel(lookAndFeelClassName);
            SwingUtilities.updateComponentTreeUI(this);
        }
        catch (ClassNotFoundException | 
               InstantiationException | 
               IllegalAccessException |
               UnsupportedLookAndFeelException e1) 
        {        
            JOptionPane.showMessageDialog(null, "Error while changing the look and feel of the window", "IO Error", JOptionPane.ERROR_MESSAGE);
        }  
    }
}