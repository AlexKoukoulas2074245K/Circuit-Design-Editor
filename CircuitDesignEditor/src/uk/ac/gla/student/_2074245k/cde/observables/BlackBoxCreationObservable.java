package uk.ac.gla.student._2074245k.cde.observables;

import uk.ac.gla.student._2074245k.cde.observers.BlackBoxCreationObserver;

public interface BlackBoxCreationObservable 
{	
	void subscribeToBlackBoxCreationEvent(final BlackBoxCreationObserver observer);
}
