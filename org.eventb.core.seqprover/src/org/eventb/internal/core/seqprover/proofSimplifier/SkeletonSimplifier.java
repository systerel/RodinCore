/*******************************************************************************
 * Copyright (c) 2009, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.proofSimplifier;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProofSkeleton;

/**
 * @author Nicolas Beauger
 * 
 */
public class SkeletonSimplifier extends Simplifier<IProofSkeleton> {

	private static class SimplifiedSkel implements IProofSkeleton {
		private final SimplifiedSkel[] newChildren;
		private final String comment;
		private final IProofRule newRule;

		public SimplifiedSkel(SimplifiedSkel[] newChildren, String comment,
				IProofRule newRule) {
			this.newChildren = newChildren;
			this.comment = comment;
			this.newRule = newRule;
		}

		public IProofSkeleton[] getChildNodes() {
			return newChildren;
		}

		public String getComment() {
			return comment;
		}

		public IProofRule getRule() {
			return newRule;
		}

		private void addToString(int tabs, StringBuilder sb) {
			for (int i = 0; i < tabs; i++) {
				sb.append('\t');
			}
			sb.append(newRule.getDisplayName());
			sb.append('\n');
			for (SimplifiedSkel child : newChildren) {
				child.addToString(tabs + 1, sb);
			}
		}

		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder();
			addToString(0, sb);
			return sb.toString();
		}
	}

	private final Set<Predicate> neededPreds = new LinkedHashSet<Predicate>();

	public SimplifiedSkel simplify(IProofSkeleton skeleton,
			IProofMonitor monitor) throws CancelException {
		final IProofSkeleton[] childNodes = skeleton.getChildNodes();
		final SimplifiedSkel[] newChildren = simplifyChildren(childNodes,
				monitor);
		checkCancel(monitor);

		final IProofRule rule = skeleton.getRule();
		final RuleSimplifier simplifier = new RuleSimplifier(neededPreds);
		final IProofRule newRule = simplifier.simplify(rule, monitor);
		checkCancel(monitor);
		if (canSkipToOnlyChild(newRule, newChildren)) {
			return newChildren[0];
		}
		neededPreds.addAll(simplifier.getNeededPreds());
		final String comment = skeleton.getComment();
		return new SimplifiedSkel(newChildren, comment, newRule);
	}

	private boolean canSkipToOnlyChild(final IProofRule newRule,
			final IProofSkeleton[] newChildren) {
		// 0 child => cannot skip
		// >= 2 children => not a trivial rule => cannot skip
		return isEmpty(newRule) && newChildren.length == 1;
	}

	private SimplifiedSkel[] simplifyChildren(
			final IProofSkeleton[] childNodes, IProofMonitor monitor)
			throws CancelException {
		final SimplifiedSkel[] newChildren = new SimplifiedSkel[childNodes.length];
		for (int i = 0; i < childNodes.length; i++) {
			final SkeletonSimplifier simplifier = new SkeletonSimplifier();
			newChildren[i] = simplifier.simplify(childNodes[i], monitor);
			checkCancel(monitor);

			neededPreds.addAll(simplifier.neededPreds);
		}
		return newChildren;
	}

	private static boolean isEmpty(IProofRule rule) {
		return rule.getAntecedents().length == 0;
	}

}
