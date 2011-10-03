/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Systerel - Initial API and implementation
 *******************************************************************************/
package org.rodinp.core.emf.tests.basis;

import static org.rodinp.core.emf.tests.RodinEmfTestPlugin.PLUGIN_ID;

import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;

/**
 * Element to hold a dependency to another element in the database, in order to
 * introduce implicit children.
 */
public class RodinTestDependency extends InternalElement {

	public static final IInternalElementType<RodinTestDependency> ELEMENT_TYPE = RodinCore
			.getInternalElementType(PLUGIN_ID + ".testDependency");

	public static final IAttributeType.Handle DEPENDENCY_ATTRIBUTE = RodinCore
			.getHandleAttrType(PLUGIN_ID + ".testDependencyAttribute");

	public RodinTestDependency(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType<? extends IInternalElement> getElementType() {
		return ELEMENT_TYPE;
	}

	public boolean hasDependency() throws RodinDBException {
		return hasAttribute(DEPENDENCY_ATTRIBUTE);
	}

	public IRodinElement getDependency() throws RodinDBException {
		return getAttributeValue(DEPENDENCY_ATTRIBUTE);
	}

	public void setDependency(IRodinElement element) throws RodinDBException {
		setAttributeValue(DEPENDENCY_ATTRIBUTE, element, null);
	}

}
