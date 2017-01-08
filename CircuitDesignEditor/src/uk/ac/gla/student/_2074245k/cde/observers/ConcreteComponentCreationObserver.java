package uk.ac.gla.student._2074245k.cde.observers;

import java.util.List;

import uk.ac.gla.student._2074245k.cde.components.Component;
import uk.ac.gla.student._2074245k.cde.gui.PortView;

public interface ConcreteComponentCreationObserver 
{
	void callbackOnConcreteComponentCreationEvent(final Component concreteComponent, final List<PortView> portViews);
}
