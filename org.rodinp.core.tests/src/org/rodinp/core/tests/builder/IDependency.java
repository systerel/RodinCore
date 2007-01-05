package org.rodinp.core.tests.builder;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;

public interface IDependency extends IInternalElement {

	public static final IInternalElementType<IDependency> ELEMENT_TYPE = 
		RodinCore.getInternalElementType("org.rodinp.core.tests.dependency");

}