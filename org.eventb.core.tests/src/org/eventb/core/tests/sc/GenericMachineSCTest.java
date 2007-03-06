/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.tests.sc;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IMachineFile;
import org.eventb.core.ISCMachineFile;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.tests.GenericMachineTest;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class GenericMachineSCTest extends GenericMachineTest<BasicSCTest> implements
		IGenericSCTest<IMachineFile, ISCMachineFile> {

	public GenericMachineSCTest(BasicSCTest test) {
		super(test);
	}

	public void containsIdents(ISCMachineFile element, String... strings) throws RodinDBException {
		test.containsVariables(element, strings);
	}

	public void containsNonTheorems(ISCMachineFile element, ITypeEnvironment environment, String[] labels, String[] strings) throws RodinDBException {
		test.containsInvariants(element, environment, labels, strings);
	}

	public void containsTheorems(ISCMachineFile element, ITypeEnvironment environment, String[] labels, String[] strings) throws RodinDBException {
		test.containsTheorems(element, environment, labels, strings);
	}

	public ISCMachineFile getSCElement(IMachineFile element) throws RodinDBException {
		return element.getSCMachineFile();
	}

	public void save(IMachineFile element) throws RodinDBException {
		element.save(null, true);
	}

	public void containsMarkers(IMachineFile element, boolean yes) throws CoreException {
		test.containsMarkers(element, yes);
	}


}
