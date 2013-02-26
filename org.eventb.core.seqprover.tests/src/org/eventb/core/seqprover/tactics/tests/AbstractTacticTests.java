/*******************************************************************************
 * Copyright (c) 2012, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.tactics.tests;

import static org.eventb.core.seqprover.tests.TestLib.genExpr;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashSet;
import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.datatype.IDatatypeExtension;
import org.eventb.core.seqprover.IAutoTacticRegistry;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SequentProver;
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

	private static FormulaFactory makeFormulaFactory(
			IDatatypeExtension... dtExts) {
		final FormulaFactory factory = FormulaFactory.getDefault();
		final Set<IFormulaExtension> exts = new HashSet<IFormulaExtension>();
		for (final IDatatypeExtension dtExt : dtExts) {
			exts.addAll(factory.makeDatatype(dtExt).getExtensions());
		}
		return FormulaFactory.getInstance(exts);
	}

	protected final ITactic tactic;
	protected final String tacticId;
	protected FormulaFactory ff;
	protected DLib dl;
	protected ITypeEnvironmentBuilder typenv;

	public AbstractTacticTests(ITactic tactic, String tacticId) {
		this(tactic, tacticId, FormulaFactory.getDefault());
	}

	public AbstractTacticTests(ITactic tactic, String tacticId,
			IDatatypeExtension... dtExtensions) {
		this(tactic, tacticId, makeFormulaFactory(dtExtensions));
	}

	public AbstractTacticTests(ITactic tactic, String tacticId,
			FormulaFactory ff) {
		this.tactic = tactic;
		this.tacticId = tacticId;
		setFormulaFactory(ff);
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
		this.typenv = ff.makeTypeEnvironment();
	}

	/**
	 * Completes the type environment for this test. The type environment
	 * defined by the parameter is added to this test type environment
	 * 
	 * @param typenvImage
	 *            string representation of some type environment
	 */
	protected void addToTypeEnvironment(String typenvImage) {
		final ITypeEnvironment newTypenv = TestLib.mTypeEnvironment(typenvImage, ff);
		typenv.addAll(newTypenv);
	}

	/**
	 * Parses the predicate using the current factory and checks its type using
	 * the current type environment.
	 */
	protected Predicate parsePredicate(String predImage) {
		return TestLib.genPred(typenv, predImage);
	}

	/**
	 * Parses the expression using the current factory and checks its type using
	 * the current type environment.
	 */
	protected Expression parseExpression(String exprImage) {
		return genExpr(typenv, exprImage);
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
		return TestLib.genFullSeq(sequentImage, typenv);
	}

	/**
	 * Returns the root node of a proof tree built for the given sequent image
	 */
	protected IProofTreeNode genProofTreeNode(String sequentImage) {
		final IProverSequent sequent = genSeq(sequentImage);
		return ProverFactory.makeProofTree(sequent, null).getRoot();
	}

}
