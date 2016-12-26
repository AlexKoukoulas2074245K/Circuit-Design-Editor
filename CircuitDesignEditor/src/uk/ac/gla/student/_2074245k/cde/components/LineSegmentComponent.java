package uk.ac.gla.student._2074245k.cde.components;

import java.awt.Rectangle;
import java.util.Iterator;

import uk.ac.gla.student._2074245k.cde.gui.Colors;
import uk.ac.gla.student._2074245k.cde.gui.MainCanvas;
import uk.ac.gla.student._2074245k.cde.gui.Strokes;
import uk.ac.gla.student._2074245k.cde.util.GraphicsGenerator;

public final class LineSegmentComponent extends Component
{	
	public static MovementType globalLineSegmentMovementType = MovementType.FREE;
	public static boolean globalPathVisibility = true;
	
	private static final double MOUSE_INTERSECTION_THRESHOLD = -1.0f; 
		
	private Component startPoint, endPoint;	
	
	public LineSegmentComponent(final MainCanvas canvas, final int x, final int y, final int x2, final int y2, final boolean movable)
	{
		super(canvas, movable);
		
		startPoint  = new HingeComponent(canvas, x  - HingeComponent.HINGE_DIAMETER/2, y  - HingeComponent.HINGE_DIAMETER/2, movable);
		endPoint    = new HingeComponent(canvas, x2 - HingeComponent.HINGE_DIAMETER/2, y2 - HingeComponent.HINGE_DIAMETER/2, movable);		
		
		canvas.addNewComponent(startPoint);
		canvas.addNewComponent(endPoint);
	}
	
	public LineSegmentComponent(final MainCanvas canvas, final Component startPoint, final int x2, final int y2, final boolean movable)
	{
		super(canvas, movable);
		
		this.startPoint = startPoint;
		this.endPoint   = new HingeComponent(canvas, x2 - HingeComponent.HINGE_DIAMETER/2, y2 - HingeComponent.HINGE_DIAMETER/2, movable);
		
		canvas.addNewComponent(endPoint);
	}
	
	public LineSegmentComponent(final MainCanvas canvas, final int x, final int y, final Component endPoint, final boolean movable)
	{
		super(canvas, movable);
		this.startPoint = new HingeComponent(canvas, x  - HingeComponent.HINGE_DIAMETER/2, y  - HingeComponent.HINGE_DIAMETER/2, movable);		
		this.endPoint   = endPoint;
		
		canvas.addNewComponent(startPoint);		
	}
	
	public LineSegmentComponent(final MainCanvas canvas, final Component startPoint, final Component endPoint, final boolean movable)
	{
		super(canvas, startPoint.isMovable && endPoint.isMovable);
		
		this.startPoint = startPoint;
		this.endPoint   = endPoint;		
	}
	
	@Override
	public AlignedComponentsList moveTo(final int x, final int y) 
	{		
		AlignedComponentsList alignedComponents = new AlignedComponentsList();
		
		int targetX = getRectangle().x;
		int targetY = getRectangle().y;
		
		
		boolean canMoveHorizontally = isMovable && (startPoint.isMovable() || endPoint.isMovable()) && (getOrientation() == Orientation.VERTICAL || globalLineSegmentMovementType == MovementType.FREE);
		boolean canMoveVertically   = isMovable && (startPoint.isMovable() || endPoint.isMovable()) && (getOrientation() == Orientation.HORIZONTAL || globalLineSegmentMovementType == MovementType.FREE);		
		if (canMoveHorizontally)
		{
			targetX = x;
			if (globalAlignmentEnabled)
			{
				Iterator<Component> compIter = canvas.getComponentsIterator();
				while (compIter.hasNext())
				{
					Component component = compIter.next();
					if (component.getComponentType() != ComponentType.HINGE ||
						component == startPoint || 
						component == endPoint)
					{
						continue;					
					}
						
					if (Math.abs(component.getRectangle().x - startPoint.getRectangle().x) < ALIGNMENT_THRESHOLD)
					{
						alignedComponents.addHorAlignedComponentOrReplaceFirst(component);
						break;
					}														
				}						
			}																	
		}
				
		
		if (canMoveVertically)
		{
			targetY = y;
			if (globalAlignmentEnabled)
			{
				Iterator<Component> compIter = canvas.getComponentsIterator();
				while (compIter.hasNext())
				{
					Component component = compIter.next();
				
					if (component.getComponentType() != ComponentType.HINGE ||
					    component == startPoint || 
					    component == endPoint)
					{
						
						continue;					
					}
					
					if (Math.abs(component.getRectangle().y - startPoint.getRectangle().y) < ALIGNMENT_THRESHOLD)
					{
						alignedComponents.addVerAlignedComponentOrReplaceFirst(component);
						break;
					}														
				}						
			}									
		}
		
		setPosition(targetX, targetY);
		
		return alignedComponents;	
	}
	
	@Override
	public void finalizeMovement(final AlignedComponentsList alignedComponents)
	{		
		int finalX = getRectangle().x;
		int finalY = getRectangle().y;
		
		if (alignedComponents != null)
		{
			if (alignedComponents.hasHorAlignedComponents())
			{
				finalX = (int)alignedComponents.getHorAlignedComponents().get(0).getRectangle().getCenterX();				
			}
			if (alignedComponents.hasVerAlignedComponents())
			{
				finalY = (int)alignedComponents.getVerAlignedComponents().get(0).getRectangle().getCenterY();
			}
		}
		
		setPosition(finalX, finalY);
	}
	
	@Override
	public boolean mouseIntersection(final int mouseX, final int mouseY)
	{
		double lineLength = Math.hypot(endPoint.getRectangle().getCenterX() - startPoint.getRectangle().getCenterX(),
				                       endPoint.getRectangle().getCenterY() - startPoint.getRectangle().getCenterY());			
		double startToMouseDistance = Math.hypot(startPoint.getRectangle().getCenterX() - mouseX, startPoint.getRectangle().getCenterY() - mouseY);			
		double mouseToEndDistance = Math.hypot(mouseX - endPoint.getRectangle().getCenterX(), mouseY - endPoint.getRectangle().getCenterY());
		
		return lineLength - startToMouseDistance - mouseToEndDistance >= MOUSE_INTERSECTION_THRESHOLD;
	}
	
	@Override
	public void delete()
	{
		if (isPort(canvas.getComponentsIterator()) != null)
		{				
			return;
		}
			
		canvas.removeComponent(this);			
		startPoint.delete();
		endPoint.delete();
	}		
	
	public Component isPort(final Iterator<Component> compIter)
	{
		while (compIter.hasNext())
		{
			Component nextComp = compIter.next();
			if (nextComp.getComponentType() == ComponentType.BLACK_BOX ||
				nextComp.getComponentType() == ComponentType.GATE)
			{
				if (((ConcreteComponent)nextComp).hasPort(this))
					return nextComp;
			}
		}
		
		return null;
	}
	
	@Override
	public void render(final GraphicsGenerator g, 
			           final boolean isHighlighted, 
			           final boolean isSelected, 
			           final boolean isInMultiSelectionMovement) 
	{
		if (isSelected || isHighlighted)
		{
			g.setStroke(Strokes.BOLD_STROKE);
		}
		else
		{
			g.setStroke(Strokes.THIN_STROKE);
		}
		
		if (isSelected)
		{
			if (isMovable || isInMultiSelectionMovement)
			{
				g.setColor(Colors.SELECTION_COLOR);			
			}
			else
			{
				if ((startPoint.isMovable && !endPoint.isMovable) ||
					(!startPoint.isMovable && endPoint.isMovable))
					g.setColor(Colors.EFFECTIVE_IMMOVABLE_COLOR);
				else
					g.setColor(Colors.IMMOVABLE_COLOR);
			}
		}
		else
		{
			g.setColor(Colors.DEFAULT_COLOR);
		}
		
		g.drawLine(getRectangle().x, 
				   getRectangle().y, 
				   getRectangle().x + getRectangle().width,
				   getRectangle().y + getRectangle().height); 	
		
		if (isSelected && HingeComponent.globalHingeVisibility)
		{			
			startPoint.render(g, true, false, false);					
			endPoint.render(g, true, false, false);
		}
	}
	
	@Override
	public void renderAligned(final GraphicsGenerator g)
	{
		render(g, false, false, false);
	}
	
	@Override
	public String serialize()
	{
		return "" + isMovable;
	}
	
	@Override
	public Rectangle getRectangle()
	{
		return new Rectangle((int)startPoint.getRectangle().getCenterX(),
				             (int)startPoint.getRectangle().getCenterY(),
				             (int)endPoint.getRectangle().getCenterX() - (int)startPoint.getRectangle().getCenterX(), 
				             (int)endPoint.getRectangle().getCenterY() - (int) startPoint.getRectangle().getCenterY()); 
	}
	
	@Override
	public void setPosition(final int x, final int y)
	{
		int targetDeltaX = x - getRectangle().x;
		int targetDeltaY = y - getRectangle().y;
			
		startPoint.setPosition(startPoint.getRectangle().x + targetDeltaX, startPoint.getRectangle().y + targetDeltaY);
		endPoint.setPosition(endPoint.getRectangle().x + targetDeltaX, endPoint.getRectangle().y + targetDeltaY);
	}
	
	@Override
	public ComponentType getComponentType()
	{
		return ComponentType.LINE_SEGMENT;
	}
	
	public void renderAsPathEdge(final GraphicsGenerator g)
	{
		if (!globalPathVisibility)
		{
			return;
		}
		
		g.setStroke(Strokes.BOLD_STROKE);
		
		if (startPoint.isMovable && endPoint.isMovable)
			g.setColor(Colors.SELECTION_COLOR);
		else if (startPoint.isMovable && !endPoint.isMovable)
			g.setColor(Colors.EFFECTIVE_IMMOVABLE_COLOR);
		else if (!startPoint.isMovable && endPoint.isMovable)
			g.setColor(Colors.EFFECTIVE_IMMOVABLE_COLOR);
		else
			g.setColor(Colors.IMMOVABLE_COLOR);
		
		g.drawLine(getRectangle().x, 
				   getRectangle().y, 
				   getRectangle().x + getRectangle().width,
				   getRectangle().y + getRectangle().height); 	
		
		if (HingeComponent.globalHingeVisibility)
		{	
			startPoint.render(g, true, false, false);					
			endPoint.render(g, true, false, false);			
		}		
	}
	
	public Component getStartPoint()
	{
		return startPoint;
	}
	
	public Component getEndPoint()
	{
		return endPoint;
	}
	
	public void setStartPoint(final Component component)
	{
		this.startPoint = component;
	}
	
	public void setEndPoint(final Component component)
	{
		this.endPoint = component;
	}
	
	public Orientation getOrientation()
	{
		Rectangle rect = getRectangle();
		if (Math.abs(rect.x - (rect.x + rect.width)) < 
			Math.abs(rect.y - (rect.y + rect.height)))
		{
			return Orientation.VERTICAL;		
		}
		else
		{
			return Orientation.HORIZONTAL;		
		}
	}
}
