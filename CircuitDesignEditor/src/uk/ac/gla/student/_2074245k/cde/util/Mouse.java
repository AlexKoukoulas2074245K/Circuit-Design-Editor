package uk.ac.gla.student._2074245k.cde.util;

import java.awt.MouseInfo;
import java.awt.event.MouseEvent;

public final class Mouse 
{
	public static final int LEFT_BUTTON  = MouseEvent.BUTTON1;
	public static final int MID_BUTTON   = MouseEvent.BUTTON2;
	public static final int RIGHT_BUTTON = MouseEvent.BUTTON3;
	
	private int x, y, prevX, prevY, prePressX, prePressY;
	private boolean[] currentState;
	private boolean[] previousState;
	private boolean doubleClick;
	
	public Mouse()
	{
		x = y = prevX = prevY = prePressX = prePressY = 0;
		currentState  = new boolean[MouseInfo.getNumberOfButtons()];
		previousState = new boolean[MouseInfo.getNumberOfButtons()];
		doubleClick   = false;
	}
	
	public int getX() { return x; }
	public int getY() { return y; }
		
	public void setX(final int x) { this.x = x; }
	public void setY(final int y) { this.y = y; }
	
	public int getPrevX() { return prevX; }
	public int getPrevY() { return prevY; }
	
	public int getDx() { return x - prevX; }
	public int getDy() { return y - prevY; }
	
	public boolean doubleClick() { return doubleClick; }
	public boolean isButtonDown(final int buttonNum) { return currentState[buttonNum] && !isButtonTapped(buttonNum); }
	public boolean isButtonTapped(final int buttonNum) { return currentState[buttonNum] && !previousState[buttonNum]; }
	public boolean isButtonJustReleased(final int buttonNum) { return !currentState[buttonNum] && previousState[buttonNum]; }
	public boolean isPerformingHorMotion() {return Math.abs(prePressX - x) > Math.abs(prePressY - y); }
	
	public void updateOnMouseDrag(final MouseEvent eventArgs)
	{
		x = eventArgs.getX();
		y = eventArgs.getY();
		currentState[eventArgs.getButton()] = true;
	}
	
	public void updateOnMouseMove(final MouseEvent eventArgs)
	{
		x = eventArgs.getX();
		y = eventArgs.getY();
	}
	
	public void updateOnMousePressed(final MouseEvent eventArgs)
	{		
		doubleClick = false;
		prePressX = eventArgs.getX();
		prePressY = eventArgs.getY();
		currentState[eventArgs.getButton()] = true;
	}
	
	public void updateOnMouseReleased(final MouseEvent eventArgs)
	{		
		prePressX = eventArgs.getX();
		prePressY = eventArgs.getY();
		currentState[eventArgs.getButton()] = false;
		doubleClick = eventArgs.getClickCount() == 2;		
	}
	
	public void updateOnFrameEnd()
	{
		for (int i = 0; i < currentState.length; ++i)
			previousState[i] = currentState[i];
		prevX = x;
		prevY = y;		
	}
}
