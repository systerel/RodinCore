/*******************************************************************************
 * Copyright (c) 2008, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.tests;

import static org.eventb.core.ast.Formula.BFALSE;
import static org.eventb.core.ast.Formula.IN;
import static org.junit.Assert.assertFalse;

import java.util.Arrays;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.seqprover.IProofSkeleton;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.core.seqprover.tactics.BasicTactics;
import org.junit.Test;

/**
 * Acceptance tests for checking type consistency in a proof tree.
 * 
 * @author Laurent Voisin
 */
public class TypeTests extends AbstractProofTreeTests {

	private static FormulaFactory ff = FormulaFactory.getDefault();

	private static final GivenType typeS = ff.makeGivenType("S");
	private static final GivenType typeT = ff.makeGivenType("T");

	private static final Predicate falsum = ff.makeLiteralPredicate(BFALSE, null);

	@Test
	public void bug2355262() {
		final IProofTreeNode rootS = makeProofTree(typeS).getRoot();
		final Predicate hyp = makeMembershipPredicate(typeS);
		final ITactic rmTac = Tactics.removeMembership(hyp, IPosition.ROOT);
		rmTac.apply(rootS, null);
		assertFalse("Tactic should have succeeded", rootS.isOpen());

		final IProofTreeNode rootT = makeProofTree(typeT).getRoot();
		final IProofSkeleton proofSkeleton = rootS.copyProofSkeleton();
		BasicTactics.rebuildTac(proofSkeleton).apply(rootT, null);
		assertFalse("Rebuild should have succeeded", rootT.isOpen());
	}

	private IProofTree makeProofTree(final GivenType type) {
		final IProverSequent sequent = makeSequent(type);
		final IProofTree pt = ProverFactory.makeProofTree(sequent, null);
		return pt;
	}

	private IProverSequent makeSequent(Type type) {
		final ITypeEnvironmentBuilder typenv = ff.makeTypeEnvironment();
		final Predicate hyp = makeMembershipPredicate(type);
		typenv.addAll(hyp.getFreeIdentifiers());
		return ProverFactory.makeSequent(typenv, Arrays.asList(hyp), falsum);
	}

	/*
	 * Returns predicate "x : {y}" where x and y bear the given type.
	 */
	private Predicate makeMembershipPredicate(Type type) {
		final Expression x = ff.makeFreeIdentifier("x", null, type);
		final Expression y = ff.makeFreeIdentifier("y", null, type);
		final Expression singleton = ff.makeSetExtension(y, null);
		return ff.makeRelationalPredicate(IN, x, singleton, null);
	}

}
