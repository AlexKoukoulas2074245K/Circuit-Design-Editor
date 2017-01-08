package uk.ac.gla.student._2074245k.cde.components;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import uk.ac.gla.student._2074245k.cde.gui.Colors;
import uk.ac.gla.student._2074245k.cde.gui.MainCanvas;
import uk.ac.gla.student._2074245k.cde.gui.Strokes;
import uk.ac.gla.student._2074245k.cde.util.GraphicsGenerator;

public class WhiteBoxComponent extends ConcreteComponent
{
	public static boolean WHITE_BOX_OPACITY = false;
	
	private final String name;
	private final int nameXOffset;
	private final int nameYOffset;
	private final Set<Component> innerComponents;
	
	public WhiteBoxComponent(final MainCanvas canvas,
			                 final Rectangle componentRect,
			                 final Set<Component> selComponents,
			                 final String name,
			                 final int nameXOffset,
			                 final int nameYOffset)
	{
		super(canvas, true);		
		this.componentRect   = componentRect;
		this.name            = name;
		this.nameXOffset     = nameXOffset;
		this.nameYOffset     = nameYOffset;				
		this.innerComponents = selComponents;				
	}
	
	@Override
	public AlignedComponentsList moveTo(int x, int y)
	{
		updateInnerComponentsRefs();
		
		int prevX = componentRect.x;
		int prevY = componentRect.y;
		AlignedComponentsList alignedComponents = super.moveTo(x, y);
		
		Iterator<Component> horAlignedComponentsIter = alignedComponents.getHorAlignedComponents().iterator();
		while (horAlignedComponentsIter.hasNext())
		{
			Component alignedComp = horAlignedComponentsIter.next();
			if (isInnerComponent(alignedComp))
			{
				horAlignedComponentsIter.remove();
			}
			
			for (Component comp: ports)
			{
				if (comp.getChildren().contains(alignedComp))
				{
					horAlignedComponentsIter.remove();
				}
			}
		}
		Iterator<Component> verAlignedComponentsIter = alignedComponents.getVerAlignedComponents().iterator();
		while (verAlignedComponentsIter.hasNext())
		{
			Component alignedComp = verAlignedComponentsIter.next();
			if (isInnerComponent(alignedComp))
			{
				verAlignedComponentsIter.remove();		
			}
			
			for (Component comp: ports)
			{
				if (comp.getChildren().contains(alignedComp))
				{
					verAlignedComponentsIter.remove();
				}
			}
		}
		
		int dx = componentRect.x - prevX;
		int dy = componentRect.y - prevY;
		
		for (Component comp: innerComponents)
		{
			if (comp.getComponentType() != ComponentType.LINE_SEGMENT)
			{
				comp.setPosition(comp.getRectangle().x + dx, comp.getRectangle().y + dy);				
			}
		}
		
		return alignedComponents;
	}
	
	@Override
	public void finalizeMovement(AlignedComponentsList alignedComponents) 
	{
		int prevX = componentRect.x;
		int prevY = componentRect.y;
		
		super.finalizeMovement(alignedComponents);
		
		int dx = componentRect.x - prevX;
		int dy = componentRect.y - prevY;
		
		for (Component comp: innerComponents)
		{
			if (comp.getComponentType() != ComponentType.LINE_SEGMENT)
			{
				comp.setPosition(comp.getRectangle().x + dx, comp.getRectangle().y + dy);				
			}
		}		
	}
	
	@Override
	public void delete()
	{
		super.delete();
		for (Component comp: innerComponents)
		{
			comp.delete();
		}
	}
	
	@Override
	public void render(final GraphicsGenerator g,
			           final boolean highlighted, 
			           final boolean selected, 
			           final boolean inMultiSelectionMovement) 
	{
		g.setStroke(Strokes.THIN_STROKE);
		
		if (WHITE_BOX_OPACITY)
		{
			g.setColor(Color.white);
			g.fillRect(componentRect.x, componentRect.y, componentRect.width, componentRect.height);								
		}
		
		g.setColor(selected ? Colors.SELECTION_COLOR : Colors.DEFAULT_COLOR);
		g.drawRect(componentRect.x, componentRect.y, componentRect.width, componentRect.height);
		
		if (WHITE_BOX_OPACITY)
		{			
			for (Component internalHinge: internalHorHinges)
			{
				((HingeComponent)internalHinge).calculateNamePosition(g);
				g.drawString(((HingeComponent)internalHinge).getName(),
						((HingeComponent)internalHinge).getNameX(),
						((HingeComponent)internalHinge).getNameY());
			}
			
			for (Component internalHinge: internalVerHinges)
			{
				((HingeComponent)internalHinge).calculateNamePosition(g);
				g.drawString(((HingeComponent)internalHinge).getName(),
						((HingeComponent)internalHinge).getNameX(),
						((HingeComponent)internalHinge).getNameY());
			}
			
			Rectangle2D stringBounds = g.getFontMetrics().getStringBounds(name, g.getCanvasContext());
			g.drawString(name,
					     componentRect.x + nameXOffset + (int)(componentRect.width - stringBounds.getWidth())/2,
					     componentRect.y + nameYOffset + (int)(componentRect.height - stringBounds.getHeight())/2);
		}		
	}

	@Override
	public void renderAligned(final GraphicsGenerator g) 
	{
		render(g, false, false, false);
	}

	@Override
	public List<Component> getParents()
	{
		return new ArrayList<Component>();
	}
	
	@Override
	public List<Component> getChildren()
	{
		List<Component> children = new ArrayList<Component>(ports);
		children.addAll(innerComponents);
		return children;
	}
	
	@Override
	public String serialize() 
	{	
		return componentRect.x + "," + 
	           componentRect.y + "," + 
			   componentRect.width + "," + 
			   componentRect.height + "," +
			   name + "," + 
			   nameXOffset + "," + 
			   nameYOffset + "\n";
	}

	@Override
	public ComponentType getComponentType() 
	{
		return ComponentType.WHITE_BOX;
	}
	
	public Iterator<Component> getInnerComponentsIter()
	{
		return innerComponents.iterator();
	}
	
	public void addInnerComponentExternally(final Component component)
	{
		innerComponents.add(component);
	}
	
	public boolean isInnerComponent(final Component comp)
	{
		return innerComponents.contains(comp);
	}
	
	private void updateInnerComponentsRefs()
	{
		Set<Component> currentInnerComponents = new HashSet<Component>(innerComponents);
		
		for (Component component: currentInnerComponents)
		{
			addComponentChildrenAndParents(component);
		}
	}
	
	private void addComponentChildrenAndParents(final Component component)
	{
		innerComponents.add(component);
		for (Component child: component.getChildren())
		{
			if (!innerComponents.contains(child))
			{					
				addComponentChildrenAndParents(child);
			}
		}
		
		for (Component parent: component.getParents())
		{			
			if (parent.getComponentType() != ComponentType.WHITE_BOX)
			{					
				if (!innerComponents.contains(parent))
				{
					addComponentChildrenAndParents(parent);					
				}
			}
		}
	}
}
