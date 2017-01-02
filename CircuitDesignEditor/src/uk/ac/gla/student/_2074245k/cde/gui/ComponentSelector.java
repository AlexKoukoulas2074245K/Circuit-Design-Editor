package uk.ac.gla.student._2074245k.cde.gui;

import java.awt.Rectangle;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import uk.ac.gla.student._2074245k.cde.components.Component;
import uk.ac.gla.student._2074245k.cde.components.Component.ComponentType;
import uk.ac.gla.student._2074245k.cde.components.ConcreteComponent;
import uk.ac.gla.student._2074245k.cde.components.LineSegmentComponent;
import uk.ac.gla.student._2074245k.cde.util.GraphicsGenerator;

public final class ComponentSelector 
{
	private Set<Component> selectedComponents;
	private Component firstSelectedComponent;
	private int startX, startY, endX, endY;
	private boolean isEnabled;
	
	public ComponentSelector(final int startX, final int startY)
	{
		this.startX = this.endX = startX;
		this.startY = this.endY = startY;		
		selectedComponents = new HashSet<Component>();
		firstSelectedComponent = null;
		isEnabled = false;
	}
	
	public void update(final int endX, final int endY, final List<Component> allComponents)
	{
		this.endX = endX;
		this.endY = endY;
		
		Rectangle absRect = getAbsoluteRectangle();
		
		selectedComponents.clear();
		firstSelectedComponent = null;
		
		for (Component component: allComponents)
		{
			if (component.getComponentType() != ComponentType.LINE_SEGMENT && absRect.intersects(component.getRectangle()))
			{
				addComponentToSelection(component);
			}
			else if (component.getComponentType() == ComponentType.LINE_SEGMENT)
			{
				LineSegmentComponent ls = (LineSegmentComponent)component;
				if (absRect.contains(ls.getStartPoint().getRectangle()) ||
					absRect.contains(ls.getEndPoint().getRectangle()))
				{
					addComponentToSelection(component);
				}
			}
		}
	}
	
	public void addComponentToSelectionExternally(final Component component)
	{
		addComponentToSelection(component);		
	}
	
	public int getNumberOfSelectedComponents()
	{
		return selectedComponents.size();
	}
	
	public Iterator<Component> getSelectedComponentsIterator()
	{
		return selectedComponents.iterator();
	}
	
	public boolean isComponentInSelection(final Component component)
	{
		return selectedComponents.contains(component);
	}
	
	public Component getFirstComponent()
	{
		return firstSelectedComponent;
	}
	
	public void render(final GraphicsGenerator gfx)
	{
		if (!isEnabled)
			return;
		
		Rectangle absRect = getAbsoluteRectangle();
		gfx.setStroke(Strokes.THIN_STROKE);
		gfx.setColor(Colors.SELECTION_COLOR);
		gfx.drawRect(absRect.x, absRect.y, absRect.width, absRect.height);
		gfx.setColor(Colors.SELECTION_TRANSP_COLOR);
		gfx.fillRect(absRect.x, absRect.y, absRect.width, absRect.height);
	}
	
	public boolean isEnabled() { return isEnabled; }
	
	public void enable() { isEnabled = true; }
	public void disable() { isEnabled = false; }
	public void setEnabled(final boolean enabled) { isEnabled = enabled; }
	
	private Rectangle getAbsoluteRectangle()
	{
		int rectX = startX;
		int rectY = startY;
		int rectX2 = endX;
		int rectY2 = endY;
		
		if (startX > endX)
		{
			rectX = endX;
			rectX2 = startX;
		}
		if (startY > endY)	
		{
			rectY = endY;
			rectY2 = startY;
		}
		
		return new Rectangle(rectX, rectY, rectX2 - rectX, rectY2 - rectY);
	}
	
	private void addComponentToSelection(final Component component)
	{
		selectedComponents.add(component);
				
		if (selectedComponents.size() == 1)
		{
			firstSelectedComponent = component;
		}
				
		if (isEnabled && component.getComponentType() == ComponentType.LINE_SEGMENT)
		{
			LineSegmentComponent ls = (LineSegmentComponent)component;
			selectedComponents.add(ls.getStartPoint());
			selectedComponents.add(ls.getEndPoint());
			
			Component concComp = ls.isPort();
			
			if (concComp != null)
			{
				selectedComponents.add(concComp);
				addConcreteComponentToSelection(concComp);
			}
		}				
		else if (isEnabled && 
				(component.getComponentType() == ComponentType.BLACK_BOX ||
				 component.getComponentType() == ComponentType.GATE))
		{						
			addConcreteComponentToSelection(component);	
		}		
	}
	
	private void addConcreteComponentToSelection(final Component component)
	{
		ConcreteComponent concComp = (ConcreteComponent)component;
		Iterator<Component> portsIter = concComp.getPortsIterator();
		while (portsIter.hasNext())
		{
			LineSegmentComponent ls = (LineSegmentComponent)portsIter.next();
			selectedComponents.add(ls);
			selectedComponents.add(ls.getStartPoint());
			selectedComponents.add(ls.getEndPoint());
		}

	}
}
