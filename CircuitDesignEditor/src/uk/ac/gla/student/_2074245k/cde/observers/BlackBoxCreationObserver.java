package uk.ac.gla.student._2074245k.cde.observers;

import java.util.List;

import uk.ac.gla.student._2074245k.cde.components.Component;
import uk.ac.gla.student._2074245k.cde.gui.PortView;

public interface BlackBoxCreationObserver 
{
	void callbackOnBlackBoxCreationEvent(final Component blackBox, final List<PortView> portViews);
}
