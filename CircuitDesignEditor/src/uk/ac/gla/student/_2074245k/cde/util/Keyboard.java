package uk.ac.gla.student._2074245k.cde.util;

import java.awt.event.KeyEvent;

public final class Keyboard 
{
	public static final int DELETE_KEY = KeyEvent.VK_DELETE;
	public static final int ESCAPE_KEY = KeyEvent.VK_ESCAPE;
	
	private boolean[] currentState;
	private boolean[] previousState;
	
	public Keyboard()
	{
		currentState = new boolean[256];
		previousState = new boolean[256];		
	}
	
	public boolean isKeyDown(final int keyNum) { return currentState[keyNum]; }
	public boolean isKeyTapped(final int keyNum) { return currentState[keyNum] && !previousState[keyNum]; }
	
	public void updateOnKeyPress(KeyEvent eventArgs)
	{		
		currentState[eventArgs.getKeyCode()] = true;
	}
	
	public void updateOnKeyRelease(KeyEvent eventArgs)
	{
		currentState[eventArgs.getKeyCode()] = false;
	}
	
	public void updateOnFrameEnd()
	{
		for (int i = 0; i < currentState.length; ++i)
			previousState[i] = currentState[i];		
	}
}
