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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.runtime.Assert;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;

/**
 * A tree type to use for dependence computation and manipulation.
 * 
 * @author Nicolas Beauger
 */
public class SawyerTree {

	private static DependSequent makeRootSequent(IProverSequent sequent) {
		return new DependSequent(sequent.hypIterable(), sequent.goal());
	}
	
	private final SawyerNode root;
	private final DependSequent rootSequent;

	public SawyerTree(IProofTreeNode root) {
		this.root = SawyerNode.fromTreeNode(root);
		this.rootSequent = makeRootSequent(root.getSequent());
	}

	public void init() {
		final Collection<RequiredSequent> required = computeRequired(root);
		satisfy(required, rootSequent);
		Assert.isTrue(required.isEmpty(), "required sequents remain: "
				+ required);
	}

	private static Collection<RequiredSequent> computeRequired(
			SawyerNode node) {
		final Collection<RequiredSequent> required = new ArrayList<RequiredSequent>();
		final SawyerNode[] children = node.getChildren();
		final ProducedSequent[] producedSequents = node.getProducedSequents();
		assert children.length == producedSequents.length;

		for (int i = 0; i < children.length; i++) {
			final ProducedSequent prod = producedSequents[i];
			final Collection<RequiredSequent> deps = computeRequired(children[i]);
			satisfy(deps, prod);
			required.addAll(deps);
		}
		required.add(node.getRequiredSequent());
		return required;
	}

	private static void satisfy(Collection<RequiredSequent> required,
			DependSequent produced) {
		final Iterator<RequiredSequent> iterator = required.iterator();
		while (iterator.hasNext()) {
			final RequiredSequent req = iterator.next();
			req.satisfy(produced);
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

}
