/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.tests.sc;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IContextFile;
import org.eventb.core.ISCContextFile;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.tests.GenericContextTest;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class GenericContextSCTest extends GenericContextTest<BasicSCTest> implements
		IGenericSCTest<IContextFile, ISCContextFile> {

	public GenericContextSCTest(BasicSCTest test) {
		super(test);
	}

	public void containsIdents(ISCContextFile element, String... strings) throws RodinDBException {
		test.containsConstants(element, strings);
	}

	public void containsNonTheorems(ISCContextFile element, ITypeEnvironment environment, String[] labels, String[] strings) throws RodinDBException {
		test.containsAxioms(element, environment, labels, strings);
	}

	public void containsTheorems(ISCContextFile element, ITypeEnvironment environment, String[] labels, String[] strings) throws RodinDBException {
		test.containsTheorems(element, environment, labels, strings);
	}

	public ISCContextFile getSCElement(IContextFile element) throws RodinDBException {
		return element.getSCContextFile();
	}

	public void save(IContextFile element) throws RodinDBException {
		element.save(null, true);
	}

	public void containsMarkers(IContextFile element, boolean yes) throws CoreException {
		test.containsMarkers(element, yes);
	}

	public IRodinElement[] getIdents(IContextFile element) throws RodinDBException {
		return element.getConstants();
	}

	public IRodinElement[] getNonTheorems(IContextFile element) throws RodinDBException {
		return element.getAxioms();
	}

	public IRodinElement[] getTheorems(IContextFile element) throws RodinDBException {
		return element.getTheorems();
	}

}
