package uk.ac.gla.student._2074245k.cde.gui;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import uk.ac.gla.student._2074245k.cde.actions.Action;
import uk.ac.gla.student._2074245k.cde.actions.MoveAction;
import uk.ac.gla.student._2074245k.cde.actions.MultiMoveAction;
import uk.ac.gla.student._2074245k.cde.actions.PasteAction;
import uk.ac.gla.student._2074245k.cde.components.AlignedComponentsList;
import uk.ac.gla.student._2074245k.cde.components.BlackBoxComponent;
import uk.ac.gla.student._2074245k.cde.components.Component;
import uk.ac.gla.student._2074245k.cde.components.Component.ComponentType;
import uk.ac.gla.student._2074245k.cde.components.Component.Orientation;
import uk.ac.gla.student._2074245k.cde.components.ConcreteComponent;
import uk.ac.gla.student._2074245k.cde.components.HingeComponent;
import uk.ac.gla.student._2074245k.cde.components.LineSegmentComponent;
import uk.ac.gla.student._2074245k.cde.observers.BlackBoxCreationObserver;
import uk.ac.gla.student._2074245k.cde.util.FrameCounter;
import uk.ac.gla.student._2074245k.cde.util.GraphicsGenerator;
import uk.ac.gla.student._2074245k.cde.util.Keyboard;
import uk.ac.gla.student._2074245k.cde.util.Mouse;
import uk.ac.gla.student._2074245k.cde.util.ProjectPersistenceUtilities;


public final class MainCanvas extends JPanel implements Runnable,											  
                                                        KeyListener,
                                                        MouseListener,
                                                        MouseMotionListener,
                                                        ComponentListener,
                                                        BlackBoxCreationObserver
{
	public enum MovementType
	{
		FREE, AXIS_RESTRICTED
	}
			
	public static Mouse mouse;
	public static Keyboard keyboard;
	
	private static final long serialVersionUID   = 1L;
	private static final int NEW_WIRE_LENGTH     = 200;
	
	private JFrame window;
	private Thread mainCanvasThread;
	private Set<Component> components;	
	private List<Component> lineSegmentPath;
	private List<Component> nubPlacementIncidentComponents;
	private List<Component> componentsToAdd;
	private List<Component> componentsToRemove;
	
	private Component highlightedComponent;
	private ComponentSelector componentSelector;
	private DOMImplementation domImpl;
	private Document document;
	
	private int selectionDx, selectionDy, dragNubX, dragNubY, incidentNubX, incidentNubY;			
	private AlignedComponentsList alignedComponents, prevAlignedComponents;
	private boolean isCreatingNub, isNubInContact, movingComponents;
	
	private List<Action> executedActionHistory;
	private List<Action> undoneActionHistory;
	private List<int[]> startPositions;
	private List<int[]> targetPositions;		
	
	private File lastSaveLocation = null;
	
	public MainCanvas(JFrame window)
	{		
		super();
		this.window = window;		
		init(null);	
	}
	
	@Override
	public void addNotify()
	{
		super.addNotify();
		
		if (mainCanvasThread == null)
		{
			mainCanvasThread = new Thread(this);
			addKeyListener(this);
			addMouseListener(this);
			addMouseMotionListener(this);		
			addComponentListener(this);
			mainCanvasThread.start();
		}
	}
		
	@Override
	public void run() 
	{
		requestFocus();
		FrameCounter frameCounter = new FrameCounter();
		
		while (true)
		{						
			checkAndAddNewComponents();
			checkAndRemoveMarkedComponents();
			inputUpdates();			
			repaint();
			frameCounter.update(components, 
					            componentSelector, 
					            executedActionHistory.size(),
					            undoneActionHistory.size(),
					            window);
		}				 
	}

	@Override
	public void paintComponent(final Graphics g)
	{
		GraphicsGenerator gfxGen = new GraphicsGenerator((Graphics2D)g);
		display(gfxGen);
	}
	
	@Override
	public void keyPressed(KeyEvent eventArgs) { requestFocus(); keyboard.updateOnKeyPress(eventArgs); }

	@Override
	public void keyReleased(KeyEvent eventArgs) { requestFocus(); keyboard.updateOnKeyRelease(eventArgs); }

	@Override
	public void keyTyped(KeyEvent __) {}	
	
	@Override
	public void mouseDragged(MouseEvent eventArgs) { requestFocus(); mouse.updateOnMouseDrag(eventArgs); }
	
	@Override
	public void mouseMoved(MouseEvent eventArgs) { requestFocus(); mouse.updateOnMouseMove(eventArgs); }

	@Override
	public void mousePressed(MouseEvent eventArgs) { requestFocus(); mouse.updateOnMousePressed(eventArgs); }
	
	@Override
	public void mouseReleased(MouseEvent eventArgs) { requestFocus(); mouse.updateOnMouseReleased(eventArgs); }
	
	@Override
	public void mouseClicked(MouseEvent eventArgs) { requestFocus(); mouse.updateOnMouseReleased(eventArgs); if (eventArgs.getButton() == MouseEvent.BUTTON2) for (Component component: components)
	{
		System.out.println("Component " + component.getComponentType() + " at: " + component.getRectangle().x + ", " + component.getRectangle().y);
	}}

	@Override
	public void mouseEntered(MouseEvent __) {}

	@Override
	public void mouseExited(MouseEvent __) {}
	
	@Override
	public void componentHidden(ComponentEvent __) {}
	
	@Override
	public void componentMoved(ComponentEvent __) {}
	
	@Override
	public void componentResized(ComponentEvent __) 
	{
		repaint();
	}
	
	@Override
	public void componentShown(ComponentEvent __) {}
	
	@Override
	public void callbackOnBlackBoxCreationEvent(final Component blackBox, final List<PortView> portViews) 
	{
				
		Rectangle rect = blackBox.getRectangle();
		
		for (PortView portView: portViews)
		{			
			Component externalHinge = null;
			Component internalHinge = null;
			Component port = null;
			
			switch (portView.portLocation)
			{					
				case LEFT:
				{
					externalHinge = new HingeComponent(this, rect.x - BlackBoxComponent.PORT_LENGTH - HingeComponent.HINGE_DIAMETER/2, portView.actualPosition - HingeComponent.HINGE_DIAMETER/2, false);
					internalHinge = new HingeComponent(this, rect.x - HingeComponent.HINGE_DIAMETER/2, portView.actualPosition - HingeComponent.HINGE_DIAMETER/2, false);
					port = new LineSegmentComponent(this, externalHinge, internalHinge, false);						
					
					((HingeComponent)internalHinge).addInternalHingeInfo(PortView.PortLocation.LEFT, portView.portName);
					((BlackBoxComponent)blackBox).addInternalHorHinge(internalHinge);
					
				} break;
				
				case RIGHT:
				{
					externalHinge = new HingeComponent(this, rect.x + rect.width + BlackBoxComponent.PORT_LENGTH - HingeComponent.HINGE_DIAMETER/2, portView.actualPosition - HingeComponent.HINGE_DIAMETER/2, false);
					internalHinge = new HingeComponent(this, rect.x + rect.width - HingeComponent.HINGE_DIAMETER/2, portView.actualPosition - HingeComponent.HINGE_DIAMETER/2, false);
					port = new LineSegmentComponent(this, externalHinge, internalHinge, false);
					
					((HingeComponent)internalHinge).addInternalHingeInfo(PortView.PortLocation.RIGHT, portView.portName);
					((BlackBoxComponent)blackBox).addInternalHorHinge(internalHinge);						
					
				} break;
				
				case TOP:
				{
					externalHinge = new HingeComponent(this, portView.actualPosition - HingeComponent.HINGE_DIAMETER/2, rect.y - BlackBoxComponent.PORT_LENGTH - HingeComponent.HINGE_DIAMETER/2, false);
					internalHinge = new HingeComponent(this, portView.actualPosition - HingeComponent.HINGE_DIAMETER/2, rect.y - HingeComponent.HINGE_DIAMETER/2, false);
					port = new LineSegmentComponent(this, externalHinge, internalHinge, false);
					
					((HingeComponent)internalHinge).addInternalHingeInfo(PortView.PortLocation.TOP, portView.portName);
					((BlackBoxComponent)blackBox).addInternalVerHinge(internalHinge);						
					
				} break;
				
				case BOTTOM:
				{
					externalHinge = new HingeComponent(this, portView.actualPosition - HingeComponent.HINGE_DIAMETER/2, rect.y + rect.height + BlackBoxComponent.PORT_LENGTH - HingeComponent.HINGE_DIAMETER/2, false);
					internalHinge = new HingeComponent(this, portView.actualPosition - HingeComponent.HINGE_DIAMETER/2, rect.y + rect.height - HingeComponent.HINGE_DIAMETER/2, false);
					port = new LineSegmentComponent(this, externalHinge, internalHinge, false);
					
					((HingeComponent)internalHinge).addInternalHingeInfo(PortView.PortLocation.BOTTOM, portView.portName);
					((BlackBoxComponent)blackBox).addInternalVerHinge(internalHinge);								
				}  break;
			}
			
			((HingeComponent)internalHinge).setIsInverted(portView.isInverted);
			
			((BlackBoxComponent)blackBox).addPort(port);
			
			addComponentToCanvas(externalHinge);
			addComponentToCanvas(internalHinge);
			addComponentToCanvas(port);	
		}
		
		addComponentToCanvas(blackBox);	
		
		Component.MovementType prevMT = ConcreteComponent.globalConcreteComponentMovementType;
		ConcreteComponent.globalConcreteComponentMovementType = Component.MovementType.FREE;
		blackBox.moveTo(getWidth()/2 - blackBox.getRectangle().width/2,
				        getHeight()/2 - blackBox.getRectangle().height/2);
		ConcreteComponent.globalConcreteComponentMovementType = prevMT;	
	}
	
	public void addComponentToCanvas(final Component component)
	{
		synchronized (componentsToAdd)
		{		
			componentsToAdd.add(component);			
		}
	}
	
	public void removeComponentFromCanvas(final Component component)
	{
		synchronized (componentsToRemove)
		{
			componentsToRemove.add(component);
		}
	}
	
	public Iterator<Component> getComponentsIterator()
	{
		synchronized (components)
		{			
			List<Component> aliveComponents = new ArrayList<Component>();
			for (Component component: components)
			{
				if (componentsToRemove.contains(component))
					continue;
				aliveComponents.add(component);
			}
			return aliveComponents.iterator();
		}
	}
	
	public void finalizeWirePosition(final int x, final int y)
	{					
		finalizeWireCreation(x, y);								
	}
	
	public void startCreatingNub(final int x, final int y)
	{
		isCreatingNub = true;
		dragNubX = x;
		dragNubY = y;
	}
	
	public void setNubCreationPosition(final int x, final int y)
	{		
		dragNubX = x;
		dragNubY = y;	
	}
	
	public void finalizeNubPosition(final int x, final int y)
	{
		finalizeNubCreation();
	}
	
	public boolean hasMultiSelection()
	{
		return componentSelector.getNumberOfSelectedComponents() > 1;
	}
	
	public File getLastSaveLocation()
	{
		return lastSaveLocation;
	}
	
	public void openProjectFromFile(final File file)	
	{
		init(file);
		ProjectPersistenceUtilities.openProject(file, this);
	}
	
	public void saveProject()
	{
		ProjectPersistenceUtilities.saveProject(lastSaveLocation, components, false);		
	}
	
	public void saveProjectToFile(final File file)
	{
		ProjectPersistenceUtilities.saveProject(file, components, true);				
		lastSaveLocation = file;
	}
	
	public void exportToSVG(final File svgOutput)
	{
		int selColorOption = JOptionPane.YES_OPTION; 
		if (componentSelector.getNumberOfSelectedComponents() > 0 || highlightedComponent != null)
		{
			selColorOption = JOptionPane.showConfirmDialog (null, "Keep selected/highlighted objects colored?", "Export Option", JOptionPane.YES_NO_OPTION);
		}		
		
		if (selColorOption == JOptionPane.NO_OPTION)
		{
			highlightedComponent = null;
			componentSelector = new ComponentSelector(0, 0);
		}
		
		boolean useCSS = true; // we want to use CSS style attributes
	    
		try (Writer outputWriter = new OutputStreamWriter(new FileOutputStream(svgOutput), "UTF-8"))
		{			
			SVGGraphics2D svgGraphics = new SVGGraphics2D(document);
		    GraphicsGenerator svgGen = new GraphicsGenerator(svgGraphics);
		    clear(svgGen);
		    display(svgGen);
		    svgGraphics.stream(outputWriter, useCSS);
		    
		    highlightedComponent = null;
			componentSelector = new ComponentSelector(0, 0);
			
		    JOptionPane.showMessageDialog(null, "Exported project to SVG successfully");		
		}
		catch (IOException e) 
		{
			JOptionPane.showMessageDialog(null, "An error has occurred while exporting project to SVG", "IO Error", JOptionPane.ERROR_MESSAGE);			
		}
	}

	public void undo()
	{
		if (executedActionHistory.size() > 0)
		{
			Action lastAction = executedActionHistory.remove(executedActionHistory.size() - 1);			
			lastAction.undo();
			undoneActionHistory.add(lastAction);
		}
	}
	
	public void redo()
	{
		if (undoneActionHistory.size() > 0)
		{
			Action lastUndoneAction = undoneActionHistory.remove(undoneActionHistory.size() - 1);
			lastUndoneAction.execute();
			executedActionHistory.add(lastUndoneAction);
		}
	}
	
	public void selectAll()
	{
		synchronized (componentSelector)
		{			
			componentSelector = new ComponentSelector(mouse.getX(), mouse.getY());
			componentSelector.disable();
			
			for (Component component: components)
			{
				componentSelector.addComponentToSelectionExternally(component);
			}
		}
	}
	
	public void copy()
	{
		synchronized (componentSelector)
		{				
			Iterator<Component> selCompsIter = componentSelector.getSelectedComponentsIterator();
			componentSelector.enable();
			while (selCompsIter.hasNext())
			{													
				componentSelector.addComponentToSelectionExternally(selCompsIter.next());							
			}
			
			componentSelector.disable();
			
			Set<Component> selComponents = new HashSet<Component>();
			selCompsIter = componentSelector.getSelectedComponentsIterator();
			while (selCompsIter.hasNext())
			{
				selComponents.add(selCompsIter.next());
			}
						
			ProjectPersistenceUtilities.saveProjectNonPersistent(selComponents);
		}
	}
	
	public void paste()
	{				
		componentSelector = new ComponentSelector(mouse.getX(), mouse.getY());
		componentSelector.enable();		
		Action pasteAction = new PasteAction(this, componentSelector);
		pasteAction.execute();
		executedActionHistory.add(pasteAction);
		componentSelector.disable();
	}
	
	public void delete()
	{
		synchronized (componentSelector)
		{			
			if (componentSelector.getNumberOfSelectedComponents() > 1)			
			{				
				Iterator<Component> selCompsIter = componentSelector.getSelectedComponentsIterator();
				List<Component> selComponents = new ArrayList<Component>();
				
				while (selCompsIter.hasNext())
				{
					selComponents.add(selCompsIter.next());
				}
				
				componentSelector.enable();
				
				for (Component selComponent: selComponents)
				{
					componentSelector.addComponentToSelectionExternally(selComponent);												
				}				
				componentSelector.disable();				
			}
			
			Iterator<Component> selCompsIter = componentSelector.getSelectedComponentsIterator();
			while (selCompsIter.hasNext())
			{
				selCompsIter.next().delete();
			}
			
			componentSelector = new ComponentSelector(mouse.getX(), mouse.getY());
		}
	}
	
	private void inputUpdates()
	{						
		highlightedComponent = getHoveredComponent(isCreatingNub ? dragNubX : mouse.getX(), isCreatingNub ? dragNubY : mouse.getY());		
		
		// On Left Mouse Button Tap
		if (mouse.isButtonTapped(Mouse.LEFT_BUTTON))
		{	
			lineSegmentPath.clear();	
			startPositions.clear();
			targetPositions.clear();
			
			if (componentSelector.getNumberOfSelectedComponents() > 1 && highlightedComponent != null && componentSelector.isComponentInSelection(highlightedComponent))
			{				
				selectionDx = componentSelector.getFirstComponent().getRectangle().x - mouse.getX();
				selectionDy = componentSelector.getFirstComponent().getRectangle().y - mouse.getY();
				
				Iterator<Component> selCompIter = componentSelector.getSelectedComponentsIterator();
				
				while (selCompIter.hasNext())
				{
					Component comp = selCompIter.next();
					startPositions.add(new int[]{ comp.getRectangle().x, comp.getRectangle().y });
				}
			}
			else
			{	
				synchronized (componentSelector)
				{					
					componentSelector = new ComponentSelector(mouse.getX(), mouse.getY());
					
					if (highlightedComponent != null)
					{
						componentSelector.disable();
						componentSelector.addComponentToSelectionExternally(highlightedComponent);
						
						selectionDx = componentSelector.getFirstComponent().getRectangle().x - mouse.getX();
						selectionDy = componentSelector.getFirstComponent().getRectangle().y - mouse.getY();					
						if (componentSelector.getFirstComponent().getComponentType() == ComponentType.LINE_SEGMENT)
						{
							constructLinePath(componentSelector.getFirstComponent());
						}
						
						startPositions.add(new int[]{ highlightedComponent.getRectangle().x, highlightedComponent.getRectangle().y });
					}
					else
					{
						componentSelector.enable();
					}		
				}
			}
		}	
		else if (mouse.isButtonDown(Mouse.LEFT_BUTTON))
		{	
			if (componentSelector.isEnabled())
			{
				synchronized (componentSelector)
				{
					componentSelector.update(mouse.getX(), mouse.getY(), components);					
				}
			}
			else
			{				
				if (componentSelector.getNumberOfSelectedComponents() == 1)
				{
					alignedComponents = componentSelector.getFirstComponent().moveTo(mouse.getX() + selectionDx, mouse.getY() + selectionDy);
					movingComponents  = true;
				}
				else if (componentSelector.getNumberOfSelectedComponents() > 1)
				{															
					Component firstSelComponent = componentSelector.getFirstComponent();
					int prevX = firstSelComponent.getRectangle().x;
					int prevY = firstSelComponent.getRectangle().y;
											
					// Temporarily move first selected component to new position
					firstSelComponent.setPosition(mouse.getX() + selectionDx, mouse.getY() + selectionDy);
					
					// Calculate global deltas
					int deltaX = firstSelComponent.getRectangle().x - prevX;
					int deltaY = firstSelComponent.getRectangle().y - prevY;
					
					// Move component to old position
					firstSelComponent.setPosition(prevX, prevY);																
					
					synchronized (componentSelector)
					{
						Iterator<Component> selIter = componentSelector.getSelectedComponentsIterator();
						while (selIter.hasNext())
						{
							Component component = selIter.next();
							if (component.getComponentType() == ComponentType.LINE_SEGMENT)
								continue;
													
							component.setPosition(component.getRectangle().x + deltaX, component.getRectangle().y + deltaY);						
						}
					}
										
					movingComponents = true;
				}
			}
		}
		else if (mouse.isButtonJustReleased(Mouse.LEFT_BUTTON) || !mouse.isButtonDown(Mouse.LEFT_BUTTON))
		{											
			lineSegmentPath.clear();
			selectionDx = selectionDy = 0;
			
			if (movingComponents)
			{								
				finalizeMovementAndCreateActions();
			}
			
			alignedComponents = null;
			movingComponents  = false;
			
			synchronized (componentSelector)
			{
				componentSelector.disable();				
			}
		}							
		
		if (isCreatingNub)
		{							
			nubPlacementIncidentComponents.clear();			
			for (Component component: components)			
			{					
				if (component.getComponentType() == ComponentType.LINE_SEGMENT)
				{					
					double lineLength = Math.hypot(component.getChildren().get(1).getRectangle().getCenterX() - component.getChildren().get(0).getRectangle().getCenterX(),
		                                           component.getChildren().get(1).getRectangle().getCenterY() - component.getChildren().get(0).getRectangle().getCenterY());
					
					double startToMouseDistance = Math.hypot(component.getChildren().get(0).getRectangle().getCenterX() - dragNubX, 
							                                 component.getChildren().get(0).getRectangle().getCenterY() - dragNubY);
					
					double mouseToEndDistance = Math.hypot(dragNubX - component.getChildren().get(1).getRectangle().getCenterX(),
							                               dragNubY - component.getChildren().get(1).getRectangle().getCenterY());

					if (lineLength - startToMouseDistance - mouseToEndDistance >= -1.0f)				
						nubPlacementIncidentComponents.add(component);
				}					
			}
						
			int nIncidentLineSegments = nubPlacementIncidentComponents.size();
						
			if (highlightedComponent != null && highlightedComponent.getComponentType() == ComponentType.HINGE)
			{								
				isNubInContact = true;	
				incidentNubX = (int)highlightedComponent.getRectangle().getCenterX() - HingeComponent.NUB_DIAMETER/2;
				incidentNubY = (int)highlightedComponent.getRectangle().getCenterY() - HingeComponent.NUB_DIAMETER/2;
			}
			else if (nIncidentLineSegments > 0)
			{				
				isNubInContact = true;				
				
				if (nIncidentLineSegments == 1)
				{
					incidentNubX = dragNubX - HingeComponent.HINGE_DIAMETER/2;
					incidentNubY = dragNubY - HingeComponent.HINGE_DIAMETER/2;
					
					if (((LineSegmentComponent)nubPlacementIncidentComponents.get(0)).getOrientation() == Orientation.HORIZONTAL)
						incidentNubY = nubPlacementIncidentComponents.get(0).getRectangle().y - HingeComponent.NUB_DIAMETER/2 + 1;
					else
						incidentNubX = nubPlacementIncidentComponents.get(0).getRectangle().x - HingeComponent.NUB_DIAMETER/2 + 1;
				}
				else
				{
					for (Component incidentLineSegment: nubPlacementIncidentComponents)
					{
						if (((LineSegmentComponent)incidentLineSegment).getOrientation() == Orientation.HORIZONTAL)
						{
							incidentNubY = incidentLineSegment.getRectangle().y - HingeComponent.NUB_DIAMETER/2 + 1; 
						}
						else
						{
							incidentNubX = incidentLineSegment.getRectangle().x - HingeComponent.NUB_DIAMETER/2 + 1;
						}
					}
				}
			}
			else
			{
				isNubInContact = false;
			}
		}
				
		mouse.updateOnFrameEnd();
		keyboard.updateOnFrameEnd();
	}
	
	private void display(final GraphicsGenerator gfx)
	{
		clear(gfx);
		
		synchronized (componentSelector)
		{
			synchronized (components)
			{				
				render(gfx);					
			}
		}
	}
	
	private void clear(final GraphicsGenerator gfx)
	{
		gfx.setColor(Colors.CANVAS_BKG_COLOR);
		gfx.fillRect(0, 0, getWidth(), getHeight());
	}
	
	private void render(final GraphicsGenerator gfx)
	{							
		List<Component> delayedRenderedComponents = new ArrayList<Component>();
		
		for (Component component: components)
		{
			if (component.getComponentType() == ComponentType.LINE_SEGMENT)
			{
				boolean isInSelection = componentSelector.isComponentInSelection(component);
				component.render(gfx, 
						         highlightedComponent == component && !isCreatingNub, 
						         isInSelection,
						         isInSelection && componentSelector.getNumberOfSelectedComponents() > 1);
			}
			else
			{
				delayedRenderedComponents.add(component);
			}
		}

			
		Iterator<Component> iter = componentSelector.getSelectedComponentsIterator();
		while (iter.hasNext())
		{
			Component component = iter.next();
			if (component.getComponentType() == ComponentType.LINE_SEGMENT)
			{
				for (Component pathComponent: lineSegmentPath)
				{
					if (component != pathComponent)
					{
						((LineSegmentComponent)pathComponent).renderAsPathEdge(gfx);				
					}
				}				
			}
		}		
		
		List<Component> lastPassComponents = new ArrayList<Component>();
		
		for (Component component: delayedRenderedComponents)
		{
			if (component.getComponentType() == ComponentType.HINGE)
			{
				boolean isInSelection = componentSelector.isComponentInSelection(component);
				component.render(gfx, 
				         highlightedComponent == component && !isCreatingNub, 
				         isInSelection,
				         isInSelection && componentSelector.getNumberOfSelectedComponents() > 1);			
			}
			else
				lastPassComponents.add(component);
		}
		
		for (Component component: lastPassComponents)
		{
			boolean isInSelection = componentSelector.isComponentInSelection(component);
			component.render(gfx, 
			         highlightedComponent == component && !isCreatingNub, 
			         isInSelection,
			         isInSelection && componentSelector.getNumberOfSelectedComponents() > 1);		
		}
		
		if (isCreatingNub)
		{
			gfx.setColor(isNubInContact ? Colors.DEFAULT_COLOR : Colors.NUB_TRANSP_COLOR);
			gfx.fillOval(dragNubX - HingeComponent.NUB_DIAMETER/2, dragNubY - HingeComponent.NUB_DIAMETER/2, HingeComponent.NUB_DIAMETER, HingeComponent.NUB_DIAMETER);
		}
				
		componentSelector.render(gfx);
		
		if (prevAlignedComponents != null)
		{					
			if (prevAlignedComponents.hasHorAlignedComponents())
			{
				if (!prevAlignedComponents.getHorAlignedComponents().contains(componentSelector.getFirstComponent()))					
				{
					for (Component component: prevAlignedComponents.getHorAlignedComponents())
					{
						component.renderAligned(gfx);
					}									
				}
				
				gfx.setStroke(new BasicStroke(1.0f));
				gfx.setColor(Colors.ALIGNMENT_COLOR);
				gfx.drawLine((int)prevAlignedComponents.getHorAlignedComponents().get(0).getRectangle().getCenterX(), 0, (int)prevAlignedComponents.getHorAlignedComponents().get(0).getRectangle().getCenterX(), getHeight());
			}
			
			if (prevAlignedComponents.hasVerAlignedComponents())
			{
				if (!prevAlignedComponents.getVerAlignedComponents().contains(componentSelector.getFirstComponent()))
				{
					for (Component component: prevAlignedComponents.getVerAlignedComponents())
					{
						component.renderAligned(gfx);
					}					
				}
				
				gfx.setStroke(new BasicStroke(1.0f));
				gfx.setColor(Colors.ALIGNMENT_COLOR);
				gfx.drawLine(0, (int)prevAlignedComponents.getVerAlignedComponents().get(0).getRectangle().getCenterY(), getWidth(), (int)prevAlignedComponents.getVerAlignedComponents().get(0).getRectangle().getCenterY());			
			}		
		}
		
		prevAlignedComponents = alignedComponents;
	}

	
	private void checkAndAddNewComponents()
	{
		synchronized (componentsToAdd)
		{		
			synchronized (components)
			{				
				for (Component component: componentsToAdd)
				{					
					components.add(component);
				}				
				componentsToAdd.clear();
			}
		}
	}
	
	private void checkAndRemoveMarkedComponents()
	{
		synchronized (componentsToRemove)
		{
			synchronized (components)
			{				
				for (Component component: componentsToRemove)
				{
					components.remove(component);
				}
				
				componentsToRemove.clear();
			}
		}
	}
	
	private Component getHoveredComponent(final int mouseX, final int mouseY)
	{		
		Component highlightedComponent = null;
		for (Iterator<Component> componentIter = components.iterator(); componentIter.hasNext();)
		{
			Component component = componentIter.next();
			if (component.mouseIntersection(mouseX, mouseY))
			{	
				// Prioritize Gate > Hinge > Line Segment
				if (highlightedComponent != null && 
					highlightedComponent.getComponentType() == ComponentType.GATE &&
					component.getComponentType() != ComponentType.GATE)
				{	
					continue;				
				}				 
				else if (highlightedComponent != null &&
					highlightedComponent.getComponentType() == ComponentType.HINGE &&
					component.getComponentType() == ComponentType.LINE_SEGMENT)
				{
					continue;
				}
				
				highlightedComponent = component;
			}
		}
		
		return highlightedComponent;
	}
	
	private void finalizeWireCreation(final int x, final int y)
	{								
		
		Component startPoint = new HingeComponent(this, x - NEW_WIRE_LENGTH/2 - HingeComponent.HINGE_DIAMETER/2, y - HingeComponent.HINGE_DIAMETER/2, true);
		Component endPoint   = new HingeComponent(this, x + NEW_WIRE_LENGTH/2 - HingeComponent.HINGE_DIAMETER/2, y - HingeComponent.HINGE_DIAMETER/2, true);		
		
		addComponentToCanvas(new LineSegmentComponent(this, startPoint, endPoint, true));		
		addComponentToCanvas(startPoint);
		addComponentToCanvas(endPoint);		
	}
	
	private void finalizeNubCreation()
	{		
		isCreatingNub = false;
		
		if (isNubInContact)
		{			
			HingeComponent nubHinge = new HingeComponent(this, incidentNubX, incidentNubY, true);
			nubHinge.setHasNub(true);
			
			Component incidentHinge = null;			
			List<Component> excludedLineSegments = new ArrayList<Component>();			
			Iterator<Component> compsIter = getComponentsIterator();
			while (compsIter.hasNext())
			{
				Component comp = compsIter.next();
				
				if (comp.getComponentType() == ComponentType.HINGE)
				{					
					if (comp.getRectangle().intersects(nubHinge.getRectangle()))
					{
						incidentHinge = comp;
						nubHinge.setPosition(incidentHinge.getRectangle().x, incidentHinge.getRectangle().y);
						nubHinge.setMovable(incidentHinge.isMovable());						
					}
				}
			}
			
			compsIter = getComponentsIterator();
			
			while(compsIter.hasNext())
			{
				Component comp = compsIter.next();
				
				if (comp.getComponentType() == ComponentType.LINE_SEGMENT)
				{
					LineSegmentComponent ls = (LineSegmentComponent)comp;
					
					if (ls.getChildren().get(0) == incidentHinge)
					{
						ls.setStartPoint(nubHinge);
						excludedLineSegments.add(ls);
					}
					else if (ls.getChildren().get(1) == incidentHinge)
					{
						ls.setEndPoint(nubHinge);
						excludedLineSegments.add(ls);
					}
				}
			}
			
			removeComponentFromCanvas(incidentHinge);
			addComponentToCanvas(nubHinge);
			
			if (nubPlacementIncidentComponents.size() > 0)
			{							
				for (Component component: nubPlacementIncidentComponents)
				{
					if (excludedLineSegments.contains(component))
						continue;				
					
					removeComponentFromCanvas(component);											
										
					LineSegmentComponent newSegment1 = new LineSegmentComponent(this, component.getChildren().get(0), nubHinge, true);
					LineSegmentComponent newSegment2 = new LineSegmentComponent(this, nubHinge, component.getChildren().get(1), true);
					
					addComponentToCanvas(newSegment1);
					addComponentToCanvas(newSegment2);
				}						
			}
		}		
	}

	private void constructLinePath(final Component component)
	{
		lineSegmentPath.add(component);
		for (int i = 0; i < lineSegmentPath.size(); ++i)
		{
			Component selectedSegment = lineSegmentPath.get(i);
			for (Component comp: components)
			{
				if (comp.getComponentType() != ComponentType.LINE_SEGMENT)
					continue;
				
				if (lineSegmentPath.contains(comp))
					continue;
				
				Component startPoint = comp.getChildren().get(0);
				Component endPoint = comp.getChildren().get(1);
				
				if (startPoint == selectedSegment.getChildren().get(0) ||
					endPoint   == selectedSegment.getChildren().get(1)||
					startPoint == selectedSegment.getChildren().get(1)||
				    endPoint   == selectedSegment.getChildren().get(0))
						lineSegmentPath.add(comp);					
			}											
		}
	}
	
	private void finalizeMovementAndCreateActions()
	{			
		if (componentSelector.getNumberOfSelectedComponents() == 1)
		{		
			componentSelector.getFirstComponent().finalizeMovement(alignedComponents);						
			alignedComponents = null;
			
			Action currentAction = new MoveAction(componentSelector.getFirstComponent(),
					startPositions.get(0),
					new int[]{ componentSelector.getFirstComponent().getRectangle().x,
							   componentSelector.getFirstComponent().getRectangle().y });
			currentAction.execute();
			
			// Differentiate between a simple click and an actual move
			if (startPositions.get(0)[0] != componentSelector.getFirstComponent().getRectangle().x ||
				startPositions.get(0)[1] != componentSelector.getFirstComponent().getRectangle().y)
			{
				executedActionHistory.add(currentAction);								
			}		
		}
		else if (componentSelector.getNumberOfSelectedComponents() > 1)
		{				
			Iterator<Component> selCompIter = componentSelector.getSelectedComponentsIterator();
			while (selCompIter.hasNext())
			{
				Component selComponent = selCompIter.next();
				targetPositions.add(new int[]{ selComponent.getRectangle().x, selComponent.getRectangle().y });
			}
			
			Action currentAction = new MultiMoveAction(componentSelector.getSelectedComponentsIterator(),
					startPositions.iterator(), 
					targetPositions.iterator());
			
			currentAction.execute();
						
			// Differentiate between a simple click and an actual move
			if (startPositions.get(0)[0] != targetPositions.get(0)[0] ||
				startPositions.get(0)[1] != targetPositions.get(0)[1])
			{
				executedActionHistory.add(currentAction);								
			}						
		}	
	}
	
	private void init(final File saveFile)
	{
		selectionDx = selectionDy = dragNubX = dragNubY = 0;		
		highlightedComponent = null;
		
		components            = new HashSet<Component>();
		lineSegmentPath       = new ArrayList<Component>();
		nubPlacementIncidentComponents  = new ArrayList<Component>();		
		componentsToAdd       = new ArrayList<Component>();
		componentsToRemove    = new ArrayList<Component>();
		executedActionHistory = new ArrayList<Action>();
		undoneActionHistory   = new ArrayList<Action>();
		startPositions        = new ArrayList<int[]>();
		targetPositions       = new ArrayList<int[]>();				
		componentSelector     = new ComponentSelector(0, 0);
		mouse                 = new Mouse();
		keyboard              = new Keyboard();		
		
		isCreatingNub = isNubInContact = movingComponents = false;		
		
		alignedComponents = prevAlignedComponents = null;
		domImpl = GenericDOMImplementation.getDOMImplementation();
		document = domImpl.createDocument("https://www.w3.org/2000/svg", "svg", null);
		
		lastSaveLocation = saveFile;		
	}
}
