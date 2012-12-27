/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     Universitaet Duesseldorf - added theorem attribute
 *******************************************************************************/
package org.eventb.core.tests;

import org.eventb.core.IContextRoot;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class GenericContextTest<T extends EventBTest> 
extends GenericTest<T>
implements IGenericElementTest<IContextRoot> {

	@Override
	public void addIdents(IContextRoot element, String... names)
			throws RodinDBException {
		test.addConstants(element, names);
	}

	@Override
	public void addPredicates(IContextRoot element, String[] names,
			String[] nonTheorems, boolean...derived) throws RodinDBException {
		test.addAxioms(element, names, nonTheorems, derived);
	}

	@Override
	public IContextRoot createElement(String bareName) throws RodinDBException {
		return test.createContext(bareName);
	}
	
	public GenericContextTest(final T test) {
		super(test);
	}

}
