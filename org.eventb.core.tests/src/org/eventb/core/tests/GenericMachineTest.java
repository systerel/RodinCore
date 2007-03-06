/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.tests;

import org.eventb.core.IMachineFile;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class GenericMachineTest<T extends EventBTest> 
extends GenericTest<T>
implements IGenericElementTest<IMachineFile> {
	
	public void addIdents(IMachineFile element, String... names) throws RodinDBException {
		test.addVariables(element, names);
	}

	public void addNonTheorems(IMachineFile element, String[] names, String[] nonTheorems) throws RodinDBException {
		test.addInvariants(element, names, nonTheorems);
	}

	public void addTheorems(IMachineFile element, String[] names, String[] theorems) throws RodinDBException {
		test.addTheorems(element, names, theorems);
	}

	public IMachineFile createElement(String bareName) throws RodinDBException {
		return test.createMachine(bareName);
	}

	public GenericMachineTest(final T test) {
		super(test);
	}

}
