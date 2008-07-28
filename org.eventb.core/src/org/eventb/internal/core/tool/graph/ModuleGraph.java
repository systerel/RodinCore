/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.tool.graph;

import java.util.List;

import org.eventb.internal.core.tool.ModuleDesc;
import org.eventb.internal.core.tool.types.IModule;

/**
 * @author Stefan Hallerstede
 *
 */
public class ModuleGraph extends Graph<ModuleDesc<? extends IModule>> {

	public ModuleGraph(String creator) {
		super(creator);
	}

	public void analyse(ParentGraph parentGraph) {
		complete(parentGraph);
		analyse();
	}

	@Override
	public ModuleNode getNode(String id) {
		assert id != null;
		return (ModuleNode) super.getNode(id);
	}
	
	private void complete(ParentGraph parentGraph) {
		for (String id : parentGraph) {
			ModuleNode node = getNode(id);
			String parentId = node.getObject().getParent();
			if (parentId == null)
				continue;
			ModuleNode pNode = getNode(parentId);
			if (pNode == null)
				throw new IllegalStateException("Unknown parent " + parentId + " for module type " + id);
			if (!pNode.canBeParent())
				throw new IllegalStateException("Module type " + id + " cannot be parent");
			for (String reqId : node.getPredecs()) {
				pNode.addPredec(reqId);
			}
			pNode.addPredec(id);
			
			mergePredecs(pNode);
			
			node.storeFilterInParent(pNode);
		}
	}

	private void mergePredecs(ModuleNode pNode) {
		String parentId =  pNode.getId();
		String ppId = pNode.getObject().getParent();
		if (ppId == null) {
			for (String mId : pNode.getPredecs()) {
				if (mId.equals(parentId) || nodeHasRoot(mId, parentId))
					continue;
				throw new IllegalStateException("Root module type " + parentId + " has proper prereq");
			}
		} else {
			for (String mId : pNode.getPredecs()) {
				ModuleNode mNode = getNode(mId);
				if (mNode == null)
					throw new IllegalStateException("Prereq module type " + mId + " unknown");
				String mParent = mNode.getObject().getParent();
				if (mParent != null && !mNode.getParents().contains(parentId) && !mParent.equals(ppId)) {
					// the predec has a different parent
					addPredecForAncestor(pNode, mNode);
				}
			}
		}
	}
	
	private void addPredecForAncestor(final ModuleNode node, final ModuleNode predec) {
		List<String> nParents = node.getParents();
		
		ModuleNode pNode = predec;
		String pParent = predec.getObject().getParent(); 
		while (pParent != null) {
			if (nParents.contains(pParent)) {
				node.addPredec(pNode.getId());
				return;
			}
			pNode = getNode(pParent);
			pParent = pNode.getObject().getParent();
		}
		throw new IllegalStateException(
				"Cannot satisfy prereq " + predec.getId() + 
				" for module type " + node.getId());
	}

	private boolean nodeHasRoot(final String id, final String parentId) {
		return getNode(id).getParents().contains(parentId);
	}

	@Override
	protected Node<ModuleDesc<? extends IModule>> createNode(final ModuleDesc<? extends IModule> object) {
		return object.createNode(this);
	}

	@Override
	public String getName() {
		return super.getName() + " Module graph";
	}

}
