/*******************************************************************************
 * Copyright (c) 2008, 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *     Systerel - added getFormulaFactory()
 *******************************************************************************/
package org.eventb.internal.core.pm;

import static org.rodinp.core.IRodinElementDelta.REMOVED;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPRProof;
import org.eventb.core.IPSStatus;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.pm.IProofAttempt;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.internal.core.pom.POLoader;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Unique implementation of {@link IProofAttempt}.
 * 
 * @author Laurent Voisin
 */
public class ProofAttempt implements IProofAttempt, IElementChangedListener {

	private final ProofComponent component;

	private final String name;

	private final String owner;

	private final FormulaFactory ff;
	
	private final IProofTree proofTree;

	private final Long poStamp;

	private transient boolean broken;

	private boolean disposed;

	ProofAttempt(ProofComponent component, String name, String owner) throws RodinDBException {
		this.component = component;
		this.name = name;
		this.owner = owner;
		this.ff = component.getFormulaFactory();
		final IPOSequent poSequent = getPOSequent();
		this.proofTree = createProofTree(poSequent);
		if (poSequent.hasPOStamp()) {
			poStamp = poSequent.getPOStamp();
		} else {
			poStamp = null;
		}
		RodinCore.addElementChangedListener(this);
	}

	private IProofTree createProofTree(final IPOSequent poSequent)
			throws RodinDBException {
		final IProverSequent sequent = POLoader.readPO(poSequent, ff);
		return ProverFactory.makeProofTree(sequent, this);
	}

	private IPOSequent getPOSequent() {
		return component.getPORoot().getSequent(name);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getOwner() {
		return owner;
	}

	@Override
	public ProofComponent getComponent() {
		return component;
	}

	@Override
	public FormulaFactory getFormulaFactory() {
		return ff;
	}
	
	@Override
	public synchronized void dispose() {
		if (!disposed) {
			RodinCore.removeElementChangedListener(this);
			component.remove(this);
			disposed = true;
		}
	}

	@Override
	public String toString() {
		return "ProofAttempt(" + component + "|" + name + "|" + owner + ")";
	}

	@Override
	public boolean isDisposed() {
		return disposed;
	}

	@Override
	public IProofTree getProofTree() {
		return proofTree;
	}

	@Override
	public void commit(boolean manual, IProgressMonitor monitor)
			throws RodinDBException {
		commit(manual, false, monitor);
	}

	@Override
	public void commit(boolean manual, boolean simplify,
			IProgressMonitor monitor) throws RodinDBException {
		if (isDisposed()) {
			throw new IllegalStateException(this + " has been disposed");
		}
		final CommitProofOperation op = new CommitProofOperation(this, manual,
				simplify);
		RodinCore.run(op, component.getSchedulingRule(), monitor);
	}

	public IPRProof getProof() {
		return getComponent().getProof(name);
	}

	@Override
	public IPSStatus getStatus() {
		return getComponent().getStatus(name);
	}

	@Override
	public boolean isBroken() throws RodinDBException {
		if (!broken) {
			broken |= checkBroken();
		}
		return broken;
	}

	private boolean checkBroken() throws RodinDBException {
		try {
			if (poStamp == null) {
				return getPOSequent().hasPOStamp();
			}
			return poStamp != getPOSequent().getPOStamp();
		} catch (RodinDBException e) {
			if (e.isDoesNotExist()) {
				return true;
			}
			throw e;
		}
	}

	@Override
	public void elementChanged(ElementChangedEvent event) {
		final IPOSequent poSequent = getPOSequent();
		final IRodinElementDelta delta = event.getDelta();
		traverseDelta(delta, poSequent);
	}

	private void traverseDelta(IRodinElementDelta delta, IPOSequent poSequent) {
		final IRodinElement elem = delta.getElement();
		if (elem.isAncestorOf(poSequent)) {
			if (delta.getKind() == REMOVED) {
				broken = true;
				return;
			}
			for (IRodinElementDelta child : delta.getRemovedChildren()) {
				traverseDelta(child, poSequent);
			}
			for (IRodinElementDelta child : delta.getChangedChildren()) {
				traverseDelta(child, poSequent);
			}
		}
	}

	public Long getPoStamp() {
		return poStamp;
	}

}