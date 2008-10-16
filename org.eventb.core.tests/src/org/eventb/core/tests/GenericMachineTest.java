/*******************************************************************************
 * Copyright (c) 2006, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.core.tests;

import org.eventb.core.IMachineRoot;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class GenericMachineTest<T extends EventBTest> 
extends GenericTest<T>
implements IGenericElementTest<IMachineRoot> {
	
	public void addIdents(IMachineRoot element, String... names) throws RodinDBException {
		test.addVariables(element, names);
	}

	public void addNonTheorems(IMachineRoot element, String[] names, String[] nonTheorems) throws RodinDBException {
		test.addInvariants(element, names, nonTheorems);
	}

	public void addTheorems(IMachineRoot element, String[] names, String[] theorems) throws RodinDBException {
		test.addTheorems(element, names, theorems);
	}

	public IMachineRoot createElement(String bareName) throws RodinDBException {
		return test.createMachine(bareName);
	}

	public GenericMachineTest(final T test) {
		super(test);
	}

}
