package uk.ac.gla.student._2074245k.cde.observers;

import uk.ac.gla.student._2074245k.cde.gui.PortView;

/**
* The PortModificationObserver interface is used in the concrete component builder panels
* (ConcreteComponentBuilderPanel & ConcreteComponentBuilderViewPanel) for implementing an Observer-Observable
* pattern regarding port modification events. 
* 
* @author  Alexios Koukoulas
* @version 0.40
* @since   2/2/2017 
*/
public interface PortModificationObserver 
{
    void callbackOnPortInsertionEvent(final PortView portView);
    void callbackOnPortDeletionEvent(final PortView portView);
}
