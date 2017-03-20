package uk.ac.gla.student._2074245k.cde.actions;

/**
* The InvalidActionStateException error is thrown when an Action subclass 
* is found to be in the wrong state during an undo or redo execution.
*
* @author  Alexios Koukoulas
* @version 0.40
* @since   2/2/2017 
*/
public class InvalidActionStateException extends RuntimeException
{    
    private static final long serialVersionUID = -4781478980788599540L;
    
    public InvalidActionStateException(final String errorMsg)
    {
        super(errorMsg);
    }
}
