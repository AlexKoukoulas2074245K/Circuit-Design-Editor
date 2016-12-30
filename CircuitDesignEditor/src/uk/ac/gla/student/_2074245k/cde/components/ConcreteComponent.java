package uk.ac.gla.student._2074245k.cde.components;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import uk.ac.gla.student._2074245k.cde.gui.MainCanvas;

public abstract class ConcreteComponent extends Component
{
	public static final int PORT_LENGTH = 50;
	public static MovementType globalConcreteComponentMovementType = MovementType.FREE;
	
	protected List<Component> internalHorHinges;
	protected List<Component> internalVerHinges;
	protected List<Component> ports;
	protected Rectangle componentRect;
	private int alignmentDx, alignmentDy;
	private int moveTargetX, moveTargetY;
	
	public ConcreteComponent(MainCanvas canvas, boolean movable) 
	{
		super(canvas, movable);
		
		this.internalHorHinges = new ArrayList<Component>();
		this.internalVerHinges = new ArrayList<Component>();
		this.ports             = new ArrayList<Component>();
	}

	@Override
	public AlignedComponentsList moveTo(int x, int y) 
	{	
		AlignedComponentsList alignedComponents = new AlignedComponentsList();
		alignmentDx = alignmentDy = 0;
		moveTargetX = x;
		moveTargetY = y;
		
		if (Component.globalAlignmentEnabled)
		{			
			Iterator<Component> compIter = canvas.getComponentsIterator();
			while (compIter.hasNext())
			{
				Component component = compIter.next();			
		
				if (component.getComponentType() != ComponentType.HINGE)
					continue;
				
				if (internalHorHinges.contains(component))
					continue;
				
				if (internalVerHinges.contains(component))
					continue;
				
				if (isPointOfPort(component))
					continue;
				
				if (((HingeComponent)component).isInternal())
					continue;
				
				for (Component p: ports)
				{
					LineSegmentComponent port = (LineSegmentComponent) p;
					if (!internalHorHinges.contains(port.getStartPoint()) && !internalVerHinges.contains(port.getStartPoint()))
					{
						if (Math.abs(component.getRectangle().y - port.getStartPoint().getRectangle().y) < ALIGNMENT_THRESHOLD)
						{
							alignedComponents.addVerAlignedComponent(component);
							alignedComponents.addVerAlignedComponent(port.getStartPoint());
							alignmentDy = component.getRectangle().y - port.getStartPoint().getRectangle().y; 
						}
						if (Math.abs(component.getRectangle().x - port.getStartPoint().getRectangle().x) < ALIGNMENT_THRESHOLD)
						{
							alignedComponents.addHorAlignedComponent(component);
							alignedComponents.addVerAlignedComponent(port.getStartPoint());
							alignmentDx = component.getRectangle().x - port.getStartPoint().getRectangle().x;
						}
					}
					if (!internalHorHinges.contains(port.getEndPoint()) && !internalVerHinges.contains(port.getEndPoint()))
					{
						if (Math.abs(component.getRectangle().y - port.getEndPoint().getRectangle().y) < ALIGNMENT_THRESHOLD)
						{
							alignedComponents.addVerAlignedComponent(component);
							alignedComponents.addVerAlignedComponent(port.getEndPoint());
							alignmentDy = component.getRectangle().y - port.getEndPoint().getRectangle().y; 
						}
						if (Math.abs(component.getRectangle().x - port.getEndPoint().getRectangle().x) < ALIGNMENT_THRESHOLD)
						{
							alignedComponents.addHorAlignedComponent(component);
							alignedComponents.addVerAlignedComponent(port.getEndPoint());
							alignmentDx = component.getRectangle().x - port.getEndPoint().getRectangle().x;
						}
					}					
				}
			}			
		}
		
		int prevBBx = this.componentRect.x;
		int prevBBy = this.componentRect.y;

		this.componentRect.x = moveTargetX;
		this.componentRect.y = moveTargetY;
		
		int dBBx = this.componentRect.x - prevBBx;
		int dBBy = this.componentRect.y - prevBBy;		
		
		if (globalConcreteComponentMovementType == MovementType.AXIS_RESTRICTED)
		{
			for (Component internalHorHinge: internalHorHinges)
			{	
				internalHorHinge.setPosition(internalHorHinge.getRectangle().x + dBBx, internalHorHinge.getRectangle().y);
			}
			for (Component internalVerHinge: internalVerHinges)
			{
				internalVerHinge.setPosition(internalVerHinge.getRectangle().x, internalVerHinge.getRectangle().y + dBBy);
			}
			
			for (Component ls: ports)
			{
				if (((LineSegmentComponent)ls).getOrientation() == Orientation.HORIZONTAL)
				{					
					ls.setPosition(ls.getRectangle().x, ls.getRectangle().y + dBBy);
				}
				else
				{
					ls.setPosition(ls.getRectangle().x + dBBx, ls.getRectangle().y);
				}
			}
		}
		else if (globalConcreteComponentMovementType == MovementType.FREE)
		{
			for (Component ls: ports)
			{
				ls.setPosition(ls.getRectangle().x + dBBx, ls.getRectangle().y + dBBy);
			}
		}
		
		return alignedComponents;
	}
	
	@Override
	public void finalizeMovement(AlignedComponentsList alignedComponents) 
	{		
		int prevBBx = this.componentRect.x;
		int prevBBy = this.componentRect.y;
		
		if (alignedComponents != null && alignedComponents.hasHorAlignedComponents() && getRectangle().contains(MainCanvas.mouse.getX(), MainCanvas.mouse.getY()))
		{						
			this.componentRect.x += alignmentDx;
		}
		else
		{			
			this.componentRect.x = moveTargetX;
		}
		
		if (alignedComponents != null && alignedComponents.hasVerAlignedComponents() && getRectangle().contains(MainCanvas.mouse.getX(), MainCanvas.mouse.getY()))
		{						
			this.componentRect.y += alignmentDy;			
		}
		else
		{			
			this.componentRect.y = moveTargetY;
		}
		
		int dBBx = this.componentRect.x - prevBBx;
		int dBBy = this.componentRect.y - prevBBy;		
		
		if (globalConcreteComponentMovementType == MovementType.AXIS_RESTRICTED)
		{
			for (Component internalHorHinge: internalHorHinges)
			{	
				internalHorHinge.setPosition(internalHorHinge.getRectangle().x + dBBx, internalHorHinge.getRectangle().y);
			}
			for (Component internalVerHinge: internalVerHinges)
			{
				internalVerHinge.setPosition(internalVerHinge.getRectangle().x, internalVerHinge.getRectangle().y + dBBy);
			}
			
			for (Component ls: ports)
			{
				if (((LineSegmentComponent)ls).getOrientation() == Orientation.HORIZONTAL)
				{					
					ls.setPosition(ls.getRectangle().x, ls.getRectangle().y + dBBy);
				}
				else
				{
					ls.setPosition(ls.getRectangle().x + dBBx, ls.getRectangle().y);
				}
			}
		}
		else if (globalConcreteComponentMovementType == MovementType.FREE)
		{
			for (Component ls: ports)
			{
				ls.setPosition(ls.getRectangle().x + dBBx, ls.getRectangle().y + dBBy);
			}
		}
		
		moveTargetX = componentRect.x;
		moveTargetY = componentRect.y;
		alignmentDx = alignmentDy = 0;		
	}

	@Override
	public boolean mouseIntersection(final int mouseX, final int mouseY) 
	{
		return getRectangle().contains(mouseX, mouseY);		
	}
	
	@Override
	public void delete() 
	{
		canvas.removeComponent(this);
		
		Iterator<Component> portsIter = getPortsIterator();
		while (portsIter.hasNext())
		{
			Component port = portsIter.next();
			port.delete();
			portsIter.remove();
		}
		
		Iterator<Component> internalHorHingeIter = getInternalHorHingeIterator();
		while (internalHorHingeIter.hasNext())
		{	
			Component internalHorHinge = internalHorHingeIter.next();
			internalHorHinge.delete();
			internalHorHingeIter.remove();
		}
		
		Iterator<Component> internalVerHingeIter = getInternalVerHingeIterator();
		while (internalVerHingeIter.hasNext())
		{	
			Component internalVerHinge = internalVerHingeIter.next();
			internalVerHinge.delete();
			internalVerHingeIter.remove();
		}	
	}
	
	@Override
	public Rectangle getRectangle() 
	{	
		return componentRect;
	}

	@Override
	public void setPosition(int x, int y) 
	{
		componentRect.x = x;
		componentRect.y = y;
	}
	
	public boolean hasPort(final Component component) { return ports.contains(component); }
	public boolean hasInternalHorHinge(final Component component) { return internalHorHinges.contains(component); }
	public boolean hasInternalVerHinge(final Component component) { return internalVerHinges.contains(component); }
	
	public void addPort(final Component port) { ports.add(port); }	
	public void addInternalHorHinge(final Component component) { internalHorHinges.add(component); }	
	public void addInternalVerHinge(final Component component) { internalVerHinges.add(component); }
	
	public void removePort(final Component port) { ports.remove(port); }
	public void removeHorHinge(final Component hinge) { internalHorHinges.remove(hinge); }
	public void removeVerHinge(final Component hinge) { internalVerHinges.remove(hinge); }
	
	public Iterator<Component> getPortsIterator() { return ports.iterator(); }
	public Iterator<Component> getInternalHorHingeIterator() { return internalHorHinges.iterator(); }
	public Iterator<Component> getInternalVerHingeIterator() { return internalVerHinges.iterator(); } 
	
	public int indexOfPort(final Component port) { return ports.indexOf(port); }
	public int indexOfInternalHorHinge(final Component component) { return internalHorHinges.indexOf(component); }
	public int indexOfInternalVerHinge(final Component component) { return internalVerHinges.indexOf(component); }
	
	public void setPort(final int index, final Component port) { ports.set(index, port); }
	public void setInternalHorHinge(final int index, final Component comp) { internalHorHinges.set(index, comp); }
	public void setInternalVerHinge(final int index, final Component comp) { internalVerHinges.set(index, comp); }
	
	public int getNPorts() { return ports.size(); }
	public int getNInternalHorHinges() { return internalHorHinges.size(); }
	public int getNInternalVerHinges() { return internalVerHinges.size(); }
	
	private boolean isPointOfPort(final Component component)
	{
		for (Component port: ports)
		{
			if (((LineSegmentComponent)port).getStartPoint() == component ||
			    ((LineSegmentComponent)port).getEndPoint() == component)						
				return true;
		}
		
		return false;
	}
}
