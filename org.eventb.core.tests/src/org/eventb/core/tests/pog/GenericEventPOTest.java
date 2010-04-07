/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.tests.pog;

import org.eventb.core.IEvent;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IPORoot;
import org.eventb.core.tests.GenericEventTest;
import org.rodinp.core.RodinDBException;

/**
 * @author Laurent Voisin
 */
public class GenericEventPOTest extends GenericEventTest<EventBPOTest>
		implements IGenericPOTest<IEvent> {

	public GenericEventPOTest(EventBPOTest test) {
		super(test);
	}

	public void addSuper(IEvent event, IEvent abs) throws RodinDBException {
		final String absName = getMachineRoot(abs).getElementName();
		test.addMachineRefines(getMachineRoot(event), absName);
		test.addEventRefines(event, abs.getLabel());
	}

	private IMachineRoot getMachineRoot(IEvent event) {
		return (IMachineRoot) event.getRoot();
	}

	public IPORoot getPOFile(IEvent event) throws RodinDBException {
		final IEventBRoot root = (IEventBRoot) event.getRoot();
		return root.getPORoot();
	}

}
