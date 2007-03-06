/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.tests;

import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IMachineFile;
import org.eventb.core.IVariable;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class GenericEventTest<T extends EventBTest> 
extends GenericTest<T> 
implements IGenericElementTest<IEvent> {

	public GenericEventTest(T test) {
		super(test);
	}

	public void addIdents(IEvent element, String... names) throws RodinDBException {
		for (String name : names) {
			IVariable variable = element.getVariable(test.getUniqueName());
			variable.create(null, null);
			variable.setIdentifierString(name, null);
		}
			
	}

	public void addNonTheorems(IEvent element, String[] names, String[] nonTheorems) throws RodinDBException {
		for (int i=0; i<names.length; i++) {
			IGuard guard = element.getGuard(test.getUniqueName());
			guard.create(null, null);
			guard.setLabel(names[i], null);
			guard.setPredicateString(nonTheorems[i], null);
		}
	}

	public void addTheorems(IEvent element, String[] names, String[] theorems) throws RodinDBException {
// TODO implement proper tests for theorems
		for (int i=0; i<names.length; i++) {
			IGuard guard = element.getGuard(test.getUniqueName());
			guard.create(null, null);
			guard.setLabel(names[i], null);
			guard.setPredicateString(theorems[i], null);
		}
	}

	public IEvent createElement(String bareName) throws RodinDBException {
		IMachineFile file = test.createMachine("mch");
		test.addInitialisation(file);
		IEvent event = test.addEvent(file, bareName);
		return event;
	}


}
