
package org.eventb.core.basis;

import org.eventb.core.ISCVariable;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * Implementation of Event-B SC variable as an extension of the Rodin database.
 * <p>
 * This class is intended to be implemented by clients that want to extend this
 * internal element type.
 * </p>
 * <p>
 * This class should not be used in any other way than subclassing it
 * in a database extension. In particular, clients should not use it,
 * but rather use its associated interface <code>ISCVariable</code>.
 * </p>
 *
 * @author Stefan Hallerstede
 *
 */
public class SCVariable extends Variable implements ISCVariable {

	public SCVariable(String name, IRodinElement parent) {
		super(name, parent);
	}
	
	@Override
	public String getElementType() {
		return ISCVariable.ELEMENT_TYPE;
	}

	public String getName() {
		return getElementName();
	}
	
	public String getType() throws RodinDBException {
		return getContents();
	}

}
