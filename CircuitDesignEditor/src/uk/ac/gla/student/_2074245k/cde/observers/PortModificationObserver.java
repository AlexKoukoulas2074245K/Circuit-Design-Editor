package uk.ac.gla.student._2074245k.cde.observers;

import uk.ac.gla.student._2074245k.cde.gui.PortView;

public interface PortModificationObserver 
{
	void callbackOnPortInsertionEvent(final PortView portView);
	void callbackOnPortDeletionEvent(final PortView portView);
}
