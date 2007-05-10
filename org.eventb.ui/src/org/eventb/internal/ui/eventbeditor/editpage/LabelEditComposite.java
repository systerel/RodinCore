package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eventb.core.ILabeledElement;
import org.rodinp.core.RodinDBException;

public class LabelEditComposite extends TextEditComposite {

	@Override
	public void setValue() {
		assert element instanceof ILabeledElement;
		final ILabeledElement lElement = (ILabeledElement) element;
		Text text = (Text) control;
		String str = text.getText();

		if (!getValue().equals(str)) {
			try {
				lElement.setLabel(str, null);
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public String getValue() {
		assert element instanceof ILabeledElement;
		final ILabeledElement lElement = (ILabeledElement) element;
		try {
			return lElement.getLabel();
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	@Override
	public void createMainComposite(FormToolkit toolkit, Composite parent) {
		createMainComposite(toolkit, parent, SWT.SINGLE);
	}
}
