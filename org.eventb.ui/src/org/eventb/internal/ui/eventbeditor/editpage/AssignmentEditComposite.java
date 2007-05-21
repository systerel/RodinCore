package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IAssignmentElement;
import org.rodinp.core.IAttributedElement;
import org.rodinp.core.RodinDBException;

public class AssignmentEditComposite extends DefaultAttributeEditor implements
		IAttributeEditor {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.editpage.IAttributeEditor#setAttribute(org.rodinp.core.IAttributedElement,
	 *      java.lang.String)
	 */
	@Override
	public void setAttribute(IAttributedElement element, String newValue,
			IProgressMonitor monitor) throws RodinDBException {
		assert element instanceof IAssignmentElement;
		final IAssignmentElement aElement = (IAssignmentElement) element;

		String value;
		try {
			value = getAttribute(element, monitor);
		} catch (RodinDBException e) {
			value = null;
		}

		if (value == null || !value.equals(newValue)) {
			aElement.setAssignmentString(newValue, monitor);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.editpage.IAttributeEditor#getAttribute(org.rodinp.core.IAttributedElement)
	 */
	@Override
	public String getAttribute(IAttributedElement element,
			IProgressMonitor monitor) throws RodinDBException {
		assert element instanceof IAssignmentElement;
		final IAssignmentElement pElement = (IAssignmentElement) element;
		return pElement.getAssignmentString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.editpage.IAttributeEditor#setDefaultAttribute(org.rodinp.core.IAttributedElement)
	 */
	@Override
	public void setDefaultAttribute(IAttributedElement element,
			IProgressMonitor monitor) throws RodinDBException {
		final IAssignmentElement aElement = (IAssignmentElement) element;
		aElement.setAssignmentString("", monitor);
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
		element.removeAttribute(EventBAttributes.ASSIGNMENT_ATTRIBUTE, monitor);
	}

}
