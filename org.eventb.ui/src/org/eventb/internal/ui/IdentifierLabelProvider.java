package org.eventb.internal.ui;

import org.eventb.core.IIdentifierElement;
import org.eventb.ui.IElementLabelProvider;
import org.rodinp.core.RodinDBException;

public class IdentifierLabelProvider implements IElementLabelProvider {

	public String getLabel(Object obj) {
		if (obj instanceof IIdentifierElement) {
			try {
				return ((IIdentifierElement) obj)
						.getIdentifierString();
			} catch (RodinDBException e) {
				if (UIUtils.DEBUG)
					e.printStackTrace();
				return null;
			}
		}
		return null;
	}

}
