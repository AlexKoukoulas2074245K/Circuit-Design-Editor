package uk.ac.gla.student._2074245k.cde.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.NumberFormatter;

import uk.ac.gla.student._2074245k.cde.components.Component;
import uk.ac.gla.student._2074245k.cde.gui.PortView.PortLocation;
import uk.ac.gla.student._2074245k.cde.observables.BlackBoxCreationObservable;
import uk.ac.gla.student._2074245k.cde.observers.BlackBoxCreationObserver;
import uk.ac.gla.student._2074245k.cde.observers.PortModificationObserver;

public final class BlackBoxBuilderPanel extends JPanel implements PortModificationObserver, BlackBoxCreationObservable
{
	protected static final int MARGIN_DENOMINATOR  = 8;
	private static final long serialVersionUID   = -5262618480592072518L;
	private static final int DEFAULT_VIEW_WIDTH  = 256;
	private static final int DEFAULT_VIEW_HEIGHT = 256;		
	private static final Dimension DEFAULT_VIEW_DIMENSION = new Dimension(DEFAULT_VIEW_WIDTH + DEFAULT_VIEW_WIDTH / MARGIN_DENOMINATOR,
			                                                              DEFAULT_VIEW_HEIGHT + DEFAULT_VIEW_HEIGHT / MARGIN_DENOMINATOR);
	protected static String componentName  = "Name of component";
	protected static int nameXOffset       = 0;
	protected static int nameYOffset       = 0;
	
	private JDialog modalAncestor = null;
	private BlackBoxBuilderViewPanel blackBoxView   = null;
	private JPanel leftPortsPanel  = null;
	private JPanel rightPortsPanel = null;
	private JPanel topPortsPanel   = null;
	private JPanel botPortsPanel   = null;
	private Map<PortView, JPanel> portViewsToPanels = null;
	private List<BlackBoxCreationObserver> blackBoxCreationObservers = null;
	private int numLeftPorts = 0;
	private int numRightPorts = 0;
	private int numTopPorts = 0;
	private int numBotPorts = 0;
	
	public BlackBoxBuilderPanel(final MainCanvas canvas, final JDialog ancestor)
	{
		super();
		this.modalAncestor = ancestor;
		
		portViewsToPanels = new HashMap<PortView, JPanel>();
		blackBoxCreationObservers = new ArrayList<BlackBoxCreationObserver>();
		
		setLayout(new BorderLayout());						
			
		JPanel blackBoxOptionsPanel = new JPanel();
		blackBoxOptionsPanel.setLayout(new BoxLayout(blackBoxOptionsPanel, BoxLayout.Y_AXIS));
		
		JLabel nameLabel = new JLabel("Component's name:");
		componentName = "Name of component";
		
		JTextField componentNameField = new JTextField(componentName, 20);		
		componentNameField.addFocusListener(new SelectAllFocusListener(componentNameField));
		componentNameField.getDocument().addDocumentListener(new DocumentListener()
		{
			@Override
			public void changedUpdate(DocumentEvent __) { componentName = componentNameField.getText(); blackBoxView.repaint(); }

			@Override
			public void insertUpdate(DocumentEvent __) { componentName = componentNameField.getText(); blackBoxView.repaint(); }

			@Override
			public void removeUpdate(DocumentEvent __) { componentName = componentNameField.getText(); blackBoxView.repaint(); }		
		});
		
		NumberFormatter nameOffsetFormatter = new NumberFormatter(NumberFormat.getInstance());
		nameOffsetFormatter.setValueClass(Integer.class);
		nameOffsetFormatter.setCommitsOnValidEdit(false);
		
		JLabel nameXOffsetLabel = new JLabel("Component's name X offset (px):  ");
		JFormattedTextField nameXOffsetField = new JFormattedTextField(nameOffsetFormatter);
		nameXOffsetField.setColumns(4);
		nameXOffsetField.setValue(0);
		nameXOffsetField.addFocusListener(new SelectAllFocusListener(nameXOffsetField));
		nameXOffsetField.addPropertyChangeListener("value", new PropertyChangeListener()
		{
			@Override
			public void propertyChange(PropertyChangeEvent evt)
			{
				nameXOffset = (int)evt.getNewValue();
				blackBoxView.repaint();
			}
		});
		
		JLabel nameYOffsetLabel = new JLabel("Component's name Y offset: (px):  ");
		JFormattedTextField nameYOffsetField = new JFormattedTextField(nameOffsetFormatter);
		nameYOffsetField.setColumns(4);
		nameYOffsetField.setValue(0);
		nameYOffsetField.addFocusListener(new SelectAllFocusListener(nameYOffsetField));
		nameYOffsetField.addPropertyChangeListener("value", new PropertyChangeListener()
		{
			@Override
			public void propertyChange(PropertyChangeEvent evt)
			{
				nameYOffset = (int)evt.getNewValue();
				blackBoxView.repaint();
			}
		});
		
		JPanel namePanel = new JPanel(new WrapLayout());
		namePanel.add(nameLabel);
		namePanel.add(componentNameField);	
		
		
		JPanel nameXOffsetPanel = new JPanel();
		nameXOffsetPanel.add(nameXOffsetLabel);
		nameXOffsetPanel.add(nameXOffsetField);
		
		JPanel nameYOffsetPanel = new JPanel();
		nameYOffsetPanel.add(nameYOffsetLabel);
		nameYOffsetPanel.add(nameYOffsetField);
		
		NumberFormatter dimensionsFormatter = new NumberFormatter(NumberFormat.getInstance());
		dimensionsFormatter.setValueClass(Integer.class);
		dimensionsFormatter.setMinimum(48);
		dimensionsFormatter.setCommitsOnValidEdit(false);
		
		JLabel widthLabel = new JLabel("Component's width (>= 48px):  ");
		JFormattedTextField widthField = new JFormattedTextField(dimensionsFormatter);		
		widthField.setValue(DEFAULT_VIEW_WIDTH);
		widthField.setColumns(10);
		widthField.addFocusListener(new SelectAllFocusListener(widthField));		
		widthField.addPropertyChangeListener("value", new PropertyChangeListener()
		{
			@Override
			public void propertyChange(PropertyChangeEvent evt)
			{
				int canvasHeight = blackBoxView.getHeight();
				int canvasWidth  = (int)evt.getNewValue() + (int)evt.getNewValue() / MARGIN_DENOMINATOR;
				Dimension newCanvasDimension = new Dimension(canvasWidth, canvasHeight);	
				
				blackBoxView.setSize(newCanvasDimension);
				blackBoxView.setPreferredSize(newCanvasDimension);		
				blackBoxView.setMinimumSize(newCanvasDimension);
				blackBoxView.setMaximumSize(newCanvasDimension);
				validate();
				modalAncestor.pack();				
				blackBoxView.repaint();
			}
		});
					
		JLabel heightLabel = new JLabel("Component's height (>= 48px): ");
		JFormattedTextField heightField = new JFormattedTextField(dimensionsFormatter);		
		heightField.setValue(DEFAULT_VIEW_HEIGHT);
		heightField.setColumns(10);
		heightField.addFocusListener(new SelectAllFocusListener(heightField));		
		heightField.addPropertyChangeListener("value", new PropertyChangeListener()
		{
			@Override
			public void propertyChange(PropertyChangeEvent evt)
			{
				int canvasWidth  = blackBoxView.getWidth(); 
				int canvasHeight = (int)evt.getNewValue() + (int)evt.getNewValue() / MARGIN_DENOMINATOR;
				Dimension newCanvasDimension = new Dimension(canvasWidth, canvasHeight);
								
				blackBoxView.setSize(newCanvasDimension);
				blackBoxView.setPreferredSize(newCanvasDimension);
				blackBoxView.setMinimumSize(newCanvasDimension);
				blackBoxView.setMaximumSize(newCanvasDimension);				
				validate();
				modalAncestor.pack();				
				blackBoxView.repaint();
			}
		});
		
		JPanel widthPanel = new JPanel();
		widthPanel.add(widthLabel);
		widthPanel.add(widthField);
		
		JPanel heightPanel = new JPanel();
		heightPanel.add(heightLabel);
		heightPanel.add(heightField);
		
		JPanel dimensionsPanel = new JPanel();
		dimensionsPanel.setLayout(new BoxLayout(dimensionsPanel, BoxLayout.Y_AXIS));
		dimensionsPanel.add(widthPanel);
		dimensionsPanel.add(heightPanel);		
		
		blackBoxOptionsPanel.add(namePanel);
		blackBoxOptionsPanel.add(nameXOffsetPanel);
		blackBoxOptionsPanel.add(nameYOffsetPanel);
		blackBoxOptionsPanel.add(dimensionsPanel);						
		
		JCheckBox gridVisibilityCheckbox = new JCheckBox("Port Grid visibility");
		gridVisibilityCheckbox.setSelected(true);
		gridVisibilityCheckbox.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent __) 
			{
				blackBoxView.toggleGridVisibility();
			}		
		});
		
		
		// Left Port Panels
		JPanel leftPortsLabelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		leftPortsLabelPanel.add(new JLabel("Left Ports:      "));
		
		JButton leftNewPort = new JButton("New Port");
		leftNewPort.setForeground(Color.blue);
		leftNewPort.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent __) 
			{
				numLeftPorts++;
				PortView portView = new PortView(PortLocation.LEFT, blackBoxView.getComponentRectangle().width/2, 1.0f/(numLeftPorts + 1), "port");
				callbackOnPortInsertionEvent(portView);
				blackBoxView.portInserted(portView);
			}		
		});
		
		leftPortsLabelPanel.add(leftNewPort);
		
		leftPortsPanel = new JPanel();
		leftPortsPanel.setLayout(new BoxLayout(leftPortsPanel, BoxLayout.Y_AXIS));
		
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		leftPanel.add(leftPortsLabelPanel);
		leftPanel.add(leftPortsPanel);
		leftPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		blackBoxOptionsPanel.add(leftPanel);
		
		// Right Port Panels
		JPanel rightPortsLabelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		rightPortsLabelPanel.add(new JLabel("Right Ports:    "));		
		
		JButton rightNewPort = new JButton("New Port");
		rightNewPort.setForeground(Color.blue);
		rightNewPort.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent __) 
			{
				numRightPorts++;
				PortView portView = new PortView(PortLocation.RIGHT, blackBoxView.getComponentRectangle().width/2, 1.0f/(numRightPorts + 1), "port");
				callbackOnPortInsertionEvent(portView);
				blackBoxView.portInserted(portView);
			}		
		});
		rightPortsLabelPanel.add(rightNewPort);
		
		rightPortsPanel = new JPanel();
		rightPortsPanel.setLayout(new BoxLayout(rightPortsPanel, BoxLayout.Y_AXIS));
		
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
		rightPanel.add(rightPortsLabelPanel);
		rightPanel.add(rightPortsPanel);
		rightPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		blackBoxOptionsPanel.add(rightPanel);
		
		// Top Port Panels 
		JPanel topPortsLabelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		topPortsLabelPanel.add(new JLabel("Top Ports:      "));		
		
		JButton topNewPort = new JButton("New Port");
		topNewPort.setForeground(Color.blue);
		topNewPort.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent __) 
			{
				numTopPorts++;
				PortView portView = new PortView(PortLocation.TOP, blackBoxView.getComponentRectangle().width/2, 1.0f/(numTopPorts + 1), "port");
				callbackOnPortInsertionEvent(portView);
				blackBoxView.portInserted(portView);
			}		
		});
		topPortsLabelPanel.add(topNewPort);
		
		topPortsPanel = new JPanel();
		topPortsPanel.setLayout(new BoxLayout(topPortsPanel, BoxLayout.Y_AXIS));		
		
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
		topPanel.add(topPortsLabelPanel);
		topPanel.add(topPortsPanel);
		topPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		blackBoxOptionsPanel.add(topPanel);
		
		// Bottom Port Panels
		JPanel botPortsLabelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		botPortsLabelPanel.add(new JLabel("Bottom Ports: "));				
		
		JButton botNewPort = new JButton("New Port");
		botNewPort.setForeground(Color.blue);
		botNewPort.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent __) 
			{
				numBotPorts++;
				PortView portView = new PortView(PortLocation.BOTTOM, blackBoxView.getComponentRectangle().width/2, 1.0f/(numBotPorts + 1), "port");
				callbackOnPortInsertionEvent(portView);
				blackBoxView.portInserted(portView);
			}		
		});
		botPortsLabelPanel.add(botNewPort);
		
		botPortsPanel = new JPanel();
		botPortsPanel.setLayout(new BoxLayout(botPortsPanel, BoxLayout.Y_AXIS));		
		
		JPanel botPanel = new JPanel();
		botPanel.setLayout(new BoxLayout(botPanel, BoxLayout.Y_AXIS));
		botPanel.add(botPortsLabelPanel);
		botPanel.add(botPortsPanel);
		botPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		blackBoxOptionsPanel.add(botPanel);
		
		// Black box View Panels
		JLabel blackBoxPortCue = new JLabel("[You can also click outside the black box area to place ports]");
		JPanel blackBoxPortCuePanel = new JPanel();
		blackBoxPortCuePanel.add(blackBoxPortCue);
		blackBoxOptionsPanel.add(blackBoxPortCuePanel);
		
		blackBoxView = new BlackBoxBuilderViewPanel(canvas);
		blackBoxView.subscribeToPortInsertionEvent(this);
		blackBoxView.subscribeToPortDeletionEvent(this);
		blackBoxView.setPreferredSize(DEFAULT_VIEW_DIMENSION);
		
		JPanel wrapperFixedViewPanel = new JPanel(new GridBagLayout());
        wrapperFixedViewPanel.add(blackBoxView, new GridBagConstraints());        
		blackBoxOptionsPanel.add(wrapperFixedViewPanel);
		
		
		JPanel gridVisibilityPanel = new JPanel();
		gridVisibilityPanel.add(gridVisibilityCheckbox);				
		blackBoxOptionsPanel.add(gridVisibilityPanel);
				
		JButton createButton = new JButton("Create Component");
		createButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				Component blackBoxComponent = blackBoxView.buildBlackBox();
				List<PortView> portViews = blackBoxView.getPortViews();
				
				for (BlackBoxCreationObserver observer: blackBoxCreationObservers)
				{
					observer.callbackOnBlackBoxCreationEvent(blackBoxComponent, portViews);
				}
				modalAncestor.dispose();
			}			
		});
		
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent __) 
			{
				modalAncestor.dispose();
			}			
		});
			
		JPanel optionsPanel = new JPanel();
		optionsPanel.add(createButton);
		optionsPanel.add(cancelButton);
		
		JPanel finalOptionsPanel = new JPanel(new BorderLayout());
		finalOptionsPanel.add(optionsPanel, BorderLayout.EAST);
		
		add(blackBoxOptionsPanel, BorderLayout.NORTH);
		add(finalOptionsPanel, BorderLayout.SOUTH);	
	}

	@Override
	public void callbackOnPortInsertionEvent(final PortView portView)
	{
		JPanel portPanel = new JPanel();		
		portViewsToPanels.put(portView, portPanel);
		portPanel.add(new JLabel("Port:"));
		
		JTextField portNameField = new JTextField(portView.portName, 6);		
		portNameField.addFocusListener(new SelectAllFocusListener(portNameField));
		portNameField.getDocument().addDocumentListener(new DocumentListener()
		{
			@Override
			public void changedUpdate(DocumentEvent __) { blackBoxView.portNameChange(portView, portNameField.getText()); }

			@Override
			public void insertUpdate(DocumentEvent __) { blackBoxView.portNameChange(portView, portNameField.getText()); }

			@Override
			public void removeUpdate(DocumentEvent __) { blackBoxView.portNameChange(portView, portNameField.getText()); }		
		});		
		portPanel.add(portNameField);
		
		JCheckBox invertedCheckbox = new JCheckBox("Inv");		
		invertedCheckbox.setSelected(false);
		invertedCheckbox.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent __) 
			{
				portView.isInverted = !portView.isInverted;
				blackBoxView.repaint();				
			}			
		});
		
		portPanel.add(invertedCheckbox);
		
		NumberFormatter portPositionFormatter = new NumberFormatter(NumberFormat.getInstance());
		portPositionFormatter.setValueClass(Integer.class);
		portPositionFormatter.setCommitsOnValidEdit(false);
		
				
		switch (portView.portLocation)
		{		
			case LEFT:
			{
				portPositionFormatter.setMinimum(blackBoxView.getComponentRectangle().y);
				portPositionFormatter.setMaximum(blackBoxView.getComponentRectangle().y + blackBoxView.getComponentRectangle().height);
				
				JLabel portYLabel = new JLabel("Y (px):  ");
				JFormattedTextField portYField = new JFormattedTextField(portPositionFormatter);
				portYField.setColumns(4);
				portYField.setValue(portView.actualPosition);
				portYField.addFocusListener(new SelectAllFocusListener(portYField));
				portYField.addPropertyChangeListener("value", new PropertyChangeListener()
				{
					@Override
					public void propertyChange(PropertyChangeEvent evt)
					{
						blackBoxView.portPositionChanged(portView, (int)evt.getNewValue());
					}
				});
				
				portPanel.add(portYLabel);
				portPanel.add(portYField);
								
				JButton deleteButton = new JButton("Delete");
				deleteButton.setForeground(Color.red);
				deleteButton.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent __) 
					{
						numLeftPorts--;
						blackBoxView.portDeleted(portView);
						leftPortsPanel.remove(portPanel);
						validate();										
					}					
				});
				portPanel.add(deleteButton);
				
				leftPortsPanel.add(portPanel);
			} break;
			
			case RIGHT:
			{
				portPositionFormatter.setMinimum(blackBoxView.getComponentRectangle().y);
				portPositionFormatter.setMaximum(blackBoxView.getComponentRectangle().y + blackBoxView.getComponentRectangle().height);
				
				JLabel portYLabel = new JLabel("Y (px):  ");
				JFormattedTextField portYField = new JFormattedTextField(portPositionFormatter);
				portYField.setColumns(4);
				portYField.setValue(portView.actualPosition);
				portYField.addFocusListener(new SelectAllFocusListener(portYField));
				portYField.addPropertyChangeListener("value", new PropertyChangeListener()
				{
					@Override
					public void propertyChange(PropertyChangeEvent evt)
					{
						blackBoxView.portPositionChanged(portView, (int)evt.getNewValue());
					}
				});
				
				portPanel.add(portYLabel);
				portPanel.add(portYField);
				
				JButton deleteButton = new JButton("Delete");
				deleteButton.setForeground(Color.red);
				deleteButton.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent __) 
					{
						numRightPorts--;
						blackBoxView.portDeleted(portView);
						rightPortsPanel.remove(portPanel);
						validate();										
					}					
				});
				portPanel.add(deleteButton);
				
				rightPortsPanel.add(portPanel);
			} break;
			
			case TOP:    
			{
				portPositionFormatter.setMinimum(blackBoxView.getComponentRectangle().x);
				portPositionFormatter.setMaximum(blackBoxView.getComponentRectangle().x + blackBoxView.getComponentRectangle().width);
				
				JLabel portXLabel = new JLabel("X (px):  ");
				JFormattedTextField portXField = new JFormattedTextField(portPositionFormatter);
				portXField.setColumns(4);
				portXField.setValue(portView.actualPosition);
				portXField.addFocusListener(new SelectAllFocusListener(portXField));
				portXField.addPropertyChangeListener("value", new PropertyChangeListener()
				{
					@Override
					public void propertyChange(PropertyChangeEvent evt)
					{
						blackBoxView.portPositionChanged(portView, (int)evt.getNewValue());
					}
				});
				
				portPanel.add(portXLabel);
				portPanel.add(portXField);
				
				JButton deleteButton = new JButton("Delete");
				deleteButton.setForeground(Color.red);
				deleteButton.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent __) 
					{
						numTopPorts--;
						blackBoxView.portDeleted(portView);
						topPortsPanel.remove(portPanel);
						validate();											
					}					
				});
				portPanel.add(deleteButton);
				
				topPortsPanel.add(portPanel); 
			} break;
			
			case BOTTOM:
			{
				portPositionFormatter.setMinimum(blackBoxView.getComponentRectangle().x);
				portPositionFormatter.setMaximum(blackBoxView.getComponentRectangle().x + blackBoxView.getComponentRectangle().width);
				
				JLabel portXLabel = new JLabel("X (px):  ");
				JFormattedTextField portXField = new JFormattedTextField(portPositionFormatter);
				portXField.setColumns(4);
				portXField.setValue(portView.actualPosition);
				portXField.addFocusListener(new SelectAllFocusListener(portXField));
				portXField.addPropertyChangeListener("value", new PropertyChangeListener()
				{
					@Override
					public void propertyChange(PropertyChangeEvent evt)
					{
						blackBoxView.portPositionChanged(portView, (int)evt.getNewValue());
					}
				});
				
				portPanel.add(portXLabel);
				portPanel.add(portXField);
				
				JButton deleteButton = new JButton("Delete");
				deleteButton.setForeground(Color.red);
				deleteButton.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent __) 
					{
						numBotPorts--;
						blackBoxView.portDeleted(portView);
						botPortsPanel.remove(portPanel);
						validate();												
					}					
				});
				portPanel.add(deleteButton);				
				botPortsPanel.add(portPanel);
			} break;
		}		
		validate();
		modalAncestor.validate();		
	}

	@Override
	public void callbackOnPortDeletionEvent(final PortView portView) 
	{		
		switch (portView.portLocation)
		{
			case LEFT:   leftPortsPanel.remove(portViewsToPanels.get(portView)); numLeftPorts--; break;
			case RIGHT:  rightPortsPanel.remove(portViewsToPanels.get(portView)); numRightPorts--; break;
			case TOP:    topPortsPanel.remove(portViewsToPanels.get(portView)); numTopPorts--; break;
			case BOTTOM: botPortsPanel.remove(portViewsToPanels.get(portView)); numBotPorts--; break;
		}
		
		validate();
		modalAncestor.validate();		
	}

	@Override
	public void subscribeToBlackBoxCreationEvent(final BlackBoxCreationObserver observer) 
	{
		blackBoxCreationObservers.add(observer);
	}	
}
