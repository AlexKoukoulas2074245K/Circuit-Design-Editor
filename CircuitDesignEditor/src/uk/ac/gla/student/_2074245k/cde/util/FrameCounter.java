package uk.ac.gla.student._2074245k.cde.util;

import java.io.File;

import javax.swing.JFrame;

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
	
	public void update(final ComponentSelector selector, 			           
			           final JFrame frame,
			           final File selFile,
			           final boolean hasTakenActionSinceLastSave)
	{
		
		frameCounter++;
		long timeNow = System.nanoTime();
		if (timeNow - startTime >= 1000000000)
		{
			startTime = timeNow;			
			
			frame.setTitle(MainFrame.WINDOW_TITLE + " - " +
		                   (selFile == null ? "New Canvas" : selFile.getName()) + 
		                   (hasTakenActionSinceLastSave ? "*" : "") + 
					       " -  FPS: " + frameCounter +					       				      
					       " | Mem: " + (((runtime.totalMemory() - runtime.freeMemory())/1024L)/1024L) + "MB"); 
			
			frameCounter = 0;				
		} 	
	}
}
