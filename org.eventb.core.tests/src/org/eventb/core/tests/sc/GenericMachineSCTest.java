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
package org.eventb.core.tests.sc;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IMachineRoot;
import org.eventb.core.ISCMachineRoot;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.tests.GenericMachineTest;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class GenericMachineSCTest extends GenericMachineTest<BasicSCTest> implements
		IGenericSCTest<IMachineRoot, ISCMachineRoot> {

	public GenericMachineSCTest(BasicSCTest test) {
		super(test);
	}

	public void containsIdents(ISCMachineRoot element, String... strings) throws RodinDBException {
		test.containsVariables(element, strings);
	}

	public void containsNonTheorems(ISCMachineRoot element, ITypeEnvironment environment, String[] labels, String[] strings) throws RodinDBException {
		test.containsInvariants(element, environment, labels, strings);
	}

	public void containsTheorems(ISCMachineRoot element, ITypeEnvironment environment, String[] labels, String[] strings) throws RodinDBException {
		test.containsTheorems(element, environment, labels, strings);
	}

	public ISCMachineRoot getSCElement(IMachineRoot element) throws RodinDBException {
		return element.getSCMachineRoot();
	}

//	public void save(IRodinFile element) throws RodinDBException {
//		element.save(null, true);
//	}

	public void save(IMachineRoot element) throws RodinDBException {
		element.getRodinFile().save(null, true);
	}

	
	public void containsMarkers(IMachineRoot element, boolean yes) throws CoreException {
		test.containsMarkers(element.getRodinFile(), yes);
	}

	public IRodinElement[] getIdents(IMachineRoot element) throws RodinDBException {
		return element.getVariables();
	}

	public IRodinElement[] getNonTheorems(IMachineRoot element) throws RodinDBException {
		return element.getInvariants();
	}

	public IRodinElement[] getTheorems(IMachineRoot element) throws RodinDBException {
		return element.getTheorems();
	}


}
