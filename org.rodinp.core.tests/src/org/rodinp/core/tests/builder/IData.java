package org.rodinp.core.tests.builder;

import static org.rodinp.core.tests.AbstractRodinDBTests.PLUGIN_ID;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;

public interface IData extends IInternalElement {

	public static final IInternalElementType<IData> ELEMENT_TYPE = 
		RodinCore.getInternalElementType(PLUGIN_ID + ".data");

}