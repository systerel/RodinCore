package org.eventb.internal.ui;

import org.eventb.ui.IElementLabelProvider;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

public class RodinFileLabelProvider implements IElementLabelProvider {

	public String getLabel(Object obj) throws RodinDBException {
		if (obj instanceof IRodinFile) {
			return ((IRodinFile) obj).getBareName();
		}
		return null;
	}

}
