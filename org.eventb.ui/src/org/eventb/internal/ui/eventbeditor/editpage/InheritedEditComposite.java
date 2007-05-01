package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.custom.CCombo;
import org.eventb.core.IEvent;
import org.rodinp.core.RodinDBException;

public class InheritedEditComposite extends CComboEditComposite {

	private final String TRUE = "true";
	
	private final String FALSE = "false";
	
	@Override
	public String getValue() {
		IEvent event = (IEvent) element;
		try {
			return event.isInherited() ? TRUE : FALSE;
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return FALSE;
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

	@Override
	public void setValue() {
		assert element instanceof IEvent;
		CCombo combo = (CCombo) control;
		IEvent event = (IEvent) element;
		String str = combo.getText();

		if (!getValue().equals(str)) {
			try {
				event.setInherited(str.equalsIgnoreCase(TRUE),
						new NullProgressMonitor());
			} catch (RodinDBException exc) {
				// TODO Auto-generated catch block
				exc.printStackTrace();
			}

		}
	}

}
