package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.ICommentedElement;
import org.eventb.core.IExpressionElement;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IAttributedElement;
import org.rodinp.core.RodinDBException;

public class ExpressionAttributeFactory implements IAttributeFactory {

	public String getValue(IAttributedElement element,
			IProgressMonitor monitor) throws RodinDBException {
		assert element instanceof IExpressionElement;
		final IExpressionElement cElement = (IExpressionElement) element;
		return cElement.getExpressionString();
	}

	public void setValue(IAttributedElement element, String newValue,
			IProgressMonitor monitor) throws RodinDBException {
		assert element instanceof ICommentedElement;
		final IExpressionElement eElement = (IExpressionElement) element;

		String value;
		try {
			value = getValue(element, monitor);
		} catch (RodinDBException e) {
			value = null;
		}
		if (value == null || !value.equals(newValue)) {
			eElement.setExpressionString(newValue, monitor);
		}
	}

	public void setDefaultValue(IEventBEditor<?> editor,
			IAttributedElement element, IProgressMonitor monitor)
			throws RodinDBException {
		final IExpressionElement eElement = (IExpressionElement) element;
		eElement.setExpressionString("", monitor);
	}

	public void removeAttribute(IAttributedElement element,
			IProgressMonitor monitor) throws RodinDBException {
		element.removeAttribute(EventBAttributes.EXPRESSION_ATTRIBUTE, monitor);
	}

	public String[] getPossibleValues(IAttributedElement element,
			IProgressMonitor monitor) throws RodinDBException {
		// Not applicable for Expression Element.
		return null;
	}

}
