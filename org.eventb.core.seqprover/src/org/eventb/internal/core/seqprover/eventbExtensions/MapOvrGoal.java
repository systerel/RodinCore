/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import static org.eventb.core.ast.Formula.PFUN;
import static org.eventb.core.ast.Formula.REL;
import static org.eventb.core.ast.Formula.TFUN;
import static org.eventb.core.ast.Formula.TREL;
import static org.eventb.core.seqprover.ProverFactory.makeAntecedent;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.eventbExtensions.DLib;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.reasonerInputs.HypothesisReasoner;

/**
 * Split goal such as <code>f<+{x↦y}∈A<i>op</i>B</code> as follows :
 * <ul>
 * <li><code>f∈A<i>op</i>B</code> if it is contained in the sequent's hypotheses
 * </li>
 * <li><code>x∈A</code></li>
 * <li><code>y∈B</code></li>
 * </ul>
 * With <i>op</i> a relation among :
 * <ul>
 * <li>RELATION : ↔</li>
 * <li>TOTAL RELATION : </li>
 * <li>PARTIAL FUNCTION : ⇸</li>
 * <li>TOTAL FUNCTION : →</li>
 * </ul>
 * 
 * @author Emmanuel Billaud
 */
public class MapOvrGoal extends HypothesisReasoner {
	private Expression _A, _B, f, x, y;

	public static final String REASONER_ID = SequentProver.PLUGIN_ID
			+ ".mapOvrG";

	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	protected IAntecedent[] getAntecedents(IProverSequent sequent,
			Predicate pred) throws IllegalArgumentException {
		final FormulaFactory ff = sequent.getFormulaFactory();
		final DLib lib = DLib.mDLib(ff);

		final int goalTypeRelation = preCompute(sequent.goal());
		checkInput(sequent, pred, goalTypeRelation);

		final Predicate secondSubGoal = lib.makeInclusion(x, _A);
		final Predicate thirdSubGoal = lib.makeInclusion(y, _B);

		final IAntecedent firstAnt = makeAntecedent(pred);
		final IAntecedent secondAnt = makeAntecedent(secondSubGoal);
		final IAntecedent thirdAnt = makeAntecedent(thirdSubGoal);

		return new IAntecedent[] { firstAnt, secondAnt, thirdAnt };
	}

	/**
	 * Checks that the goal is such as <code>f<+{x↦y}∈A<i>op</i>B</code>
	 * (<i>op</i> is either a relation, or a total relation, or a partial
	 * function or a total function). If so, it is decomposed and information
	 * are stored in the class' attribute.
	 * 
	 * @param goal
	 *            the considered goal
	 * @return the tag of the relation <i>op</i> if the goal is valid.
	 */
	private int preCompute(final Predicate goal) {
		if (!Lib.isInclusion(goal)) {
			throw new IllegalArgumentException("Goal is not an Inclusion");
		}

		final Expression goalLeft = ((RelationalPredicate) goal).getLeft();
		if (!Lib.isOvr(goalLeft)) {
			throw new IllegalArgumentException(
					"Left member of the inclusion in goal is not an Overriding");
		}
		final Expression goalRight = ((RelationalPredicate) goal).getRight();
		final int goalTypeRelation = getTypeRelation(goalRight);
		if (goalTypeRelation == -1) {
			throw new IllegalArgumentException(
					"Right member of the inclusion in goal is not a Relation or a Total Relation or a Partial Function or a Total Function");
		}

		final Expression singleton = (Expression) goalLeft.getChild(1);
		if (!Lib.isSingletonSet(singleton)) {
			throw new IllegalArgumentException(
					"The function is not override by a singleton");
		}
		final Expression mapplet = ((SetExtension) singleton).getChild(0);
		if (!Lib.isMapping(mapplet)) {
			throw new IllegalArgumentException("The singleton is not a mapplet");
		}
		_A = Lib.getLeft(goalRight);
		_B = Lib.getRight(goalRight);
		f = (Expression) goalLeft.getChild(0);
		x = Lib.getLeft(mapplet);
		y = Lib.getRight(mapplet);
		return goalTypeRelation;
	}

	/**
	 * Returns the tag of the expression <code>expression</code> if it is either
	 * a relation, or a total relation, or a partial function or a total
	 * function, <code>-1</code> else.
	 * 
	 * @param expression
	 *            the considered expression
	 * @return the tag of the expression <code>expression</code> if it is either
	 *         a relation, or a total relation, or a partial function or a total
	 *         function, <code>-1</code> else.
	 */
	private int getTypeRelation(final Expression expression) {
		final int tag = expression.getTag();
		switch (tag) {
		case REL:
		case TREL:
		case PFUN:
		case TFUN:
			return tag;
		default:
			return -1;
		}
	}

	/**
	 * Checks that the input is such as <code>f∈A<i>op</i>B</code> and that
	 * <code>f</code> and <code>A</code> and <code>B</code> and
	 * <code><i>op</i></code> are the same as the one present in the goal.
	 * 
	 * @param sequent
	 *            the sequent on which the reasoner is applied
	 * @param pred
	 *            the input
	 * @param goalTypeRelation
	 *            the tag of the relation in the goal
	 */
	private void checkInput(IProverSequent sequent, Predicate pred,
			final int goalTypeRelation) {
		if (pred == null) {
			throw new IllegalArgumentException("The predicate is null");
		}
		if (!sequent.containsHypothesis(pred)) {
			throw new IllegalArgumentException(
					"The predicate is not a hypothesis of the sequent");
		}
		if (!Lib.isInclusion(pred)) {
			throw new IllegalArgumentException(
					"The predicate in input is not an Inclusion");
		}
		final Expression predRight = ((RelationalPredicate) pred).getRight();
		final int predTypeRelation = getTypeRelation(predRight);
		if (predTypeRelation == -1) {
			throw new IllegalArgumentException(
					"Right member of the inclusion of the input is not a Relation or a Total Relation or a Partial Function or a Total Function");
		}
		if (goalTypeRelation != predTypeRelation) {
			throw new IllegalArgumentException(
					"The relations in the input and in the goal are different");
		}
		final Expression predA = Lib.getLeft(predRight);
		final Expression predB = Lib.getRight(predRight);
		final Expression predF = ((RelationalPredicate) pred).getLeft();
		if (!predF.equals(f)) {
			throw new IllegalArgumentException("The functions are different");
		}
		if (!predA.equals(_A)) {
			throw new IllegalArgumentException(
					"The domains of the functions are different");
		}
		if (!predB.equals(_B)) {
			throw new IllegalArgumentException(
					"The ranges of the functions are different");
		}
	}

	@Override
	protected String getDisplay(Predicate pred) {
		return "Remove  in goal";
	}
}
