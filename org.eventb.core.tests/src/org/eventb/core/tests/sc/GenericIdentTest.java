/*******************************************************************************
 * Copyright (c) 2006, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - ensure that all AST problems are reported
 *     Universitaet Duesseldorf - added theorem attribute
 *     Systerel - added check on primed identifiers
 *     Systerel - use marker matcher
 *******************************************************************************/
package org.eventb.core.tests.sc;

import static org.eventb.core.EventBAttributes.IDENTIFIER_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.PREDICATE_ATTRIBUTE;
import static org.eventb.core.sc.GraphProblem.FreeIdentifierFaultyDeclError;
import static org.eventb.core.sc.GraphProblem.InvalidIdentifierError;
import static org.eventb.core.sc.GraphProblem.UndeclaredFreeIdentifierError;
import static org.eventb.core.sc.ParseProblem.LexerError;
import static org.eventb.core.tests.MarkerMatcher.marker;
import static org.eventb.core.tests.pom.POUtil.mTypeEnvironment;

import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.junit.Test;
import org.rodinp.core.IRodinElement;


/**
 * @author Stefan Hallerstede
 *
 */
public abstract class GenericIdentTest <E extends IRodinElement, SCE extends IRodinElement> 
extends GenericEventBSCTest<E, SCE> {

	/**
	 * Creating a constant or variable without a type must fail
	 */
	@Test
	public void testIdents_00() throws Exception {
		E cmp = getGeneric().createElement("cmp");

		getGeneric().addIdents(cmp, makeSList("V1"));
		
		getGeneric().save(cmp);
		
		runBuilderCheck(marker(getGeneric().getIdents(cmp)[0],
				IDENTIFIER_ATTRIBUTE, getGeneric().getUntypedProblem(), "V1"));
		
		SCE file = getGeneric().getSCElement(cmp);
		
		getGeneric().containsIdents(file);
	}

	/**
	 * Creating a constant or variable with a type must succeed
	 */
	@Test
	public void testIdents_01() throws Exception {
		E cmp = getGeneric().createElement("cmp");

		getGeneric().addIdents(cmp, makeSList("V1"));
		getGeneric().addPredicates(cmp, makeSList("I1"), makeSList("V1∈ℤ"), false);
		getGeneric().addInitialisation(cmp, "V1");
		
		getGeneric().save(cmp);
		
		runBuilderCheck();
		
		final ITypeEnvironmentBuilder environment = mTypeEnvironment("V1=ℤ", factory);
		
		SCE file = getGeneric().getSCElement(cmp);
		
		getGeneric().containsIdents(file, "V1");
		
		getGeneric().containsPredicates(file, environment, makeSList("I1"), makeSList("V1∈ℤ"), false);
	}
	
	/**
	 * refering to a nondeclared identifier should fail
	 */
	@Test
	public void testIdents_02() throws Exception {
		E cmp = getGeneric().createElement("cmp");

		getGeneric().addIdents(cmp, makeSList("V1"));
		getGeneric().addPredicates(cmp, makeSList("I1"), makeSList("V2∈ℤ"), false);

		getGeneric().save(cmp);
		
		runBuilderCheck(
				marker(getGeneric().getIdents(cmp)[0], IDENTIFIER_ATTRIBUTE,
						getGeneric().getUntypedProblem(), "V1"),
				marker(getGeneric().getPredicates(cmp)[0], PREDICATE_ATTRIBUTE,
						0, 2, UndeclaredFreeIdentifierError, "V2"));
		
		SCE file = getGeneric().getSCElement(cmp);
		
		getGeneric().containsIdents(file);
		
		getGeneric().containsPredicates(file, emptyEnv, makeSList(), makeSList());
	}
	
	/**
	 * refering to identifiers with faulty declaration should fail
	 */
	@Test
	public void testIdents_03() throws Exception {
		E cmp = getGeneric().createElement("cmp");

		getGeneric().addIdents(cmp, makeSList("V1", "V1"));
		getGeneric().addPredicates(cmp, makeSList("I1"), makeSList("V1∈ℤ"), false);

		getGeneric().save(cmp);
		
		runBuilderCheck(
				marker(getGeneric().getIdents(cmp)[0], IDENTIFIER_ATTRIBUTE,
						getGeneric().getUntypedProblem(), "V1"),
				marker(getGeneric().getIdents(cmp)[0], IDENTIFIER_ATTRIBUTE,
						getGeneric().getIdentConflictProblem(), "V1"),
				marker(getGeneric().getIdents(cmp)[1], IDENTIFIER_ATTRIBUTE,
						getGeneric().getIdentConflictProblem(), "V1"),
				marker(getGeneric().getPredicates(cmp)[0], PREDICATE_ATTRIBUTE,
						0, 2, FreeIdentifierFaultyDeclError, "V1"));
		
		SCE file = getGeneric().getSCElement(cmp);
		
		getGeneric().containsIdents(file);
		
		getGeneric().containsPredicates(file, emptyEnv, makeSList(), makeSList());
	}

	/**
	 * An identifier declaration containing an invalid character is reported
	 */
	@Test
	public void testIdents_04_bug2689872() throws Exception {
		E cmp = getGeneric().createElement("cmp");

		getGeneric().addIdents(cmp, makeSList("/V1"));
		getGeneric().addPredicates(cmp, makeSList("I1"), makeSList("/V1∈ℤ"), false);

		getGeneric().save(cmp);
		
		runBuilderCheck(
				marker(getGeneric().getIdents(cmp)[0], IDENTIFIER_ATTRIBUTE,
						InvalidIdentifierError, "/V1"),
				marker(getGeneric().getPredicates(cmp)[0], PREDICATE_ATTRIBUTE,
						0, 1, LexerError, "/"));
		
		SCE file = getGeneric().getSCElement(cmp);
		
		getGeneric().containsIdents(file);
		
		getGeneric().containsPredicates(file, emptyEnv, makeSList(), makeSList());
	}

	
	/**
	 * An identifier declaration cannot be primed.
	 */
	@Test
	public void testIdents_05_bug2815882() throws Exception {
		final E cmp = getGeneric().createElement("cmp");

		getGeneric().addIdents(cmp, makeSList("v'"));
		getGeneric().addPredicates(cmp, makeSList("I1"), makeSList("v'∈ℤ"), false);

		getGeneric().save(cmp);
		
		runBuilderCheck(
				marker(getGeneric().getIdents(cmp)[0], IDENTIFIER_ATTRIBUTE,
						InvalidIdentifierError, "v'"),
				marker(getGeneric().getPredicates(cmp)[0], PREDICATE_ATTRIBUTE,
						0, 2, UndeclaredFreeIdentifierError, "v'"));
		
		final SCE file = getGeneric().getSCElement(cmp);
		
		getGeneric().containsIdents(file);
		
		getGeneric().containsPredicates(file, emptyEnv, makeSList(), makeSList());
	}
	
}
