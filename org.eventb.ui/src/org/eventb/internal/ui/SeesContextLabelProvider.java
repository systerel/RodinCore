package org.eventb.internal.ui;

import org.eventb.core.ISeesContext;
import org.eventb.ui.IElementLabelProvider;
import org.rodinp.core.RodinDBException;

public class SeesContextLabelProvider implements IElementLabelProvider {

	public String getLabel(Object obj) throws RodinDBException {
		if (obj instanceof ISeesContext) {
			return ((ISeesContext) obj).getSeenContextName();
		}
		return null;
	}

}
