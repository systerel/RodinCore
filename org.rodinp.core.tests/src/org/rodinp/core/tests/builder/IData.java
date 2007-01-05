package org.rodinp.core.tests.builder;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;

public interface IData extends IInternalElement {

	public static final IInternalElementType<IData> ELEMENT_TYPE = 
		RodinCore.getInternalElementType("org.rodinp.core.tests.data");

}