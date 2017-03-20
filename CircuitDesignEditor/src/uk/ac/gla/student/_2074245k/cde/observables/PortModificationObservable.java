package uk.ac.gla.student._2074245k.cde.observables;

import uk.ac.gla.student._2074245k.cde.observers.PortModificationObserver;

/**
* The PortModificationObservable interface is used in the concrete component builder panels
* (ConcreteComponentBuilderPanel & ConcreteComponentBuilderViewPanel) for implementing an Observer-Observable
* pattern regarding port modification events.
*  
* @author  Alexios Koukoulas
* @version 0.40
* @since   2/2/2017 
*/
public interface PortModificationObservable 
{
    void subscribeToPortInsertionEvent(final PortModificationObserver observer);
    void subscribeToPortDeletionEvent(final PortModificationObserver observer);
}
