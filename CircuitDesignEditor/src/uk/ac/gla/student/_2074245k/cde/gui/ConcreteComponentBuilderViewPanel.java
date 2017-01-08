package uk.ac.gla.student._2074245k.cde.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.JPanel;

import uk.ac.gla.student._2074245k.cde.components.BlackBoxComponent;
import uk.ac.gla.student._2074245k.cde.components.Component;
import uk.ac.gla.student._2074245k.cde.components.WhiteBoxComponent;
import uk.ac.gla.student._2074245k.cde.gui.PortView.PortLocation;
import uk.ac.gla.student._2074245k.cde.observables.PortModificationObservable;
import uk.ac.gla.student._2074245k.cde.observers.PortModificationObserver;
import uk.ac.gla.student._2074245k.cde.util.Mouse;

public final class ConcreteComponentBuilderViewPanel extends JPanel implements MouseListener, MouseMotionListener, PortModificationObservable
{	
	private static final long serialVersionUID = -1384668565961127584L;	
	private Color backgroundColor = Colors.CANVAS_BKG_COLOR;
	private Mouse panelMouse;
	private boolean isInsidePanel = false;
	private boolean gridVisibility = true;
	
	private List<PortModificationObserver> insertionObservers;
	private List<PortModificationObserver> deletionObservers;
	
	private List<PortView> portViews;	
	private final MainCanvas canvas;
	private final boolean shouldBuildWhiteBox;
	private final Set<Component> selComponents;
	
	public ConcreteComponentBuilderViewPanel(final MainCanvas canvas, 
			                                 final boolean shouldBuildWhiteBox, 
			                                 final Set<Component> selComponents)
	{
		super();		
		this.canvas              = canvas;
		this.shouldBuildWhiteBox = shouldBuildWhiteBox;
		this.selComponents       = selComponents;
		panelMouse               = new Mouse();		
		portViews                = new ArrayList<PortView>();
		
		insertionObservers = new ArrayList<PortModificationObserver>();
		deletionObservers = new ArrayList<PortModificationObserver>();
	}
	
	public Component buildConcreteComponent()
	{
		if (shouldBuildWhiteBox)
		{
			return new WhiteBoxComponent(canvas,
					                     getComponentRectangle(), 				     
					                     selComponents,
					                     ConcreteComponentBuilderPanel.componentName,					                     
					                     ConcreteComponentBuilderPanel.nameXOffset,
					                     ConcreteComponentBuilderPanel.nameYOffset);
		}
		else
		{
			return new BlackBoxComponent(canvas,
					                     getComponentRectangle(), 				     					                     
					                     ConcreteComponentBuilderPanel.componentName,					                     
					                     ConcreteComponentBuilderPanel.nameXOffset,
					                     ConcreteComponentBuilderPanel.nameYOffset);
		}		
	}
	
	public List<PortView> getPortViews()
	{
		for (PortView portView: portViews)
		{
			switch (portView.portLocation)
			{
				case LEFT: 
				case RIGHT: portView.actualPosition = (int)(portView.normalizedPosition * getHeight()); break;
				
				case TOP: 
				case BOTTOM: portView.actualPosition = (int)(portView.normalizedPosition * getWidth()); break;
			}
		}
		
		return portViews;
	}
	
	public void toggleGridVisibility()
	{
		gridVisibility = !gridVisibility;
	}
	
	public void portNameChange(final PortView port, final String name)
	{
		port.portName = name;
		repaint();
	}
	
	public void portPositionChanged(final PortView port, final int position)
	{
		port.actualPosition = position;
		
		switch (port.portLocation)
		{
			case LEFT: 
			case RIGHT: port.normalizedPosition = (float)port.actualPosition/getHeight(); break;
			
			case TOP: 
			case BOTTOM: port.normalizedPosition = (float)port.actualPosition/getWidth(); break;
		}
		
		repaint();
	}
	
	public void portInserted(final PortView port)
	{
		portViews.add(port);
		repaint();
	}
	
	public void portDeleted(final PortView port)
	{
		portViews.remove(port);
		repaint();
	}
	
	public Rectangle getComponentRectangle()
	{
		int viewWidth = getWidth();
		int viewHeight = getHeight();
		int xmargin = viewWidth / ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR;
		int ymargin = viewHeight / ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR;				
		return new Rectangle(xmargin, ymargin, viewWidth - 2 * xmargin, viewHeight - 2 * ymargin);
	}

	@Override
	public void subscribeToPortInsertionEvent(PortModificationObserver observer) 
	{
		insertionObservers.add(observer);
	}

	@Override
	public void subscribeToPortDeletionEvent(PortModificationObserver observer) 
	{
		deletionObservers.add(observer);
	}	
	
	@Override
	public void addNotify()
	{
		super.addNotify();				
		addMouseListener(this);		
		addMouseMotionListener(this);
	}	
	
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		setBackground(backgroundColor);		
						
		Rectangle componentRect = getComponentRectangle();
		
		// Render Grid
		if (isInsidePanel && gridVisibility && !componentRect.contains(panelMouse.getX(), panelMouse.getY()))
		{
			g.setColor(new Color(220, 220, 220));
			
			int nRows = getHeight() / ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR;
			int nCols = getWidth() / ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR;
			
			for (int y = 0; y < nRows; ++y)
			{
				g.drawLine(0, y * ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR, getWidth(), y * ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR);
				
				for (int x = 0; x < nCols; ++x)
				{
					g.drawLine(x * ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR, 0, x * ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR, getHeight());
				}
			}						
		}
		
		// Render Component's body
		g.setColor(Colors.CANVAS_BKG_COLOR);
		g.fillRect(componentRect.x, componentRect.y, componentRect.width, componentRect.height);					
		
		g.setColor(Colors.DEFAULT_COLOR);
		g.drawRect(componentRect.x, componentRect.y, componentRect.width, componentRect.height);
		
		Rectangle2D stringBounds = g.getFontMetrics().getStringBounds(ConcreteComponentBuilderPanel.componentName, g);
		g.drawString(ConcreteComponentBuilderPanel.componentName,
				     ConcreteComponentBuilderPanel.nameXOffset + (int)(getWidth() - stringBounds.getWidth())/2,
				     ConcreteComponentBuilderPanel.nameYOffset + (int)(getHeight() - stringBounds.getHeight())/2);
		
		// Render ports
		for (PortView portView: portViews)
		{				
			switch (portView.portLocation)
			{
				case LEFT:
				{
					g.drawLine(0,
							   (int)(portView.normalizedPosition * getHeight()), 
							   componentRect.x - (portView.isInverted ? ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR : 0),  
							   (int)(portView.normalizedPosition * getHeight()));
					
					if (portView.isInverted)
					{						
						g.drawOval(componentRect.x - ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR,
								   (int)(portView.normalizedPosition * getHeight()) - ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR/2,
							       ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR, 
								   ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR);
					}
					
					g.drawString(portView.portName, 
							     componentRect.x + 5, 
							     (int)(portView.normalizedPosition * getHeight()) + ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR/2);
				} break;
				
				case RIGHT:
				{
					g.drawLine(componentRect.x + componentRect.width + (portView.isInverted ? ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR : 0),
							   (int)(portView.normalizedPosition * getHeight()), 
							   getWidth(), 
							   (int)(portView.normalizedPosition * getHeight()));
					
					if (portView.isInverted)
					{						
						g.drawOval(componentRect.x + componentRect.width,
								   (int)(portView.normalizedPosition * getHeight()) - ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR/2,
							       ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR, 
								   ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR);
					}
					
					Rectangle2D nameBounds = g.getFontMetrics().getStringBounds(portView.portName, g);
					g.drawString(portView.portName, 
							     componentRect.x + componentRect.width - 5 - (int)nameBounds.getWidth(),
							     (int)(portView.normalizedPosition * getHeight()) + ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR/2);
				} break;
				
				case TOP:
				{
					g.drawLine((int)(portView.normalizedPosition * getWidth()),
							   0, 
							   (int)(portView.normalizedPosition * getWidth()), 
							   componentRect.y - (portView.isInverted ? ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR : 0));
					
					if (portView.isInverted)
					{						
						g.drawOval((int)(portView.normalizedPosition * getWidth()) - ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR/2,
								   componentRect.y - ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR,
							       ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR, 
								   ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR);
					}
					
					Rectangle2D nameBounds = g.getFontMetrics().getStringBounds(portView.portName, g);
					g.drawString(portView.portName,
							     (int)(portView.normalizedPosition * getWidth()) - (int)nameBounds.getWidth()/2,
							     componentRect.y + ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR + 5);
				} break;
				
				case BOTTOM:
				{
					g.drawLine((int)(portView.normalizedPosition * getWidth()),
							   componentRect.y + componentRect.height + (portView.isInverted ? ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR : 0), 
							   (int)(portView.normalizedPosition * getWidth()), 
							   getHeight());
					
					if (portView.isInverted)
					{						
						g.drawOval((int)(portView.normalizedPosition * getWidth()) - ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR/2,
								   componentRect.y + componentRect.height,
							       ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR, 
								   ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR);
					}
					
					Rectangle2D nameBounds = g.getFontMetrics().getStringBounds(portView.portName, g);
					g.drawString(portView.portName, 
							(int)(portView.normalizedPosition * getWidth()) - (int)nameBounds.getWidth()/2,
							     componentRect.y + componentRect.height - 5);							     							     
				} break;
			}			
		}						
	}

	@Override
	public void mouseEntered(MouseEvent __) { isInsidePanel = true; repaint(); }

	@Override
	public void mouseExited(MouseEvent __) { isInsidePanel = false; repaint(); }

	@Override
	public void mouseClicked(MouseEvent __) {	}

	@Override
	public void mousePressed(MouseEvent evt) 
	{
		panelMouse.updateOnMousePressed(evt);
		repaint(); 
	}

	@Override
	public void mouseReleased(MouseEvent evt) 
	{ 
		panelMouse.updateOnMouseReleased(evt);
		
		if (evt.getButton() == MouseEvent.BUTTON1)
		{
			checkForPortInsertion();
		}
		else if (evt.getButton() == MouseEvent.BUTTON3)
		{
			checkForPortDeletion();
		}
		
		repaint(); 
	}
	
	@Override
	public void mouseDragged(MouseEvent evt) 
	{ 
		panelMouse.updateOnMouseDrag(evt);
		repaint(); 
	}

	@Override
	public void mouseMoved(MouseEvent evt) 
	{
		panelMouse.updateOnMouseMove(evt);
		repaint(); 
	}
	
	private void checkForPortInsertion()
	{
		int mouseX = panelMouse.getX();
		int mouseY = panelMouse.getY();
		
		Rectangle componentRect = getComponentRectangle();
		if (!componentRect.contains(mouseX, mouseY))
		{		
			PortView addedPortView = null;
			if (mouseX < componentRect.x && mouseY > componentRect.y && mouseY < componentRect.y + componentRect.height)
			{				
				int row = mouseY / ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR;
				
				if (deletePortViewOnPositionIfPresent(0, row, PortLocation.LEFT))
					;
				else
				{
					int actualPosition = row * ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR + ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR/2;
					addedPortView = new PortView(PortLocation.LEFT, actualPosition, (float)actualPosition/getHeight(), "port");
					portViews.add(addedPortView);					
				}
			}
			else if (mouseX > componentRect.x + componentRect.width && mouseY > componentRect.y && mouseY < componentRect.y + componentRect.height)
			{
				int row = mouseY / ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR;
				
				if (deletePortViewOnPositionIfPresent(0, row, PortLocation.RIGHT))
					;
				else
				{
					int actualPosition = row * ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR + ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR / 2;
					addedPortView = new PortView(PortLocation.RIGHT, actualPosition, (float)actualPosition/getHeight(), "port"); 
					portViews.add(addedPortView);					
				}
			}
			else if (mouseY < componentRect.y && mouseX > componentRect.x && mouseX < componentRect.x + componentRect.width)
			{
				int col = mouseX / ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR;
				
				if (deletePortViewOnPositionIfPresent(col, 0, PortLocation.TOP))
					;
				else
				{
					int actualPosition = col * ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR + ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR / 2;
					addedPortView = new PortView(PortLocation.TOP, actualPosition, (float)actualPosition/getWidth(), "port"); 
					portViews.add(addedPortView);
				}
			}						
			else if (mouseY > componentRect.y + componentRect.height && mouseX > componentRect.x && mouseX < componentRect.x + componentRect.width)
			{
				int col = mouseX / ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR;
				
				if (deletePortViewOnPositionIfPresent(col, 0, PortLocation.BOTTOM))
					;
				else
				{
					int actualPosition = col * ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR + ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR / 2;
					addedPortView = new PortView(PortLocation.BOTTOM, actualPosition, (float)actualPosition/getWidth(), "port"); 
					portViews.add(addedPortView);					
				}
			}
			
			if (addedPortView != null)
			{
				for (PortModificationObserver observer: insertionObservers)
				{	
					observer.callbackOnPortInsertionEvent(addedPortView);
				}					
			}
		}
	}
	
	private void checkForPortDeletion()
	{
		int mouseX = panelMouse.getX();
		int mouseY = panelMouse.getY();
		Rectangle componentRect = getComponentRectangle();
		
		if (!componentRect.contains(mouseX, mouseY))
		{
			int row = mouseY / ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR;
			int col = mouseX / ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR;
			
			PortLocation mouseLocation = null;
			
			if (mouseX < componentRect.x)
				mouseLocation = PortLocation.LEFT;
			else if (mouseX > componentRect.x + componentRect.width)
				mouseLocation = PortLocation.RIGHT;
			else if (mouseY < componentRect.y)
				mouseLocation = PortLocation.TOP;
			else if (mouseY > componentRect.y + componentRect.height)
				mouseLocation = PortLocation.BOTTOM;
			
			deletePortViewOnPositionIfPresent(col, row, mouseLocation);
		}			
	}
	
	private boolean deletePortViewOnPositionIfPresent(final int col, final int row, final PortLocation mouseLocation)
	{
		PortView portViewToDelete = null;
		for (PortView portView: portViews)
		{
			switch (portView.portLocation)
			{
				case LEFT:   if (portView.actualPosition/ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR == row && mouseLocation == PortLocation.LEFT) portViewToDelete = portView; break;				
				case RIGHT:  if (portView.actualPosition/ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR == row && mouseLocation == PortLocation.RIGHT) portViewToDelete = portView; break;				
				case TOP:    if (portView.actualPosition/ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR == col && mouseLocation == PortLocation.TOP) portViewToDelete = portView; break;
				case BOTTOM: if (portView.actualPosition/ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR == col && mouseLocation == PortLocation.BOTTOM) portViewToDelete = portView; break;
			}			
		}
		
		if (portViewToDelete != null)
		{
			portViews.remove(portViewToDelete);
			
			for (PortModificationObserver observer: deletionObservers)
			{
				observer.callbackOnPortDeletionEvent(portViewToDelete);
			}
			
			return true;
		}
		
		return false;
	}
}
