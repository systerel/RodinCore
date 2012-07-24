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
	protected ITypeEnvironment typenv;
	protected DLib dl;

	public AbstractTacticTests(ITactic tactic, String tacticId) {
		this.tactic = tactic;
		this.tacticId = tacticId;
		setTypeEnvironment(FormulaFactory.getDefault(), "");
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
	 * Defines the type environment for this test.
	 * 
	 * @param ff
	 *            mathematical language
	 * @param typeEnvImage
	 *            string representation of the type environment
	 */
	protected void setTypeEnvironment(FormulaFactory ff, String typeEnvImage) {
		typenv = TestLib.genTypeEnv(typeEnvImage, ff);
		this.dl = mDLib(typenv.getFormulaFactory());
	}

	/**
	 * Parses the predicate using the current factory and checks its type using
	 * the current type environment.
	 */
	public Predicate parsePredicate(String predStr) {
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
		return genFullSeq(typenv, "", "", split[0], split[1]);
	}

}
