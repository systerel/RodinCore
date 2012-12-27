/*******************************************************************************
 * Copyright (c) 2010, 2012 Systerel and others.
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
import org.rodinp.core.RodinDBException;

/**
 * Abstract class for proof modification operations.
 * 
 * @author Nicolas Beauger
 * @since 1.3
 */
/* package */ abstract class ProofModifier {

	protected final IPRProof proof;
	protected final FormulaFactory factory;
	protected final String owner;
	private final boolean simplify;

	public ProofModifier(IPRProof proof, String owner, boolean simplify) {
		this.factory = ((IEventBRoot) proof.getRoot()).getFormulaFactory();
		this.proof = proof;
		this.owner = owner;
		this.simplify = simplify;
	}

	/**
	 * Attempts to perform the modification of the proof.
	 * <p>
	 * If the modification is successful, then the proof attempt is committed
	 * (with the simplify option turned on).
	 * </p>
	 * 
	 * @param monitor
	 *            the progress monitor to use for reporting progress to the
	 *            user. It is the caller's responsibility to call done() on the
	 *            given monitor. Accepts <code>null</code>, indicating that no
	 *            progress should be reported and that the operation cannot be
	 *            cancelled.
	 * @return <code>true</code> iff the proof was successfully modified
	 * @throws RodinDBException
	 *             if there was a problem accessing the proof
	 */
	public boolean perform(IProgressMonitor monitor) throws RodinDBException {
		if (!isValidProof()) {
			return false;
		}
		final SubMonitor subMonitor = SubMonitor.convert(monitor, 10);
		subMonitor.subTask(proof.getRoot().getElementName() + ": "
				+ proof.getElementName());

		final IProofComponent pc = getProofComponent();
		final IProofAttempt pa = createPrAttempt(pc, subMonitor.newChild(1));
		try {
			final IProofSkeleton skel = getPrSkel(pc, subMonitor.newChild(2));
			if (subMonitor.isCanceled()) {
				return false;
			}
			final boolean success = makeNewProof(pa, skel, subMonitor.newChild(4));
			if (!success || subMonitor.isCanceled()) {
				return false;
			}
			commit(pa, subMonitor.newChild(2));
			pc.save(subMonitor.newChild(1), false);
		} finally {
			pa.dispose();
		}
		return (!subMonitor.isCanceled());
	}

	/**
	 * Makes a new proof in the given proof attempt from the given original
	 * proof skeleton.
	 * 
	 * @param pa
	 *            a fresh new proof attempt
	 * @param originalSkeleton
	 *            the skeleton of the original proof
	 * @param monitor
	 *            the progress monitor to use for reporting progress to the
	 *            user.
	 * @return <code>true</code> iff the new proof was successfully made; if
	 *         <code>false</code> is returned, the proof attempt will not be
	 *         committed
	 */
	protected abstract boolean makeNewProof(IProofAttempt pa,
			IProofSkeleton originalSkeleton, IProgressMonitor monitor);

	private boolean isValidProof() {
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

	private IProofSkeleton getPrSkel(IProofComponent pc,
			IProgressMonitor monitor) throws RodinDBException {
		return pc.getProofSkeleton(proof.getElementName(), factory, monitor);
	}

	private IProofAttempt createPrAttempt(IProofComponent pc,
			IProgressMonitor monitor) throws RodinDBException {
		return pc.createProofAttempt(proof.getElementName(), owner, monitor);
	}

	private void commit(IProofAttempt pa, IProgressMonitor monitor)
			throws RodinDBException {
		pa.commit(proof.getHasManualProof(), simplify, monitor);
	}

}
