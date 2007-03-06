/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.tests;

import org.eventb.core.IContextFile;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class GenericContextTest<T extends EventBTest> 
extends GenericTest<T>
implements IGenericElementTest<IContextFile> {

	public void addIdents(IContextFile element, String... names)
			throws RodinDBException {
		test.addConstants(element, names);
	}

	public void addNonTheorems(IContextFile element, String[] names,
			String[] nonTheorems) throws RodinDBException {
		test.addAxioms(element, names, nonTheorems);
	}

	public void addTheorems(IContextFile element, String[] names,
			String[] theorems) throws RodinDBException {
		test.addTheorems(element, names, theorems);
	}

	public IContextFile createElement(String bareName)
			throws RodinDBException {
		return test.createContext(bareName);
	}
	
	public GenericContextTest(final T test) {
		super(test);
	}

}
