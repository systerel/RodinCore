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
package org.eventb.internal.core.seqprover.proofSimplifier2;

import static org.eventb.core.seqprover.ProverFactory.makeProofTree;
import static org.eventb.core.seqprover.proofBuilder.ProofBuilder.rebuild;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.runtime.Assert;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;

/**
 * A tree type to use for dependence computation and manipulation.
 * 
 * @author Nicolas Beauger
 */
public class SawyerTree {

	private final SawyerNode root;
	private final IProverSequent rootSequent;

	public SawyerTree(IProofTreeNode root) {
		this.root = SawyerNode.fromTreeNode(root);
		this.rootSequent = root.getSequent();
	}

	public void init() {
		final Collection<RequiredSequent> required = computeDeps(root);
		checkRootSatisfies(required);
	}

	private void checkRootSatisfies(Collection<RequiredSequent> required) {
		for (RequiredSequent req : required) {
			req.satisfyWith(rootSequent);
			Assert.isTrue(req.isSatisfied(),
					"Simplification: unsatisfied sequent (there may be others): "
							+ req);
		}
	}

	private static Collection<RequiredSequent> computeDeps(SawyerNode node) {
		final Collection<RequiredSequent> required = new ArrayList<RequiredSequent>();
		final SawyerNode[] children = node.getChildren();
		final ProducedSequent[] producedSequents = node.getProducedSequents();
		assert children.length == producedSequents.length;

		for (int i = 0; i < children.length; i++) {
			final ProducedSequent prod = producedSequents[i];
			final Collection<RequiredSequent> deps = computeDeps(children[i]);
			satisfy(deps, prod);
			required.addAll(deps);
		}
		required.add(node.getRequiredSequent());
		return required;
	}

	private static void satisfy(Collection<RequiredSequent> required,
			ProducedSequent produced) {
		final Iterator<RequiredSequent> iterator = required.iterator();
		while (iterator.hasNext()) {
			final RequiredSequent req = iterator.next();
			req.satisfyWith(produced);
			if (req.isSatisfied()) {
				iterator.remove();
			}
		}
	}

	public void simplify() {
		deleteUnneededRec(root);
	}

	private static void deleteUnneededRec(SawyerNode node) {
		node.deleteIfUnneeded();
		for (SawyerNode child : node.getChildren()) {
			deleteUnneededRec(child);
		}
	}

	public IProofTree toProofTree(IProofMonitor monitor) {
		final IProofTree proofTree = makeProofTree(rootSequent, this);
		final boolean success = rebuild(proofTree.getRoot(), root, monitor);
		if (!success || !proofTree.isClosed()) {
			return null;
		}
		return proofTree;
	}
}
