/*******************************************************************************
 * Copyright (c) 2005, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - removed deprecated method getType()
 *******************************************************************************/
package org.eventb.core.basis;

import org.eventb.core.IPOIdentifier;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;

/**
 * Implementation of Event-B PO typed identifier as an extension of the Rodin database.
 * <p>
 * This class is intended to be implemented by clients that want to extend this
 * internal element type.
 * </p>
 * <p>
 * This class should not be used in any other way than subclassing it
 * in a database extension. In particular, clients should not use it,
 * but rather use its associated interface <code>IPOIdentifier</code>.
 * </p>
 *
 * @author Stefan Hallerstede
 *
 * @since 1.0
 */
public class POIdentifier extends SCIdentifierElement implements IPOIdentifier {

	public POIdentifier(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType<IPOIdentifier> getElementType() {
		return ELEMENT_TYPE;
	}
	
	@Override
	@Deprecated
	public String getName() {
		return getElementName();
	}
	
}
