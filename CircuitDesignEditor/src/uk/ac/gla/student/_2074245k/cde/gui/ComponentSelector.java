package uk.ac.gla.student._2074245k.cde.gui;

import java.awt.Rectangle;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import uk.ac.gla.student._2074245k.cde.components.Component;
import uk.ac.gla.student._2074245k.cde.components.Component.ComponentType;
import uk.ac.gla.student._2074245k.cde.util.GraphicsGenerator;

public final class ComponentSelector 
{
	private Set<Component> selectedComponents;
	private Component firstSelectedComponent;
	private int startX, startY, endX, endY;
	private boolean isEnabled;
	private Rectangle absRect;
	
	public ComponentSelector(final int startX, final int startY)
	{		
		this.startX = this.endX = startX;
		this.startY = this.endY = startY;		
		selectedComponents = new HashSet<Component>();
		firstSelectedComponent = null;
		isEnabled = false;
		absRect = new Rectangle();
	}
	
	public void update(final int endX, final int endY, final Set<Component> allComponents)
	{
		this.endX = endX;
		this.endY = endY;
		
		constructAbsoluteRectangle();
		
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
				if (absRect.contains(component.getChildren().get(0).getRectangle()) ||
					absRect.contains(component.getChildren().get(1).getRectangle()))
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
	
	private Rectangle constructAbsoluteRectangle()
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
		
		absRect.x = rectX;
		absRect.y = rectY;
		absRect.width = rectX2 - rectX;
		absRect.height = rectY2 - rectY;
		
		return absRect;
	}
	
	private void addComponentToSelection(final Component component)
	{
		selectedComponents.add(component);
				
		if (selectedComponents.size() == 1)
		{
			firstSelectedComponent = component;
		}
		
		if (isEnabled)
		{
			for (Component child: component.getChildren())
			{
				if (!selectedComponents.contains(child))
				{					
					addComponentToSelection(child);
				}
			}
			
			for (Component parent: component.getParents())
			{								
				if (!selectedComponents.contains(parent))
				{
					addComponentToSelection(parent);					
				}			
			}
		}
	}	
}
