
package org.eventb.core.basis;

import org.eventb.core.ISCVariable;
import org.rodinp.core.IRodinElement;

/**
 * @author halstefa
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

}
