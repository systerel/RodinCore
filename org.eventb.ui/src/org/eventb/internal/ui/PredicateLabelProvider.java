package org.eventb.internal.ui;

import org.eventb.core.IPredicateElement;
import org.eventb.ui.IElementLabelProvider;
import org.rodinp.core.RodinDBException;

@Deprecated
public class PredicateLabelProvider implements IElementLabelProvider {

	public String getLabel(Object obj) throws RodinDBException {
		if (obj instanceof IPredicateElement) {
			return ((IPredicateElement) obj).getPredicateString();
		}
		return null;
	}

}
