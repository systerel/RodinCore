package org.eventb.core.basis;

import org.eventb.core.EventBPlugin;
import org.eventb.core.ITheorem;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.basis.InternalElement;

/**
 * Implementation of Event-B theorems as an extension of the Rodin database.
 * <p>
 * This class is intended to be implemented by clients that want to extend this
 * internal element type.
 * </p>
 * <p>
 * This class should not be used in any other way than subclassing it
 * in a database extension. In particular, clients should not use it,
 * but rather use its associated interface <code>ITheorem</code>.
 * </p>
 *
 * @author Laurent Voisin
 */
public class Theorem extends InternalElement implements ITheorem {
	
	public final String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".theorem";

	public Theorem(String name, IRodinElement parent) {
		super(name, parent);
	}
	
	@Override
	public String getElementType() {
		return ELEMENT_TYPE;
	}

}
