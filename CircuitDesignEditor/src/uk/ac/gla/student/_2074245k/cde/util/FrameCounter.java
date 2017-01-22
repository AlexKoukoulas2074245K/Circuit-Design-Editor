package uk.ac.gla.student._2074245k.cde.util;

import java.io.File;
import java.util.Set;

import javax.swing.JFrame;

import uk.ac.gla.student._2074245k.cde.components.Component;
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
			           final JFrame frame,
			           final File selFile,
			           final boolean hasTakenActionSinceLastSave)
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
			int nWBs    = 0;
			int nTBs    = 0;
			
			for (Component component: components)
			{
				switch (component.getComponentType())
				{
					case HINGE:        nHinges++; break;
					case LINE_SEGMENT: nLses++; break;
					case GATE:         nGates++; break;
					case BLACK_BOX:    nBBs++; break;
					case WHITE_BOX:    nWBs++; break;
					case TEXT_BOX:     nTBs++; break;
				}				
			} 
			
			frame.setTitle(MainFrame.WINDOW_TITLE + " - " +
		                   (selFile == null ? "New Canvas" : selFile.getName()) + 
		                   (hasTakenActionSinceLastSave ? "*" : "") + 
					       " -  FPS: " + frameCounter +
					       "  | TBs: " + nTBs + 
					       ", BBs: " + nBBs +
					       ", WBs: " + nWBs +
					       ", Gates: " + nGates +
					       ", LS: " + nLses +
					       ", Hinges: " + nHinges + 					      
					       ", Sel: " + selector.getNumberOfSelectedComponents() +					       
					       " | Mem: " + (((runtime.totalMemory() - runtime.freeMemory())/1024L)/1024L) + "MB"); 
			
			frameCounter = 0;				
		} 	
	}
}
