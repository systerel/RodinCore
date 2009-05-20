/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
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
import org.eventb.core.IEvent;
import org.eventb.core.IMachineRoot;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCMachineRoot;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.tests.GenericEventTest;
import org.rodinp.core.IRodinElement;
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
		test.containsMarkers(element, yes);
	}

	public void containsPredicates(ISCEvent element, ITypeEnvironment environment, String[] labels, String[] strings, boolean... derived) throws RodinDBException {
		test.containsGuards(element, environment, labels, strings, derived);
	}

	public ISCEvent getSCElement(IEvent element) throws RodinDBException {
		final IMachineRoot mchRoot = (IMachineRoot) element.getRoot();
		final ISCMachineRoot scRoot = mchRoot.getSCMachineRoot();
		final ISCEvent[] events = 
			test.getSCEvents(scRoot, IEvent.INITIALISATION, element.getLabel());
		return events[1];
	}

	public void save(IEvent element) throws RodinDBException {
		saveRodinFileOf(element);
	}

	public IRodinElement[] getIdents(IEvent element) throws RodinDBException {
		return element.getParameters();
	}

	public IRodinElement[] getPredicates(IEvent element) throws RodinDBException {
		return element.getGuards();
	}



}
