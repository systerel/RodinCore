package org.eventb.internal.ui;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.core.ILabeledElement;
import org.eventb.ui.IElementModifier;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

public class LabelModifier implements IElementModifier {

	public void modify(IRodinElement element, String text)
			throws RodinDBException {
		if (element instanceof ILabeledElement) {
			ILabeledElement lElement = (ILabeledElement) element;
			if (!lElement.getLabel().equals(text))
				lElement.setLabel(text, new NullProgressMonitor());
		}
		return;
	}

}
