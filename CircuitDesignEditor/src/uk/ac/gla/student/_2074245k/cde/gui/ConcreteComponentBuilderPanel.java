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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import uk.ac.gla.student._2074245k.cde.observables.ConcreteComponentCreationObservable;
import uk.ac.gla.student._2074245k.cde.observers.ConcreteComponentCreationObserver;
import uk.ac.gla.student._2074245k.cde.observers.PortModificationObserver;

public final class ConcreteComponentBuilderPanel extends JPanel implements PortModificationObserver, ConcreteComponentCreationObservable
{
	protected static final int MARGIN_DENOMINATOR  = 8;
	private static final long serialVersionUID   = -5262618480592072518L;
	
	protected static String componentName  = "Name of component";
	protected static int nameXOffset       = 0;
	protected static int nameYOffset       = 0;
	
	private JDialog modalAncestor = null;
	private ConcreteComponentBuilderViewPanel concreteComponentView   = null;
	private JPanel leftPortsPanel  = null;
	private JPanel rightPortsPanel = null;
	private JPanel topPortsPanel   = null;
	private JPanel botPortsPanel   = null;
	private Map<PortView, JPanel> portViewsToPanels = null;
	private List<ConcreteComponentCreationObserver> concreteComponentCreationObservers = null;	
	private Set<Component> selComponents = null;
	private int numLeftPorts = 0;
	private int numRightPorts = 0;
	private int numTopPorts = 0;
	private int numBotPorts = 0;
	private int minComponentWidth = 80;
	private int minComponentHeight = 80;
	
	public ConcreteComponentBuilderPanel(final MainCanvas canvas,
			                             final boolean shouldBuildWhiteBox,
			                             final Iterator<Component> selectedComponentsIter, 
			                             final JDialog ancestor)
	{
		super();		
		this.modalAncestor          = ancestor;
		
		portViewsToPanels = new HashMap<PortView, JPanel>();
		concreteComponentCreationObservers = new ArrayList<ConcreteComponentCreationObserver>();
		
		setLayout(new BorderLayout());						
			
		JPanel concreteComponentOptionsPanel = new JPanel();
		concreteComponentOptionsPanel.setLayout(new BoxLayout(concreteComponentOptionsPanel, BoxLayout.Y_AXIS));
		
		JLabel nameLabel = new JLabel("Component's name:");
		componentName = "Name";
		
		JTextField componentNameField = new JTextField(componentName, 20);		
		componentNameField.addFocusListener(new SelectAllFocusListener(componentNameField));
		componentNameField.getDocument().addDocumentListener(new DocumentListener()
		{
			@Override
			public void changedUpdate(DocumentEvent __) { componentName = componentNameField.getText(); concreteComponentView.repaint(); }

			@Override
			public void insertUpdate(DocumentEvent __) { componentName = componentNameField.getText(); concreteComponentView.repaint(); }

			@Override
			public void removeUpdate(DocumentEvent __) { componentName = componentNameField.getText(); concreteComponentView.repaint(); }		
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
				concreteComponentView.repaint();
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
				concreteComponentView.repaint();
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
		
		if (shouldBuildWhiteBox)
		{
			selComponents = new HashSet<Component>();
			while (selectedComponentsIter.hasNext()) selComponents.add(selectedComponentsIter.next());
			calculateMinDimensions();
		}
		
		NumberFormatter dimensionsWidthFormatter = new NumberFormatter(NumberFormat.getInstance());
		dimensionsWidthFormatter.setValueClass(Integer.class);
		dimensionsWidthFormatter.setMinimum(minComponentWidth);
		dimensionsWidthFormatter.setCommitsOnValidEdit(false);
		
		JLabel widthLabel = new JLabel("Component's width (>= " + minComponentWidth + "px):  ");
		JFormattedTextField widthField = new JFormattedTextField(dimensionsWidthFormatter);		
		widthField.setValue(minComponentWidth);
		widthField.setColumns(10);
		widthField.addFocusListener(new SelectAllFocusListener(widthField));		
		widthField.addPropertyChangeListener("value", new PropertyChangeListener()
		{
			@Override
			public void propertyChange(PropertyChangeEvent evt)
			{
				int canvasHeight = concreteComponentView.getHeight();
				int canvasWidth  = (int)evt.getNewValue() + 2 * ((int)evt.getNewValue() / MARGIN_DENOMINATOR);
				Dimension newCanvasDimension = new Dimension(canvasWidth, canvasHeight);	
				
				concreteComponentView.setSize(newCanvasDimension);
				concreteComponentView.setPreferredSize(newCanvasDimension);		
				concreteComponentView.setMinimumSize(newCanvasDimension);
				concreteComponentView.setMaximumSize(newCanvasDimension);
				validate();
				modalAncestor.pack();				
				concreteComponentView.repaint();
			}
		});
					
		NumberFormatter dimensionsHeightFormatter = new NumberFormatter(NumberFormat.getInstance());
		dimensionsHeightFormatter.setValueClass(Integer.class);
		dimensionsHeightFormatter.setMinimum(minComponentHeight);
		dimensionsHeightFormatter.setCommitsOnValidEdit(false);
		
		JLabel heightLabel = new JLabel("Component's height (>= " + minComponentHeight + "px): ");
		JFormattedTextField heightField = new JFormattedTextField(dimensionsHeightFormatter);		
		heightField.setValue(minComponentHeight);
		heightField.setColumns(10);
		heightField.addFocusListener(new SelectAllFocusListener(heightField));		
		heightField.addPropertyChangeListener("value", new PropertyChangeListener()
		{
			@Override
			public void propertyChange(PropertyChangeEvent evt)
			{
				int canvasWidth  = concreteComponentView.getWidth(); 
				int canvasHeight = (int)evt.getNewValue() + 2 * ((int)evt.getNewValue() / MARGIN_DENOMINATOR);
				Dimension newCanvasDimension = new Dimension(canvasWidth, canvasHeight);
								
				concreteComponentView.setSize(newCanvasDimension);
				concreteComponentView.setPreferredSize(newCanvasDimension);
				concreteComponentView.setMinimumSize(newCanvasDimension);
				concreteComponentView.setMaximumSize(newCanvasDimension);				
				validate();
				modalAncestor.pack();				
				concreteComponentView.repaint();
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
		
		concreteComponentOptionsPanel.add(namePanel);
		concreteComponentOptionsPanel.add(nameXOffsetPanel);
		concreteComponentOptionsPanel.add(nameYOffsetPanel);
		concreteComponentOptionsPanel.add(dimensionsPanel);						
		
		JCheckBox gridVisibilityCheckbox = new JCheckBox("Port Grid visibility");
		gridVisibilityCheckbox.setSelected(true);
		gridVisibilityCheckbox.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent __) 
			{
				concreteComponentView.toggleGridVisibility();
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
				PortView portView = new PortView(PortLocation.LEFT, concreteComponentView.getComponentRectangle().width/2, 1.0f/(numLeftPorts + 1), "port");
				callbackOnPortInsertionEvent(portView);
				concreteComponentView.portInserted(portView);
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
		concreteComponentOptionsPanel.add(leftPanel);
		
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
				PortView portView = new PortView(PortLocation.RIGHT, concreteComponentView.getComponentRectangle().width/2, 1.0f/(numRightPorts + 1), "port");
				callbackOnPortInsertionEvent(portView);
				concreteComponentView.portInserted(portView);
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
		concreteComponentOptionsPanel.add(rightPanel);
		
		// Top Port Panels 
		JPanel topPortsLabelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		topPortsLabelPanel.add(new JLabel("Top Ports:       "));		
		
		JButton topNewPort = new JButton("New Port");
		topNewPort.setForeground(Color.blue);
		topNewPort.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent __) 
			{
				numTopPorts++;
				PortView portView = new PortView(PortLocation.TOP, concreteComponentView.getComponentRectangle().width/2, 1.0f/(numTopPorts + 1), "port");
				callbackOnPortInsertionEvent(portView);
				concreteComponentView.portInserted(portView);
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
		concreteComponentOptionsPanel.add(topPanel);
		
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
				PortView portView = new PortView(PortLocation.BOTTOM, concreteComponentView.getComponentRectangle().width/2, 1.0f/(numBotPorts + 1), "port");
				callbackOnPortInsertionEvent(portView);
				concreteComponentView.portInserted(portView);
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
		concreteComponentOptionsPanel.add(botPanel);
		
		// Component View Panels
		JLabel concreteComponentPortCue = new JLabel("[You can also click in the grid below to place ports]");
		JPanel concreteComponentPortCuePanel = new JPanel();
		concreteComponentPortCuePanel.add(concreteComponentPortCue);
		concreteComponentOptionsPanel.add(concreteComponentPortCuePanel);
		
		concreteComponentView = new ConcreteComponentBuilderViewPanel(canvas, shouldBuildWhiteBox, selComponents);
		concreteComponentView.subscribeToPortInsertionEvent(this);
		concreteComponentView.subscribeToPortDeletionEvent(this);
		concreteComponentView.setPreferredSize(new Dimension(minComponentWidth + 2 * (minComponentWidth/MARGIN_DENOMINATOR),
				                                             minComponentHeight + 2 * (minComponentHeight/MARGIN_DENOMINATOR)));
		
		JPanel wrapperFixedViewPanel = new JPanel(new GridBagLayout());
        wrapperFixedViewPanel.add(concreteComponentView, new GridBagConstraints());        
		concreteComponentOptionsPanel.add(wrapperFixedViewPanel);
		
		
		JPanel gridVisibilityPanel = new JPanel();
		gridVisibilityPanel.add(gridVisibilityCheckbox);				
		concreteComponentOptionsPanel.add(gridVisibilityPanel);
				
		JButton createButton = new JButton("Create Component");
		createButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				Component concreteComponent = concreteComponentView.buildConcreteComponent();
				List<PortView> portViews = concreteComponentView.getPortViews();
				
				for (ConcreteComponentCreationObserver observer: concreteComponentCreationObservers)
				{
					observer.callbackOnConcreteComponentCreationEvent(concreteComponent, portViews);
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
		
		add(concreteComponentOptionsPanel, BorderLayout.NORTH);
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
			public void changedUpdate(DocumentEvent __) { concreteComponentView.portNameChange(portView, portNameField.getText()); }

			@Override
			public void insertUpdate(DocumentEvent __) { concreteComponentView.portNameChange(portView, portNameField.getText()); }

			@Override
			public void removeUpdate(DocumentEvent __) { concreteComponentView.portNameChange(portView, portNameField.getText()); }		
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
				concreteComponentView.repaint();				
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
				portPositionFormatter.setMinimum(concreteComponentView.getComponentRectangle().y);
				portPositionFormatter.setMaximum(concreteComponentView.getComponentRectangle().y + concreteComponentView.getComponentRectangle().height);
				
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
						concreteComponentView.portPositionChanged(portView, (int)evt.getNewValue());
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
						concreteComponentView.portDeleted(portView);
						leftPortsPanel.remove(portPanel);
						validate();										
					}					
				});
				portPanel.add(deleteButton);
				
				leftPortsPanel.add(portPanel);
			} break;
			
			case RIGHT:
			{
				portPositionFormatter.setMinimum(concreteComponentView.getComponentRectangle().y);
				portPositionFormatter.setMaximum(concreteComponentView.getComponentRectangle().y + concreteComponentView.getComponentRectangle().height);
				
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
						concreteComponentView.portPositionChanged(portView, (int)evt.getNewValue());
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
						concreteComponentView.portDeleted(portView);
						rightPortsPanel.remove(portPanel);
						validate();										
					}					
				});
				portPanel.add(deleteButton);
				
				rightPortsPanel.add(portPanel);
			} break;
			
			case TOP:    
			{
				portPositionFormatter.setMinimum(concreteComponentView.getComponentRectangle().x);
				portPositionFormatter.setMaximum(concreteComponentView.getComponentRectangle().x + concreteComponentView.getComponentRectangle().width);
				
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
						concreteComponentView.portPositionChanged(portView, (int)evt.getNewValue());
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
						concreteComponentView.portDeleted(portView);
						topPortsPanel.remove(portPanel);
						validate();											
					}					
				});
				portPanel.add(deleteButton);
				
				topPortsPanel.add(portPanel); 
			} break;
			
			case BOTTOM:
			{
				portPositionFormatter.setMinimum(concreteComponentView.getComponentRectangle().x);
				portPositionFormatter.setMaximum(concreteComponentView.getComponentRectangle().x + concreteComponentView.getComponentRectangle().width);
				
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
						concreteComponentView.portPositionChanged(portView, (int)evt.getNewValue());
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
						concreteComponentView.portDeleted(portView);
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
	public void subscribeToConcreteComponentCreationEvent(final ConcreteComponentCreationObserver observer) 
	{
		concreteComponentCreationObservers.add(observer);
	}
	
	private void calculateMinDimensions()
	{
		Component firstComponent = selComponents.iterator().next();
		int minX = firstComponent.getRectangle().x;
		int minY = firstComponent.getRectangle().y;
		int maxX = firstComponent.getRectangle().x + firstComponent.getRectangle().width;
		int maxY = firstComponent.getRectangle().y + firstComponent.getRectangle().height;
		
		// Find min and max points from selection
		for (Component comp: selComponents)
		{
			if (comp.getRectangle().x < minX)
				minX = comp.getRectangle().x;
			if (comp.getRectangle().y < minY)
				minY = comp.getRectangle().y;
			if (comp.getRectangle().x + comp.getRectangle().width > maxX)
				maxX = comp.getRectangle().x + comp.getRectangle().width;
			if (comp.getRectangle().y + comp.getRectangle().height > maxY)
				maxY = comp.getRectangle().y + comp.getRectangle().height;
		}
		
		// Add padding
		minX -= 32;
		minY -= 32;
		maxX += 32;
		maxY += 32;
		
		minComponentWidth  = maxX - minX;
		minComponentHeight = maxY - minY;
	}
}
