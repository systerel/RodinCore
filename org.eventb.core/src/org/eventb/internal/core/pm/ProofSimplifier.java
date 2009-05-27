/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.pm;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IPORoot;
import org.eventb.core.IPRProof;
import org.eventb.core.IPRRoot;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.pm.IProofAttempt;
import org.eventb.core.pm.IProofComponent;
import org.eventb.core.pm.IProofManager;
import org.eventb.core.seqprover.IProofSkeleton;
import org.eventb.core.seqprover.proofBuilder.ProofBuilder;
import org.eventb.internal.core.ProofMonitor;
import org.rodinp.core.RodinDBException;

/**
 * @author Nicolas Beauger
 * 
 */
public class ProofSimplifier {

	private static final String SIMPLIFIER = "Simplifier";

	private final IPRProof proof;
	private final FormulaFactory factory;

	public ProofSimplifier(IPRProof proof, FormulaFactory factory) {
		this.proof = proof;
		this.factory = factory;
	}

	public boolean simplify(IProgressMonitor monitor) throws RodinDBException {
		try {
			if (!validProof()) {
				return false;
			}
			final SubMonitor subMonitor = SubMonitor.convert(monitor, 10);
			subMonitor.subTask(proof.getElementName());

			final IProofComponent pc = getProofComponent();
			final IProofAttempt pa = createPrAttempt(pc, subMonitor.newChild(1));
			final IProofSkeleton skel = getPrSkel(pc, subMonitor.newChild(2));
			if (subMonitor.isCanceled()) {
				return false;
			}
			try {
				final boolean success = reuse(pa, skel, subMonitor.newChild(2));
				if (!success || subMonitor.isCanceled()) {
					return false;
				}
				commitSimplified(pa, subMonitor.newChild(4));
				pc.save(subMonitor.newChild(1), false);
			} finally {
				pa.dispose();
			}
			return (!subMonitor.isCanceled());
		} finally {
			if (monitor != null) {
				monitor.done();
			}
		}
	}

	private boolean validProof() {
		if (!proof.exists()) {
			return false;
		}
		final String name = proof.getElementName();
		final IPORoot poRoot = ((IPRRoot) proof.getRoot()).getPORoot();
		return poRoot.getSequent(name).exists();
	}

	private IProofComponent getProofComponent() {
		final IProofManager pm = EventBPlugin.getProofManager();
		final IEventBRoot prRoot = (IEventBRoot) proof.getRoot();
		return pm.getProofComponent(prRoot);
	}

	private IProofAttempt createPrAttempt(IProofComponent pc,
			IProgressMonitor monitor) throws RodinDBException {
		return pc.createProofAttempt(proof.getElementName(), SIMPLIFIER,
				monitor);
	}

	private IProofSkeleton getPrSkel(IProofComponent pc,
			IProgressMonitor monitor) throws RodinDBException {
		return pc.getProofSkeleton(proof.getElementName(), factory, monitor);
	}

	private boolean reuse(IProofAttempt pa, IProofSkeleton skel,
			IProgressMonitor monitor) {
		final ProofMonitor pm = new ProofMonitor(monitor);
		return ProofBuilder.reuse(pa.getProofTree().getRoot(), skel, pm);
	}

	private void commitSimplified(IProofAttempt pa, IProgressMonitor monitor)
			throws RodinDBException {
		pa.commit(proof.getHasManualProof(), true, monitor);
	}

}
