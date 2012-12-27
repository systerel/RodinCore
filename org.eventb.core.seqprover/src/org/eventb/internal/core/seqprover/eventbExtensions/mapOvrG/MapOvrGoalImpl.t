/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions.mapOvrG;

import static org.eventb.core.ast.Formula.IN;
import static org.eventb.internal.core.seqprover.eventbExtensions.mapOvrG.Operator.P_FUNCTION;
import static org.eventb.internal.core.seqprover.eventbExtensions.mapOvrG.Operator.RELATION;
import static org.eventb.internal.core.seqprover.eventbExtensions.mapOvrG.Operator.T_FUNCTION;
import static org.eventb.internal.core.seqprover.eventbExtensions.mapOvrG.Operator.T_RELATION;
import static org.eventb.internal.core.seqprover.eventbExtensions.mapOvrG.Operator.integerToOp;

import java.math.BigInteger;
import java.util.Set;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.seqprover.IProverSequent;

/**
 * Common implementation for MapOvrGoal reasoner and tactic.
 * 
 * @author Emmanuel Billaud
 */
@SuppressWarnings("unused")
public class MapOvrGoalImpl {

	private final IProverSequent sequent;
	private final FormulaFactory ff;
	private final Operator goalOp;
	private Expression f, x, y, A, B;

	%include {FormulaV2.tom}

	public MapOvrGoalImpl(IProverSequent sequent) {
		this.sequent = sequent;
		this.ff = sequent.getFormulaFactory();
		this.goalOp = analyseGoal(sequent.goal());
		
	}

	private Operator analyseGoal(Predicate goal) {
		%match(goal) {
			In(Ovr(eList(f, SetExtension(eList(Mapsto(x, y))))), op@(Rel|Trel|Pfun|Tfun)(A, B)) -> {
				this.f = `f;
				this.x = `x;
				this.y = `y;
				this.A = `A;
				this.B = `B;
				return integerToOp(`op.getTag());
			}
		}
		return null;
	}

	private Predicate in(Expression member, Expression set) {
		return this.ff.makeRelationalPredicate(IN, member, set, null);
	}

	private Expression relation(int tag, Expression left, Expression right) {
		return this.ff.makeBinaryExpression(tag, left, right, null);
	}

	/**
	 * Returns <code>true</code> iff the goal is in the right form,
	 * <code>false</code> else.
	 */
	public boolean checkGoal() {
		return goalOp != null;
	}

	/**
	 * Returns, if it exists among hypotheses of the given sequent, the predicate
	 * that allow to infer a part of the goal, else returns <code>null</code>.
	 */
	public Predicate findNeededHyp(IProverSequent sequent) {
		for (Operator higherOp : this.goalOp.getHigherRel()) {
			final Predicate hyp = in(this.f, relation(higherOp.getTag(), this.A, this.B));
			if (sequent.containsHypothesis(hyp)) {
				return hyp;
			}
		}
		return null;
	}

	/**
	 * Returns <code>true</code> iff the given predicate allow to infers a part
	 * of the goal, <code>false</code> else.
	 */
	public boolean infersGoal(Predicate predicate) {
		%match(predicate) {
			In(f, op@(Rel|Trel|Srel|Strel|Pfun|Tfun|Pinj|Tinj|Psur|Tsur|Tbij)(A, B)) -> {
				if (!`f.equals(this.f)) {
					return false;
				}
				if (!`A.equals(this.A)) {
					return false;
				}
				if (!`B.equals(this.B)) {
					return false;
				}
				return goalOp.isInferredBy(integerToOp(`op.getTag()));
			}
		}
		return false;
	}

	/**
	 * Returns an array of predicate containing exactly two elements.
	 */
	public Predicate[] createSubgoals() {
		return new Predicate[] {in(this.x, this.A), in(this.y, this.B)};
	}

}
