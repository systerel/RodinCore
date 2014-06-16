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

import static org.eventb.core.EventBAttributes.PREDICATE_ATTRIBUTE;
import static org.eventb.core.sc.ParseProblem.TypeUnknownError;
import static org.eventb.core.sc.ParseProblem.TypesDoNotMatchError;
import static org.eventb.core.tests.MarkerMatcher.marker;
import static org.eventb.core.tests.pom.POUtil.mTypeEnvironment;

import org.eventb.core.IContextRoot;
import org.eventb.core.ISCContextRoot;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.junit.Test;

/**
 * @author Stefan Hallerstede
 *
 */
public class TestAxiomsAndTheorems extends GenericPredicateTest<IContextRoot, ISCContextRoot> {
	
	
	/**
	 * check partial typing
	 */
	@Test
	public void testAxiomsAndTheorems_05_axiomPartialTyping() throws Exception {
		IContextRoot con = createContext("ctx");
		
		ITypeEnvironmentBuilder typeEnvironment = mTypeEnvironment("S1=ℙ(S1)", factory);

		addCarrierSets(con, "S1");
		addConstants(con, "C1");
		addAxioms(con, makeSList("A1", "A2"), makeSList("C1∈ℕ∪S1", "C1∈S1"), false, false);
	
		saveRodinFileOf(con);
		
		runBuilderCheck(marker(con.getAxioms()[0], PREDICATE_ATTRIBUTE, 3, 7,
				TypesDoNotMatchError, "ℤ", "S1"));
		
		ISCContextRoot file = con.getSCContextRoot();
		
		containsAxioms(file, typeEnvironment, makeSList("A2"), makeSList("C1∈S1"), false);
	}
	
	/**
	 * more on partial typing (more complex)
	 */
	@Test
	public void testAxiomsAndTheorems_06_axiomPartialTyping() throws Exception {
		IContextRoot con = createContext("ctx");
		
		ITypeEnvironmentBuilder typeEnvironment = mTypeEnvironment("S1=ℙ(S1); C1=S1", factory);
		
		addCarrierSets(con, "S1");
		addConstants(con, "C1");
		addAxioms(con, makeSList("A1", "A2", "A3", "A4"), makeSList("C1=C1", "C1∈S1", "C1∈{C1}", "S1 ⊆ {C1}"), false, false, false, false);
	
		saveRodinFileOf(con);
		
		runBuilderCheck(marker(con.getAxioms()[0], PREDICATE_ATTRIBUTE, 0, 2,
				TypeUnknownError));
		
		ISCContextRoot file = con.getSCContextRoot();
		
		containsAxioms(file, typeEnvironment, makeSList("A2", "A3", "A4"), makeSList("C1∈S1", "C1∈{C1}", "S1 ⊆ {C1}"), false, false, false);
	}

	@Override
	protected IGenericSCTest<IContextRoot, ISCContextRoot> newGeneric() {
		return new GenericContextSCTest(this);
	}
	
}
