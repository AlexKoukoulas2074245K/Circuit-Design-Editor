package uk.ac.gla.student._2074245k.cde.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
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
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import uk.ac.gla.student._2074245k.cde.actions.Action;
import uk.ac.gla.student._2074245k.cde.actions.ColorComponentsAction;
import uk.ac.gla.student._2074245k.cde.actions.DeleteAction;
import uk.ac.gla.student._2074245k.cde.actions.MoveAction;
import uk.ac.gla.student._2074245k.cde.actions.MultiMoveAction;
import uk.ac.gla.student._2074245k.cde.actions.PasteAction;
import uk.ac.gla.student._2074245k.cde.components.AlignedComponentsList;
import uk.ac.gla.student._2074245k.cde.components.BlackBoxComponent;
import uk.ac.gla.student._2074245k.cde.components.Component;
import uk.ac.gla.student._2074245k.cde.components.Component.ComponentType;
import uk.ac.gla.student._2074245k.cde.components.Component.Orientation;
import uk.ac.gla.student._2074245k.cde.components.ComponentRectangleComparator;
import uk.ac.gla.student._2074245k.cde.components.ConcreteComponent;
import uk.ac.gla.student._2074245k.cde.components.HingeComponent;
import uk.ac.gla.student._2074245k.cde.components.LineSegmentComponent;
import uk.ac.gla.student._2074245k.cde.components.TextBoxComponent;
import uk.ac.gla.student._2074245k.cde.components.WhiteBoxComponent;
import uk.ac.gla.student._2074245k.cde.observers.ConcreteComponentCreationObserver;
import uk.ac.gla.student._2074245k.cde.util.FrameCounter;
import uk.ac.gla.student._2074245k.cde.util.GraphicsGenerator;
import uk.ac.gla.student._2074245k.cde.util.LoadingResult;
import uk.ac.gla.student._2074245k.cde.util.Mouse;
import uk.ac.gla.student._2074245k.cde.util.ProjectPersistenceUtilities;


public final class MainCanvas extends JPanel implements Runnable,											                                                          
                                                        MouseListener,
                                                        MouseMotionListener,
                                                        MouseWheelListener,
                                                        KeyListener,
                                                        ComponentListener,
                                                        ConcreteComponentCreationObserver
{
	public enum MovementType
	{
		FREE, AXIS_RESTRICTED
	}
			
	public static Mouse mouse;	
	
	private static final long serialVersionUID   = 1L;
	private static final int NEW_WIRE_LENGTH     = 200;
	
	private JFrame window;
	private JScrollPane scrollPane;
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
	
	private int selectionDx, selectionDy, dragNubX, dragNubY, dragTextboxX, dragTextboxY, incidentNubX, incidentNubY, canvasOrigWidth, canvasOrigHeight;	
	private AlignedComponentsList alignedComponents, prevAlignedComponents;
	private boolean isCreatingNub, isCreatingTextbox, isNubInContact, movingComponents;
	
	private Action executedAction;
	private Action undoneAction;
	
	private List<int[]> startPositions;
	private List<int[]> targetPositions;		
	
	private File lastSaveLocation = null;
	private boolean hasTakenActionSinceLastSave = false;
	
	public MainCanvas(final JFrame window)
	{		
		super();
		this.window = window;		
		init(null);		
	}
	
	public void init(final File saveFile)
	{
		selectionDx = selectionDy = dragNubX = dragNubY = dragTextboxX = dragTextboxY = 0;		
		highlightedComponent = null;
		
		components            = new HashSet<Component>();
		lineSegmentPath       = new ArrayList<Component>();
		nubPlacementIncidentComponents  = new ArrayList<Component>();		
		componentsToAdd       = new ArrayList<Component>();
		componentsToRemove    = new ArrayList<Component>();
		executedAction        = null;
		undoneAction          = null;
		startPositions        = new ArrayList<int[]>();
		targetPositions       = new ArrayList<int[]>();				
		componentSelector     = new ComponentSelector(0, 0);
		mouse                 = new Mouse();	
		canvasOrigWidth       = getWidth();
		canvasOrigHeight      = getHeight();
		isCreatingNub = isNubInContact = movingComponents = isCreatingTextbox = false;		
		
		alignedComponents = prevAlignedComponents = null;
		domImpl = GenericDOMImplementation.getDOMImplementation();
		document = domImpl.createDocument("https://www.w3.org/2000/svg", "svg", null);
		
		lastSaveLocation = saveFile;
		hasTakenActionSinceLastSave = true;
	}
	
	@Override
	public void addNotify()
	{
		super.addNotify();
		
		if (mainCanvasThread == null)
		{
			mainCanvasThread = new Thread(this);			
			addMouseListener(this);
			addMouseMotionListener(this);			
			addMouseWheelListener(this);
			addComponentListener(this);
			addKeyListener(this);
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
			frameCounter.update(componentSelector, 					            
					            window,
					            lastSaveLocation,
					            hasTakenActionSinceLastSave);
		}				 
	}

	@Override
	public void paintComponent(final Graphics g)
	{		
		Graphics2D gfxCopy = (Graphics2D)g.create();
		GraphicsGenerator gfxGen = new GraphicsGenerator(gfxCopy);
		display(gfxGen);		
		gfxCopy.dispose();
	}
	

	@Override
	public void mouseWheelMoved(MouseWheelEvent evt) 
	{
		int prevScale = mouse.getScalePercent();
		mouse.updateOnMouseWheelMoved(evt);		
		
		if (mouse.getScalePercent() != prevScale)
		{			
			setPreferredSize(new Dimension((int)(canvasOrigWidth * (mouse.getScalePercent()/100.0D)),
					                       (int)(canvasOrigHeight * (mouse.getScalePercent()/100.0D))));
		}
		
		revalidate();
		repaint();
	}
	
	@Override
	public void keyPressed(KeyEvent evt) 
	{
		synchronized (componentSelector)
		{
			Iterator<Component> selCompsIter = componentSelector.getSelectedComponentsIterator();
			while (selCompsIter.hasNext())
			{
				Component comp = selCompsIter.next();
				if (comp.getComponentType() == ComponentType.TEXT_BOX)
				{
					TextBoxComponent tb = (TextBoxComponent)comp;
					if (tb.isInEditMode())
					{
						tb.updateText(evt);
					}
				}				
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent __) {}
	
	@Override
	public void keyTyped(KeyEvent __) {}
	
	@Override
	public void mouseDragged(MouseEvent eventArgs) { mouse.updateOnMouseDrag(eventArgs); }
	
	@Override
	public void mouseMoved(MouseEvent eventArgs) { mouse.updateOnMouseMove(eventArgs); }

	@Override
	public void mousePressed(MouseEvent eventArgs) { mouse.updateOnMousePressed(eventArgs); }
	
	@Override
	public void mouseReleased(MouseEvent eventArgs) 
	{		
		mouse.updateOnMouseReleased(eventArgs);
		
		synchronized (lineSegmentPath)
		{
			lineSegmentPath.clear();			
		}
		
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
	
	@Override
	public void mouseClicked(MouseEvent eventArgs) { mouse.updateOnMouseReleased(eventArgs); }

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
	public void callbackOnConcreteComponentCreationEvent(final Component comp, final List<PortView> portViews) 
	{
		ConcreteComponent concComp = (ConcreteComponent)comp;
		Rectangle rect = concComp.getRectangle();
		
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
					
					concComp.addInternalHorHinge(internalHinge);
					((HingeComponent)internalHinge).addInternalHingeInfo(portView.portLocation, portView.portName);
					((HingeComponent)externalHinge).addExternalHingeInfo(portView.portLocation, portView.portResultDir);
					
				} break;
				
				case RIGHT:
				{
					externalHinge = new HingeComponent(this, rect.x + rect.width + BlackBoxComponent.PORT_LENGTH - HingeComponent.HINGE_DIAMETER/2, portView.actualPosition - HingeComponent.HINGE_DIAMETER/2, false);
					internalHinge = new HingeComponent(this, rect.x + rect.width - HingeComponent.HINGE_DIAMETER/2, portView.actualPosition - HingeComponent.HINGE_DIAMETER/2, false);
					port = new LineSegmentComponent(this, externalHinge, internalHinge, false);
					
					concComp.addInternalHorHinge(internalHinge);						
					((HingeComponent)internalHinge).addInternalHingeInfo(portView.portLocation, portView.portName);
					((HingeComponent)externalHinge).addExternalHingeInfo(portView.portLocation, portView.portResultDir);
					
				} break;
				
				case TOP:
				{
					externalHinge = new HingeComponent(this, portView.actualPosition - HingeComponent.HINGE_DIAMETER/2, rect.y - BlackBoxComponent.PORT_LENGTH - HingeComponent.HINGE_DIAMETER/2, false);
					internalHinge = new HingeComponent(this, portView.actualPosition - HingeComponent.HINGE_DIAMETER/2, rect.y - HingeComponent.HINGE_DIAMETER/2, false);
					port = new LineSegmentComponent(this, externalHinge, internalHinge, false);
					
					concComp.addInternalVerHinge(internalHinge);						
					((HingeComponent)internalHinge).addInternalHingeInfo(portView.portLocation, portView.portName);
					((HingeComponent)externalHinge).addExternalHingeInfo(portView.portLocation, portView.portResultDir);
					
				} break;
				
				case BOTTOM:
				{
					externalHinge = new HingeComponent(this, portView.actualPosition - HingeComponent.HINGE_DIAMETER/2, rect.y + rect.height + BlackBoxComponent.PORT_LENGTH - HingeComponent.HINGE_DIAMETER/2, false);
					internalHinge = new HingeComponent(this, portView.actualPosition - HingeComponent.HINGE_DIAMETER/2, rect.y + rect.height - HingeComponent.HINGE_DIAMETER/2, false);
					port = new LineSegmentComponent(this, externalHinge, internalHinge, false);
					
					concComp.addInternalVerHinge(internalHinge);								
					((HingeComponent)internalHinge).addInternalHingeInfo(portView.portLocation, portView.portName);
					((HingeComponent)externalHinge).addExternalHingeInfo(portView.portLocation, portView.portResultDir);
				}  break;
			}
						
			((HingeComponent)internalHinge).setIsInverted(portView.isInverted);
			
			concComp.addPort(port);
			
			addComponentToCanvas(externalHinge);
			addComponentToCanvas(internalHinge);
			addComponentToCanvas(port);	
		}
		
		addComponentToCanvas(concComp);	
		
		
		int targetX = 0;
		int targetY = 0;
		
		if (getWidth() < scrollPane.getViewportBorderBounds().width)
		{
			targetX = getWidth()/2;
		}
		else
		{
			targetX = scrollPane.getViewport().getViewPosition().x + scrollPane.getViewportBorderBounds().width/2; 
		}
		
		if (getHeight() < scrollPane.getViewportBorderBounds().height)
		{
			targetY = getHeight()/2;
		}
		else
		{
			targetY = scrollPane.getViewport().getViewPosition().y + scrollPane.getViewportBorderBounds().height/2;
		}
		
		targetX -= concComp.getRectangle().width/2;
		targetY -= concComp.getRectangle().height/2;
		
		Component.MovementType prevMT = ConcreteComponent.globalConcreteComponentMovementType;
		ConcreteComponent.globalConcreteComponentMovementType = Component.MovementType.FREE;
		concComp.moveTo(targetX, targetY);
		
		// Position selected components inside white box
		if (concComp.getComponentType() == ComponentType.WHITE_BOX)
		{
			// Find component with min position
			Component componentWithMinX = componentSelector.getSelectedComponentsIterator().next();	
			Component componentWithMinY = componentSelector.getSelectedComponentsIterator().next();
			
			Iterator<Component> selCompsIter = componentSelector.getSelectedComponentsIterator();
			while (selCompsIter.hasNext())
			{
				Component selComp = selCompsIter.next();
				if (selComp.getRectangle().x < componentWithMinX.getRectangle().x)
				{
					componentWithMinX = selComp;
				}
				if (selComp.getRectangle().y < componentWithMinY.getRectangle().y)
				{					
					componentWithMinY = selComp;
				}
			}
			
			int dx = componentWithMinX.getRectangle().x - (concComp.getRectangle().x + 32);
			int dy = componentWithMinY.getRectangle().y - (concComp.getRectangle().y + 32);
			
			selCompsIter = componentSelector.getSelectedComponentsIterator();
			while (selCompsIter.hasNext())
			{
				Component selComp = selCompsIter.next();
				if (selComp.getComponentType() != ComponentType.LINE_SEGMENT)
				{
					selComp.setPosition(selComp.getRectangle().x - dx, selComp.getRectangle().y - dy);					
				}
			}
		}
		
		ConcreteComponent.globalConcreteComponentMovementType = prevMT;	
	}
	
	public void setOriginalSize(final int origWidth, final int origHeight)
	{
		canvasOrigWidth  = origWidth;
		canvasOrigHeight = origHeight;
	}
	
	public void setScrollPane(final JScrollPane scrollPane)
	{
		this.scrollPane = scrollPane;
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
	
	public boolean hasComponentExpired(final Component component)
	{
		Iterator<Component> aliveComponents = getComponentsIterator();
		while (aliveComponents.hasNext())
		{
			if (aliveComponents.next() == component)
			{
				return false;
			}
		}
		
		return true;
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
		grabFocus();
	}
	
	public void startCreatingTextbox(final int x, final int y)
	{
		isCreatingTextbox = true;
		dragTextboxX = x;
		dragTextboxY = y;
	}
	
	public void setTextboxCreationPosition(final int x, final int y)
	{
		dragTextboxX = x;
		dragTextboxY = y;
	}
	
	public void finalizeTextboxPosition(final int x, final int y)
	{
		finalizeTextboxCreation();	
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
	
	public int getNumberOfSelectedComponents()
	{
		return componentSelector.getNumberOfSelectedComponents();
	}
	
	public Iterator<Component> getSelectedComponentsIterator()
	{
		return componentSelector.getSelectedComponentsIterator();
	}
	
	public boolean hasMultiSelection()
	{
		return componentSelector.getNumberOfSelectedComponents() > 1;
	}
	
	public File getLastSaveLocation()
	{
		return lastSaveLocation;
	}
	
	public boolean hasTakenActionSinceLastSave()
	{
		return hasTakenActionSinceLastSave;
	}
	
	public void openProjectFromFile(final File file)	
	{
		init(file);
		LoadingResult result = ProjectPersistenceUtilities.openProject(file, false, this);
		setPreferredSize(result.canvasDimension);
		canvasOrigWidth = result.canvasDimension.width;
		canvasOrigHeight = result.canvasDimension.height;
		revalidate();
		repaint();
		
		if (result.loadedComponents.size() == 0) { JOptionPane.showMessageDialog(null, "Could not load project"); }
		else { JOptionPane.showMessageDialog(null, "Loaded project successfully"); }
		hasTakenActionSinceLastSave = false;
	}
	
	public void saveProject()
	{
		ProjectPersistenceUtilities.saveProject(lastSaveLocation, false, components, getSize(), false);	
		hasTakenActionSinceLastSave = false;
	}
	
	public void saveProjectToFile(final File file)
	{
		ProjectPersistenceUtilities.saveProject(file, false, components, getSize(), true);				
		lastSaveLocation = file;
		hasTakenActionSinceLastSave = false;
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
			int prevScale = mouse.getScalePercent();
			mouse.setScalePercent(100);
			SVGGraphics2D svgGraphics = new SVGGraphics2D(document);
		    GraphicsGenerator svgGen = new GraphicsGenerator(svgGraphics);
		    clear(svgGen);		    
		    display(svgGen);
		    svgGraphics.stream(outputWriter, useCSS);
		    mouse.setScalePercent(prevScale);
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
		if (executedAction != null)
		{
			executedAction.undo();
			undoneAction = executedAction;
			executedAction = null;			
			hasTakenActionSinceLastSave = true;
		}
	}
	
	public void redo()
	{
		if (undoneAction != null)
		{
			undoneAction.execute();
			executedAction = undoneAction;
			undoneAction = null;			
			hasTakenActionSinceLastSave = true;
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
	
	public void addChildrenAndParentsToSelection()
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
		hasTakenActionSinceLastSave = true;
	}
	
	public void copy()
	{
		synchronized (componentSelector)
		{				
			addChildrenAndParentsToSelection();			
			Set<Component> selComponents = new HashSet<Component>();
			Iterator<Component>selCompsIter = componentSelector.getSelectedComponentsIterator();
			while (selCompsIter.hasNext())
			{
				selComponents.add(selCompsIter.next());
			}						
			ProjectPersistenceUtilities.saveProjectNonPersistent(selComponents, getSize());
			hasTakenActionSinceLastSave = true;
		}
	}
	
	public void paste()
	{				
		componentSelector = new ComponentSelector(mouse.getX(), mouse.getY());
		componentSelector.enable();		
		Action pasteAction = new PasteAction(this, componentSelector);
		pasteAction.execute();
		executedAction = pasteAction;
		componentSelector.disable();
		hasTakenActionSinceLastSave = true;
	}
	
	public void delete()
	{
		synchronized (componentSelector)
		{																
			Action deleteAction = new DeleteAction(this, componentSelector);
			deleteAction.execute();
			executedAction = deleteAction;
			hasTakenActionSinceLastSave = true;
			componentSelector = new ComponentSelector(mouse.getX(), mouse.getY());
		}
	}
	
	public void zoomIn()
	{
		mouse.addZoom(20);			
		setPreferredSize(new Dimension((int)(canvasOrigWidth * (mouse.getScalePercent()/100.0D)),
					                   (int)(canvasOrigHeight * (mouse.getScalePercent()/100.0D))));
		
		revalidate();
		repaint();
	}
		
	public void zoomOut()
	{
		
		mouse.addZoom(-20);
		setPreferredSize(new Dimension((int)(canvasOrigWidth * (mouse.getScalePercent()/100.0D)),
					                   (int)(canvasOrigHeight * (mouse.getScalePercent()/100.0D))));
		
		revalidate();
		repaint();
	}
	
	public void colorSelectedComponents(final Color selColor)
	{
		synchronized (componentSelector)
		{																
			Action colorComponentsAction = new ColorComponentsAction(componentSelector, selColor);
			colorComponentsAction.execute();
			executedAction = colorComponentsAction;
			hasTakenActionSinceLastSave = true;
			componentSelector = new ComponentSelector(mouse.getX(), mouse.getY());
		}
	}
	
	public void toggleOpacity()
	{
		synchronized (componentSelector)
		{
			
			Iterator<Component> selCompsIter = componentSelector.getSelectedComponentsIterator();
			while (selCompsIter.hasNext())
			{
				Component comp = selCompsIter.next();
				if (comp.getComponentType() == ComponentType.WHITE_BOX)
				{
					((WhiteBoxComponent)comp).toggleOpacity();
				}
			}
			
			hasTakenActionSinceLastSave = true;
		}
	}
	
	private void inputUpdates()
	{	
		try { highlightedComponent = getHoveredComponent(isCreatingNub ? dragNubX : mouse.getX(), isCreatingNub ? dragNubY : mouse.getY()); }
		catch (Exception e) {}
		
		// On Left Mouse Button Tap
		if (mouse.isButtonTapped(Mouse.LEFT_BUTTON))
		{	
			grabFocus();
			Iterator<Component> componentsIter = getComponentsIterator();
			Set<Component> components = new HashSet<Component>();
			while (componentsIter.hasNext()) components.add(componentsIter.next());			
			ProjectPersistenceUtilities.saveProjectNonPersistent(components, getSize());			
			
			synchronized (lineSegmentPath)
			{
				lineSegmentPath.clear();					
			}
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
					hasTakenActionSinceLastSave = true;
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
					
					hasTakenActionSinceLastSave = true;
					movingComponents = true;
				}
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
			hasTakenActionSinceLastSave = true;
		}
				
		mouse.updateOnFrameEnd();		
	}
	
	private void display(final GraphicsGenerator gfx)
	{
		clear(gfx);
		
		synchronized (componentSelector)
		{
			synchronized (components)
			{					
				((Graphics2D)gfx.getCanvasContext()).scale(mouse.getScalePercent()/100.0D, mouse.getScalePercent()/100.0D);				
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
		renderComponents(gfx, ComponentType.TEXT_BOX);
		renderComponents(gfx, ComponentType.LINE_SEGMENT);
		renderPathSegments(gfx);
		renderComponents(gfx, ComponentType.GATE);
		renderComponents(gfx, ComponentType.BLACK_BOX);
		renderComponents(gfx, ComponentType.HINGE);		
		renderWhiteBoxes(gfx);
		
		if (isCreatingNub)
		{
			gfx.setColor(isNubInContact ? Colors.DEFAULT_COLOR : Colors.TRANSP_COLOR);
			gfx.fillOval(dragNubX - HingeComponent.NUB_DIAMETER/2, dragNubY - HingeComponent.NUB_DIAMETER/2, HingeComponent.NUB_DIAMETER, HingeComponent.NUB_DIAMETER);
		}
		
		if (isCreatingTextbox)
		{
			gfx.setColor(Colors.TRANSP_COLOR);			
						
			gfx.drawString("Text", dragTextboxX - 20, dragTextboxY);						
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
	
	private void renderComponents(final GraphicsGenerator gfx, final ComponentType compType)
	{
		Iterator<Component> compsIter = getComponentsIterator();
		while (compsIter.hasNext())
		{
			Component component = compsIter.next();
			if (component.getComponentType() == compType)
			{
				boolean isInSelection = componentSelector.isComponentInSelection(component);
				component.render(gfx, 
		                         highlightedComponent == component && !isCreatingNub, 
		                         isInSelection,
		                         isInSelection && componentSelector.getNumberOfSelectedComponents() > 1);
			}
		}
	}
	
	private void renderPathSegments(final GraphicsGenerator gfx)
	{
		Iterator<Component> iter = componentSelector.getSelectedComponentsIterator();
		while (iter.hasNext())
		{
			Component component = iter.next();
			if (component.getComponentType() == ComponentType.LINE_SEGMENT)
			{
				synchronized (lineSegmentPath)
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
		}	
	}
	
	private void renderWhiteBoxes(final GraphicsGenerator gfx)
	{
		List<Component> whiteBoxes = new LinkedList<Component>();
		for (Iterator<Component> componentsIter = components.iterator(); componentsIter.hasNext();)
		{
			Component comp = componentsIter.next();
			if (comp.getComponentType() == ComponentType.WHITE_BOX)
			{
				whiteBoxes.add(comp);
			}
		}
		
		if (whiteBoxes.size() > 0)
		{			
			try { whiteBoxes.sort(new ComponentRectangleComparator(false)); }
			catch (Exception e){}
			
			for (Component comp: whiteBoxes)			
			{
				boolean isInSelection = componentSelector.isComponentInSelection(comp);
				comp.render(gfx, 
                            highlightedComponent == comp && !isCreatingNub, 
                            isInSelection,
                            isInSelection && componentSelector.getNumberOfSelectedComponents() > 1);
			}
		}
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
					hasTakenActionSinceLastSave = true;
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
					hasTakenActionSinceLastSave = true;
				}
				
				componentsToRemove.clear();
			}
		}
	}
	
	private Component getHoveredComponent(final int mouseX, final int mouseY)
	{		
		List<Component> componentsList = new ArrayList<Component>(components);
		
		// Remove white box children
		Iterator<Component> compsIter = components.iterator();
		while (compsIter.hasNext())
		{
			Component comp = compsIter.next();
			if (comp.getComponentType() == ComponentType.WHITE_BOX && 
				((WhiteBoxComponent)comp).isOpaque())
			{
				List<Component> whiteBoxChildren = comp.getChildren();
				for (Component child: whiteBoxChildren)
				{
					if (((ConcreteComponent)comp).indexOfPort(child) == -1)
					{
						componentsList.remove(child);						
					}
				}
			}
		}
		
		try {componentsList.sort(new ComponentRectangleComparator(false));}
		catch(Exception e){}
		
		return getHoveredComponentInBucket(mouseX, mouseY, componentsList);
	}
	
	private Component getHoveredComponentInBucket(final int mouseX, 
			                                      final int mouseY,
 			                                      final List<Component> bucket)
	{
		for (Component comp: bucket)
		{
			if (comp.mouseIntersection(mouseX, mouseY))
				return comp;
		}
		
		return null;
	}
	
	private void finalizeWireCreation(final int x, final int y)
	{								
		
		Component startPoint = new HingeComponent(this, x - NEW_WIRE_LENGTH/2 - HingeComponent.HINGE_DIAMETER/2, y - HingeComponent.HINGE_DIAMETER/2, true);
		Component endPoint   = new HingeComponent(this, x + NEW_WIRE_LENGTH/2 - HingeComponent.HINGE_DIAMETER/2, y - HingeComponent.HINGE_DIAMETER/2, true);		
		
		addComponentToCanvas(new LineSegmentComponent(this, startPoint, endPoint, true));		
		addComponentToCanvas(startPoint);
		addComponentToCanvas(endPoint);		
		hasTakenActionSinceLastSave = true;
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
						if (nubHinge.isMovable())
						{
							nubHinge.setMovable(incidentHinge.isMovable());													
						}
						
						Iterator<Component> compsIter2 = getComponentsIterator();						
						while(compsIter2.hasNext())
						{
							Component comp2 = compsIter2.next();
							
							if (comp2.getComponentType() == ComponentType.LINE_SEGMENT)
							{
								LineSegmentComponent ls = (LineSegmentComponent)comp2;
								
								if (ls.getChildren().get(0) == incidentHinge)
								{
									ls.setStartPoint(nubHinge);
									ls.setMovable(ls.getChildren().get(0).isMovable() && ls.getChildren().get(1).isMovable());
									excludedLineSegments.add(ls);
								}
								else if (ls.getChildren().get(1) == incidentHinge)
								{
									ls.setEndPoint(nubHinge);
									ls.setMovable(ls.getChildren().get(0).isMovable() && ls.getChildren().get(1).isMovable());
									excludedLineSegments.add(ls);
								}
							}
						}
						
						removeComponentFromCanvas(incidentHinge);
					}
				}
			}
						
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
			hasTakenActionSinceLastSave = true;
		}
	}

	private void finalizeTextboxCreation()
	{		
		int textboxX = Math.max(40, dragTextboxX - 20);
		int textboxY = Math.max(40, dragTextboxY);
		
		addComponentToCanvas(new TextBoxComponent(this, "Text", textboxX, textboxY));
		hasTakenActionSinceLastSave = true;
		isCreatingTextbox = false;
	}
	
	private void constructLinePath(final Component component)
	{
		synchronized (lineSegmentPath)
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
	}
	
	private void finalizeMovementAndCreateActions()
	{			
		if (componentSelector.getNumberOfSelectedComponents() == 1)
		{		
			componentSelector.getFirstComponent().finalizeMovement(alignedComponents);						
			alignedComponents = null;
			
			Action currentAction = new MoveAction(this,
					                              componentSelector.getFirstComponent(),
    					                          startPositions.get(0),
					                              new int[]{ componentSelector.getFirstComponent().getRectangle().x,
							                      componentSelector.getFirstComponent().getRectangle().y });
			currentAction.execute();
			
			// Differentiate between a simple click and an actual move
			if (startPositions.get(0)[0] != componentSelector.getFirstComponent().getRectangle().x ||
				startPositions.get(0)[1] != componentSelector.getFirstComponent().getRectangle().y)
			{
				executedAction = currentAction;								
			}
			
			// Force execution on segment spawn
			if (HingeComponent.globalHingeDraggingMode == Component.HingeDraggingMode.SPAWN_SEGMENT_AXIS_RES ||
				HingeComponent.globalHingeDraggingMode == Component.HingeDraggingMode.SPAWN_SEGMENT_UNRES)
			{
				executedAction = currentAction;
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
						
			if (startPositions.size() > 0 && targetPositions.size() > 0)
			{		
				// Differentiate between a simple click and an actual move
				if (startPositions.get(0)[0] != targetPositions.get(0)[0] ||
						startPositions.get(0)[1] != targetPositions.get(0)[1])
				{
					executedAction = currentAction;								
				}						
			}
		}	
	}
}
