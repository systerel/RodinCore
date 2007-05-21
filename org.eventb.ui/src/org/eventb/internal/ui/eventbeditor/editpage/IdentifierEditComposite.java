package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IIdentifierElement;
import org.rodinp.core.IAttributedElement;
import org.rodinp.core.RodinDBException;

public class IdentifierEditComposite extends DefaultAttributeEditor implements
		IAttributeEditor {

	@Override
	public void setAttribute(IAttributedElement element, String newValue,
			IProgressMonitor monitor) throws RodinDBException {
		assert element instanceof IIdentifierElement;
		final IIdentifierElement iElement = (IIdentifierElement) element;
		String value;
		try {
			value = getAttribute(element, monitor);
		} catch (RodinDBException e) {
			value = null;
		}
		if (value == null || !value.equals(newValue)) {
			iElement.setIdentifierString(newValue, new NullProgressMonitor());
		}
	}

	@Override
	public String getAttribute(IAttributedElement element,
			IProgressMonitor monitor) throws RodinDBException {
		assert element instanceof IIdentifierElement;
		final IIdentifierElement iElement = (IIdentifierElement) element;
		return iElement.getIdentifierString();
	}

	@Override
	public void setDefaultAttribute(IAttributedElement element,
			IProgressMonitor monitor) throws RodinDBException {
		assert element instanceof IIdentifierElement;
		final IIdentifierElement iElement = (IIdentifierElement) element;
		iElement.setIdentifierString("", new NullProgressMonitor());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.editpage.IAttributeEditor#removeAttribute(org.rodinp.core.IAttributedElement,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void removeAttribute(IAttributedElement element,
			IProgressMonitor monitor) throws RodinDBException {
		element.removeAttribute(EventBAttributes.IDENTIFIER_ATTRIBUTE, monitor);
	}

}
