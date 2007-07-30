package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.ICommentedElement;
import org.rodinp.core.IAttributedElement;
import org.rodinp.core.RodinDBException;

public class CommentEditComposite extends DefaultAttributeEditor implements IAttributeEditor {

	@Override
	public String getAttribute(IAttributedElement element,
			IProgressMonitor monitor) {
		assert element instanceof ICommentedElement;
		final ICommentedElement cElement = (ICommentedElement) element;
		try {
			return cElement.getComment();
		}
		catch (RodinDBException e) {
			return "";
		}
	}

	@Override
	public void setAttribute(IAttributedElement element, String newValue,
			IProgressMonitor monitor) throws RodinDBException {
		assert element instanceof ICommentedElement;
		final ICommentedElement cElement = (ICommentedElement) element;

		String value = getAttribute(element, monitor);
		if (!value.equals(newValue)) {
			cElement.setComment(newValue, monitor);
		}
	}

	@Override
	public void setDefaultAttribute(IAttributedElement element,
			IProgressMonitor monitor) throws RodinDBException {
		final ICommentedElement cElement = (ICommentedElement) element;
		cElement.setComment("", new NullProgressMonitor());
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
		element.removeAttribute(EventBAttributes.COMMENT_ATTRIBUTE, monitor);
	}

}
