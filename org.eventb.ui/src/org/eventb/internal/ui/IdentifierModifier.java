package org.eventb.internal.ui;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.core.IIdentifierElement;
import org.eventb.ui.IElementModifier;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

public class IdentifierModifier implements IElementModifier {

	public void modify(IRodinElement element, String text)
			throws RodinDBException {
		if (element instanceof IIdentifierElement) {
			IIdentifierElement iElement = (IIdentifierElement) element;
			String identifierString = null;
			try {
				identifierString = iElement.getIdentifierString();
			}
			catch (RodinDBException e) {
				// Do nothing
			}
			if (identifierString == null || !identifierString.equals(text))
				iElement.setIdentifierString(text, new NullProgressMonitor());
		}
		return;
	}

}
