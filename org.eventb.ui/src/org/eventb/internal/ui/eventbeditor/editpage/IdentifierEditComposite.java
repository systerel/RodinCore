package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eventb.core.IIdentifierElement;
import org.rodinp.core.RodinDBException;

public class IdentifierEditComposite extends TextEditComposite {

	public void setValue() {
		assert element instanceof IIdentifierElement;
		final IIdentifierElement iElement = (IIdentifierElement) element;
		Text text = (Text) control;
		String str = text.getText();

		String value;
		try {
			value = getValue();
		} catch (RodinDBException e) {
			value = null;
		}
		if (value == null || !value.equals(str)) {
			try {
				iElement.setIdentifierString(str, new NullProgressMonitor());
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public String getValue() throws RodinDBException {
		assert element instanceof IIdentifierElement;
		final IIdentifierElement iElement = (IIdentifierElement) element;
		return iElement.getIdentifierString();
	}

	@Override
	public void createMainComposite(FormToolkit toolkit, Composite parent) {
		createMainComposite(toolkit, parent, SWT.SINGLE);
	}
}
