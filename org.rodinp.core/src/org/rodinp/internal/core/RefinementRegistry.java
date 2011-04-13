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
package org.rodinp.internal.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRefinementParticipant;

/**
 * @author Nicolas Beauger
 * 
 */
public class RefinementRegistry {

	public static class RefinementException extends Exception {
		private static final long serialVersionUID = 3388507398186210462L;

		public RefinementException(String message) {
			super(message);
		}
	}

	private static final RefinementRegistry DEFAULT_INSTANCE = new RefinementRegistry();

	private RefinementRegistry() {
		// singleton
	}

	public static RefinementRegistry getDefault() {
		return DEFAULT_INSTANCE;
	}

	private static class Refinement {
		private final Map<String, IRefinementParticipant> participants = new HashMap<String, IRefinementParticipant>();
		private final Map<String, Set<String>> order = new HashMap<String, Set<String>>();
		private final List<IRefinementParticipant> orderedRefParts = new ArrayList<IRefinementParticipant>();
		private boolean ordered = false;
		private boolean cycleDetected = false;

		public Refinement() {
			// avoid synthetic accessor
		}

		public List<IRefinementParticipant> getOrderedParticipants() {
			if (!ordered) {
				orderParticipants();
			}
			if (cycleDetected) {
				return null;
			}
			return orderedRefParts;
		}

		public void add(IRefinementParticipant refinementParticipant,
				String participantId) {
			participants.put(participantId, refinementParticipant);
		}

		private void orderParticipants() {
			// FIXME order
			orderedRefParts.addAll(participants.values());
		}

		public void setOrder(String before, String after)
				throws RefinementException {
			ordered = false;
			if (before.equals(after)) {
				processCycleFound(before, after);
			}
			Set<String> afters = order.get(before);
			if (afters == null) {
				afters = new HashSet<String>();
				order.put(before, afters);
			}
			final Set<String> afterAfters = order.get(after);
			if (afterAfters != null && afterAfters.contains(before)) {
				processCycleFound(before, after);
			}
			afters.add(after);
			for (Entry<String, Set<String>> entry : order.entrySet()) {
				final Set<String> afts = entry.getValue();
				if (afts.contains(before)) {
					if (entry.getKey().equals(before)) {
						processCycleFound(before, after);
					}
					afts.add(after);
				}

			}
		}

		private static void processCycleFound(String before, String after)
				throws RefinementException {
			throw new RefinementException(
					"cycle in refinement partitipant order, introduced by "
							+ before + " < " + after);
		}

	}

	// refinements per root type
	private final Map<IInternalElementType<?>, Refinement> refinements = new HashMap<IInternalElementType<?>, Refinement>();

	// refinements per id
	private final Map<String, Refinement> refinementIds = new HashMap<String, RefinementRegistry.Refinement>();

	// all participant ids with associated refinement
	private final Map<String, Refinement> participants = new HashMap<String, RefinementRegistry.Refinement>();

	/**
	 * Returns an ordered list of refinement participants for the given root
	 * element type. The order is computed from contributions. In case no
	 * refinement is defined for the given root element type, or if a cycle
	 * occurs, <code>null</code> is returned.
	 * 
	 * @param rootType
	 *            a root element type
	 * @return a list of refinement participants, or <code>null</code>
	 */
	public List<IRefinementParticipant> getRefinementParticipants(
			IInternalElementType<?> rootType) throws RefinementException {
		load(rootType);
		final Refinement refinement = refinements.get(rootType);
		if (refinement == null) {
			return null;
		}
		return refinement.getOrderedParticipants();
	}

	private void load(IInternalElementType<?> rootType)
			throws RefinementException {
		if (refinements.containsKey(rootType)) { // already loaded
			// FIXME refinements might have been programmatically added before
			// for now we suppose that, except in tests, the only contributions
			// come from the extension point
			return;
		}
		// TODO
	}

	// TODO refinements could be removed and replaced with references to root
	// types
	// but that would prevent from adding new attributes to refinements in the
	// future
	public void addRefinement(IInternalElementType<?> rootType,
			String refinementId) throws RefinementException {
		if (refinements.containsKey(rootType)) {
			throw new RefinementException(
					"A refinement for the same root type already exists: failed to add "
							+ refinementId);
		}
		if (refinementIds.containsKey(refinementId)) {
			throw new RefinementException(
					"A refinement with the same id already exists: failed to add "
							+ refinementId);
		}
		
		final Refinement refinement = new Refinement();
		refinementIds.put(refinementId, refinement);
		refinements.put(rootType, refinement);
	}

	public void addParticipant(IRefinementParticipant refinementParticipant,
			String participantId, IInternalElementType<?> rootType)
			throws RefinementException {
		if (participants.containsKey(participantId)) {
			throw new RefinementException(
					"A refinement participant with the same id already exists: "
							+ participantId);
		}

		final Refinement refinement = refinements.get(rootType);
		if (refinement == null) {
			throw new RefinementException("failed to add participant "
					+ participantId
					+ " because no refinement is defined for root type "
					+ rootType);
		}
		refinement.add(refinementParticipant, participantId);
		participants.put(participantId, refinement);
	}

	public void addOrder(String participantId1, String participantId2)
			throws RefinementException {
		checkKnownParticipant(participantId1);
		checkKnownParticipant(participantId2);
		final Refinement ref = checkAndGetSameRefinement(participantId1,
				participantId2);
		ref.setOrder(participantId1, participantId2);
	}

	private void checkKnownParticipant(String partId)
			throws RefinementException {
		if (!participants.containsKey(partId)) {
			throw new RefinementException("Unknown participant " + partId);
		}
	}

	private Refinement checkAndGetSameRefinement(String partId1, String partId2)
			throws RefinementException {
		final Refinement ref1 = participants.get(partId1);
		final Refinement ref2 = participants.get(partId2);
		if (ref1 != ref2) {
			throw new RefinementException(
					"Cannot set an order between participants of different refinements: "
							+ partId1 + ", " + partId2);
		}
		return ref1;
	}

	public void clear() {
		refinements.clear();
		refinementIds.clear();
		participants.clear();
	}

}
