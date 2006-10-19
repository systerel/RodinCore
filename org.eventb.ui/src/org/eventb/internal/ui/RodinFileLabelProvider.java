package org.eventb.internal.ui;

import org.eventb.core.EventBPlugin;
import org.eventb.ui.IElementLabelProvider;
import org.rodinp.core.IRodinFile;

public class RodinFileLabelProvider implements IElementLabelProvider {

	public String getLabel(Object obj) {
		if (obj instanceof IRodinFile) {
			return EventBPlugin.getComponentName(((IRodinFile) obj)
					.getElementName());
		}
		return null;
	}

}
