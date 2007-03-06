/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.tests;

import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public interface IGenericElementTest<E extends IRodinElement> {
	
	void addTheorems(E element, String[] names, String[] theorems) 
	throws RodinDBException;

	void addNonTheorems(E element, String[] names, String[] nonTheorems) 
	throws RodinDBException;
	
	void addIdents(E element, String... names) throws RodinDBException;
	
	abstract E createElement(String bareName) throws RodinDBException;

}
