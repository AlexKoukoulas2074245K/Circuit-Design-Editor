package uk.ac.gla.student._2074245k.cde.actions;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import uk.ac.gla.student._2074245k.cde.components.Component;
import uk.ac.gla.student._2074245k.cde.gui.ComponentSelector;
import uk.ac.gla.student._2074245k.cde.gui.MainCanvas;
import uk.ac.gla.student._2074245k.cde.util.LoadingResult;
import uk.ac.gla.student._2074245k.cde.util.ProjectPersistenceUtilities;

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
				case HINGE: if (!selComp.isMovable()) canvas.addChildrenAndParentsToSelection(); break;
				case LINE_SEGMENT: if (!selComp.isMovable()) canvas.addChildrenAndParentsToSelection(); break;
				case GATE: canvas.addChildrenAndParentsToSelection(); break;
				case BLACK_BOX: canvas.addChildrenAndParentsToSelection(); break;
				case WHITE_BOX: canvas.addChildrenAndParentsToSelection(); break;
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
			
			Iterator<Component> selComponentsIter = selector.getSelectedComponentsIterator();
			while (selComponentsIter.hasNext()) selComponentsIter.next().delete();			
			
			state = ActionState.EXECUTED;
		}
		else
		{
			throw new InvalidActionStateException("PasteAction: " + this.toString() + " attempted to do an execute on state: " + state);
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
			throw new InvalidActionStateException("PasteMoveAction: " + this.toString() + " attempted to do an undo on state: " + state);
		}
	}
}
