package org.eventb.internal.ui;

import org.eventb.ui.IElementLabelProvider;
import org.rodinp.core.RodinDBException;

public class VariantLabelProvider implements IElementLabelProvider {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.ui.IElementLabelProvider#getLabel(java.lang.Object)
	 */
	public String getLabel(Object obj) throws RodinDBException {
		return "Variant";
	}

}
