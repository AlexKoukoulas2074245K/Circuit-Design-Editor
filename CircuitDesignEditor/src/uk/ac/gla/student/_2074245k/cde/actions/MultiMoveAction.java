package uk.ac.gla.student._2074245k.cde.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import uk.ac.gla.student._2074245k.cde.components.Component;
import uk.ac.gla.student._2074245k.cde.components.Component.ComponentType;

public final class MultiMoveAction implements Action
{
	private final List<Component> components;
	private final List<int[]> startPositions;
	private final List<int[]> targetPositions;	
	
	private ActionState state; 
	
	public MultiMoveAction(final Iterator<Component> componentsIter,
			               final Iterator<int[]> startPositionsIter, 
			               final Iterator<int[]> targetPositionsIter)
	{
		this.components      = new ArrayList<Component>();
		while (componentsIter.hasNext()) { components.add(componentsIter.next()); }
		
		this.startPositions  = new ArrayList<int[]>();
		while (startPositionsIter.hasNext()) 
		{
			int[] startPos = startPositionsIter.next();
			startPositions.add(new int[]{ startPos[0], startPos[1] });
		}
		
		this.targetPositions  = new ArrayList<int[]>();
		while (targetPositionsIter.hasNext()) 
		{
			int[] targetPos = targetPositionsIter.next();
			targetPositions.add(new int[]{ targetPos[0], targetPos[1] });
		}
		
		state                = ActionState.IDLE;
	}
	
	@Override
	public void execute() 
	{
		if (state == ActionState.IDLE)
		{			
			for (int i = 0; i < components.size(); ++i)
			{
				if (components.get(i).getComponentType() == ComponentType.HINGE)
					continue;
				
				components.get(i).setPosition(targetPositions.get(i)[0], targetPositions.get(i)[1]);
			}
						
			state = ActionState.EXECUTED;
		}
		else
		{
			throw new InvalidActionStateException("MultiMoveAction: " + this.toString() + " attempted to do an execute on state: " + state);
		}
	}

	@Override
	public void undo() 
	{
		if (state == ActionState.EXECUTED)
		{			
			for (int i = 0; i < components.size(); ++i)
			{
				if (components.get(i).getComponentType() == ComponentType.HINGE)
					continue;
				
				components.get(i).setPosition(startPositions.get(i)[0], startPositions.get(i)[1]);
			}
			
			state = ActionState.IDLE;
		}
		else
		{
			throw new InvalidActionStateException("MultiMoveAction: " + this.toString() + " attempted to do an undo on state: " + state);
		}
	}
}
