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
 *******************************************************************************/
package org.eventb.core.tests.pog;

import org.eventb.core.IMachineRoot;
import org.eventb.core.IPORoot;
import org.eventb.core.tests.GenericMachineTest;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class GenericMachinePOTest extends GenericMachineTest<EventBPOTest>
		implements IGenericPOTest<IMachineRoot> {

	public GenericMachinePOTest(EventBPOTest test) {
		super(test);
	}

	@Override
	public void addSuper(IMachineRoot root, IMachineRoot abstraction)
			throws RodinDBException {
		test.addMachineRefines(root, abstraction.getElementName());
	}

	@Override
	public IPORoot getPOFile(IMachineRoot root) throws RodinDBException {
		return root.getPORoot();
	}

}
