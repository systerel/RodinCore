package org.eventb.internal.ui;

import org.eventb.core.IIdentifierElement;
import org.eventb.ui.IElementLabelProvider;
import org.rodinp.core.RodinDBException;

@Deprecated
public class IdentifierLabelProvider implements IElementLabelProvider {

	public String getLabel(Object obj) throws RodinDBException {
		if (obj instanceof IIdentifierElement) {
			return ((IIdentifierElement) obj).getIdentifierString();
		}
		return null;
	}

}
