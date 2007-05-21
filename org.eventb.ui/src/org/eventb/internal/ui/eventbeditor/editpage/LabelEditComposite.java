package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.ILabeledElement;
import org.rodinp.core.IAttributedElement;
import org.rodinp.core.RodinDBException;

public class LabelEditComposite extends DefaultAttributeEditor implements
		IAttributeEditor {

	@Override
	public void setAttribute(IAttributedElement element, String newValue,
			IProgressMonitor monitor) throws RodinDBException {
		assert element instanceof ILabeledElement;
		final ILabeledElement lElement = (ILabeledElement) element;

		String value;
		try {
			value = getAttribute(element, monitor);
		} catch (RodinDBException e) {
			value = null;
		}
		if (value == null || !value.equals(newValue)) {
			lElement.setLabel(newValue, monitor);
		}
	}

	@Override
	public String getAttribute(IAttributedElement element,
			IProgressMonitor monitor) throws RodinDBException {
		assert element instanceof ILabeledElement;
		final ILabeledElement lElement = (ILabeledElement) element;
		return lElement.getLabel();
	}

	@Override
	public void setDefaultAttribute(IAttributedElement element,
			IProgressMonitor monitor) throws RodinDBException {
		assert element instanceof ILabeledElement;
		final ILabeledElement lElement = (ILabeledElement) element;
		lElement.setLabel("", monitor);
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
		element.removeAttribute(EventBAttributes.LABEL_ATTRIBUTE, monitor);
	}
}
