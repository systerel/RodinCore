package org.eventb.core.basis;

import org.eventb.core.EventBPlugin;
import org.eventb.core.IInvariant;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.basis.InternalElement;

/**
 * Implementation of Event-B invariants as an extension of the Rodin database.
 * <p>
 * This class is intended to be implemented by clients that want to extend this
 * internal element type.
 * </p>
 * <p>
 * This class should not be used in any other way than subclassing it
 * in a database extension. In particular, clients should not use it,
 * but rather use its associated interface <code>IInvariant</code>.
 * </p>
 *
 * @author Laurent Voisin
 */
public class Invariant extends InternalElement implements IInvariant {
	
	public final String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".invariant";

	public Invariant(String name, IRodinElement parent) {
		super(name, parent);
	}
	
	@Override
	public String getElementType() {
		return ELEMENT_TYPE;
	}

}
