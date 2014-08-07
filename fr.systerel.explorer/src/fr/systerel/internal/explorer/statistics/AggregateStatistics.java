/*******************************************************************************
 * Copyright (c) 2008, 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.internal.explorer.statistics;
import static fr.systerel.internal.explorer.statistics.StatisticsUtil.getParentLabelOf;

import java.util.ArrayList;
import java.util.Arrays;

import org.eventb.core.IAxiom;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEvent;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineRoot;
import org.rodinp.core.IInternalElementType;

import fr.systerel.explorer.IElementNode;
import fr.systerel.internal.explorer.model.ModelAxiom;
import fr.systerel.internal.explorer.model.ModelController;
import fr.systerel.internal.explorer.model.ModelEvent;
import fr.systerel.internal.explorer.model.ModelInvariant;
import fr.systerel.internal.explorer.model.ModelPOContainer;
import fr.systerel.internal.explorer.model.ModelProofObligation;

/**
 * This is a statistics that is composed of one or several <code>IStatistics</code>.
 * This is used to represent overview statistics on selection. 
 */
public class AggregateStatistics implements IStatistics {

	private IStatistics[] internal_statistics;
	private final Object parent;
	private int total;
	private int undischarged;
	private int manual;
	private int reviewed;
	private ArrayList<ModelProofObligation> pos = new ArrayList<ModelProofObligation>();

	public AggregateStatistics(IStatistics[] statistics) {
		if (statistics.length == 1 && statistics[0]!= null) {
			this.parent = statistics[0].getParent();
		} else {
			this.parent = null;
		}
		internal_statistics = statistics.clone();
		calculate();

	}

	/**
	 * calculates the statistics.
	 */
	public void calculate() {
		collectPOs();
		// no proof obligations collected: directly get it from the internal
		// statistics
		if (pos.size() == 0) {
			calculateFromInternalStats();
		}
		// proof obligations collected: calculate statistics from those proof
		// obligations
		else {
			calculateFromPOs();
		}

	}

	/**
	 * In some cases it's not possible to just add up the values of the internal
	 * statistics, because there maybe duplicated Proof Obligation (e.g. the
	 * same proof obligation appears for an invariant and an event). For those
	 * cases, the original proof obligations are collected here, so that the
	 * calculation can be remade freshly.
	 */
	public void collectPOs() {
		for (IStatistics stat : internal_statistics) {
			// special case for element nodes or invariants, theorems etc. there
			// may be duplicate statistics
			if (stat.getParent() instanceof IElementNode) {
				final IElementNode node = (IElementNode) stat.getParent();
				ModelPOContainer cont = null;
				if (node.getParent() instanceof IContextRoot) {
					cont = ModelController.getContext((IContextRoot) node
							.getParent());
				}
				if (node.getParent() instanceof IMachineRoot) {
					cont = ModelController.getMachine((IMachineRoot) node
							.getParent());
				}
				if (cont != null) {
					addPOs(cont.getProofObligations(), node.getChildrenType());
				}
			}
			if (stat.getParent() instanceof ModelAxiom) {
				final ModelAxiom axiom = (ModelAxiom) stat.getParent();
				addPOs((axiom.getProofObligations()));
			}
			if (stat.getParent() instanceof ModelInvariant) {
				final ModelInvariant inv = (ModelInvariant) stat.getParent();
				addPOs((inv.getProofObligations()));
			}
			if (stat.getParent() instanceof ModelEvent) {
				final ModelEvent event = (ModelEvent) stat.getParent();
				addPOs((event.getProofObligations()));
			}

		}

	}

	@Override
	public int getAuto() {
		return total - undischarged - manual;
	}

	@Override
	public int getManual() {
		return manual;
	}

	/**
	 * Aggregate statistics don't have a parentLabel.
	 */
	@Override
	public String getParentLabel() {
		if (parent == null)
			return ("Total");
		return getParentLabelOf(parent);
	}

	@Override
	public int getReviewed() {
		return reviewed;
	}

	@Override
	public int getTotal() {
		return total;
	}

	@Override
	public int getUndischarged() {
		return undischarged;
	}

	@Override
	public int getUndischargedRest() {
		return undischarged - reviewed;
	}

	@Override
	public boolean isAggregate() {
		return true;
	}

	/**
	 * Aggregates have no parent.
	 */
	@Override
	public Object getParent() {
		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(internal_statistics);
		result = prime * result + manual;
		result = prime * result + ((pos == null) ? 0 : pos.hashCode());
		result = prime * result + reviewed;
		result = prime * result + total;
		result = prime * result + undischarged;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final AggregateStatistics other = (AggregateStatistics) obj;
		if (!Arrays.equals(internal_statistics, other.internal_statistics))
			return false;
		if (manual != other.manual)
			return false;
		if (pos == null) {
			if (other.pos != null)
				return false;
		} else if (!pos.equals(other.pos))
			return false;
		if (reviewed != other.reviewed)
			return false;
		if (total != other.total)
			return false;
		if (undischarged != other.undischarged)
			return false;
		return true;
	}

	private void calculateFromInternalStats() {
		total = 0;
		undischarged = 0;
		manual = 0;
		reviewed = 0;
		for (IStatistics stat : internal_statistics) {
			total += stat.getTotal();
			undischarged += stat.getUndischarged();
			manual += stat.getManual();
			reviewed += stat.getReviewed();
		}
	}

	private void calculateFromPOs() {
		total = 0;
		undischarged = 0;
		manual = 0;
		reviewed = 0;
		for (ModelProofObligation po : pos) {
			total++;
			if (po.isDischarged()) {
				if (po.isManual()) {
					manual++;
				}
			} else {
				if (po.isReviewed()) {
					reviewed++;
				}
				undischarged++;
			}
		}
	}

	/**
	 * Add proof obligations without duplicates
	 */
	private void addPOs(ModelProofObligation[] proofObligations) {
		for (ModelProofObligation po : proofObligations) {
			if (!pos.contains(po)) {
				pos.add(po);
			}
		}
	}

	/**
	 * Add proof obligations without duplicates and only if the belong to a
	 * certain element type
	 */
	private void addPOs(ModelProofObligation[] proofObligations,
			IInternalElementType<?> type) {
		for (ModelProofObligation po : proofObligations) {
			if (!pos.contains(po)) {
				if (type == IAxiom.ELEMENT_TYPE) {
					if (po.getAxioms().length > 0) {
						pos.add(po);
					}
				}
				if (type == IEvent.ELEMENT_TYPE) {
					if (po.getEvents().length > 0) {
						pos.add(po);
					}
				}
				if (type == IInvariant.ELEMENT_TYPE) {
					if (po.getInvariants().length > 0) {
						pos.add(po);
					}
				}
			}
		}
	}

	@Override
	public void buildCopyString(StringBuilder builder, boolean copyLabel, Character separator) {
		if (copyLabel) {
			builder.append(getParentLabel()) ;
			builder.append(separator);
		}
		builder.append(getTotal());
		builder.append(separator);
		builder.append(getAuto());
		builder.append(separator);
		builder.append(getManual());
		builder.append(separator);
		builder.append(getReviewed());
		builder.append(separator);
		builder.append(getUndischargedRest());
		builder.append(System.getProperty("line.separator"));
	}

}
