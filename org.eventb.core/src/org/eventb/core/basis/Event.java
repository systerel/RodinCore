package org.eventb.core.basis;

import org.eventb.core.EventBPlugin;
import org.eventb.core.IEvent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.basis.InternalElement;

/**
 * Implementation of Event-B events as an extension of the Rodin database.
 * <p>
 * This class is intended to be implemented by clients that want to extend this
 * internal element type.
 * </p>
 * <p>
 * This class should not be used in any other way than subclassing it
 * in a database extension. In particular, clients should not use it,
 * but rather use its associated interface <code>IEvent</code>.
 * </p>
 *
 * @author Laurent Voisin
 */
public class Event extends InternalElement implements IEvent {
	
	public final String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".event";

	public Event(String name, IRodinElement parent) {
		super(name, parent);
	}
	
	@Override
	public String getElementType() {
		return ELEMENT_TYPE;
	}

}
