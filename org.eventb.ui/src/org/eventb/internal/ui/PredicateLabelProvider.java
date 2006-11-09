package org.eventb.internal.ui;

import org.eventb.core.IPredicateElement;
import org.eventb.ui.IElementLabelProvider;
import org.rodinp.core.RodinDBException;

public class PredicateLabelProvider implements IElementLabelProvider {

	public String getLabel(Object obj) {
		if (obj instanceof IPredicateElement) {
			try {
				return ((IPredicateElement) obj).getPredicateString();
			} catch (RodinDBException e) {
				if (UIUtils.DEBUG)
					e.printStackTrace();
				return null;
			}
		}
		return null;
	}

}
