/*******************************************************************************
 * Copyright (c) 2009, 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.proofSimplifier;

import static org.eventb.core.seqprover.ProverFactory.makeProofTree;
import static org.eventb.core.seqprover.proofBuilder.ProofBuilder.rebuild;
import static org.eventb.internal.core.seqprover.Util.getNullProofMonitor;

import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofSkeleton;
import org.eventb.core.seqprover.IProofTree;

/**
 * @author Nicolas Beauger
 */
public class ProofTreeSimplifier extends Simplifier<IProofTree> {

	/**
	 * Returns a closed simplified version of the given closed proof tree.
	 * <p>
	 * A simplified proof tree contains less unused data. Callers of that method
	 * must firstly check that the tree is closed.
	 * </p>
	 * 
	 * @param tree
	 *            the tree to simplify
	 * @param monitor
	 *            a monitor to manage cancellation
	 * @return a closed simplified proof tree, or <code>null</code> if unable to
	 *         simplify
	 * @throws CancelException
	 */
	@Override
	public IProofTree simplify(IProofTree tree, IProofMonitor monitor)
			throws CancelException {
		if (!tree.isClosed()) {
			throw new IllegalArgumentException(
					"Cannot simplify a non closed proof tree");
		}
		if (monitor == null) {
			monitor = getNullProofMonitor();
		}
		final SkeletonSimplifier simplifier = new SkeletonSimplifier();
		// FIXME Fix RuleSimplifer throws assertion exceptions.
		// See bug item #3052238
		try {
			final IProofSkeleton simplified = simplifier.simplify(
					tree.getRoot(), monitor);
			checkCancel(monitor);

			final IProofTree result = makeProofTree(tree.getSequent(), this);
			final boolean success = rebuild(result.getRoot(), simplified,
					monitor);
			checkCancel(monitor);
			if (!success || !result.isClosed()) {
				return null;
			}
			return result;
		} catch (Throwable t) {
			return null;
		}
	}

}
