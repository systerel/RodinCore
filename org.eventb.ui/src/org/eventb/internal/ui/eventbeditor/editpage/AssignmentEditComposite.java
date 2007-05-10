package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eventb.core.IAssignmentElement;
import org.rodinp.core.RodinDBException;

public class AssignmentEditComposite extends TextEditComposite {

	@Override
	public void setValue() {
		assert element instanceof IAssignmentElement;
		final IAssignmentElement aElement = (IAssignmentElement) element;
		Text text = (Text) control;
		String str = text.getText();

		if (!getValue().equals(str)) {
			try {
				aElement.setAssignmentString(str, null);
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public String getValue() {
		assert element instanceof IAssignmentElement;
		final IAssignmentElement pElement = (IAssignmentElement) element;
		try {
			return pElement.getAssignmentString();
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}


	@Override
	public void createMainComposite(FormToolkit toolkit, Composite parent) {
		createMainComposite(toolkit, parent, SWT.MULTI);
	}
	
}
