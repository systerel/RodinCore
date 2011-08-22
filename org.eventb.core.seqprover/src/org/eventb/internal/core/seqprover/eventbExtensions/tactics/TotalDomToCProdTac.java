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

import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.core.seqprover.tactics.BasicTactics;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.TotalDomRewrites;

/**
 * Applies TotalDomRewrites if the domain can be subsituted by a cartesian
 * product. It applies only in goal matching <code>x↦y∈dom(g)</code>.
 * 
 * @author Emmanuel Billaud
 */
public class TotalDomToCProdTac implements ITactic {

	@Override
	public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
		final Predicate goal = ptNode.getSequent().goal();
		if (!Lib.isInclusion(goal)) {
			return "Goal is not an inclusion";
		}
		final Expression mapplet = ((RelationalPredicate) goal).getLeft();
		if (!Lib.isMapping(mapplet)) {
			return "Left Member is not a mapplet";
		}
		final Expression dom = ((RelationalPredicate) goal).getRight();
		if (dom.getTag() != Formula.KDOM) {
			return "Right member is not a Kdom";
		}
		final IPosition pos = goal.getPosition(dom.getSourceLocation());
		final Set<Expression> substitutes = Tactics.totalDomGetSubstitutions(
				ptNode.getSequent(), ((UnaryExpression) dom).getChild());
		Expression substitute = null;
		for (Expression exp : substitutes) {
			if (exp.getTag() == Formula.CPROD) {
				substitute = exp;
				break;
			}
		}
		if (substitute == null) {
			return "The Kdom expression cannot be substituted";
		}
		return BasicTactics.reasonerTac(new TotalDomRewrites(),
				new TotalDomRewrites.Input(null, pos, substitute)).apply(
				ptNode, pm);
	}

}
