package org.eventb.internal.ui;

import org.eventb.core.IIdentifierElement;
import org.eventb.ui.IElementModifier;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

public class IdentifierModifier implements IElementModifier {

	public void modify(IRodinElement element, String text)
			throws RodinDBException {
		if (element instanceof IIdentifierElement) {
			((IIdentifierElement) element).setIdentifierString(text);
		}
		return;
	}

}
