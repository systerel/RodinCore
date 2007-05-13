package org.eventb.internal.ui;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.core.IPredicateElement;
import org.eventb.ui.IElementModifier;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

public class PredicateModifier implements IElementModifier {

	public void modify(IRodinElement element, String text)
			throws RodinDBException {
		if (element instanceof IPredicateElement) {
			IPredicateElement pElement = (IPredicateElement) element;
			String predicateString = null;
			try {
				predicateString = pElement.getPredicateString();
			}
			catch (RodinDBException e) {
				// Do nothing
			}
			if (predicateString == null || !predicateString.equals(text))
				pElement.setPredicateString(text, new NullProgressMonitor());
		}
		return;
	}

}
