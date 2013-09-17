/*******************************************************************************
 * Copyright (c) 2005, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.core.basis;

import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPORoot;
import org.eventb.core.IPOSequent;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * Implementation of Event-B (unchecked) contexts as an extension of the Rodin database.
 * <p>
 * This class should not be used directly by any client except the Rodin
 * database. In particular, clients should not use it, but rather use its
 * associated interface <code>IPORoot</code>.
 * </p>
 *
 * @author Laurent Voisin
 * @since 1.0
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class PORoot extends EventBRoot implements IPORoot{
	
	/**
	 *  Constructor used by the Rodin database. 
	 */
	public PORoot(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType<IPORoot> getElementType() {
		return ELEMENT_TYPE;
	}

	@Override
	public IPOPredicateSet getPredicateSet(String elementName) {
		return getInternalElement(IPOPredicateSet.ELEMENT_TYPE, elementName);
	}

	@Override
	public IPOSequent[] getSequents() throws RodinDBException {
		return getChildrenOfType(IPOSequent.ELEMENT_TYPE); 
	}

	@Override
	public IPOPredicateSet[] getPredicateSets() throws RodinDBException {
		return getChildrenOfType(POPredicateSet.ELEMENT_TYPE);
	}

	@Override
	public IPOSequent getSequent(String elementName) {
		return getInternalElement(IPOSequent.ELEMENT_TYPE, elementName);
	}

}
