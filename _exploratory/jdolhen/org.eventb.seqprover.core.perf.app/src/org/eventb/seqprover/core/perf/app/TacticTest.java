/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.seqprover.core.perf.app;

import org.eventb.core.EventBPlugin;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IPORoot;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPSRoot;
import org.eventb.core.IPSStatus;
import org.eventb.core.pm.IProofAttempt;
import org.eventb.core.pm.IProofComponent;
import org.eventb.core.pm.IProofManager;
import org.eventb.core.seqprover.ITacticDescriptor;
import org.eventb.core.seqprover.ITactic;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

/**
 * Represents a test case for a given tactic.
 * 
 * @author Laurent Voisin
 */
public class TacticTest {

	private static final IProofManager PM = EventBPlugin.getProofManager();

	// Name of this test case
	private final String name;

	// Tactic to run
	private final ITactic tactic;

	public TacticTest(String name, ITacticDescriptor tactic) {
		super();
		this.name = name;
		this.tactic = tactic.getTacticInstance();
	}

	/*
	 * Runs this tactic on all proof obligations of the given project.
	 */
	public void run(IRodinProject rodinProject) throws Exception {
		System.out.print(String.format("  %-16s", name));
		Chrono c = new Chrono();
		c.startMeasure();
		for (final IPORoot poRoot : getPORoots(rodinProject)) {
			final IProofComponent proofComponent = PM.getProofComponent(poRoot);
			initializeProof(proofComponent);
			run(proofComponent);
		}
		final ProofCounter counter = new ProofCounter(rodinProject);
		counter.count();
		System.out.println(String.format("%5d / %5d  %5dms",
				counter.getNbDischarged(), counter.getNbProofs(),
				c.endMeasure()));
		Archivist.archiveResults(rodinProject, name);
	}

	/*
	 * Runs this tactic on all proof obligations of the proof component.
	 */
	private void run(IProofComponent pc) throws RodinDBException {
		for (final IPOSequent poSequent : pc.getPORoot().getSequents()) {
			final IProofAttempt proofAttempt = pc.createProofAttempt(
					poSequent.getElementName(), null, null);
			try {
				tactic.apply(proofAttempt.getProofTree().getRoot(), null);
				proofAttempt.commit(false, null);
				pc.save(null, false);
			} finally {
				proofAttempt.dispose();
			}
		}
	}

	private IPORoot[] getPORoots(IRodinProject rodinProject)
			throws RodinDBException {
		return rodinProject.getRootElementsOfType(IPORoot.ELEMENT_TYPE);
	}

	private void initializeProof(IProofComponent pc) throws RodinDBException {
		initializeRoot(pc.getPRRoot());
		initializeRoot(pc.getPSRoot());
		createStatuses(pc);
	}

	private void initializeRoot(IEventBRoot root) throws RodinDBException {
		if (root.exists()) {
			root.clear(false, null);
		} else {
			root.getRodinFile().create(false, null);
		}
	}

	private void createStatuses(IProofComponent pc) throws RodinDBException {
		final IPSRoot psRoot = pc.getPSRoot();
		final IPOSequent[] poSequents = pc.getPORoot().getSequents();
		for (final IPOSequent poSequent : poSequents) {
			final String poName = poSequent.getElementName();
			final IPSStatus status = psRoot.getStatus(poName);
			status.create(null, null);
		}
	}

}
