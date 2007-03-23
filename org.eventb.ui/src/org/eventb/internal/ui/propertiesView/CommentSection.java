package org.eventb.internal.ui.propertiesView;

import org.eclipse.core.runtime.NullProgressMonitor;
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
		ICommentedElement cElement = (ICommentedElement) element;
		if (cElement.exists() && cElement.hasComment())
			return cElement.getComment();
		return "";
	}

	@Override
	void setStyle() {
		style = SWT.MULTI;
		math = true;
	}

	@Override
	void setText(String text) throws RodinDBException {
		if (!getText().equals(text))
			((ICommentedElement) element).setComment(text,
					new NullProgressMonitor());
	}

}
