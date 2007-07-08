package org.eventb.internal.ui.propertiesView;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eventb.core.ICommentedElement;
import org.rodinp.core.RodinDBException;

public class CommentSection extends TextSection {

	@Override
	String getLabel() {
		return "Comment";
	}

	@Override
	String getText() throws RodinDBException {
		if (element == null)
			return null;
		if (element instanceof ICommentedElement) {
			ICommentedElement cElement = (ICommentedElement) element;
			if (cElement.exists() && cElement.hasComment())
				return cElement.getComment();
			return "";
		}
		return null;
	}

	@Override
	void setStyle() {
		style = SWT.MULTI;
		math = false;
	}

	@Override
	void setText(String text, IProgressMonitor monitor) throws RodinDBException {
		if (element instanceof ICommentedElement) {
			String comment = getText();
			if (comment != null && !comment.equals(text))
				((ICommentedElement) element).setComment(text, monitor);
		}
	}

}
