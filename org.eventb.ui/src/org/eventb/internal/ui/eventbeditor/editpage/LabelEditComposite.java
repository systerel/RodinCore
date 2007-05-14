package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eventb.core.ILabeledElement;
import org.rodinp.core.RodinDBException;

public class LabelEditComposite extends TextEditComposite {

	public void setValue() {
		assert element instanceof ILabeledElement;
		final ILabeledElement lElement = (ILabeledElement) element;
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
				lElement.setLabel(str, null);
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public String getValue() throws RodinDBException {
		assert element instanceof ILabeledElement;
		final ILabeledElement lElement = (ILabeledElement) element;
		return lElement.getLabel();
	}

	@Override
	public void createMainComposite(FormToolkit toolkit, Composite parent) {
		createMainComposite(toolkit, parent, SWT.SINGLE);
	}

	@Override
	public void setDefaultValue() {
		assert element instanceof ILabeledElement;
		final ILabeledElement lElement = (ILabeledElement) element;
		try {
			lElement.setLabel("", new NullProgressMonitor());
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.setDefaultValue();
	}
	
	
}
