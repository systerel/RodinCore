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
package org.eventb.internal.core.seqprover.eventbExtensions.mbGoal;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.internal.core.seqprover.eventbExtensions.mbGoal.Rationale.DomProjection;
import org.eventb.internal.core.seqprover.eventbExtensions.mbGoal.Rationale.RanProjection;

/**
 * Extract useful inclusion predicates from a set of hypotheses.
 * 
 * @author Laurent Voisin
 * @author Emmanuel Billaud
 */
@SuppressWarnings("unused")
public class GeneratorExtractor extends AbstractExtractor {

	private final List<Generator> result;

	%include {FormulaV2.tom}

	public GeneratorExtractor(MembershipGoalRules rf, Set<Predicate> hyps, 
			IProofMonitor pm) {
		super(rf, hyps, pm);
		this.result = new ArrayList<Generator>();
	}

	public List<Generator> extract() {
		for (Predicate hyp : hyps) {
			extract(hyp);
		}
		return result;
	}

	protected void extractSubset(boolean strict, Expression left,
			Expression right, Rationale rat) {
		%match (Expression left, Expression right) {
			_, _ -> {
				result.add(new Inclusion(rat));
			}
			A, B -> {
				if (`A.getType().getSource() != null) {
					extractSubset(new DomProjection(false, rf.subset(
						strict, rf.dom(`A), rf.dom(`B)), rat));
					extractSubset(new RanProjection(false, rf.subset(
						strict, rf.ran(`A), rf.ran(`B)), rat));
				}
			}
		}
	}

}
