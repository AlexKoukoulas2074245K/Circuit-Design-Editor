package uk.ac.gla.student._2074245k.cde.actions;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import uk.ac.gla.student._2074245k.cde.components.Component;
import uk.ac.gla.student._2074245k.cde.gui.ComponentSelector;

public final class ColorComponentsAction implements Action
{
	private ActionState state;
	private List<Component> selComponents;
	private List<Color> prevColors;
	private Color newColor;
	
	public ColorComponentsAction(final ComponentSelector selector, final Color selColor)
	{		
		selComponents = new ArrayList<Component>();
		Iterator<Component> selCompsIter = selector.getSelectedComponentsIterator();
		while (selCompsIter.hasNext()) selComponents.add(selCompsIter.next());
		prevColors = new ArrayList<Color>();
		newColor = selColor;
		state = ActionState.IDLE;
	}
	
	public void execute()
	{
		if (state == ActionState.IDLE)
		{		
			prevColors.clear();
			for (Component comp: selComponents)
			{
				prevColors.add(comp.getColor());
				comp.setColor(newColor);
			}
			state = ActionState.EXECUTED;
		}
		else
		{
			throw new InvalidActionStateException("ColorComponentsAction: " + this.toString() + " attempted to do an execute on state: " + state);
		}
	}
	
	public void undo()
	{
		if (state == ActionState.EXECUTED)
		{				
			for (Component comp: selComponents)
			{				
				comp.setColor(prevColors.get(selComponents.indexOf(comp)));
			}
			state = ActionState.IDLE;
		}
		else
		{
			throw new InvalidActionStateException("ColorComponentsAction: " + this.toString() + " attempted to do an undo on state: " + state);
		}
	}
}
