/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.tests.sc;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IEvent;
import org.eventb.core.IMachineFile;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCMachineFile;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.tests.GenericEventTest;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class GenericEventSCTest extends GenericEventTest<BasicSCTest> implements
		IGenericSCTest<IEvent, ISCEvent> {

	public GenericEventSCTest(BasicSCTest test) {
		super(test);
	}

	public void containsIdents(ISCEvent element, String... strings) throws RodinDBException {
		test.containsParameters(element, strings);
	}

	public void containsMarkers(IEvent element, boolean yes) throws CoreException {
		IRodinFile rodinFile = element.getRodinFile();
		test.containsMarkers(rodinFile, yes);
	}

	public void containsNonTheorems(ISCEvent element, ITypeEnvironment environment, String[] labels, String[] strings) throws RodinDBException {
		test.containsGuards(element, environment, labels, strings);
	}

	public void containsTheorems(ISCEvent element, ITypeEnvironment environment, String[] labels, String[] strings) throws RodinDBException {
		// TODO implement proper tests for theorems
		test.containsGuards(element, environment, labels, strings);		
	}

	public ISCEvent getSCElement(IEvent element) throws RodinDBException {
		ISCMachineFile scFile = ((IMachineFile) element.getRodinFile()).getSCMachineFile();
		ISCEvent[] events = 
			test.getSCEvents(scFile, IEvent.INITIALISATION, element.getLabel());
		return events[1];
	}

	public void save(IEvent element) throws RodinDBException {
		element.getRodinFile().save(null, true);
	}

	public IRodinElement[] getIdents(IEvent element) throws RodinDBException {
		return element.getParameters();
	}

	public IRodinElement[] getNonTheorems(IEvent element) throws RodinDBException {
		return element.getGuards();
	}

	public IRodinElement[] getTheorems(IEvent element) throws RodinDBException {
		// TODO adapt to new db layout with mix of guards and theorems
		return new IRodinElement[0];
	}


}
