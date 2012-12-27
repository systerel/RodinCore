/*******************************************************************************
 * Copyright (c) 2010, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.proofBuilderTests;

import static java.math.BigInteger.ZERO;
import static org.eventb.core.ast.Formula.BFALSE;
import static org.eventb.core.ast.Formula.BTRUE;
import static org.eventb.core.ast.Formula.EQUAL;
import static org.eventb.core.ast.Formula.LAND;
import static org.eventb.core.ast.Formula.LIMP;
import static org.eventb.core.ast.Formula.NOT;
import static org.eventb.core.seqprover.ProverFactory.makeProofTree;
import static org.eventb.core.seqprover.ProverFactory.makeSequent;

import java.util.Arrays;
import java.util.List;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;

/**
 * Factory for building formulas, sequents, proof trees, etc. for tests.
 * 
 * @author Laurent Voisin
 */
public class Factory {

	private static final FormulaFactory ff = FormulaFactory.getDefault();
	private static final Type Z = ff.makeIntegerType();
	private static final Expression zero = ff.makeIntegerLiteral(ZERO, null);

	public static final Predicate P = makePredicate("P");
	public static final Predicate Q = makePredicate("Q");
	public static final Predicate R = makePredicate("R");
	public static final Predicate S = makePredicate("S");

	public static final Predicate bfalse = ff
			.makeLiteralPredicate(BFALSE, null);
	public static final Predicate btrue = ff.makeLiteralPredicate(BTRUE, null);

	private static Predicate makePredicate(String name) {
		final FreeIdentifier id = ff.makeFreeIdentifier(name, null, Z);
		return ff.makeRelationalPredicate(EQUAL, id, zero, null);
	}

	public static Predicate land(Predicate... conjuntcs) {
		return ff.makeAssociativePredicate(LAND, conjuntcs, null);
	}

	public static Predicate limp(Predicate left, Predicate right) {
		return ff.makeBinaryPredicate(LIMP, left, right, null);
	}

	public static Predicate not(Predicate predicate) {
		return ff.makeUnaryPredicate(NOT, predicate, null);
	}

	/*
	 * Parameters are all hypotheses and then goal.
	 */
	public static IProverSequent sequent(Predicate... preds) {
		final int nbHyps = preds.length - 1;
		final List<Predicate> hyps = Arrays.asList(preds).subList(0, nbHyps);
		final Predicate goal = preds[nbHyps];
		final ITypeEnvironmentBuilder typenv = ff.makeTypeEnvironment();
		for (final Predicate pred : preds) {
			typenv.addAll(pred.getFreeIdentifiers());
		}
		return makeSequent(typenv, hyps, goal);
	}

	/*
	 * Parameters are all hypotheses and then goal for seauent of the returned
	 * proof tree node.
	 */
	public static IProofTreeNode makeProofTreeNode(Predicate... preds) {
		final IProverSequent sequent = sequent(preds);
		final IProofTree tree = makeProofTree(sequent, null);
		return tree.getRoot();
	}

}