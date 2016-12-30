package uk.ac.gla.student._2074245k.cde.components;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.Set;

import uk.ac.gla.student._2074245k.cde.gui.Colors;
import uk.ac.gla.student._2074245k.cde.gui.MainCanvas;
import uk.ac.gla.student._2074245k.cde.gui.Strokes;
import uk.ac.gla.student._2074245k.cde.util.GraphicsGenerator;

public final class BlackBoxComponent extends ConcreteComponent 
{	
	private final String name;
	private final int nameXOffset;
	private final int nameYOffset;
	
	public BlackBoxComponent(final MainCanvas canvas,
			                 final Rectangle componentRect,						     
			                 final String name,
			                 final int nameXOffset,
			                 final int nameYOffset)
	{
		super(canvas, true);		
		this.componentRect     = componentRect;
		this.name              = name;
		this.nameXOffset       = nameXOffset;
		this.nameYOffset       = nameYOffset;		
	}

	@Override
	public void render(final GraphicsGenerator g,
			           final boolean highlighted, 
			           final boolean selected, 
			           final boolean inMultiSelectionMovement) 
	{
		g.setStroke(Strokes.THIN_STROKE);
		
		g.setColor(Color.white);
		g.fillRect(componentRect.x, componentRect.y, componentRect.width, componentRect.height);					
		
		g.setColor(selected ? Colors.SELECTION_COLOR : Colors.DEFAULT_COLOR);
		g.drawRect(componentRect.x, componentRect.y, componentRect.width, componentRect.height);
		
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

	@Override
	public void renderAligned(final GraphicsGenerator g) 
	{
		render(g, false, false, false);
	}

	@Override
	public Component clone(Set<Component> outClonedComponents)
	{
		BlackBoxComponent clonedComponent = new BlackBoxComponent(canvas,
				                                                  new Rectangle(componentRect),
				                                                  new String(name),
				                                                  nameXOffset,
				                                                  nameYOffset);
		
		for (Component port: ports)
		{
			Component clonedPort = port.clone(outClonedComponents);						
			clonedComponent.addPort(clonedPort);
			
			if (internalHorHinges.contains(((LineSegmentComponent)port).getStartPoint()))
			{
				clonedComponent.addInternalHorHinge(((LineSegmentComponent)clonedPort).getStartPoint());
			}
			else if (internalHorHinges.contains(((LineSegmentComponent)port).getEndPoint()))
			{
				clonedComponent.addInternalHorHinge(((LineSegmentComponent)clonedPort).getEndPoint());
			}
			else if (internalVerHinges.contains(((LineSegmentComponent)port).getStartPoint()))
			{	
				clonedComponent.addInternalVerHinge(((LineSegmentComponent)clonedPort).getStartPoint());
			}
			else if (internalVerHinges.contains(((LineSegmentComponent)port).getEndPoint()))
			{
				clonedComponent.addInternalVerHinge(((LineSegmentComponent)clonedPort).getEndPoint());
			}
				
		}
		
		outClonedComponents.add(clonedComponent);
		return clonedComponent;
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
		return ComponentType.BLACK_BOX;
	}
}
