package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eventb.core.IIdentifierElement;
import org.rodinp.core.RodinDBException;

public class IdentifierEditComposite extends TextEditComposite {

	@Override
	public void setValue() {
		assert element instanceof IIdentifierElement;
		final IIdentifierElement iElement = (IIdentifierElement) element;
		Text text = (Text) control;
		String str = text.getText();

		if (!getValue().equals(str)) {
			try {
				iElement.setIdentifierString(str, new NullProgressMonitor());
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public String getValue() {
		assert element instanceof IIdentifierElement;
		final IIdentifierElement iElement = (IIdentifierElement) element;
		try {
			return iElement.getIdentifierString();
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	@Override
	public void createComposite(FormToolkit toolkit, Composite parent) {
		createComposite(toolkit, parent, SWT.SINGLE);
	}
}
