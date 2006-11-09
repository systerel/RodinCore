package org.eventb.internal.ui;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.core.ILabeledElement;
import org.eventb.ui.IElementLabelProvider;
import org.rodinp.core.RodinDBException;

public class LabelLabelProvider implements IElementLabelProvider {

	public String getLabel(Object obj) {
		if (obj instanceof ILabeledElement) {
			try {
				return ((ILabeledElement) obj).getLabel(new NullProgressMonitor());
			} catch (RodinDBException e) {
				if (UIUtils.DEBUG)
					e.printStackTrace();
				return null;
			}
		}
		return null;
	}

}
