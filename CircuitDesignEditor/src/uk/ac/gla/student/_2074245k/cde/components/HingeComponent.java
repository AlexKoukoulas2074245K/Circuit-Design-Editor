package uk.ac.gla.student._2074245k.cde.components;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import uk.ac.gla.student._2074245k.cde.gui.Colors;
import uk.ac.gla.student._2074245k.cde.gui.MainCanvas;
import uk.ac.gla.student._2074245k.cde.gui.PortView;
import uk.ac.gla.student._2074245k.cde.gui.Strokes;
import uk.ac.gla.student._2074245k.cde.util.GraphicsGenerator;
import uk.ac.gla.student._2074245k.cde.util.Mouse;

public final class HingeComponent extends Component
{	
	public static HingeDraggingMode globalHingeDraggingMode = HingeDraggingMode.MOVE_UNRES;
	public static boolean globalHingeVisibility = true;
	public static boolean arrowVisibility = true;
	
	public static final int HINGE_DIAMETER = 18;
	public static final int NUB_DIAMETER  = 20;
	
	private Rectangle rect;
	private int nameX, nameY;
	private boolean hasNub;
	private boolean shouldFinalizeMovement;
	private boolean isMousePerformingHorizontalMotion;
	private boolean isInternalHinge;
	private boolean isInternalHingeOfWhiteBox;
	private boolean isInverted;
	private String name;
	private PortView.PortResultDirectionality internalHingeResultDir; 
	private PortView.PortLocation internalHingeLocation;
	
	public HingeComponent(final MainCanvas canvas, final int x, final int y, final boolean movable)
	{
		super(canvas, movable);
		rect = new Rectangle(x, y, HINGE_DIAMETER, HINGE_DIAMETER);		
		shouldFinalizeMovement = false;
		isMousePerformingHorizontalMotion = true;
		isInternalHinge = isInternalHingeOfWhiteBox = false;
		isInverted = false;
		name = " ";
		internalHingeLocation = PortView.PortLocation.BOTTOM;
		internalHingeResultDir = PortView.PortResultDirectionality.NEUTRAL;
	}
	
	@Override
	public AlignedComponentsList moveTo(final int targetX, final int targetY) 
	{		
		isMousePerformingHorizontalMotion = MainCanvas.mouse.isPerformingHorMotion();
		shouldFinalizeMovement = true;
		switch (globalHingeDraggingMode)
		{
			case MOVE_UNRES: case MOVE_AXIS_RES: return moveHinge(targetX, targetY);					
			case SPAWN_SEGMENT_UNRES: case SPAWN_SEGMENT_AXIS_RES: return beginSpawnLineSegment();
		}
		
		return null;
	}
	
	@Override
	public void finalizeMovement(final AlignedComponentsList alignedComponents)
	{		
		if (!shouldFinalizeMovement)
			return;
		else
			shouldFinalizeMovement = false;
		
		switch (globalHingeDraggingMode)
		{
			case MOVE_UNRES: case MOVE_AXIS_RES: finalizeHingeMovement(alignedComponents); break;
			case SPAWN_SEGMENT_UNRES: case SPAWN_SEGMENT_AXIS_RES: finalizeSpawnLineSegment(alignedComponents); break;
		}
	}
	
	@Override
	public boolean mouseIntersection(final int mouseX, final int mouseY) 
	{		
		if (isInternalHinge && !isInternalHingeOfWhiteBox)
			return false;
		return Math.hypot(mouseX - rect.x - HINGE_DIAMETER/2, mouseY - rect.y - HINGE_DIAMETER/2) <= HINGE_DIAMETER/2;
	}
	
	@Override
	public void render(final GraphicsGenerator g,
			           final boolean isHighlighted, 
			           final boolean isSelected,
			           final boolean isInMultiSelection) 
	{			
		if (isInternalHinge)		
		{
			if (isInverted)
			{
				int xOffset = 0;
				int yOffset = 0;
				
				switch (internalHingeLocation)
				{
					case LEFT:   xOffset = -HINGE_DIAMETER/2; break;
					case RIGHT:  xOffset = +HINGE_DIAMETER/2; break;
					case TOP:    yOffset = -HINGE_DIAMETER/2; break;
					case BOTTOM: yOffset = +HINGE_DIAMETER/2; break;
				}
				g.setColor(Color.white);
				g.setStroke(Strokes.THIN_STROKE);				
				g.fillRect(rect.x + xOffset + 1, rect.y + yOffset + 1, HINGE_DIAMETER - 2, HINGE_DIAMETER - 2);							
				g.setColor(isSelected || isInMultiSelection ? Colors.SELECTION_COLOR : customColor);				
				g.drawOval(rect.x + xOffset, rect.y + yOffset, HINGE_DIAMETER, HINGE_DIAMETER);
			}			
			return;
		}
			
		// Render input/output arrows 
		if (internalHingeResultDir != PortView.PortResultDirectionality.NEUTRAL && arrowVisibility)
		{			
			g.setColor(isSelected || isInMultiSelection ? Colors.SELECTION_COLOR : customColor);
			g.setStroke(isSelected || isHighlighted || isInMultiSelection ? Strokes.BOLD_STROKE : Strokes.THIN_STROKE);
			final int x0 = rect.x + HINGE_DIAMETER/2;
			final int y0 = rect.y + HINGE_DIAMETER/2;
			int x1 = 0;
			int y1 = 0;
			int x2 = 0;
			int y2 = 0;
			switch (internalHingeLocation)
			{
				case LEFT: 
				{
					if (internalHingeResultDir == PortView.PortResultDirectionality.INPUT)
					{					
						x1 = rect.x + HINGE_DIAMETER/2 - 2 * PortView.ARROW_HALF_LEN;
						y1 = rect.y + HINGE_DIAMETER/2 - 2 * PortView.ARROW_HALF_LEN;
						
						x2 = rect.x + HINGE_DIAMETER/2 - 2 * PortView.ARROW_HALF_LEN;
						y2 = rect.y + HINGE_DIAMETER/2 + 2 * PortView.ARROW_HALF_LEN;
					}
					else
					{						
						x1 = rect.x + HINGE_DIAMETER/2 + 2 * PortView.ARROW_HALF_LEN;
						y1 = rect.y + HINGE_DIAMETER/2 - 2 * PortView.ARROW_HALF_LEN;
						
						x2 = rect.x + HINGE_DIAMETER/2 + 2 * PortView.ARROW_HALF_LEN;
						y2 = rect.y + HINGE_DIAMETER/2 + 2 * PortView.ARROW_HALF_LEN;
					}
				} break;
				
				case RIGHT:
				{
					if (internalHingeResultDir == PortView.PortResultDirectionality.INPUT)
					{					
						x1 = rect.x + HINGE_DIAMETER/2 + 2 * PortView.ARROW_HALF_LEN;
						y1 = rect.y + HINGE_DIAMETER/2 - 2 * PortView.ARROW_HALF_LEN;
						
						x2 = rect.x + HINGE_DIAMETER/2 + 2 * PortView.ARROW_HALF_LEN;
						y2 = rect.y + HINGE_DIAMETER/2 + 2 * PortView.ARROW_HALF_LEN;
					}
					else
					{						
						x1 = rect.x + HINGE_DIAMETER/2 - 2 * PortView.ARROW_HALF_LEN;
						y1 = rect.y + HINGE_DIAMETER/2 - 2 * PortView.ARROW_HALF_LEN;
						
						x2 = rect.x + HINGE_DIAMETER/2 - 2 * PortView.ARROW_HALF_LEN;
						y2 = rect.y + HINGE_DIAMETER/2 + 2 * PortView.ARROW_HALF_LEN;
					}
				} break;
				
				case TOP: 
				{
					if (internalHingeResultDir == PortView.PortResultDirectionality.INPUT)
					{					
						x1 = rect.x + HINGE_DIAMETER/2 - 2 * PortView.ARROW_HALF_LEN;
						y1 = rect.y + HINGE_DIAMETER/2 - 2 * PortView.ARROW_HALF_LEN;
						
						x2 = rect.x + HINGE_DIAMETER/2 + 2 * PortView.ARROW_HALF_LEN;
						y2 = rect.y + HINGE_DIAMETER/2 - 2 * PortView.ARROW_HALF_LEN;
					}
					else
					{						
						x1 = rect.x + HINGE_DIAMETER/2 - 2 * PortView.ARROW_HALF_LEN;
						y1 = rect.y + HINGE_DIAMETER/2 + 2 * PortView.ARROW_HALF_LEN;
						
						x2 = rect.x + HINGE_DIAMETER/2 + 2 * PortView.ARROW_HALF_LEN;
						y2 = rect.y + HINGE_DIAMETER/2 + 2 * PortView.ARROW_HALF_LEN;
					}
				} break;
				
				case BOTTOM: 
				{
					if (internalHingeResultDir == PortView.PortResultDirectionality.INPUT)
					{					
						x1 = rect.x + HINGE_DIAMETER/2 - 2 * PortView.ARROW_HALF_LEN;
						y1 = rect.y + HINGE_DIAMETER/2 + 2 * PortView.ARROW_HALF_LEN;
						
						x2 = rect.x + HINGE_DIAMETER/2 + 2 * PortView.ARROW_HALF_LEN;
						y2 = rect.y + HINGE_DIAMETER/2 + 2 * PortView.ARROW_HALF_LEN;
					}
					else
					{						
						x1 = rect.x + HINGE_DIAMETER/2 - 2 * PortView.ARROW_HALF_LEN;
						y1 = rect.y + HINGE_DIAMETER/2 - 2 * PortView.ARROW_HALF_LEN;
						
						x2 = rect.x + HINGE_DIAMETER/2 + 2 * PortView.ARROW_HALF_LEN;
						y2 = rect.y + HINGE_DIAMETER/2 - 2 * PortView.ARROW_HALF_LEN;
					}
				} break;
			}
			
			g.drawLine(x0, y0, x1, y1);
			g.drawLine(x0, y0, x2, y2);
		}
					
		if ((globalHingeDraggingMode == HingeDraggingMode.SPAWN_SEGMENT_UNRES ||
			 globalHingeDraggingMode == HingeDraggingMode.SPAWN_SEGMENT_AXIS_RES) &&
			 isSelected && 
			 MainCanvas.mouse.isButtonDown(Mouse.LEFT_BUTTON) &&
			 !canvas.hasMultiSelection())	
		{
			g.setStroke(Strokes.BOLD_STROKE);
			g.setColor(Colors.SELECTION_COLOR);
			
			int endX = 0;
			int endY = 0;
			
			if ((globalHingeDraggingMode == HingeDraggingMode.SPAWN_SEGMENT_AXIS_RES && isMousePerformingHorizontalMotion) || 
				 globalHingeDraggingMode == HingeDraggingMode.SPAWN_SEGMENT_UNRES)
				endX = MainCanvas.mouse.getX();
			else
				endX = (int)this.getRectangle().getCenterX();
			
			if ((globalHingeDraggingMode == HingeDraggingMode.SPAWN_SEGMENT_AXIS_RES && !isMousePerformingHorizontalMotion) || 
			     globalHingeDraggingMode == HingeDraggingMode.SPAWN_SEGMENT_UNRES)
				endY = MainCanvas.mouse.getY();
			else
				endY = (int)this.getRectangle().getCenterY();
			
			g.drawLine((int)this.getRectangle().getCenterX(), (int)this.getRectangle().getCenterY(), endX, endY);					           
		}				
		
		if (globalHingeVisibility)
		{			
			if (isSelected)
			{
				g.setColor(Colors.SELECTION_COLOR);				
				g.setStroke(Strokes.BOLD_STROKE);
				drawCross(g);
			}
			else if (isHighlighted)
			{						
				g.setColor(customColor);				
				g.setStroke(Strokes.BOLD_STROKE);
				drawCross(g);
			}		
		}
		
		if (hasNub)
		{		
			if (isSelected)
			{
				g.setColor(Colors.SELECTION_COLOR);					
			}
			else
			{
				g.setColor(customColor);								
			}
						
			g.fillOval((int)getRectangle().getCenterX() - NUB_DIAMETER/2, (int)getRectangle().getCenterY() - NUB_DIAMETER/2, NUB_DIAMETER, NUB_DIAMETER);
		}
	}
	
	@Override
	public void renderAligned(final GraphicsGenerator g)
	{
		if (isInternalHinge && !isInternalHingeOfWhiteBox)
			return;
		g.setColor(customColor);		
		g.setStroke(Strokes.BOLD_STROKE);
		drawCross(g);
	}
	
	@Override
	public void delete() 
	{		
		if (getParents().size() >= 1)
			return;
		
		canvas.removeComponentFromCanvas(this);
		
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
			
			if (comp.getComponentType() != ComponentType.LINE_SEGMENT)
			{
				continue;
			}
						
			if (comp.getChildren().get(0) == this || comp.getChildren().get(1) == this)
				parents.add(comp);
		}
		
		return parents;
	}
	
	@Override
	public List<Component> getChildren()
	{
		return new ArrayList<Component>();
	}
	
	@Override
	public void removeChild(final Component component)
	{
		
	}
	
	@Override
	public String serialize() 
	{			
		return rect.x + "," + 
	           rect.y + "," + 
			   customColor.getRed() + "," + 
	           customColor.getGreen() + "," + 
			   customColor.getBlue() + "," + 
	           customColor.getAlpha() + "," +
			   hasNub + "," + 
	           isMovable + "," + 
			   isInternalHinge + "," + 
	           isInverted + "," + 
			   internalHingeLocation + "," + 
	           internalHingeResultDir + "," + 
	           (name.length() == 0 ? "@" : name.replace(" ", "@"));
	}

	@Override
	public Rectangle getRectangle() 
	{	
		return rect;
	}
	
	@Override
	public void setPosition(final int x, final int y)
	{
		rect.x = x;
		rect.y = y;
	}
			
	@Override
	public ComponentType getComponentType()
	{
		return ComponentType.HINGE;
	}
	
	public boolean isInternal()
	{
		return isInternalHinge;
	}
	public String getName()
	{
		return name;
	}
	
	public int getNameX()
	{
		return nameX;
	}
	
	public int getNameY()
	{
		return nameY;
	}
	
	public boolean hasNub()
	{
		return hasNub;
	}
		
	public void setHasNub(final boolean hasNub)
	{
		this.hasNub = hasNub;
	}
	
	public void setInternalHingeInfo(final PortView.PortLocation location, final String portName)
	{	
		this.internalHingeLocation = location;		
		this.name = portName;				
		this.isInternalHinge = true;
	}	
	
	public PortView.PortLocation getParentsPortLocation()
	{
		return internalHingeLocation;
	}
	
	public PortView.PortResultDirectionality getPortResultDir()
	{
		return internalHingeResultDir;
	}
	
	public void setExternalHingeInfo(final PortView.PortLocation location, final PortView.PortResultDirectionality resultDir)
	{
		this.internalHingeLocation = location;
		this.internalHingeResultDir = resultDir;
	}
		
	public void setIsInverted(final boolean isInverted)
	{
		this.isInverted = isInverted;
	}
	
	public void setIsInternalOfWhiteBox(final boolean isInternalOfWhiteBox)
	{
		this.isInternalHingeOfWhiteBox = isInternalOfWhiteBox;
	}
	
	private AlignedComponentsList moveHinge(final int x, final int y)
	{
		AlignedComponentsList alignedComponents = new AlignedComponentsList();
		
		if (!isMovable)
			return alignedComponents;
		
		int targetX = x;
		int targetY = y;
		
		if (isMousePerformingHorizontalMotion && globalHingeDraggingMode == HingeDraggingMode.MOVE_AXIS_RES)
			targetY = rect.y;
		if (!isMousePerformingHorizontalMotion && globalHingeDraggingMode == HingeDraggingMode.MOVE_AXIS_RES)
			targetX = rect.x;
		
		rect.x = targetX;
		rect.y = targetY;
		
		
		if (globalAlignmentEnabled)
		{			
			Rectangle hingeMouseDistanceRect = new Rectangle(rect.x - HINGE_DIAMETER, rect.y - HINGE_DIAMETER, HINGE_DIAMETER * 2, HINGE_DIAMETER * 2);			
			if (hingeMouseDistanceRect.contains(x, y))
			{	
				Iterator<Component> compIter = canvas.getComponentsIterator();
				while (compIter.hasNext())
				{
					Component component = compIter.next();			
				
					if (component == this || component.getComponentType() != ComponentType.HINGE)
						continue;
					
					if (Math.abs(component.getRectangle().x - rect.x) < ALIGNMENT_THRESHOLD)
						alignedComponents.addHorAlignedComponentOrReplaceFirst(component);
					if (Math.abs(component.getRectangle().y - rect.y) < ALIGNMENT_THRESHOLD)
						alignedComponents.addVerAlignedComponentOrReplaceFirst(component);
				}
			}
		}
		
		return alignedComponents;
	}
	
	private AlignedComponentsList beginSpawnLineSegment()
	{		
		AlignedComponentsList alignedComponents = new AlignedComponentsList();
		
		boolean shouldAddHorizontallyAlignedComponents = isMousePerformingHorizontalMotion || globalHingeDraggingMode == HingeDraggingMode.SPAWN_SEGMENT_UNRES;
		boolean shouldAddVerticallyAlignedComponents = !isMousePerformingHorizontalMotion || globalHingeDraggingMode == HingeDraggingMode.SPAWN_SEGMENT_UNRES;
	
		Iterator<Component> compIter = canvas.getComponentsIterator();
		while (compIter.hasNext())
		{
			Component component = compIter.next();			

			if (component.getComponentType() != ComponentType.HINGE)
				continue;
					
			if (shouldAddHorizontallyAlignedComponents && Math.abs(component.getRectangle().x - MainCanvas.mouse.getX()) < 10 && Component.globalAlignmentEnabled)
				alignedComponents.addHorAlignedComponentOrReplaceFirst(component);
			
			if (shouldAddVerticallyAlignedComponents && Math.abs(component.getRectangle().y - MainCanvas.mouse.getY()) < 10 && Component.globalAlignmentEnabled)
				alignedComponents.addVerAlignedComponentOrReplaceFirst(component);			
		}
				
		return alignedComponents;
	}
	
	private void finalizeHingeMovement(final AlignedComponentsList alignedComponents)
	{		
		if (alignedComponents.hasHorAlignedComponents())
			rect.x = alignedComponents.getHorAlignedComponents().get(0).getRectangle().x;
		
		if (alignedComponents.hasVerAlignedComponents())
			rect.y = alignedComponents.getVerAlignedComponents().get(0).getRectangle().y;
	}
	
	private void finalizeSpawnLineSegment(final AlignedComponentsList alignedComponents)
	{						
		int endPointX = 0;
		int endPointY = 0;
		
		if (isMousePerformingHorizontalMotion || HingeComponent.globalHingeDraggingMode == HingeDraggingMode.SPAWN_SEGMENT_UNRES)
		{			
			if (alignedComponents != null && alignedComponents.hasHorAlignedComponents())
			{		
				endPointX = (int)alignedComponents.getHorAlignedComponents().get(0).getRectangle().x;			
			}
			else
			{
				endPointX = MainCanvas.mouse.getX() - HINGE_DIAMETER/2;				
			}
			
			if (HingeComponent.globalHingeDraggingMode == HingeDraggingMode.SPAWN_SEGMENT_AXIS_RES)
			{
				endPointY = (int)getRectangle().y;
			}
		}
		
		if (!isMousePerformingHorizontalMotion || HingeComponent.globalHingeDraggingMode == HingeDraggingMode.SPAWN_SEGMENT_UNRES)
		{			
			if (alignedComponents != null && alignedComponents.hasVerAlignedComponents())
			{
				endPointY = (int)alignedComponents.getVerAlignedComponents().get(0).getRectangle().y;
			}
			else
			{
				endPointY = MainCanvas.mouse.getY() - HINGE_DIAMETER/2;				
			}
			
			if (HingeComponent.globalHingeDraggingMode == HingeDraggingMode.SPAWN_SEGMENT_AXIS_RES)
			{			
				endPointX = (int)getRectangle().x;				
			}
		}
		
		HingeComponent endPoint = new HingeComponent(canvas, endPointX, endPointY, true);		
	   	    
	    // Check for attempts to finalize the line segment's end point on top of 
	    // another hinge
    	Component identicalHinge = null;    	
    	Iterator<Component> compIter = canvas.getComponentsIterator();
		while (compIter.hasNext())
		{
			Component component = compIter.next();	    	
    		if (component.getComponentType() != ComponentType.HINGE)
    			continue;
    		
    		if (component.getRectangle().intersects(endPoint.getRectangle()))
    		{
    			identicalHinge = component;
    			break;
    		}	    		
    	}
    	
    	LineSegmentComponent lineSegment = new LineSegmentComponent(canvas, this, endPoint, true);
    	if (identicalHinge != null)
    	{
    		lineSegment = new LineSegmentComponent(canvas, this, identicalHinge, true);
    	}
    	else
    	{
    		lineSegment = new LineSegmentComponent(canvas, this, endPoint, true);
    		canvas.addComponentToCanvas(endPoint);	    		
    	}
    	
    	canvas.addComponentToCanvas(lineSegment);	    		    
	}
	
	private void drawCross(final GraphicsGenerator g)
	{
		g.drawLine((int)getRectangle().getCenterX() - HINGE_DIAMETER/2,
				   (int)getRectangle().getCenterY() - HINGE_DIAMETER/2, 
				   (int)getRectangle().getCenterX() + HINGE_DIAMETER/2,
				   (int)getRectangle().getCenterY() + HINGE_DIAMETER/2);
		
		g.drawLine((int)getRectangle().getCenterX() + HINGE_DIAMETER/2,
				   (int)getRectangle().getCenterY() - HINGE_DIAMETER/2, 
				   (int)getRectangle().getCenterX() - HINGE_DIAMETER/2,
				   (int)getRectangle().getCenterY() + HINGE_DIAMETER/2);
	}
	
	public void calculateNamePosition(final GraphicsGenerator g)
	{
		nameX = 0;
		nameY = 0;
		
		Rectangle2D nameBounds = g.getFontMetrics().getStringBounds(name, g.getCanvasContext());
		
		switch (internalHingeLocation)
		{
		
			case LEFT:
			{
				nameX = rect.x + HINGE_DIAMETER;
				nameY = rect.y + HINGE_DIAMETER/2 + (int)nameBounds.getHeight()/3;
			}  break;
			case RIGHT:
			{
				nameX = rect.x + HINGE_DIAMETER/2 - 5 - (int)nameBounds.getWidth(); 
				nameY = rect.y + HINGE_DIAMETER/2 + (int)nameBounds.getHeight()/3; 
			} break;
			case TOP:
			{
				nameX = rect.x - (int)nameBounds.getWidth()/2 + HINGE_DIAMETER/2;
				nameY = rect.y + HINGE_DIAMETER + 8;
			} break;
			case BOTTOM:
			{
				nameX = rect.x - (int)nameBounds.getWidth()/2 + HINGE_DIAMETER/2;
				nameY = rect.y + 5;
			} break;
		}
	}
}
