package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eventb.core.ICommentedElement;
import org.rodinp.core.RodinDBException;

public class CommentEditComposite extends TextEditComposite {

	@Override
	public String getValue() {
		assert element instanceof ICommentedElement;
		final ICommentedElement cElement = (ICommentedElement) element;
		try {
			return cElement.getComment();
		} catch (RodinDBException e) {
			return "";
		}
	}

	@Override
	public void createMainComposite(FormToolkit toolkit, Composite parent) {
		createMainComposite(toolkit, parent, SWT.MULTI);
	}

	@Override
	public void setValue() {
		assert element instanceof ICommentedElement;
		final ICommentedElement cElement = (ICommentedElement) element;
		Text text = (Text) control;
		String str = text.getText();
		if (!getValue().equals(str)) {
			try {
				cElement.setComment(str, new NullProgressMonitor());
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
