package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;
import org.eventb.core.IPredicateElement;
import org.rodinp.core.RodinDBException;

public class PredicateEditComposite extends TextEditComposite {

	public void setValue() {
		assert element instanceof IPredicateElement;
		final IPredicateElement pElement = (IPredicateElement) element;
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
				pElement.setPredicateString(str, null);
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public String getValue() throws RodinDBException {
		assert element instanceof IPredicateElement;
		final IPredicateElement pElement = (IPredicateElement) element;
		return pElement.getPredicateString();
	}

	@Override
	public void setDefaultValue() {
		assert element instanceof IPredicateElement;
		final IPredicateElement pElement = (IPredicateElement) element;
		try {
			pElement.setPredicateString("", new NullProgressMonitor());
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
