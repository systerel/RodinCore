package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.ICommentedElement;
import org.eventb.core.IExpressionElement;
import org.rodinp.core.IAttributedElement;
import org.rodinp.core.RodinDBException;

public class ExpressionEditor extends DefaultAttributeEditor implements IAttributeEditor {

	@Override
	public String getAttribute(IAttributedElement element,
			IProgressMonitor monitor) throws RodinDBException {
		assert element instanceof IExpressionElement;
		final IExpressionElement cElement = (IExpressionElement) element;
		return cElement.getExpressionString();
	}

	@Override
	public void setAttribute(IAttributedElement element, String newValue,
			IProgressMonitor monitor) throws RodinDBException {
		assert element instanceof ICommentedElement;
		final IExpressionElement eElement = (IExpressionElement) element;

		String value;
		try {
			value = getAttribute(element, monitor);
		} catch (RodinDBException e) {
			value = null;
		}
		if (value == null || !value.equals(newValue)) {
			eElement.setExpressionString(newValue, monitor);
		}
	}

	@Override
	public void setDefaultAttribute(IAttributedElement element,
			IProgressMonitor monitor) throws RodinDBException {
		final IExpressionElement eElement = (IExpressionElement) element;
		eElement.setExpressionString("", monitor);
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
		element.removeAttribute(EventBAttributes.EXPRESSION_ATTRIBUTE, monitor);
	}

}
