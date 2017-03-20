package uk.ac.gla.student._2074245k.cde.actions;

import java.util.Iterator;

import uk.ac.gla.student._2074245k.cde.components.Component;
import uk.ac.gla.student._2074245k.cde.gui.MainCanvas;
import uk.ac.gla.student._2074245k.cde.util.LoadingResult;
import uk.ac.gla.student._2074245k.cde.util.ProjectPersistenceUtilities;

/**
* The ColorComponentsAction encapsulates the action of 
* moving a single component, including the side effects of its movement.
*
* @author  Alexios Koukoulas
* @version 0.40
* @since   2/2/2017 
*/
public final class MoveAction implements Action
{
    private final Component component;

    private final int[] startPos;
    private final int[] targetPos;
        
    private ActionState state; 
    private MainCanvas canvas;
    
    public MoveAction(final MainCanvas canvas, final Component component, final int[] startPos, final int[] targetPos)
    {
        this.canvas    = canvas;
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
            Iterator<Component> componentsIter = canvas.getComponentsIterator();
            while (componentsIter.hasNext()) canvas.removeComponentFromCanvas(componentsIter.next());
            LoadingResult result = ProjectPersistenceUtilities.openProjectNonPersistent(canvas);
            for (Component component: result.loadedComponents) canvas.addComponentToCanvas(component);            
            state = ActionState.IDLE;
        }
        else
        {
            throw new InvalidActionStateException("MoveAction: " + this.toString() + " attempted to do an undo on state: " + state);
        }
    }        
}
