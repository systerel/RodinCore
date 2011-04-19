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

import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRefinementParticipant;
import org.rodinp.internal.core.RefinementRegistry.RefinementException;
import org.rodinp.internal.core.util.sort.DefaultNode;
import org.rodinp.internal.core.util.sort.Sorter;

/**
 * @author Nicolas Beauger
 * 
 */
public class Refinement {

	private static class Node extends DefaultNode<IRefinementParticipant, Node> {

		public Node(IRefinementParticipant refinePart) {
			super(refinePart);
		}

		public void setSuccsPreds(List<Node> succs, List<Node> preds) {
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
	private final IInternalElementType<?> rootType;
	private final String refinementId;

	public Refinement(IInternalElementType<?> rootType, String refinementId) {
		this.rootType = rootType;
		this.refinementId = refinementId;
	}

	public IInternalElementType<?> getRootType() {
		return rootType;
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
		final Collection<Node> nodes = toNodes(succs, preds);
		final Sorter<IRefinementParticipant, Node> sorter = new Sorter<IRefinementParticipant, Node>(
				nodes);
		// nodes are guaranteed cycle free
		final List<Node> sorted = sorter.sort();
		for (Node node : sorted) {
			orderedRefParts.add(node.getLabel());
		}
		ordered = true;
	}

	private void computeSuccPreds(
			Map<IRefinementParticipant, List<IRefinementParticipant>> succMap,
			Map<IRefinementParticipant, List<IRefinementParticipant>> predMap) {
		for (Entry<String, Set<String>> entry : order.entrySet()) {
			final IRefinementParticipant part = participants
					.get(entry.getKey());
			final List<IRefinementParticipant> succs = new ArrayList<IRefinementParticipant>();
			for (String succId : entry.getValue()) {
				final IRefinementParticipant succ = participants.get(succId);
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

	private Collection<Node> toNodes(
			Map<IRefinementParticipant, List<IRefinementParticipant>> succMap,
			Map<IRefinementParticipant, List<IRefinementParticipant>> predMap) {
		final Map<IRefinementParticipant, Node> nodes = new HashMap<IRefinementParticipant, Node>();
		for (IRefinementParticipant part : participants.values()) {
			nodes.put(part, new Node(part));
		}
		for (Entry<IRefinementParticipant, Node> entry : nodes.entrySet()) {
			final IRefinementParticipant participant = entry.getKey();
			final List<Node> succN = toNodeList(participant, succMap, nodes);
			final List<Node> predN = toNodeList(participant, predMap, nodes);
			entry.getValue().setSuccsPreds(succN, predN);
		}
		return nodes.values();
	}

	private List<Node> toNodeList(IRefinementParticipant participant,
			Map<IRefinementParticipant, List<IRefinementParticipant>> map,
			Map<IRefinementParticipant, Node> nodes) {
		List<IRefinementParticipant> succs = map.get(participant);
		if (succs == null) {
			return Collections.emptyList();
		}
		final List<Node> succN = new ArrayList<Node>();
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

	private void processCycleFound(String before, String after)
			throws RefinementException {
		cycleDetected = true;
		throw new RefinementException(
				"cycle in refinement participant order for " + refinementId
						+ ", introduced by " + before + " < " + after);
	}

}