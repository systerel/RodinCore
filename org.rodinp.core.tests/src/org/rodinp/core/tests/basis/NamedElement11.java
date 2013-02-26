/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.core.tests.basis;

import static org.rodinp.core.tests.AbstractRodinDBTests.PLUGIN_ID;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinCore;
import org.rodinp.core.basis.InternalElement;

/**
 * An element class for child elements of NamedElements.
 * 
 * @author Thomas Muller
 */
public class NamedElement11 extends InternalElement {

	private static final IInternalElementType<?> ELEMENT_TYPE = RodinCore
			.getInternalElementType(PLUGIN_ID + ".namedElement11");

	/**
	 * @param name
	 *            the value of this element
	 * @param parent
	 *            the parent of this element
	 */
	public NamedElement11(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType<? extends IInternalElement> getElementType() {
		return ELEMENT_TYPE;
	}

}
