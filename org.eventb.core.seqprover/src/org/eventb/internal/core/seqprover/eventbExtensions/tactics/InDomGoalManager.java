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

package org.eventb.internal.core.seqprover.eventbExtensions.tactics;

import static org.eventb.core.seqprover.tactics.BasicTactics.composeUntilFailure;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.AutoTactics.TrueGoalTac;
import org.eventb.core.seqprover.eventbExtensions.AutoTactics.TypeRewriteTac;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.core.seqprover.reasonerInputs.EmptyInput;
import org.eventb.core.seqprover.tactics.BasicTactics;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AutoRewriterImpl;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AutoRewrites;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AutoRewrites.Level;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.TotalDomRewrites;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.TotalDomRewrites.Input;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.TypeRewriterImpl;

/**
 * Finds possible substitution for a total domain occurrence and tries to apply
 * tactics to discharge the sequent.
 */
public class InDomGoalManager {

	protected final UnaryExpression domExpression;

	private IPosition domPosition;

	private boolean truegoalTac;

	private Expression substitute;

	public InDomGoalManager(UnaryExpression domExpression, IPosition position) {
		this.domExpression = domExpression;
		this.domPosition = position;
		this.truegoalTac = false;
	}

	/**
	 * Checks if the tactic is applicable.
	 * 
	 * @param ptNode
	 *            current proof tree node.
	 * @return true iff the tactic is appplicable.
	 */
	public boolean isApplicable(IProofTreeNode ptNode) {

		final IProverSequent sequent = ptNode.getSequent();
		final Set<Expression> substitutes = Tactics.totalDomGetSubstitutions(
				sequent, domExpression.getChild());
		final List<Expression> substitutesList = new ArrayList<Expression>(
				substitutes);
		final FormulaFactory ff = sequent.getFormulaFactory();
		final List<Predicate> autoGoals = new ArrayList<Predicate>();

		for (Expression substitute : substitutesList) {
			final Predicate rewrittenGoal = Lib.equalityRewrite(sequent.goal(),
					domExpression, substitute, ff);
			final Predicate typerewrittenGoal = rewrittenGoal
					.rewrite(new TypeRewriterImpl(ff));
			if (typerewrittenGoal.getTag() == Formula.BTRUE) {
				truegoalTac = true;
				this.substitute = substitute;
				return true;
			}
			final Predicate autorewrittenGoal = rewrittenGoal
					.rewrite(new AutoRewriterImpl(ff, Level.LATEST));
			autoGoals.add(autorewrittenGoal);
		}
		for (int i = 0; i < autoGoals.size(); i++) {
			for (Predicate hyp : sequent.visibleHypIterable()) {
				if (hyp.equals(autoGoals.get(i))) {
					this.substitute = substitutesList.get(i);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Applies the tactic to the current node.
	 * 
	 * @param ptNode
	 *            The open proof tree node where the tactic must be applied.
	 * @param pm
	 *            The proof monitor that monitors the progress of the tactic
	 *            application.
	 * @return null iff the tactic has been successfully applied.
	 */
	public Object applyTactics(IProofTreeNode ptNode, IProofMonitor pm) {

		final ITactic trueGoalTactic = new TrueGoalTac();
		final ITactic rewritesTac = new TypeRewriteTac();
		final IProofTreeNode initialNode = ptNode;
		
		final Input input = new Input(null, domPosition, substitute);
		(BasicTactics.reasonerTac(new TotalDomRewrites(), input)).apply(ptNode,
				pm);
		ptNode = ptNode.getFirstOpenDescendant();

		if (pm != null && pm.isCanceled()) {
			initialNode.pruneChildren();
			return "Canceled";
		}
		if (truegoalTac) {
			
			ITactic tac = composeUntilFailure(rewritesTac,trueGoalTactic);
			if (tac.apply(ptNode, pm) == null) {
				return null;
			}			
		} else {		
			//Can't use composeUntilFailure because typeRewrites is not always necessary
			(BasicTactics.reasonerTac(new AutoRewrites(), new EmptyInput()))
			.apply(ptNode, pm);
			ptNode = ptNode.getFirstOpenDescendant();
			if (Tactics.hyp().apply(ptNode, pm) == null) {
				return null;
			}		
		}
		initialNode.pruneChildren();
		return "Tactic unapplicable for this domain substitution";
	}
}
