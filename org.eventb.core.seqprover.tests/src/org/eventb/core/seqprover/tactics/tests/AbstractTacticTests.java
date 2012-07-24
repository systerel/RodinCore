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
import static org.junit.Assert.*;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IAutoTacticRegistry;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.eventbExtensions.DLib;
import org.eventb.core.seqprover.tests.TestLib;
import org.junit.Test;

/**
 * Common protocol for testing Event-B tactics. To take advantage of this class,
 * just subclass it, initialize it with some tactic. The subclass will then
 * benefit from a free test checking that the tactic is correctly registered and
 * of several helper methods to check the correct implementation of the given
 * tactic.
 * 
 * @author Laurent Voisin
 * @author Thomas Muller
 */
public abstract class AbstractTacticTests {

	private final ITactic tactic;
	private final String tacticId;
	protected FormulaFactory ff;
	protected DLib dl;
	protected ITypeEnvironment typenv;

	public AbstractTacticTests(ITactic tactic, String tacticId) {
		this.tactic = tactic;
		this.tacticId = tacticId;
		setFormulaFactory(FormulaFactory.getDefault());
	}

	/**
	 * Ensures that the tactic is correctly registered with the sequent prover.
	 */
	@Test
	public void tacticRegistered() {
		final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
		final ITacticDescriptor desc = reg.getTacticDescriptor(tacticId);
		assertNotNull(desc);
		assertEquals(tactic.getClass(), desc.getTacticInstance().getClass());
	}

	/**
	 * Defines the mathematical language for this test.
	 * 
	 * @param ff
	 *            formula factory of the mathematical language to use
	 */
	protected void setFormulaFactory(FormulaFactory ff) {
		this.ff = ff;
		this.dl = mDLib(ff);
		this.typenv = ff.makeTypeEnvironment();
	}

	/**
	 * Completes the type environment for this test. The type environment
	 * defined by the parameter is added to this test type environment
	 * 
	 * @param typeEnvImage
	 *            string representation of some type environment
	 */
	protected void addToTypeEnvironment(String typeEnvImage) {
		final ITypeEnvironment newTypenv = TestLib.genTypeEnv(typeEnvImage, ff);
		typenv.addAll(newTypenv);
	}

	/**
	 * Parses the predicate using the current factory and checks its type using
	 * the current type environment.
	 */
	protected Predicate parsePredicate(String predStr) {
		final Predicate pred = dl.parsePredicate(predStr);
		final ITypeCheckResult tcResult = pred.typeCheck(typenv);
		assertFalse(tcResult.toString(), tcResult.hasProblem());
		assertTrue(pred.isTypeChecked());
		return pred;
	}

	/**
	 * Generates the sequent corresponding to the given sequent image, checks
	 * that the tactic is successfully applied, and verifies that the output
	 * proof tree shape.
	 */
	protected void assertSuccess(String sequentImage, TreeShape expected) {
		TacticTestUtils.assertSuccess(genSeq(sequentImage), expected, tactic);
	}

	/**
	 * Generates the sequent corresponding to the given sequent image, checks
	 * that the tactic is not applied, and verifies that the proof tree is not
	 * modified.
	 */
	protected void assertFailure(String sequentImage) {
		TacticTestUtils.assertFailure(genSeq(sequentImage), tactic);
	}

	private IProverSequent genSeq(String sequentImage) {
		final String[] split = sequentImage.split("\\|-");
		return genFullSeq(typenv, "", "", split[0], split[1]);
	}

}
