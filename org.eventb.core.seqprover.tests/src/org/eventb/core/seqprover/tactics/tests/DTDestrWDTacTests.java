/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.tactics.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashSet;
import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.datatype.IDatatype;
import org.eventb.core.seqprover.IAutoTacticRegistry;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.eventbExtensions.AutoTactics;
import org.eventb.core.seqprover.reasonerExtentionTests.RecordDatatype;
import org.eventb.core.seqprover.reasonerExtentionTests.SimpleDatatype;
import org.eventb.core.seqprover.tests.ProverSequentTests;
import org.eventb.core.seqprover.tests.TestLib;
import org.junit.Test;

/**
 * Unit tests for the auto-tactic <code>DTDestrWDTac</code>.
 */
public class DTDestrWDTacTests {

	private static final FormulaFactory DEFAULT_FACTORY = ProverSequentTests.factory;
	private static final IDatatype RECORD_DT = DEFAULT_FACTORY
			.makeDatatype(RecordDatatype.getInstance());
	private static final IDatatype SIMPLE_DT = DEFAULT_FACTORY
	.makeDatatype(SimpleDatatype.getInstance());
	private static final Set<IFormulaExtension> EXTENSIONS = new HashSet<IFormulaExtension>();
	static {
		EXTENSIONS.addAll(RECORD_DT.getExtensions());
		EXTENSIONS.addAll(SIMPLE_DT.getExtensions());
	}

	private static final FormulaFactory DT_FAC = FormulaFactory
			.getInstance(EXTENSIONS);

	private static final ITactic tac = new AutoTactics.DTDestrWDTac();

	private static final String TAC_ID = "org.eventb.core.seqprover.dtDestrWDTac";

	private static void assertSuccess(IProofTreeNode node, TreeShape expected) {
		TreeShape.assertSuccess(node, expected, tac);
	}

	private static void assertFailure(IProofTreeNode node) {
		TreeShape.assertFailure(node, tac);
	}

	private static IProofTree genProofTree(String... preds) {
		final IProverSequent seq = genSeq(preds);
		return ProverFactory.makeProofTree(seq, null);
	}

	private static IProverSequent genSeq(String... preds) {
		final int nbHyps = preds.length - 1;
		final StringBuilder b = new StringBuilder();
		String sep = "";
		for (int i = 0; i < nbHyps; i++) {
			b.append(sep);
			sep = ";;";
			b.append(preds[i]);
		}
		b.append("|-");
		b.append(preds[nbHyps]);
		return TestLib.genSeq(b.toString(), DT_FAC);
	}

	/**
	 * Ensures that the tactic is correctly registered with the sequent prover.
	 */
	@Test
	public void tacticRegistered() {
		final IAutoTacticRegistry registry = SequentProver
				.getAutoTacticRegistry();
		final ITacticDescriptor desc = registry.getTacticDescriptor(TAC_ID);
		assertNotNull(desc);
		assertEquals(tac.getClass(), desc.getTacticInstance().getClass());
	}

	/**
	 * Ensures that the tactic succeeds 
	 */
	@Test
	public void success() {
		final IProofTree pt = genProofTree(//
				"∃m,n·x=rd(m,n)"
		);
		final FormulaFactory fac = pt.getRoot().getFormulaFactory();
		final FreeIdentifier prm0 = fac.makeFreeIdentifier("p_intDestr", null, fac.makeIntegerType());
		final FreeIdentifier prm1 = fac.makeFreeIdentifier("p_boolDestr", null, fac.makeBooleanType());
		assertSuccess(pt.getRoot(), TreeShape.dtDestrWD("2.0", prm0, prm1));
	}

	/**
	 * Ensures that the tactic fails when the datatype is not a record
	 */
	@Test
	public void notRecord() {
		final IProofTree pt = genProofTree(//
				"∃m,n·x=cons2(m,n)"
		);
		assertFailure(pt.getRoot());
	}

	/**
	 * Ensures that the tactic fails when the the tree is not closed
	 */
	@Test
	public void notClosed() {
		final IProofTree pt = genProofTree(//
				"∃m,n·x=rd(m+m,n)"
		);
		assertFailure(pt.getRoot());
	}

}
