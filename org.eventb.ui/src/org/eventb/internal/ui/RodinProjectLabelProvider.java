package org.eventb.internal.ui;

import org.eventb.ui.IElementLabelProvider;
import org.rodinp.core.IRodinProject;

public class RodinProjectLabelProvider implements IElementLabelProvider {

	public String getLabel(Object obj) {
		if (obj instanceof IRodinProject) {
			return ((IRodinProject) obj).getElementName();
		}
		return null;
	}

}
