/*******************************************************************************
 * Copyright (c) 2006, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Universitaet Duesseldorf - added theorem attribute
 *     Systerel - use marker matcher
 *******************************************************************************/
package org.eventb.core.tests;

import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public interface IGenericElementTest<E extends IRodinElement> {
	
	void addPredicates(E element, String[] names, String[] predicates, boolean...derived) 
	throws RodinDBException;
	
	void addIdents(E element, String... names) throws RodinDBException;
	
	void addInitialisation(E element, String... names) throws RodinDBException;
	
	abstract E createElement(String bareName) throws RodinDBException;

}
