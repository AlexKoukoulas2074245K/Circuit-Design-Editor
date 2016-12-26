package uk.ac.gla.student._2074245k.cde.components;

import java.awt.Rectangle;

import uk.ac.gla.student._2074245k.cde.gui.MainCanvas;
import uk.ac.gla.student._2074245k.cde.util.GraphicsGenerator;

public abstract class Component 
{	
	public enum ComponentType
	{
		LINE_SEGMENT, HINGE, GATE, BLACK_BOX
	}
	
	public enum MovementType
	{
		FREE, AXIS_RESTRICTED
	}
	
	public enum HingeDraggingMode
	{
		SPAWN_SEGMENT_UNRES, SPAWN_SEGMENT_AXIS_RES, MOVE_UNRES, MOVE_AXIS_RES
	}
	
	public enum Orientation
	{
		HORIZONTAL, VERTICAL
	}
	
	public static boolean globalAlignmentEnabled = true;	
	public static final int ALIGNMENT_THRESHOLD = 10;
	
	protected boolean isMovable;
	protected MainCanvas canvas;
	
	public Component(final MainCanvas canvas, final boolean movable)
	{		
		this.isMovable = movable;
		this.canvas    = canvas;
	}
		
	public abstract AlignedComponentsList moveTo(final int x, final int y);
	public abstract void finalizeMovement(final AlignedComponentsList alignedComponents);
	public abstract boolean mouseIntersection(final int mouseX, final int mouseY);
	public abstract void render(final GraphicsGenerator g, final boolean highlighted, final boolean selected, final boolean inMultiSelectionMovement);
	public abstract void renderAligned(final GraphicsGenerator g);
	public abstract void delete();
	public abstract String serialize();	
	public abstract Rectangle getRectangle();
	public abstract void setPosition(final int x, final int y);
	public abstract ComponentType getComponentType();
	
	public void setMovable(final boolean movable) { this.isMovable = movable; }	
	public boolean isMovable() { return isMovable; }
	
}