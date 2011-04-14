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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.rodinp.core.IRefinementParticipant;
import org.rodinp.internal.core.RefinementRegistry.RefinementException;
import org.rodinp.internal.core.util.sort.DefaultNode;
import org.rodinp.internal.core.util.sort.Sorter;

/**
 * @author Nicolas Beauger
 *
 */
public class Refinement {

	private static class Node extends DefaultNode<IRefinementParticipant, Refinement.Node> {
		
		public Node(IRefinementParticipant refinePart) {
			super(refinePart);
		}
		
		public void setSuccsPreds(List<Refinement.Node> succs, List<Refinement.Node> preds) {
			successors.clear();
			predecessors.clear();
			successors.addAll(succs);
			predecessors.addAll(preds);
		}
		
	}
	
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
		orderedRefParts.clear();
		final Map<IRefinementParticipant, List<IRefinementParticipant>> succs = new HashMap<IRefinementParticipant, List<IRefinementParticipant>>();
		final Map<IRefinementParticipant, List<IRefinementParticipant>> preds = new HashMap<IRefinementParticipant, List<IRefinementParticipant>>();

		computeSuccPreds(succs, preds);
		final Collection<Refinement.Node> nodes = toNodes(succs, preds);
		final Sorter<IRefinementParticipant, Refinement.Node> sorter = new Sorter<IRefinementParticipant, Refinement.Node>(
				nodes);
		final List<Refinement.Node> sorted = sorter.sort();
		for (Refinement.Node node : sorted) {
			orderedRefParts.add(node.getLabel());
		}
	}

	private void computeSuccPreds(
			Map<IRefinementParticipant, List<IRefinementParticipant>> succMap,
			Map<IRefinementParticipant, List<IRefinementParticipant>> predMap) {
		for (Entry<String, Set<String>> entry : order.entrySet()) {
			final IRefinementParticipant part = participants.get(entry
					.getKey());
			final List<IRefinementParticipant> succs = new ArrayList<IRefinementParticipant>();
			for (String succId : entry.getValue()) {
				final IRefinementParticipant succ = participants
						.get(succId);
				succs.add(succ);
				List<IRefinementParticipant> preds = predMap.get(succId);
				if (preds == null) {
					preds = new ArrayList<IRefinementParticipant>();
					predMap.put(succ, preds);
				}
				preds.add(part);
			}
			succMap.put(part, succs);
		}
	}

	private Collection<Refinement.Node> toNodes(
			Map<IRefinementParticipant, List<IRefinementParticipant>> succMap,
			Map<IRefinementParticipant, List<IRefinementParticipant>> predMap) {
		final Map<IRefinementParticipant, Refinement.Node> nodes = new HashMap<IRefinementParticipant, Refinement.Node>();
		for (IRefinementParticipant part : participants.values()) {
			nodes.put(part, new Node(part));
		}
		for (Entry<IRefinementParticipant, Refinement.Node> entry : nodes.entrySet()) {
			final IRefinementParticipant participant = entry.getKey();
			final List<Refinement.Node> succN = toNodeList(participant, succMap, nodes);
			final List<Refinement.Node> predN = toNodeList(participant, predMap, nodes);
			entry.getValue().setSuccsPreds(succN, predN);
		}
		return nodes.values();
	}

	private List<Refinement.Node> toNodeList(IRefinementParticipant participant,
			Map<IRefinementParticipant, List<IRefinementParticipant>> map,
			Map<IRefinementParticipant, Refinement.Node> nodes) {
		List<IRefinementParticipant> succs = map.get(participant);
		if (succs == null) {
			return Collections.emptyList();
		}
		final List<Refinement.Node> succN = new ArrayList<Refinement.Node>();
		for (IRefinementParticipant part : succs) {
			succN.add(nodes.get(part));
		}
		return succN;
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