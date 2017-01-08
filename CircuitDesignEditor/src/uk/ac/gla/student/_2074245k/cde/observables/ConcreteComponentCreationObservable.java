package uk.ac.gla.student._2074245k.cde.observables;

import uk.ac.gla.student._2074245k.cde.observers.ConcreteComponentCreationObserver;

public interface ConcreteComponentCreationObservable 
{	
	void subscribeToConcreteComponentCreationEvent(final ConcreteComponentCreationObserver observer);
}
