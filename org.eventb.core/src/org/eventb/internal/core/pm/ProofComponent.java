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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.MultiRule;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IPORoot;
import org.eventb.core.IPRProof;
import org.eventb.core.IPRRoot;
import org.eventb.core.IPSRoot;
import org.eventb.core.IPSStatus;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.pm.IProofAttempt;
import org.eventb.core.pm.IProofComponent;
import org.eventb.core.seqprover.IProofSkeleton;
import org.eventb.internal.core.Messages;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Unique implementation of {@link IProofComponent}.
 * <p>
 * This implementation must be thread-safe.
 * </p>
 * 
 * @author Laurent Voisin
 */
public class ProofComponent implements IProofComponent {

	// Known proof attempts that are still alive (not yet disposed)
	// Accesses must be synchronized
	private final Map<String, Map<String, ProofAttempt>> known;

	// The PS file of this component
	private final IPSRoot psRoot;

	public ProofComponent(IPSRoot psRoot) {
		this.psRoot = psRoot;
		this.known = new HashMap<String, Map<String, ProofAttempt>>();
	}

	@Override
	public IProofAttempt createProofAttempt(String poName, String owner,
			IProgressMonitor pm) throws RodinDBException {
		checkFormulaFactoryCompatibility();
		final CreateProofAttemptOperation cpa = new CreateProofAttemptOperation(
				this, poName, owner);
		RodinCore.run(cpa, getSchedulingRule(), pm);
		return cpa.getResult();
	}

	synchronized ProofAttempt get(String poName, String owner) {
		final Map<String, ProofAttempt> map = known.get(poName);
		if (map == null) {
			return null;
		}
		return map.get(owner);
	}

	@Override
	public IPORoot getPORoot() {
		return psRoot.getPORoot();
	}

	@Override
	public IPRRoot getPRRoot() {
		return psRoot.getPRRoot();
	}

	@Override
	public ProofAttempt[] getProofAttempts() {
		final Collection<ProofAttempt> res = new ArrayList<ProofAttempt>();
		addAllAttempts(res);
		return res.toArray(new ProofAttempt[res.size()]);
	}

	@Override
	public ProofAttempt[] getProofAttempts(String poName) {
		final Collection<ProofAttempt> res = values(poName);
		return res.toArray(new ProofAttempt[res.size()]);
	}

	@Override
	public ProofAttempt getProofAttempt(String poName, String owner) {
		return get(poName, owner);
	}

	@Override
	public IPSRoot getPSRoot() {
		return psRoot;
	}

	@Override
	public ISchedulingRule getSchedulingRule() {
		final ISchedulingRule[] rules = new ISchedulingRule[] {
				getPORoot().getSchedulingRule(),
				getPRRoot().getSchedulingRule(),
				getPSRoot().getSchedulingRule() };
		return MultiRule.combine(rules);
	}

	synchronized void put(ProofAttempt pa) {
		final String name = pa.getName();
		final String owner = pa.getOwner();
		final Map<String, ProofAttempt> map = known.get(name);
		if (map != null) {
			map.put(owner, pa);
			return;
		}
		final Map<String, ProofAttempt> newMap;
		newMap = new HashMap<String, ProofAttempt>();
		newMap.put(owner, pa);
		known.put(name, newMap);
	}

	synchronized void remove(ProofAttempt pa) {
		final String name = pa.getName();
		final String owner = pa.getOwner();
		final Map<String, ProofAttempt> map = known.get(name);
		if (map == null) {
			return;
		}
		map.remove(owner);
		if (map.isEmpty()) {
			known.remove(name);
		}
	}

	@Override
	public String toString() {
		return getPSRoot().toString();
	}

	synchronized void addAllAttempts(Collection<ProofAttempt> col) {
		for (final Map<String, ProofAttempt> map : known.values()) {
			if (map != null) {
				col.addAll(map.values());
			}
		}
	}

	synchronized Collection<ProofAttempt> values(String poName) {
		final Map<String, ProofAttempt> map = known.get(poName);
		if (map == null) {
			return Collections.emptySet();
		}
		return map.values();
	}

	@Override
	public IProofSkeleton getProofSkeleton(String poName, FormulaFactory ff,
			IProgressMonitor pm) throws RodinDBException {
		final ReadProofOperation rp = new ReadProofOperation(this, poName, ff);
		RodinCore.run(rp, getSchedulingRule(), pm);
		return rp.getResult();
	}

	public IPRProof getProof(String name) {
		return getPRRoot().getProof(name);
	}

	@Override
	public IPSStatus getStatus(String poName) {
		return getPSRoot().getStatus(poName);
	}

	@Override
	public boolean hasUnsavedChanges() throws RodinDBException {
		return getPRRoot().getRodinFile().hasUnsavedChanges()
				|| getPSRoot().getRodinFile().hasUnsavedChanges();
	}

	@Override
	public void makeConsistent(IProgressMonitor monitor)
			throws RodinDBException {
		final IWorkspaceRunnable op = new RevertProofComponentOperation(this);
		RodinCore.run(op, getSchedulingRule(), monitor);
	}

	@Override
	public void save(IProgressMonitor monitor, boolean force)
			throws RodinDBException {
		checkFormulaFactoryCompatibility();
		final IWorkspaceRunnable op = new SaveProofComponentOperation(this,
				force);
		RodinCore.run(op, getSchedulingRule(), monitor);
	}

	public void checkFormulaFactoryCompatibility() throws RodinDBException {
		final FormulaFactory poFF = psRoot.getPORoot().getFormulaFactory();
		final FormulaFactory prFF = psRoot.getPRRoot().getFormulaFactory();
		if (!poFF.equals(prFF)) {
			final IStatus st = new Status(IStatus.ERROR,
					EventBPlugin.PLUGIN_ID,
					Messages.ProofComponent_IncompatibleFormulaFactory(psRoot
							.getComponentName()));
			final CoreException e = new CoreException(st);
			throw new RodinDBException(e);
		}
	}

	@Override
	public FormulaFactory getFormulaFactory() {
		return psRoot.getFormulaFactory();
	}

}