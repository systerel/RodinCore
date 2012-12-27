/*******************************************************************************
 * Copyright (c) 2008, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.internal.explorer.model;

import java.util.LinkedList;
import java.util.List;

import org.eventb.core.IPOSequent;
import org.eventb.core.IPSStatus;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.RodinDBException;

/**
 * Represents a Proof Obligation in the Model.
 * 
 */
public class ModelProofObligation implements Comparable<ModelProofObligation> {
	public ModelProofObligation(IPOSequent sequent, int position) {
		this.internal_sequent = sequent;
		this.position = position;
	}

	private IPOSequent internal_sequent;
	private IPSStatus internal_status;

	private List<ModelInvariant> invariants = new LinkedList<ModelInvariant>();
	private List<ModelEvent> events = new LinkedList<ModelEvent>();
	private List<ModelAxiom> axioms = new LinkedList<ModelAxiom>();
	private ModelMachine machine; // A proof obligation can either belong to a
									// context or a machine
	private ModelContext context;
	private boolean discharged = false;
	private boolean broken = false;
	private boolean manual = false;
	private boolean reviewed = false;
	private int position;

	/**
	 * 
	 * @return the position of this proof obligation in relation to other other
	 *         proof obligations. The lower the number, the higher on the list.
	 */
	public int getPosition() {
		return position;
	}

	public void setMachine(ModelMachine machine) {
		this.machine = machine;
	}

	public ModelMachine getMachine() {
		return machine;
	}

	/**
	 * Set the status of this proof obligation. Updates stored attributes such
	 * as discharged or reviewed
	 * 
	 * @param status
	 *            The new status of this proof obligation
	 */
	public void setIPSStatus(IPSStatus status) {
		internal_status = status;
		try {
			int confidence = status.getConfidence();
			discharged = (status.getConfidence() > IConfidence.REVIEWED_MAX)
					&& !status.isBroken();
			reviewed = (confidence > IConfidence.PENDING && confidence <= IConfidence.REVIEWED_MAX);
			broken = status.isBroken();
			manual = status.getHasManualProof();
		} catch (RodinDBException e) {
			UIUtils.log(e, "when acessing " +status);
		}
	}

	public IPSStatus getIPSStatus() {
		return internal_status;
	}

	public IPOSequent getIPOSequent() {
		return internal_sequent;
	}

	public ModelInvariant[] getInvariants() {
		return invariants.toArray(new ModelInvariant[invariants.size()]);
	}

	public void addInvariant(ModelInvariant inv) {
		invariants.add(inv);
	}

	public void removeInvariants(ModelInvariant inv) {
		invariants.remove(inv);
	}

	public ModelEvent[] getEvents() {
		return events.toArray(new ModelEvent[events.size()]);
	}

	public void addEvent(ModelEvent event) {
		events.add(event);
	}

	public void removeEvents(ModelEvent event) {
		events.remove(event);
	}

	public String getName() {
		return internal_sequent.getElementName();
	}

	public void addAxiom(ModelAxiom ax) {
		axioms.add(ax);

	}

	public ModelAxiom[] getAxioms() {
		return axioms.toArray(new ModelAxiom[axioms.size()]);
	}

	public ModelContext getContext() {
		return context;
	}

	public void setContext(ModelContext ctx) {
		context = ctx;
	}

	public String getElementName() {
		return internal_sequent.getElementName();
	}

	/**
	 * 
	 * @return <code>true</code> if this PO is discharged <code>false</code>
	 *         otherwise.
	 */
	public boolean isDischarged() {
		return discharged;
	}

	public boolean isBroken() {
		return broken;
	}

	public boolean isReviewed() {
		return reviewed;
	}

	public boolean isManual() {
		return manual;
	}

	/**
	 * Compare according to the <code>position</code> of the proof obligations
	 */
	@Override
	public int compareTo(ModelProofObligation o) {
		return getPosition() - o.getPosition();
	}

}
