package org.eventb.internal.ui;

import org.eventb.ui.IElementLabelProvider;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

public class RodinProjectLabelProvider implements IElementLabelProvider {

	public String getLabel(Object obj) throws RodinDBException {
		if (obj instanceof IRodinProject) {
			return ((IRodinProject) obj).getElementName();
		}
		return null;
	}

}
