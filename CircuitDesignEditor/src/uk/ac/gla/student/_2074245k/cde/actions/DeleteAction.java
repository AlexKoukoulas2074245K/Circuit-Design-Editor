package uk.ac.gla.student._2074245k.cde.actions;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import uk.ac.gla.student._2074245k.cde.components.Component;
import uk.ac.gla.student._2074245k.cde.components.Component.ComponentType;
import uk.ac.gla.student._2074245k.cde.components.HingeComponent;
import uk.ac.gla.student._2074245k.cde.gui.ComponentSelector;
import uk.ac.gla.student._2074245k.cde.gui.MainCanvas;
import uk.ac.gla.student._2074245k.cde.gui.PortView;
import uk.ac.gla.student._2074245k.cde.util.LoadingResult;
import uk.ac.gla.student._2074245k.cde.util.ProjectPersistenceUtilities;

/**
* The DeleteAction encapsulates the action of 
* deleting a set of components.
*
* @author  Alexios Koukoulas
* @version 0.40
* @since   2/2/2017 
*/
public class DeleteAction implements Action
{
    private ActionState state; 
    private final MainCanvas canvas;
    private final ComponentSelector selector;
    
    public DeleteAction(final MainCanvas canvas, final ComponentSelector selector)
    {
        this.canvas = canvas;
        this.selector = selector;
        state = ActionState.IDLE;
    }
    
    public void execute()
    {
        if (state == ActionState.IDLE)
        {            
            if (selector.getNumberOfSelectedComponents() == 1)
            {
                Component selComp = selector.getFirstComponent();
                switch (selComp.getComponentType())
                {
                    case HINGE:
                    {
                        if (!selComp.isMovable() &&
                            !((HingeComponent)selComp).hasNub() &&
                            ((HingeComponent)selComp).getPortResultDir() == PortView.PortResultDirectionality.NEUTRAL)
                            canvas.addChildrenAndParentsToSelection();                        
                    } break;
                    
                    case LINE_SEGMENT:
                    {
                        if (!selComp.getChildren().get(0).isMovable() && !selComp.getChildren().get(1).isMovable()) 
                            canvas.addChildrenAndParentsToSelection(); 
                    } break;
                    
                    case GATE: canvas.addChildrenAndParentsToSelection(); break;
                    case BLACK_BOX: canvas.addChildrenAndParentsToSelection(); break;
                    case WHITE_BOX: canvas.addChildrenAndParentsToSelection(); break;
                    case TEXT_BOX: break;
                }
            }
            else
            {                
                canvas.addChildrenAndParentsToSelection();
            }
            
            Set<Component> components          = new HashSet<Component>();
            Iterator<Component> componentsIter = canvas.getComponentsIterator();
            while (componentsIter.hasNext()) components.add(componentsIter.next());
            
            ProjectPersistenceUtilities.saveProjectNonPersistent(components, canvas.getSize()); 
            
            if (selector.getNumberOfSelectedComponents() == 1 &&
                selector.getFirstComponent().getComponentType() == ComponentType.HINGE)
            {
                HingeComponent hinge = (HingeComponent)selector.getFirstComponent();
                
                if (hinge.hasNub())
                {
                    hinge.setHasNub(false);
                    state = ActionState.EXECUTED;
                    return;
                }
                
                if (hinge.getPortResultDir() != PortView.PortResultDirectionality.NEUTRAL)
                {
                    hinge.setExternalHingeInfo(hinge.getParentsPortLocation(), PortView.PortResultDirectionality.NEUTRAL);
                    state = ActionState.EXECUTED;
                    return;
                }
            }
            
            Iterator<Component> selComponentsIter = selector.getSelectedComponentsIterator();
            while (selComponentsIter.hasNext()) selComponentsIter.next().delete();            
            
            state = ActionState.EXECUTED;
        }
        else
        {
            throw new InvalidActionStateException("DeleteAction: " + this.toString() + " attempted to do an execute on state: " + state);
        }
    }
    
    public void undo()
    {
        if (state == ActionState.EXECUTED)
        {                
            Iterator<Component> componentsIter = canvas.getComponentsIterator();
            while (componentsIter.hasNext()) canvas.removeComponentFromCanvas(componentsIter.next());
            LoadingResult result = ProjectPersistenceUtilities.openProjectNonPersistent(canvas);
            for (Component component: result.loadedComponents) canvas.addComponentToCanvas(component);
            state = ActionState.IDLE;
        }
        else
        {
            throw new InvalidActionStateException("DeleteAction: " + this.toString() + " attempted to do an undo on state: " + state);
        }
    }
}
