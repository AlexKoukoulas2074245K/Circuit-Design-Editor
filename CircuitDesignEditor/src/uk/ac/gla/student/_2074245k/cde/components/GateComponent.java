package uk.ac.gla.student._2074245k.cde.components;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
		g.setColor(selected ? Colors.SELECTION_COLOR : customColor);
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
		
		g.setColor(selected || isInMultiSelectionMovement ? Colors.SELECTION_COLOR : customColor);				
		g.drawGate(gateType, getRectangle(), g.getCanvasContext().getColor());
	}
	
	@Override
	public void renderAligned(final GraphicsGenerator g)
	{
		render(g, false, false, false);
	}
		
	@Override
	public List<Component> getParents()
	{
		List<Component> parents = new ArrayList<Component>();
		Iterator<Component> compIter = canvas.getComponentsIterator(); 
		while (compIter.hasNext())
		{
			Component nextComp = compIter.next();
			if (nextComp.getComponentType() == ComponentType.WHITE_BOX)				
			{
				if (((WhiteBoxComponent)nextComp).isInnerComponent(this))
					parents.add(nextComp);
			}
		}
		
		return parents;	
	}
	
	@Override
	public List<Component> getChildren()
	{
		return new ArrayList<Component>(ports);
	}
	
	@Override
	public String serialize() 
	{		
		return gateType + "," + componentRect.x + "," + componentRect.y + "," + 
	           customColor.getRed() + "," + customColor.getGreen() + "," + customColor.getBlue() + "," + customColor.getAlpha();	
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
		
		addInternalHorHinge(endPointIn1);
		addPort(in1Port);		
		((HingeComponent)endPointIn1).addInternalHingeInfo(PortLocation.LEFT, "a");
		canvas.addComponentToCanvas(startPointIn1);
		canvas.addComponentToCanvas(endPointIn1);
		canvas.addComponentToCanvas(in1Port);
		
		Component startPointIn2 = new HingeComponent(canvas, componentRect.x - PORT_LENGTH - HingeComponent.HINGE_DIAMETER/2, componentRect.y + (componentRect.height * 3)/4 - HingeComponent.HINGE_DIAMETER/2, false);
		Component endPointIn2   = new HingeComponent(canvas, componentRect.x - HingeComponent.HINGE_DIAMETER/2 - 1, componentRect.y + (componentRect.height * 3)/4 - HingeComponent.HINGE_DIAMETER/2, false);
		Component in2Port       = new LineSegmentComponent(canvas, startPointIn2, endPointIn2, false);
		
		addInternalHorHinge(endPointIn2);
		addPort(in2Port);
		((HingeComponent)endPointIn2).addInternalHingeInfo(PortLocation.LEFT, "b");
		canvas.addComponentToCanvas(startPointIn2);
		canvas.addComponentToCanvas(endPointIn2);
		canvas.addComponentToCanvas(in2Port);
		
		Component startPointOut = new HingeComponent(canvas, componentRect.x + componentRect.width - HingeComponent.HINGE_DIAMETER/2 + 1, componentRect.y + componentRect.height/2 - HingeComponent.HINGE_DIAMETER/2, false);
		Component endPointOut   = new HingeComponent(canvas, componentRect.x + componentRect.width + PORT_LENGTH - HingeComponent.HINGE_DIAMETER/2, componentRect.y + componentRect.height/2 - HingeComponent.HINGE_DIAMETER/2, false);
		Component outPort       = new LineSegmentComponent(canvas, startPointOut, endPointOut, false);
		
		addInternalHorHinge(startPointOut);
		addPort(outPort);
		((HingeComponent)startPointOut).addInternalHingeInfo(PortLocation.RIGHT, "out");
		canvas.addComponentToCanvas(startPointOut);
		canvas.addComponentToCanvas(endPointOut);
		canvas.addComponentToCanvas(outPort);
	}
}

