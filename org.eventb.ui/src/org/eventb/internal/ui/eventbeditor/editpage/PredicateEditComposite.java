package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IPredicateElement;
import org.rodinp.core.IAttributedElement;
import org.rodinp.core.RodinDBException;

public class PredicateEditComposite extends DefaultAttributeEditor implements
		IAttributeEditor {

	@Override
	public void setAttribute(IAttributedElement element, String newValue,
			IProgressMonitor monitor) throws RodinDBException {
		assert element instanceof IPredicateElement;
		final IPredicateElement pElement = (IPredicateElement) element;

		String value;
		try {
			value = getAttribute(element, monitor);
		} catch (RodinDBException e) {
			value = null;
		}
		if (value == null || !value.equals(newValue)) {
			pElement.setPredicateString(newValue, null);
		}
	}

	@Override
	public String getAttribute(IAttributedElement element,
			IProgressMonitor monitor) throws RodinDBException {
		assert element instanceof IPredicateElement;
		final IPredicateElement pElement = (IPredicateElement) element;
		return pElement.getPredicateString();
	}

	@Override
	public void setDefaultAttribute(IAttributedElement element,
			IProgressMonitor monitor) throws RodinDBException {
		assert element instanceof IPredicateElement;
		final IPredicateElement pElement = (IPredicateElement) element;
		pElement.setPredicateString("", monitor);
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
		element.removeAttribute(EventBAttributes.PREDICATE_ATTRIBUTE, monitor);
	}
}
