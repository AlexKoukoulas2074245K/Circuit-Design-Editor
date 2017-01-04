package uk.ac.gla.student._2074245k.cde.components;

import java.awt.Rectangle;
import java.util.Set;

import uk.ac.gla.student._2074245k.cde.gui.Colors;
import uk.ac.gla.student._2074245k.cde.gui.MainCanvas;
import uk.ac.gla.student._2074245k.cde.gui.PortView.PortLocation;
import uk.ac.gla.student._2074245k.cde.util.GraphicsGenerator;

public final class GateComponent extends ConcreteComponent
{	
	public enum GateType
	{
		AND_GATE, NAND_GATE, OR_GATE, NOR_GATE, XOR_GATE, XNOR_GATE
	}
	
	public static final int DEFAULT_SIZE = 64;
	
	private GateType gateType;		
	
	public GateComponent(final MainCanvas canvas, final GateType gateType, final boolean movable, final int x, final int y) 
	{
		super(canvas, movable);
		
		this.gateType = gateType;	
		this.componentRect = new Rectangle(x, y, DEFAULT_SIZE, DEFAULT_SIZE);				
	}
	
	@Override
	public void render(final GraphicsGenerator g,
			           final boolean highlighted, 
			           final boolean selected, 
			           final boolean isInMultiSelectionMovement) 
	{			
		g.setColor(selected ? Colors.SELECTION_COLOR : Colors.DEFAULT_COLOR);
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
		
		g.drawGate(gateType, getRectangle(), selected ? Colors.SELECTION_COLOR : Colors.DEFAULT_COLOR);
	}
	
	@Override
	public void renderAligned(final GraphicsGenerator g)
	{
		render(g, false, false, false);
	}
	
	@Override
	public Component clone(Set<Component> outComponents)
	{
		GateComponent clonedComponent = new GateComponent(canvas, gateType, isMovable, getRectangle().x, getRectangle().y);
		clonedComponent.constructPortsAutomatically();
		outComponents.add(clonedComponent);
		
		return clonedComponent;
	}
		
	@Override
	public String serialize() 
	{		
		return gateType + "\n" + componentRect.x + "," + componentRect.y + "\n";	
	}
	
	@Override
	public ComponentType getComponentType() 
	{
		return ComponentType.GATE;
	}

	public void constructPortsAutomatically()
	{		
		Component startPointIn1 = new HingeComponent(canvas, componentRect.x - PORT_LENGTH - HingeComponent.HINGE_DIAMETER/2, componentRect.y + componentRect.height/4 - HingeComponent.HINGE_DIAMETER/2, false);
		Component endPointIn1   = new HingeComponent(canvas, componentRect.x - HingeComponent.HINGE_DIAMETER/2 - 1, componentRect.y + componentRect.height/4 - HingeComponent.HINGE_DIAMETER/2, false);
		Component in1Port       = new LineSegmentComponent(canvas, startPointIn1, endPointIn1, false);
		
		((HingeComponent)endPointIn1).addInternalHingeInfo(PortLocation.LEFT, "a");
		addInternalHorHinge(endPointIn1);
		addPort(in1Port);		
		canvas.addNewComponent(startPointIn1);
		canvas.addNewComponent(endPointIn1);
		canvas.addNewComponent(in1Port);
		
		Component startPointIn2 = new HingeComponent(canvas, componentRect.x - PORT_LENGTH - HingeComponent.HINGE_DIAMETER/2, componentRect.y + (componentRect.height * 3)/4 - HingeComponent.HINGE_DIAMETER/2, false);
		Component endPointIn2   = new HingeComponent(canvas, componentRect.x - HingeComponent.HINGE_DIAMETER/2 - 1, componentRect.y + (componentRect.height * 3)/4 - HingeComponent.HINGE_DIAMETER/2, false);
		Component in2Port       = new LineSegmentComponent(canvas, startPointIn2, endPointIn2, false);
		
		((HingeComponent)endPointIn2).addInternalHingeInfo(PortLocation.LEFT, "b");
		addInternalHorHinge(endPointIn2);
		addPort(in2Port);
		canvas.addNewComponent(startPointIn2);
		canvas.addNewComponent(endPointIn2);
		canvas.addNewComponent(in2Port);
		
		Component startPointOut = new HingeComponent(canvas, componentRect.x + componentRect.width - HingeComponent.HINGE_DIAMETER/2 + 1, componentRect.y + componentRect.height/2 - HingeComponent.HINGE_DIAMETER/2, false);
		Component endPointOut   = new HingeComponent(canvas, componentRect.x + componentRect.width + PORT_LENGTH - HingeComponent.HINGE_DIAMETER/2, componentRect.y + componentRect.height/2 - HingeComponent.HINGE_DIAMETER/2, false);
		Component outPort       = new LineSegmentComponent(canvas, startPointOut, endPointOut, false);
		
		((HingeComponent)startPointOut).addInternalHingeInfo(PortLocation.RIGHT, "out");
		addInternalHorHinge(startPointOut);
		addPort(outPort);
		canvas.addNewComponent(startPointOut);
		canvas.addNewComponent(endPointOut);
		canvas.addNewComponent(outPort);
	}
}

