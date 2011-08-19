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
package org.eventb.internal.core.seqprover.eventbExtensions.tactics;

import static org.eventb.core.ast.Formula.PFUN;
import static org.eventb.core.ast.Formula.REL;
import static org.eventb.core.ast.Formula.TFUN;
import static org.eventb.core.ast.Formula.TREL;

import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.DLib;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.reasonerInputs.HypothesisReasoner;
import org.eventb.core.seqprover.tactics.BasicTactics;
import org.eventb.internal.core.seqprover.eventbExtensions.MapOvrGoal;

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
public class MapOvrGoalTac implements ITactic {
	private Expression _A, _B, f;
	private int tag;

	@Override
	public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
		final IProverSequent sequent = ptNode.getSequent();
		final FormulaFactory ff = sequent.getFormulaFactory();
		final DLib lib = DLib.mDLib(ff);

		final String result = preCompute(sequent.goal());
		if (result != null) {
			return result;
		}
		final BinaryExpression op = ff.makeBinaryExpression(tag, _A, _B, null);
		final Predicate testedPred = lib.makeInclusion(f, op);
		if (sequent.containsHypothesis(testedPred)) {
			final HypothesisReasoner.Input input = new HypothesisReasoner.Input(
					testedPred);
			return BasicTactics.reasonerTac(new MapOvrGoal(), input).apply(
					ptNode, pm);
		} else {
			return "There misses hypothesis";
		}
	}

	/**
	 * Checks that the goal is such as <code>f<+{x↦y}∈A<i>op</i>B</code>
	 * (<i>op</i> is either a relation, or a total relation, or a partial
	 * function or a total function). If so, it is decomposed and information
	 * are stored in the class' attributes.
	 * 
	 * @param goal
	 *            the considered goal
	 * @return <code>null</code> if the goal is valid, a string else.
	 */
	private String preCompute(final Predicate goal) {
		if (!Lib.isInclusion(goal)) {
			return "Goal is not an Inclusion";
		}

		final Expression left = ((RelationalPredicate) goal).getLeft();
		if (!Lib.isOvr(left)) {
			return "Left member of the inclusion is not an Overriding";
		}
		final Expression right = ((RelationalPredicate) goal).getRight();
		tag = getTypeRelation(right);
		if (tag == -1) {
			return "Right member of the inclusion is not a Relation or a Total Relation or a Partial Function or a Total Function";
		}

		final Expression singleton = (Expression) left.getChild(1);
		if (!Lib.isSingletonSet(singleton)) {
			return "The function is not override by a singleton";
		}
		final Expression mapplet = ((SetExtension) singleton).getChild(0);
		if (!Lib.isMapping(mapplet)) {
			return "The singleton is not a mapplet";
		}
		_A = Lib.getLeft(right);
		_B = Lib.getRight(right);
		f = (Expression) left.getChild(0);
		return null;
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

}
