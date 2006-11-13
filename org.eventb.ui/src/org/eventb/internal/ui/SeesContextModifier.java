package org.eventb.internal.ui;

import org.eventb.core.ISeesContext;
import org.eventb.ui.IElementModifier;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

public class SeesContextModifier implements IElementModifier {

	public void modify(IRodinElement element, String text)
			throws RodinDBException {
		if (element instanceof ISeesContext) {
			((ISeesContext) element).setSeenContextName(text);
		}
		return;
	}

}
