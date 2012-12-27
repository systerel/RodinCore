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

import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IParameter;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class GenericEventTest<T extends EventBTest> 
extends GenericTest<T>  {

	public GenericEventTest(T test) {
		super(test);
	}

	public void addIdents(IEvent element, String... names) throws RodinDBException {
		for (String name : names) {
			IParameter parameter = element.createChild(IParameter.ELEMENT_TYPE, null, null);
			parameter.setIdentifierString(name, null);
		}
			
	}

	public void addPredicates(IEvent element, String[] names, String[] nonTheorems, boolean...derived) throws RodinDBException {
		for (int i=0; i<names.length; i++) {
			IGuard guard = element.createChild(IGuard.ELEMENT_TYPE, null, null);
			guard.setLabel(names[i], null);
			guard.setPredicateString(nonTheorems[i], null);
			guard.setTheorem(derived[i], null);
		}
	}

	private static int count = 0;
	
	public IEvent createElement(String bareName) throws RodinDBException {
		IMachineRoot root = test.createMachine("mch" + count++);
		test.addInitialisation(root);
		IEvent event = test.addEvent(root, bareName);
		return event;
	}


}
