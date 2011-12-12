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

import org.eventb.core.seqprover.IProofTreeNode;

/**
 * A tree type to use for dependence computation and manipulation.
 * 
 * @author Nicolas Beauger
 */
public class SawyerTree {

	private final SawyerNode root;

	public SawyerTree(IProofTreeNode root) {
		this.root = SawyerNode.fromTreeNode(root);
	}

	public void init() {
		computeDependencies(root);
	}
	
	private static void computeDependencies(SawyerNode node) {
		// TODO
	}

	public void simplify() {
		deleteUnneededRec(root);
	}

	private static void deleteUnneededRec(SawyerNode node) {
		
	}


}
