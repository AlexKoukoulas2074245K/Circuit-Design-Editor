package uk.ac.gla.student._2074245k.cde.actions;

import uk.ac.gla.student._2074245k.cde.components.Component;
import uk.ac.gla.student._2074245k.cde.components.Component.ComponentType;
import uk.ac.gla.student._2074245k.cde.gui.ComponentSelector;
import uk.ac.gla.student._2074245k.cde.gui.MainCanvas;
import uk.ac.gla.student._2074245k.cde.util.LoadingResult;
import uk.ac.gla.student._2074245k.cde.util.ProjectPersistenceUtilities;

public final class PasteAction implements Action 
{
	private MainCanvas canvas;
	private ComponentSelector compSelector;
	private LoadingResult loadingResult;
	
	private ActionState state; 
	
	public PasteAction(final MainCanvas canvas, 
			           final ComponentSelector compSelector)
	{
		this.canvas           = canvas;
		this.compSelector     = compSelector;
		this.loadingResult    = null;
		state                 = ActionState.IDLE;
	}
	
	@Override
	public void execute() 
	{
		if (state == ActionState.IDLE)
		{			
			loadingResult = ProjectPersistenceUtilities.openProjectNonPersistent(canvas); 
			
			for (Component comp: loadingResult.loadedComponents)
			{
				if (comp.getComponentType() != ComponentType.LINE_SEGMENT)
				{
					comp.setPosition(comp.getRectangle().x + 100, comp.getRectangle().y + 100);
				}
				compSelector.addComponentToSelectionExternally(comp);
				canvas.addComponentToCanvas(comp);				
			}
			
			state = ActionState.EXECUTED;
		}
		else
		{
			throw new InvalidActionStateException("PasteAction: " + this.toString() + " attempted to do an execute on state: " + state);
		}
	}

	@Override
	public void undo() 
	{
		if (state == ActionState.EXECUTED)
		{				
			for (Component comp: loadingResult.loadedComponents)
			{
				canvas.removeComponentFromCanvas(comp);
			}
			state = ActionState.IDLE;
		}
		else
		{
			throw new InvalidActionStateException("PasteMoveAction: " + this.toString() + " attempted to do an undo on state: " + state);
		}
	}
}
