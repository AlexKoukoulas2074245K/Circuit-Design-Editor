package uk.ac.gla.student._2074245k.cde.actions;

import uk.ac.gla.student._2074245k.cde.components.Component;

public final class MoveAction implements Action
{
	private final Component component;

	private final int[] startPos;
	private final int[] targetPos;
		
	private ActionState state; 
	
	public MoveAction(final Component component, final int[] startPos, final int[] targetPos)
	{
		this.component = component;
		this.startPos  = new int[]{ startPos[0], startPos[1] };
		this.targetPos = new int[]{ targetPos[0], targetPos[1] };
		state          = ActionState.IDLE;
	}
	
	@Override
	public void execute() 
	{
		if (state == ActionState.IDLE)
		{			
			component.moveTo(targetPos[0], targetPos[1]);
			state = ActionState.EXECUTED;
		}
		else
		{
			throw new InvalidActionStateException("MoveAction: " + this.toString() + " attempted to do an execute on state: " + state);
		}
	}

	@Override
	public void undo() 
	{
		if (state == ActionState.EXECUTED)
		{
			component.moveTo(startPos[0], startPos[1]);
			state = ActionState.IDLE;
		}
		else
		{
			throw new InvalidActionStateException("MoveAction: " + this.toString() + " attempted to do an undo on state: " + state);
		}
	}		
}
