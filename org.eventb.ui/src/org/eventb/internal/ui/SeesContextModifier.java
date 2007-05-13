package org.eventb.internal.ui;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.core.ISeesContext;
import org.eventb.ui.IElementModifier;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

public class SeesContextModifier implements IElementModifier {

	public void modify(IRodinElement element, String text)
			throws RodinDBException {
		if (element instanceof ISeesContext) {
			ISeesContext seesContext = (ISeesContext) element;
			String seenContextName = null;
			try {
				seenContextName = seesContext.getSeenContextName();
			}
			catch (RodinDBException e) {
				// Do nothing
			}
			if (!seenContextName.equals(text))
				seesContext.setSeenContextName(text, new NullProgressMonitor());
		}
		return;
	}

}
