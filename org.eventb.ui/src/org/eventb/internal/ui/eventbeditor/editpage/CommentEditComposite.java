package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;
import org.eventb.core.ICommentedElement;
import org.rodinp.core.RodinDBException;

public class CommentEditComposite extends TextEditComposite {

	public String getValue() throws RodinDBException {
		assert element instanceof ICommentedElement;
		final ICommentedElement cElement = (ICommentedElement) element;
		return cElement.getComment();
	}

	public void setValue() {
		assert element instanceof ICommentedElement;
		final ICommentedElement cElement = (ICommentedElement) element;
		if (text == null)
			return;
		
		String str = text.getText();
		String value;
		try {
			value = getValue();
		} catch (RodinDBException e) {
			value = null;
		}
		if (value == null || !value.equals(str)) {
			try {
				cElement.setComment(str, new NullProgressMonitor());
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void setDefaultValue() {
		final ICommentedElement cElement = (ICommentedElement) element;
		try {
			cElement.setComment("", new NullProgressMonitor());
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.setDefaultValue();
	}

	@Override
	protected void setStyle() {
		style = SWT.MULTI | SWT.BORDER;
	}
	
}
