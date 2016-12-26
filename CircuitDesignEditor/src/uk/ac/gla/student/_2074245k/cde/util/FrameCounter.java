package uk.ac.gla.student._2074245k.cde.util;

import java.util.List;

import javax.swing.JFrame;

import uk.ac.gla.student._2074245k.cde.components.Component;
import uk.ac.gla.student._2074245k.cde.components.Component.ComponentType;
import uk.ac.gla.student._2074245k.cde.gui.ComponentSelector;

public final class FrameCounter 
{
	private long frameCounter, startTime;
	
	public FrameCounter()
	{
		frameCounter = 0L;
		startTime    = 0L;
	}
	
	public void update(final List<Component> components, final ComponentSelector selector, final JFrame frame)
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
			frame.setTitle("C.D.E (Circuit Design Editor)  -  FPS: " + frameCounter + 
					        "    BlackBoxes: " + nBBs +
					        ", Gates: " + nGates +
					        ", Line Segments: " + nLses +
					        ", Hinges: " + nHinges + 
					        ", Selected Components: " + selector.getNumberOfSelectedComponents());
			frameCounter = 0;				
		}	
	}
}
