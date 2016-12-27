package uk.ac.gla.student._2074245k.cde.actions;

public class InvalidActionStateException extends RuntimeException
{	
	private static final long serialVersionUID = -4781478980788599540L;
	
	public InvalidActionStateException(final String errorMsg)
	{
		super(errorMsg);
	}
}
