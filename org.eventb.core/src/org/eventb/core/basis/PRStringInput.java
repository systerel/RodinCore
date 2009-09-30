package org.eventb.core.basis;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IPRStringInput;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;

/**
 * @since 1.0
 */
public class PRStringInput extends InternalElement implements IPRStringInput{

	public PRStringInput(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType<IPRStringInput> getElementType() {
		return ELEMENT_TYPE;
	}

	public String getString() throws RodinDBException {
		return getAttributeValue(EventBAttributes.STRING_VALUE_ATTRIBUTE);
	}

	public void setString(String value,IProgressMonitor monitor) throws RodinDBException {
		setAttributeValue(EventBAttributes.STRING_VALUE_ATTRIBUTE, value , monitor);
	}
	




}
