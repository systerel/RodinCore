/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.tests.pog;

import org.eventb.core.IMachineFile;
import org.eventb.core.IPOFile;
import org.eventb.core.tests.GenericMachineTest;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class GenericMachinePOTest extends GenericMachineTest<EventBPOTest> 
implements IGenericPOTest<IMachineFile> {

	public GenericMachinePOTest(EventBPOTest test) {
		super(test);
	}

	public void addSuper(IMachineFile file, String name) throws RodinDBException {
		test.addMachineRefines(file, name);
	}

	public IPOFile getPOFile(IMachineFile file) throws RodinDBException {
		return file.getPOFile();
	}

}
