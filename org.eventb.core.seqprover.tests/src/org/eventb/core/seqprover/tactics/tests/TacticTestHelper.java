/*******************************************************************************
 * Copyright (c) 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.tactics.tests;

import static org.eventb.core.seqprover.eventbExtensions.DLib.mDLib;
import static org.eventb.core.seqprover.tests.TestLib.genFullSeq;
import static org.junit.Assert.assertTrue;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.tests.TestLib;

/**
 * Helper class to check tactics. It provides methods to evaluate the output of
 * tactic application on a given sequent. This output takes the form of a tree
 * shape, for which predicates that are expected to take part in it, are creates
 * by the provided parsePredicate() method.
 * 
 * @author "Thomas Muller"
 */
public class TacticTestHelper {

	private final ITypeEnvironment typeEnvironment;
	private final FormulaFactory factory;
	private final ITactic tactic;

	public TacticTestHelper(FormulaFactory factory,
			String typeEnvImage, ITactic tactic) {
		this.factory = factory;
		this.typeEnvironment = TestLib.genTypeEnv(typeEnvImage, factory);
		this.tactic = tactic;
	}

	/**
	 * Parses the predicate using the current factory and checks its type using
	 * the current type environment.
	 */
	public Predicate parsePredicate(String predStr) {
		final Predicate pred = mDLib(factory).parsePredicate(predStr);
		final ITypeCheckResult tcResult = pred.typeCheck(typeEnvironment);
		assertTrue(tcResult.isSuccess());
		return pred;
	}
	
	/**
	 * Generates the sequent corresponding to the given sequent image, checks
	 * that the tactic is successfully applied, and verifies that the output
	 * proof tree shape.
	 */
	public void assertSuccess(String sequentImage, TreeShape expected) {
		TacticTestUtils.assertSuccess(genSeq(sequentImage), expected, tactic);
	}

	/**
	 * Generates the sequent corresponding to the given sequent image, checks
	 * that the tactic is not applied, and verifies that the proof tree is not
	 * modified.
	 */
	public void assertFailure(String sequentImage) {
		TacticTestUtils.assertFailure(genSeq(sequentImage), tactic);
	}

	private IProverSequent genSeq(String sequentImage) {
		final String[] split = sequentImage.split("\\|-");
		return genFullSeq(typeEnvironment, "", "", split[0], split[1]);
	}

}
