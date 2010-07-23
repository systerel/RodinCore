package org.eventb.internal.ui.eventbeditor.manipulation;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

public class NullAttributeManipulation implements IAttributeManipulation {

	private final String[] noPossibleValues = new String[0];

	private final String noValue = "";

	@Override
	public String[] getPossibleValues(IRodinElement element,
			IProgressMonitor monitor) {
		return noPossibleValues;
	}

	@Override
	public String getValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return noValue;
	}

	@Override
	public boolean hasValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return false;
	}

	@Override
	public void removeAttribute(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		// Do nothing
	}

	@Override
	public void setDefaultValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		// Do nothing
	}

	@Override
	public void setValue(IRodinElement element, String value,
			IProgressMonitor monitor) throws RodinDBException {
		// Do nothing
	}

}
