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

import static org.eventb.core.sc.GraphProblem.AxiomLabelConflictError;
import static org.eventb.core.sc.GraphProblem.AxiomLabelConflictWarning;
import static org.eventb.core.sc.GraphProblem.ConstantNameConflictError;
import static org.eventb.core.sc.GraphProblem.UntypedConstantError;
import static org.eventb.core.tests.BuilderTest.saveRodinFileOf;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IContextRoot;
import org.eventb.core.ISCContextRoot;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.tests.GenericContextTest;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinProblem;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class GenericContextSCTest extends GenericContextTest<BasicSCTest> implements
		IGenericSCTest<IContextRoot, ISCContextRoot> {

	public GenericContextSCTest(BasicSCTest test) {
		super(test);
	}

	@Override
	public void containsIdents(ISCContextRoot element, String... strings) throws RodinDBException {
		test.containsConstants(element, strings);
	}

	@Override
	public void containsPredicates(ISCContextRoot element, ITypeEnvironment environment, String[] labels, String[] strings, boolean... derived) throws RodinDBException {
		test.containsAxioms(element, environment, labels, strings, derived);
	}

	@Override
	public ISCContextRoot getSCElement(IContextRoot element) throws RodinDBException {
		return element.getSCContextRoot();
	}

	@Override
	public void save(IContextRoot element) throws RodinDBException {
		saveRodinFileOf(element);
	}

	@Override
	public void containsMarkers(IContextRoot element, boolean yes) throws CoreException {
		test.containsMarkers(element, yes);
	}

	@Override
	public IInternalElement[] getIdents(IContextRoot element) throws RodinDBException {
		return element.getConstants();
	}

	@Override
	public IInternalElement[] getPredicates(IContextRoot element) throws RodinDBException {
		return element.getAxioms();
	}

	@Override
	public IRodinProblem getUntypedProblem() {
		return UntypedConstantError;
	}

	@Override
	public IRodinProblem getIdentConflictProblem() {
		return ConstantNameConflictError;
	}

	@Override
	public IRodinProblem getLabelConflictError() {
		return AxiomLabelConflictError;
	}

	@Override
	public IRodinProblem getLabelConflictWarning() {
		return AxiomLabelConflictWarning;
	}
}
