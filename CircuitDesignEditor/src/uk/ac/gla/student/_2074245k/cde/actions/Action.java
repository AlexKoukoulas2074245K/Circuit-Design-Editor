package uk.ac.gla.student._2074245k.cde.actions;

public interface Action 
{
	public enum ActionState
	{
		IDLE, EXECUTED
	}
	
	void execute();
	void undo();
}
