package org.eventb.core.basis;

import org.eventb.core.EventBPlugin;
import org.eventb.core.IGuard;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.basis.InternalElement;

/**
 * Implementation of Event-B guards as an extension of the Rodin database.
 * <p>
 * This class is intended to be implemented by clients that want to extend this
 * internal element type.
 * </p>
 * <p>
 * This class should not be used in any other way than subclassing it
 * in a database extension. In particular, clients should not use it,
 * but rather use its associated interface <code>IGuard</code>.
 * </p>
 *
 * @author Laurent Voisin
 */
public class Guard extends InternalElement implements IGuard {
	
	public final String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".guard";

	public Guard(String name, IRodinElement parent) {
		super(name, parent);
	}
	
	@Override
	public String getElementType() {
		return ELEMENT_TYPE;
	}

}
