package uk.ac.gla.student._2074245k.cde.observers;

import java.util.List;

import uk.ac.gla.student._2074245k.cde.components.Component;
import uk.ac.gla.student._2074245k.cde.gui.PortView;

/**
* The ConcreteComponentCreationObserver interface is used in the concrete component builder panels
* (ConcreteComponentBuilderPanel & ConcreteComponentBuilderViewPanel) for implementing an Observer-Observable
* pattern regarding component creation events. 
*
* @author  Alexios Koukoulas
* @version 0.40
* @since   2/2/2017 
*/
public interface ConcreteComponentCreationObserver 
{
    void callbackOnConcreteComponentCreationEvent(final Component concreteComponent, final List<PortView> portViews);
}
