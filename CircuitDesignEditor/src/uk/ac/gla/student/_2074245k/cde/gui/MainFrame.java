package uk.ac.gla.student._2074245k.cde.gui;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;

import uk.ac.gla.student._2074245k.cde.components.Component;
import uk.ac.gla.student._2074245k.cde.components.ConcreteComponent;
import uk.ac.gla.student._2074245k.cde.components.GateComponent;
import uk.ac.gla.student._2074245k.cde.components.GateComponent.GateType;
import uk.ac.gla.student._2074245k.cde.components.HingeComponent;
import uk.ac.gla.student._2074245k.cde.components.LineSegmentComponent;
import uk.ac.gla.student._2074245k.cde.util.GraphicsGenerator;


public final class MainFrame 
{       
    private static JPanel menuPanel;
    private static MainCanvas canvasPanel;
    private static JPanel masterPanel;    
    
    public void begin(JFrame window)
    {
    	canvasPanel.begin(window);
    }
    
    public JComponent createComponents(final JFrame frame) throws IOException 
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
		
		JMenuItem saveProjectItem = new JMenuItem("Save project", KeyEvent.VK_3);
		saveProjectItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		saveProjectItem.getAccessibleContext().setAccessibleDescription("Saves the project to disk");
		
		JMenuItem exportProjectItem = new JMenuItem("Export to SVG", KeyEvent.VK_4);
		exportProjectItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
		exportProjectItem.getAccessibleContext().setAccessibleDescription("Export project to scalable vector graphics (svg) format");
		
		fileMenuTab.add(createNewCanvasItem);
		fileMenuTab.add(openProjectItem);
		fileMenuTab.add(saveProjectItem);
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
		
		editTab.add(undoItem);
		editTab.add(redoItem);
		
		JMenu windowTab = new JMenu("Window");
		windowTab.setMnemonic(KeyEvent.VK_W);
		windowTab.getAccessibleContext().setAccessibleDescription("Window related options");
		
		JMenuItem changeLF = new JMenuItem("Change look and feel", KeyEvent.VK_1);
		changeLF.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.SHIFT_MASK | ActionEvent.ALT_MASK));
		changeLF.getAccessibleContext().setAccessibleDescription("Open an existing project");
		
		windowTab.add(changeLF);
		
    	menuBar.add(fileMenuTab);
    	menuBar.add(editTab);
    	menuBar.add(windowTab);
    	frame.setJMenuBar(menuBar);
    	    	
        menuPanel = new JPanel(new WrapLayout());        
                        
    	final Image bbImage     = ImageIO.read(getClass().getResourceAsStream("/icons/blackbox_icon.png"));
    	final Image gateImage   = ImageIO.read(getClass().getResourceAsStream("/icons/lg_icon.png"));
    	final Image wiresImage  = ImageIO.read(getClass().getResourceAsStream("/icons/wire_icon.png"));
    	final Image nubImage    = ImageIO.read(getClass().getResourceAsStream("/icons/nub_icon.png"));
        
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
        JRadioButton moveHingeButtonUnrestricted            = new JRadioButton("Move Hinge Unrestricted");        
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
        
        // Path visibility checkbox
        JCheckBox pathVisibilityCheckbox = new JCheckBox("Wire Path Visibility");
        pathVisibilityCheckbox.setSelected(true);
        
        JPanel checkBoxPanel = new JPanel(new BorderLayout());
        checkBoxPanel.add(alignmentCheckbox, BorderLayout.NORTH);
        checkBoxPanel.add(hingeVisibilityCheckbox, BorderLayout.CENTER);
        checkBoxPanel.add(pathVisibilityCheckbox, BorderLayout.SOUTH);
        
        // Add components to the menu panel
        menuPanel.add(bbButton);        
        menuPanel.add(gateButton);
        menuPanel.add(wireButton); 
        menuPanel.add(nubButton);        
        menuPanel.add(componentMovementPanel);
        menuPanel.add(wireMovementPanel);        
        menuPanel.add(hingeMovementPanel);
        menuPanel.add(checkBoxPanel);
        canvasPanel = new MainCanvas();
        
        // Create the master panel
        masterPanel = new JPanel(new BorderLayout());
        masterPanel.add(menuPanel, BorderLayout.NORTH);
        masterPanel.add(canvasPanel, BorderLayout.CENTER);
               
        // Add functionality to components
        createNewCanvasItem.addActionListener(new ActionListener()
        {
        	public void actionPerformed(ActionEvent __) 
        	{
                System.out.println("To be implemented..");
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
                	                	
                	selFile.delete();
                	canvasPanel.saveProjectToFile(adjustedFile);                	
                }
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
                	                	
                	selFile.delete();
                	
                	canvasPanel.exportToSVG(adjustedFile);
                }
        	}
        });
        
        undoItem.addActionListener(new ActionListener()
        {
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				canvasPanel.undo();
			}
        	
        });
        
        redoItem.addActionListener(new ActionListener()
        {
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				canvasPanel.redo();
			}    
        });
        
        changeLF.addActionListener(new ActionListener()
		{
        	public void actionPerformed(ActionEvent __)
        	{
        		JDialog jg = new JDialog(frame, "Change Look & Feel", ModalityType.APPLICATION_MODAL);
        		
        		JPanel lfiPanel = new JPanel();        		 
        		JComboBox<String> cb = new JComboBox<String>();
        		
        		LookAndFeelInfo[] lfInfo = UIManager.getInstalledLookAndFeels();        		
        		for (LookAndFeelInfo lfi: lfInfo)
        			cb.addItem(lfi.getClassName());
        		lfiPanel.add(cb, BorderLayout.CENTER);        	    
        		
        		JButton applyButton = new JButton("Apply");
        		applyButton.addActionListener(new ActionListener()
        		{
        			@Override
        			public void actionPerformed(ActionEvent arg0) 
        			{
        				try 
        				{
							UIManager.setLookAndFeel((String)cb.getSelectedItem());
							SwingUtilities.updateComponentTreeUI(frame);
						}
        				catch (ClassNotFoundException | 
        						 InstantiationException | 
        						 IllegalAccessException |
        						 UnsupportedLookAndFeelException e1) 
        		        {		
        		        	JOptionPane.showMessageDialog(null, "Error while chaning the look and feel of the window", "IO Error", JOptionPane.ERROR_MESSAGE);
        				}       				
        			}			
        		});
        		
        		JButton cancelButton = new JButton("Cancel");
        		cancelButton.addActionListener(new ActionListener()
        		{
        			@Override
        			public void actionPerformed(ActionEvent __) 
        			{
        				jg.dispose();
        			}			
        		});
        		
        		JPanel optionsPanel = new JPanel();
        		optionsPanel.add(applyButton);
        		optionsPanel.add(cancelButton);
        		
        		JPanel lfOptionsPanel = new JPanel(new BorderLayout());
        		lfOptionsPanel.add(optionsPanel, BorderLayout.EAST);  
        		
        		JPanel lfPanel = new JPanel(new BorderLayout());
        		lfPanel.add(lfiPanel, BorderLayout.NORTH);
        		lfPanel.add(lfOptionsPanel, BorderLayout.SOUTH);
        		
        		jg.setContentPane(lfPanel);        		
        		jg.pack();        		
        		jg.setResizable(false);
        		jg.setLocationRelativeTo(frame);
        		jg.setVisible(true);   
        	}
		});
        
        bbButton.addActionListener(new ActionListener()
		{
        	public void actionPerformed(ActionEvent __) 
        	{
        		JDialog jg = new JDialog(frame, "Black Box specification", ModalityType.APPLICATION_MODAL);
        		jg.setResizable(false);
        		
        		BlackBoxBuilderPanel builderPanel = new BlackBoxBuilderPanel(canvasPanel, jg);
        		builderPanel.subscribeToBlackBoxCreationEvent(canvasPanel);
        		jg.setContentPane(builderPanel);        		
        		jg.setIconImage(bbImage);
        		jg.pack();
        		jg.setLocationRelativeTo(frame);        	
        		jg.setVisible(true);           		
            }
		});
                
        gateButton.addActionListener(new ActionListener()
		{
        	public void actionPerformed(ActionEvent __) 
        	{        	
        		JDialog jg = new JDialog(frame, "Gate selection", ModalityType.APPLICATION_MODAL);
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
        				GateComponent gateToAdd = new GateComponent(canvasPanel, GateComponent.GateType.valueOf((String)cb.getSelectedItem()), true, canvasPanel.getWidth()/2, canvasPanel.getHeight()/2);
                		gateToAdd.constructPortsAutomatically();                		
                		canvasPanel.addNewComponent(gateToAdd);
        				jg.dispose();
        			}			
        		});
        		
        		JButton cancelButton = new JButton("Cancel");
        		cancelButton.addActionListener(new ActionListener()
        		{
        			@Override
        			public void actionPerformed(ActionEvent __) 
        			{
        				jg.dispose();
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
        		        		
        		jg.setContentPane(modalPanel);        		
        		jg.pack();
        		jg.setIconImage(gateImage);
        		jg.setResizable(false);
        		jg.setLocationRelativeTo(frame);
        		jg.setVisible(true);        		        		
            }
		});
        
        wireButton.addActionListener(new ActionListener()
        {
        	public void actionPerformed(final ActionEvent __)
        	{
        		canvasPanel.finalizeWirePosition(canvasPanel.getWidth()/2, canvasPanel.getHeight()/2);                
        	}
        });
       
        nubButton.addMouseMotionListener(new MouseMotionListener()
        {

			@Override
			public void mouseDragged(MouseEvent e) 
			{
				canvasPanel.setNubCreationPosition(e.getX() + nubButton.getLocation().x, e.getY() + nubButton.getLocation().y - 85);
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
				canvasPanel.startCreatingNub(e.getX() + nubButton.getLocation().x, e.getY() + nubButton.getLocation().y - 85);
			} 								

			@Override
			public void mouseReleased(MouseEvent e) 
			{
				canvasPanel.finalizeNubPosition(e.getX() + nubButton.getLocation().x, e.getY() + nubButton.getLocation().y - 85);
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
        
        pathVisibilityCheckbox.addActionListener(new ActionListener()
        {
        	public void actionPerformed(ActionEvent __)
        	{
        		LineSegmentComponent.globalPathVisibility = !LineSegmentComponent.globalPathVisibility;
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
}