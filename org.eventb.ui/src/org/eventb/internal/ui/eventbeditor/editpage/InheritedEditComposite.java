package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.custom.CCombo;
import org.eventb.core.IEvent;
import org.rodinp.core.RodinDBException;

public class InheritedEditComposite extends CComboEditComposite {

	private final String TRUE = "true";
	
	private final String FALSE = "false";
	
	public String getValue() throws RodinDBException {
		IEvent event = (IEvent) element;
		return event.isInherited() ? TRUE : FALSE;
	}

	@Override
	public void refresh() {
		CCombo combo = (CCombo) control;
		if (combo.getItemCount() != 2) {
			combo.removeAll();
			combo.add(TRUE);
			combo.add(FALSE);
		}
		super.refresh();
	}

	public void setValue() {
		assert element instanceof IEvent;
		CCombo combo = (CCombo) control;
		IEvent event = (IEvent) element;
		String str = combo.getText();

		String value;
		try {
			value = getValue();
		} catch (RodinDBException e) {
			value = null;
		}
		if (value == null || !value.equals(str)) {
			try {
				event.setInherited(str.equalsIgnoreCase(TRUE),
						new NullProgressMonitor());
			} catch (RodinDBException exc) {
				// TODO Auto-generated catch block
				exc.printStackTrace();
			}

		}
	}

	@Override
	public void initialise() {
		CCombo combo = (CCombo) control;
		combo.add(TRUE);
		combo.add(FALSE);
		super.initialise();
	}

	@Override
	public void setDefaultValue() {
		IEvent event = (IEvent) element;
		try {
			event.setInherited(false,
					new NullProgressMonitor());
		} catch (RodinDBException exc) {
			// TODO Auto-generated catch block
			exc.printStackTrace();
		}
		super.setDefaultValue();
	}

}
