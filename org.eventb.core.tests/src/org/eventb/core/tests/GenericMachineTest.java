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
 *     University of Dusseldorf - added theorem attribute
 *******************************************************************************/
package org.eventb.core.tests;

import org.eventb.core.IAction;
import org.eventb.core.IEvent;
import org.eventb.core.IMachineRoot;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class GenericMachineTest<T extends EventBTest> 
extends GenericTest<T>
implements IGenericElementTest<IMachineRoot> {
	
	IEvent init;
	int k = 0;
	
	@Override
	public void addIdents(IMachineRoot element, String... names) throws RodinDBException {
		test.addVariables(element, names);
		IAction action = init.createChild(IAction.ELEMENT_TYPE, null, null);
		for (String name : names) {
			action.setLabel(name + k++, null);
			action.setAssignmentString(name + ":∣ ⊤", null);
		}
	}

	@Override
	public void addPredicates(IMachineRoot element, String[] names, String[] nonTheorems, boolean...derived) throws RodinDBException {
		test.addInvariants(element, names, nonTheorems, derived);
	}

	@Override
	public IMachineRoot createElement(String bareName) throws RodinDBException {
		IMachineRoot mac = test.createMachine(bareName);
		init = test.addInitialisation(mac);
		return mac;
	}

	public GenericMachineTest(final T test) {
		super(test);
	}

}
