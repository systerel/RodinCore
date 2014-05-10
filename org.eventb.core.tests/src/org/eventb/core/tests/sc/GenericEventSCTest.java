/*******************************************************************************
 * Copyright (c) 2006, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     Universitaet Duesseldorf - added theorem attribute
 *     Systerel - use marker matcher
 *******************************************************************************/
package org.eventb.core.tests.sc;

import static org.eventb.core.sc.GraphProblem.GuardLabelConflictError;
import static org.eventb.core.sc.GraphProblem.GuardLabelConflictWarning;
import static org.eventb.core.sc.GraphProblem.ParameterNameConflictError;
import static org.eventb.core.sc.GraphProblem.UntypedParameterError;
import static org.eventb.core.tests.BuilderTest.saveRodinFileOf;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IEvent;
import org.eventb.core.IMachineRoot;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCMachineRoot;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.tests.GenericEventTest;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinProblem;
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

	@Override
	public void containsIdents(ISCEvent element, String... strings) throws RodinDBException {
		test.containsParameters(element, strings);
	}

	@Override
	public void containsMarkers(IEvent element, boolean yes) throws CoreException {
		test.containsMarkers(element, yes);
	}

	@Override
	public void containsPredicates(ISCEvent element, ITypeEnvironment environment, String[] labels, String[] strings, boolean... derived) throws RodinDBException {
		test.containsGuards(element, environment, labels, strings, derived);
	}

	@Override
	public ISCEvent getSCElement(IEvent element) throws RodinDBException {
		final IMachineRoot mchRoot = (IMachineRoot) element.getRoot();
		final ISCMachineRoot scRoot = mchRoot.getSCMachineRoot();
		final ISCEvent[] events = 
			test.getSCEvents(scRoot, IEvent.INITIALISATION, element.getLabel());
		return events[1];
	}

	@Override
	public void save(IEvent element) throws RodinDBException {
		saveRodinFileOf(element);
	}

	@Override
	public IInternalElement[] getIdents(IEvent element) throws RodinDBException {
		return element.getParameters();
	}

	@Override
	public IInternalElement[] getPredicates(IEvent element) throws RodinDBException {
		return element.getGuards();
	}

	@Override
	public IRodinProblem getUntypedProblem() {
		return UntypedParameterError;
	}

	@Override
	public IRodinProblem getIdentConflictProblem() {
		return ParameterNameConflictError;
	}

	@Override
	public IRodinProblem getLabelConflictError() {
		return GuardLabelConflictError;
	}

	@Override
	public IRodinProblem getLabelConflictWarning() {
		return GuardLabelConflictWarning;
	}

}
