/*******************************************************************************
 * Copyright (c) 2010, 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.parser.operators;

import org.eventb.core.ast.extension.CycleError;
import org.eventb.core.ast.extension.StandardGroup;

/**
 * @author Nicolas Beauger
 *  
 */
public class OperatorRegistry {

	private final OperatorGroup group0 = new OperatorGroup(
			StandardGroup.GROUP_0.getId());
	
	private final AllInOnceMap<String, OperatorGroup> idOpGroup = new AllInOnceMap<String, OperatorGroup>();
	private final AllInOnceMap<Integer, OperatorGroup> kindOpGroup = new AllInOnceMap<Integer, OperatorGroup>();
	private final AllInOnceMap<String, Integer> idKind = new AllInOnceMap<String, Integer>();
	
	
	private final Closure<OperatorGroup> groupPriority = new Closure<OperatorGroup>();
	
	public OperatorRegistry() {
		idOpGroup.put(StandardGroup.GROUP_0.getId(), group0);
	}
	
	public OperatorGroup getGroup0() {
		return group0;
	}
	
	public AllInOnceMap<String, OperatorGroup> getIdOpGroup() {
		return idOpGroup;
	}
	
	public AllInOnceMap<Integer, OperatorGroup> getKindOpGroup() {
		return kindOpGroup;
	}
	
	public AllInOnceMap<String, Integer> getIdKind() {
		return idKind;
	}
	
	public Closure<OperatorGroup> getGroupPriority() {
		return groupPriority;
	}

	public void addOperator(Integer kind, String operatorId, String groupId, boolean isSpaced) {
		idKind.put(operatorId, kind);
		
		OperatorGroup operatorGroup = idOpGroup.getNoCheck(groupId);
		if (operatorGroup == null) {
			operatorGroup = new OperatorGroup(groupId);
			idOpGroup.put(groupId, operatorGroup);
		}
		operatorGroup.add(kind);
		
		kindOpGroup.put(kind, operatorGroup);
		
		if (isSpaced) {
			operatorGroup.setSpaced(kind);
		}
	}
	
	public void addCompatibility(String leftOpId, String rightOpId) {
		final Integer leftKind = idKind.get(leftOpId);
		final Integer rightKind = idKind.get(rightOpId);
		final OperatorGroup group = getAndCheckSameGroup(leftKind, rightKind);
		group.addCompatibility(leftKind, rightKind);
	}

	public void addAssociativity(String opId) {
		final Integer opKind = idKind.get(opId);
		final OperatorGroup group = kindOpGroup.get(opKind);
		group.addAssociativity(opKind);
	}

	// lowOpId gets a lower priority than highOpId
	public void addPriority(String lowOpId, String highOpId)
			throws CycleError {
		final Integer leftKind = idKind.get(lowOpId);
		final Integer rightKind = idKind.get(highOpId);
		final OperatorGroup group = getAndCheckSameGroup(leftKind, rightKind);
		group.addPriority(leftKind, rightKind);
	}

	// FIXME public operations that call this method should throw a caught exception
	private OperatorGroup getAndCheckSameGroup(Integer leftKind, Integer rightKind) {
		final OperatorGroup leftGroup = kindOpGroup.get(leftKind);
		final OperatorGroup rightGroup = kindOpGroup.get(rightKind);
		if (leftGroup != rightGroup) {
			throw new IllegalArgumentException("Operators " + leftKind + " and "
					+ rightKind + " do not belong to the same group");
		}
		return leftGroup;
	}
	
	// lowGroupId gets a lower priority than highGroupId
	public void addGroupPriority(String lowGroupId, String highGroupId)
			throws CycleError {
		final OperatorGroup lowGroup = idOpGroup.get(lowGroupId);
		final OperatorGroup highGroup = idOpGroup.get(highGroupId);
		groupPriority.add(lowGroup, highGroup);
	}

	public boolean hasGroup(int kind) {
		return kindOpGroup.containsKey(kind);
	}

}
