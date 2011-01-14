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
package org.eventb.internal.core.parser;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eventb.internal.core.parser.ExternalViewUtils.Instantiator;

/**
 * @author Nicolas Beauger
 * 
 */
public class OpRegistryCompactor {

	private final OperatorRegistry initOpReg;

	public OpRegistryCompactor(OperatorRegistry initOpReg) {
		this.initOpReg = initOpReg;
	}

	public OperatorRegistryCompact compact(Instantiator<Integer, Integer> kindInst) {
		final Collection<OperatorGroup> initOpGroups = initOpReg.getIdOpGroup()
				.values();
		final OperatorGroupCompact[] groups = new OperatorGroupCompact[initOpGroups
				.size()];
		final int[] firstKinds = new int[initOpGroups.size() + 1];

		final Instantiator<OperatorGroup, Integer> groupInst = new Instantiator<OperatorGroup, Integer>();
		makeGroupsAndKinds(initOpGroups, groups, firstKinds, kindInst,
				groupInst);

		final AllInOnceMap<String, Integer> idKind = new AllInOnceMap<String, Integer>();
		for (Entry<String, Integer> entry : initOpReg.getIdKind().entrySet()) {
			final Integer newKind = kindInst.instantiate(entry.getValue());
			idKind.put(entry.getKey(), newKind);
		}

		final Map<OperatorGroup, Set<OperatorGroup>> priorityMap = initOpReg
				.getGroupPriority().getRelationMap();
		final Matrix groupPriority = convert(groups.length, groupInst,
				priorityMap);

		return new OperatorRegistryCompact(groups, firstKinds, idKind,
				groupPriority);
	}

	private static Matrix convert(int groupCount,
			Instantiator<OperatorGroup, Integer> groupInst,
			Map<OperatorGroup, Set<OperatorGroup>> priorityMap) {
		final Matrix groupPriority = new Matrix(groupCount);
		for (Entry<OperatorGroup, Set<OperatorGroup>> rel : priorityMap
				.entrySet()) {
			final int left = groupInst.instantiate(rel.getKey());
			for (OperatorGroup groupRight : rel.getValue()) {
				final int right = groupInst.instantiate(groupRight);
				groupPriority.set(left, right);
			}
		}
		return groupPriority;
	}

	private void makeGroupsAndKinds(
			final Collection<OperatorGroup> initOpGroups,
			final OperatorGroupCompact[] groups, final int[] firstKinds,
			final Instantiator<Integer, Integer> kindInst,
			Instantiator<OperatorGroup, Integer> groupInst) {
		final OperatorGroup initGroup0 = initOpReg.getGroup0();
		int index = 0;
		firstKinds[index] = 0;// first kind of GROUP0
		final OpGroupCompactor gr0Compactor = new OpGroupCompactor(
				initGroup0, firstKinds[index], kindInst);
		groups[index] = gr0Compactor.compact();
		firstKinds[index + 1] = groups[index].getLastKind() + 1;
		groupInst.setInst(initGroup0, index);
		index++;
		for (OperatorGroup initOpGroup : initOpGroups) {
			if (initOpGroup == initGroup0) {
				continue;
			}
			final OpGroupCompactor compactor = new OpGroupCompactor(
					initOpGroup, firstKinds[index], kindInst);
			groups[index] = compactor.compact();
			firstKinds[index + 1] = groups[index].getLastKind() + 1;
			groupInst.setInst(initOpGroup, index);
			index++;
		}
	}
}
