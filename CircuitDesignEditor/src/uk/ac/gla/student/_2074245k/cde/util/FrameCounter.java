package uk.ac.gla.student._2074245k.cde.util;

import java.util.Set;

import javax.swing.JFrame;

import uk.ac.gla.student._2074245k.cde.components.Component;
import uk.ac.gla.student._2074245k.cde.components.Component.ComponentType;
import uk.ac.gla.student._2074245k.cde.gui.ComponentSelector;
import uk.ac.gla.student._2074245k.cde.gui.MainFrame;

public final class FrameCounter 
{
	private long frameCounter, startTime;
	private Runtime runtime;
	
	public FrameCounter()
	{
		frameCounter = 0L;
		startTime    = 0L;
		runtime      = Runtime.getRuntime();
	}
	
	public void update(final Set<Component> components, 
			           final ComponentSelector selector, 
			           final int execActionLength, 
			           final int undoneActionLength, 
			           final JFrame frame)
	{
		frameCounter++;
		long timeNow = System.nanoTime();
		if (timeNow - startTime >= 1000000000)
		{
			startTime = timeNow;
			int nHinges = 0;
			int nLses   = 0;
			int nGates  = 0;
			int nBBs    = 0;
			
			for (Component component: components)
			{
				if (component.getComponentType() == ComponentType.HINGE)
					nHinges++;
				else if (component.getComponentType() == ComponentType.LINE_SEGMENT)
					nLses++;
				else if (component.getComponentType() == ComponentType.GATE)
					nGates++;
				else if (component.getComponentType() == ComponentType.BLACK_BOX)
					nBBs++;
			}
			frame.setTitle(MainFrame.WINDOW_TITLE + 
					       " -  FPS: " + frameCounter + 
					       "   | BBs: " + nBBs +
					       ", Gates: " + nGates +
					       ", LS: " + nLses +
					       ", Hinges: " + nHinges + 					      
					       ", Sel: " + selector.getNumberOfSelectedComponents() +
					       " | Exec: " + execActionLength + 
					       " Undo: " + undoneActionLength +
					       " | Mem: " + ((runtime.totalMemory() - runtime.freeMemory())/(1024L * 1024L)) + "MB");
			
			frameCounter = 0;				
		}	
	}
}
