package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eventb.core.IPredicateElement;
import org.rodinp.core.RodinDBException;

public class PredicateEditComposite extends TextEditComposite {

	@Override
	public void setValue() {
		assert element instanceof IPredicateElement;
		final IPredicateElement pElement = (IPredicateElement) element;
		Text text = (Text) control;
		String str = text.getText();

		if (!getValue().equals(str)) {
			try {
				pElement.setPredicateString(str, null);
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public String getValue() {
		assert element instanceof IPredicateElement;
		final IPredicateElement pElement = (IPredicateElement) element;
		try {
			return pElement.getPredicateString();
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
