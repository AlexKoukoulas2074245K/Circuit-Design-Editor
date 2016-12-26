package uk.ac.gla.student._2074245k.cde.observables;

import uk.ac.gla.student._2074245k.cde.observers.PortModificationObserver;

public interface PortModificationObservable 
{
	void subscribeToPortInsertionEvent(final PortModificationObserver observer);
	void subscribeToPortDeletionEvent(final PortModificationObserver observer);
}
