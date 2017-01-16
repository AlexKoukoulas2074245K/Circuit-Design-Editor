package uk.ac.gla.student._2074245k.cde.components;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
	private Rectangle lineRect;
	
	
	public LineSegmentComponent(final MainCanvas canvas, final Component startPoint, final Component endPoint, final boolean movable)
	{
		super(canvas, startPoint.isMovable && endPoint.isMovable);
		
		this.startPoint = startPoint;
		this.endPoint   = endPoint;		
		
		lineRect = new Rectangle();
		lineRect.x = (int)startPoint.getRectangle().getCenterX();
		lineRect.y = (int)startPoint.getRectangle().getCenterY();
	    lineRect.width = (int)endPoint.getRectangle().getCenterX() - (int)startPoint.getRectangle().getCenterX();
        lineRect.height = (int)endPoint.getRectangle().getCenterY() - (int) startPoint.getRectangle().getCenterY();		
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
			g.setColor(Colors.SELECTION_COLOR);						
		}
		else
		{
			g.setColor(customColor);
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
	public void delete()
	{
		if (getParents().size() > 0 && !allParentsAreWhiteBoxes())
		{	
			return;
		}
		
		canvas.removeComponentFromCanvas(this);			
		startPoint.delete();
		endPoint.delete();
		
		for (Component comp: getParents())
		{
			comp.removeChild(this);
		}
	}
	
	@Override
	public List<Component> getParents()
	{
		List<Component> parents = new ArrayList<Component>();
		Iterator<Component> compIter = canvas.getComponentsIterator(); 
		while (compIter.hasNext())
		{
			Component comp = compIter.next();
			if (comp.getChildren().contains(this))
			{
				parents.add(comp);
			}
		}
		
		return parents;
	}
	
	@Override
	public List<Component> getChildren()
	{
		List<Component> children = new ArrayList<Component>();
		children.add(startPoint);
		children.add(endPoint);
		return children;
	}
	
	@Override
	public void removeChild(final Component component)
	{
		
	}
	
	@Override
	public String serialize()
	{
		return "" + isMovable + "," + customColor.getRed() + "," + customColor.getGreen() + "," + customColor.getBlue() + "," + customColor.getAlpha();
	}
	
	@Override
	public Rectangle getRectangle()
	{		
		lineRect.x = (int)startPoint.getRectangle().getCenterX();
		lineRect.y = (int)startPoint.getRectangle().getCenterY();
	    lineRect.width = (int)endPoint.getRectangle().getCenterX() - (int)startPoint.getRectangle().getCenterX();
        lineRect.height = (int)endPoint.getRectangle().getCenterY() - (int) startPoint.getRectangle().getCenterY();
                                 
		return lineRect;
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
	
	private boolean allParentsAreWhiteBoxes()
	{
		for (Component comp: getParents())
		{
			if (comp.getComponentType() != ComponentType.WHITE_BOX)
			{
				return false;
			}
		}
		
		return true;
	}
}
