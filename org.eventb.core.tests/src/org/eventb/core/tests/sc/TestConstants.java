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
 *     Universitaet Duesseldorf - added theorem attribute
 *******************************************************************************/
package org.eventb.core.tests.sc;

import static org.eventb.core.tests.pom.POUtil.mTypeEnvironment;

import org.eventb.core.IContextRoot;
import org.eventb.core.ISCContextRoot;
import org.eventb.core.ISCInternalContext;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.junit.Test;

/**
 * @author Stefan Hallerstede
 *
 */
public class TestConstants extends GenericIdentTest<IContextRoot, ISCContextRoot> {

	/**
	 * create constant with carrier set type
	 */
	@Test
	public void testConstants_03_constantWithCarrierSetType() throws Exception {
		IContextRoot con = createContext("ctx");

		addConstants(con, makeSList("C1"));
		addCarrierSets(con, makeSList("S1"));
		addAxioms(con, makeSList("A1"), makeSList("C1∈S1"), false);
		
		saveRodinFileOf(con);
		
		runBuilder();
		
		ITypeEnvironmentBuilder environment = mTypeEnvironment("S1=ℙ(S1); C1=S1", factory);

		ISCContextRoot file = con.getSCContextRoot();
		
		containsCarrierSets(file, "S1");
		
		containsConstants(file, "C1");
		
		containsAxioms(file, environment, makeSList("A1"), makeSList("C1∈S1"), false);
		
		containsMarkers(con, false);
	}
	
	/**
	 * copy constant from abstraction
	 */
	@Test
	public void testConstants_04_constantFromAbstraction() throws Exception {
		IContextRoot abs1 = createContext("abs1");
		addConstants(abs1, makeSList("C1"));
		addAxioms(abs1, makeSList("A1"), makeSList("C1∈ℕ"), false);
		
		saveRodinFileOf(abs1);
		
		runBuilder();
		
		IContextRoot con = createContext("ctx");
		addContextExtends(con, "abs1");

		addConstants(con, makeSList("C2"));
		addAxioms(con, makeSList("A1"), makeSList("C2∈ℕ"), false);
		
		saveRodinFileOf(con);
		
		runBuilder();

		ISCContextRoot file = con.getSCContextRoot();
		
		containsConstants(file, "C2");
	
		ISCInternalContext[] contexts = getInternalContexts(file, 1);
		
		containsConstants(contexts[0], "C1");

		containsMarkers(con, false);
	}
	
	/**
	 * name conflict with constant from abstraction
	 */
	@Test
	public void testConstants_05_constantFromAbstractionNameConflict() throws Exception {
		IContextRoot abs1 = createContext("abs1");
		addConstants(abs1, makeSList("C1"));
		addAxioms(abs1, makeSList("A1"), makeSList("C1∈ℕ"), true);
		
		saveRodinFileOf(abs1);
		
		runBuilder();
		
		IContextRoot con = createContext("ctx");

		addContextExtends(con, "abs1");

		addConstants(con, makeSList("C1"));
		addAxioms(con, makeSList("A1"), makeSList("C1∈ℕ"), true);
		
		saveRodinFileOf(con);
		
		runBuilder();

		ISCContextRoot file = con.getSCContextRoot();
		
		containsConstants(file);
	
		ISCInternalContext[] contexts = getInternalContexts(file, 1);
		
		containsConstants(contexts[0], "C1");

		hasMarker(con.getConstants()[0]);
		hasMarker(con.getExtendsClauses()[0]);
	}

	/**
	 * constant type across axioms
	 */
	@Test
	public void testConstants_06_constantTypingOK() throws Exception {
		IContextRoot con = createContext("ctx");

		addConstants(con, makeSList("d"));
		addAxioms(con, makeSList("A1", "A2"), makeSList("d∈ℕ", "d>0"), false, false);
		
		ITypeEnvironmentBuilder typeEnvironment = mTypeEnvironment("d=ℤ", factory);
		
		saveRodinFileOf(con);
		
		runBuilder();
		
		ISCContextRoot file = con.getSCContextRoot();
		
		containsConstants(file, "d");
		containsAxioms(file, typeEnvironment, makeSList("A1", "A2"), makeSList("d∈ℕ", "d>0"), false, false);
		
		containsMarkers(con, false);
	}

	/**
	 * name conflict with constant from abstraction of abstraction
	 */
	@Test
	public void testConstants_07_constantFromAbstractionAbstractionNameConflict() throws Exception {
		final IContextRoot root1 = createContext("c1");
		addConstants(root1, makeSList("C1"));
		addAxioms(root1, makeSList("A1"), makeSList("C1∈ℕ"), false);
		saveRodinFileOf(root1);
		runBuilder();
		
		final IContextRoot root2 = createContext("c2");
		addContextExtends(root2, root1.getComponentName());
		saveRodinFileOf(root2);
		runBuilder();

		final IContextRoot root3 = createContext("c3");
		addContextExtends(root3, root2.getComponentName());
		addConstants(root3, makeSList("C1"));
		addAxioms(root3, makeSList("A1"), makeSList("C1∈ℕ"), false);
		saveRodinFileOf(root3);
		runBuilder();

		final ISCContextRoot file = root3.getSCContextRoot();
		containsConstants(file);
		final ISCInternalContext[] contexts = getInternalContexts(file, 2);
		containsConstants(contexts[0], "C1");
		hasMarker(root3.getConstants()[0]);
		hasMarker(root3.getExtendsClauses()[0]);
	}

	@Override
	protected IGenericSCTest<IContextRoot, ISCContextRoot> newGeneric() {
		return new GenericContextSCTest(this);
	}

}
