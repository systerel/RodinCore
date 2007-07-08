package org.eventb.internal.ui.propertiesView;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.core.IEvent;
import org.rodinp.core.RodinDBException;

public class InheritanceSection extends CComboSection {

	private static final String TRUE = "true";

	private static final String FALSE = "false";

	@Override
	String getLabel() {
		return "Inherited";
	}

	@Override
	String getText() throws RodinDBException {
		if (((IEvent) element).isInherited())
			return TRUE;
		else {
			return FALSE;
		}
	}

	@Override
	void setData() {
		comboWidget.add(TRUE);
		comboWidget.add(FALSE);
	}

	@Override
	void setText(String text, IProgressMonitor monitor) throws RodinDBException {
		IEvent eElement = (IEvent) element;
		boolean inherited;
		try {
			inherited = eElement.isInherited();
		} catch (RodinDBException e) {
			// Set the attribute anyway if there was a problem accessing the
			// database
			eElement.setInherited(text.equalsIgnoreCase(FALSE),
					monitor);
			return;
		}
		if (inherited && text.equalsIgnoreCase(FALSE)) {
			eElement.setInherited(false, new NullProgressMonitor());
		} else if (!inherited && text.equalsIgnoreCase(TRUE)) {
			eElement.setInherited(true, new NullProgressMonitor());
		}
	}

}
