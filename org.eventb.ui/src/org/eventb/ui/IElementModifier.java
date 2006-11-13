package org.eventb.ui;

import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

public interface IElementModifier {

	void modify(IRodinElement element, String text) throws RodinDBException;

}
