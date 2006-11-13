package org.eventb.internal.ui;

import org.eventb.core.ISeesContext;
import org.eventb.ui.IElementLabelProvider;
import org.rodinp.core.RodinDBException;

public class SeesContextLabelProvider implements IElementLabelProvider {

	public String getLabel(Object obj) {
		if (obj instanceof ISeesContext) {
			try {
				return ((ISeesContext) obj).getSeenContextName(null);
			} catch (RodinDBException e) {
				if (UIUtils.DEBUG) {
					e.printStackTrace();
				}
				return null;
			}
		}
		return null;
	}

}
