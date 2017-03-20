package uk.ac.gla.student._2074245k.cde.actions;

/**
* The Action interface provides structure for the rest of
* its subclassses in the 'actions' package.
*
* @author  Alexios Koukoulas
* @version 0.40
* @since   2/2/2017 
*/
public interface Action 
{
    public enum ActionState
    {
        IDLE, EXECUTED
    }
    
    void execute();
    void undo();
}
