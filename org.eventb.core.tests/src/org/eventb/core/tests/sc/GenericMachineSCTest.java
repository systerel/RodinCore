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
package org.eventb.core.tests.sc;

import static org.eventb.core.tests.BuilderTest.saveRodinFileOf;

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

	@Override
	public void containsIdents(ISCMachineRoot element, String... strings) throws RodinDBException {
		test.containsVariables(element, strings);
	}

	@Override
	public void containsPredicates(ISCMachineRoot element, ITypeEnvironment environment, String[] labels, String[] strings, boolean...derived) throws RodinDBException {
		test.containsInvariants(element, environment, labels, strings, derived);
	}

	@Override
	public ISCMachineRoot getSCElement(IMachineRoot element) throws RodinDBException {
		return element.getSCMachineRoot();
	}

	@Override
	public void save(IMachineRoot element) throws RodinDBException {
		saveRodinFileOf(element);
	}

	@Override
	public void containsMarkers(IMachineRoot element, boolean yes) throws CoreException {
		test.containsMarkers(element, yes);
	}

	@Override
	public IRodinElement[] getIdents(IMachineRoot element) throws RodinDBException {
		return element.getVariables();
	}

	@Override
	public IRodinElement[] getPredicates(IMachineRoot element) throws RodinDBException {
		return element.getInvariants();
	}

}
