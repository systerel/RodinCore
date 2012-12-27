/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core.builder;

import java.util.HashSet;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.rodinp.core.RodinCore;
import org.rodinp.internal.core.util.Util;

/**
 * @author Stefan Hallerstede
 *
 */
public class GraphModifier {

	private final Node current;
	
	private final Graph graph;
	
	public GraphModifier(Graph graph, Node current) {
		this.graph = graph;
		this.current = current;
	}
	
	protected void addNode(IPath path, String toolId) {
		Node node = graph.getNode(path);

		if(node == null) {
			node = graph.builderAddNodeToGraph(path);
			node.setToolId(toolId);
		} else if(node.isPhantom()) {
			node.setToolId(toolId);
			node.setDated(true);
			node.setPhantom(false);
			if (node.done)
				// nodes depending on this phantom may already have been processed
				// (... by being ignored)
				graph.setInstable();
		} else {
			node.setToolId(toolId);
			node.setDated(true);
		}
		
		// set symbolic link to the current node
		node.getCreator().setPath(current.getTarget().getPath());
		
		if(RodinBuilder.DEBUG_GRAPH)
			System.out.println(getClass().getName() + ": Node added: " + node.getTarget().getName()); //$NON-NLS-1$
	}
	
	protected boolean isPermissibleTarget(Node target) {
		String creatorName = target.getCreator().getName();
		return creatorName != null && creatorName.equals(current.getTarget().getName());
	}

	protected Node getNodeOrPhantom(IPath path) {
		Node node = graph.getNode(path);
		if(node == null) {
			node = graph.builderAddNodeToGraph(path);
			node.setDated(false);
			node.setPhantom(true);
		}		
		return node;
	}

	protected void addDependency(Link link, Node target) {
		if(current == null && RodinBuilder.DEBUG_GRAPH)
			System.out.println("No current node"); //$NON-NLS-1$
		if (!isPermissibleTarget(target))
			Util.log(new CoreException(new Status(IStatus.ERROR,
					RodinCore.PLUGIN_ID, 
					Platform.PLUGIN_ERROR, 
					"Target [" + target.toString() +  //$NON-NLS-1$
					" not permitted", null)), " while modifying dependency graph"); //$NON-NLS-1$
		boolean currentEqualsSource = current.equals(link.source);
		boolean targetIsSuccessor = current.hasSuccessorNode(target);
		if(currentEqualsSource || targetIsSuccessor) {
			target.addPredecessorLink(link);
			boolean instable = false;
			instable |= currentEqualsSource && target.done; 
				// the new target of the present node was already processed
			instable |= targetIsSuccessor && !link.source.done && !link.source.isPhantom();
				// the new source in the predecessor list of target has not been processed
			instable |= link.prio == Link.Priority.HIGH && link.source.getSuccessorPos() > 0; 
				// child nodes already traversed partially (and the new source is first in list)
			instable |= link.source.getSuccessorPos() >= link.source.getSuccessorCount(); 
				// the list of child nodes was already completely traversed
			if(instable) {
				graph.setInstable();
				target.setDated(true);
			}
		} else
			Util.log(new CoreException(new Status(IStatus.ERROR,
					RodinCore.PLUGIN_ID, 
					Platform.PLUGIN_ERROR, 
					"Dependency [" + link.source.toString() + " / " + target.toString() + "] from " +  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					current.getTarget().getName() + " not permitted", null)), " while modifying dependency graph"); //$NON-NLS-1$ //$NON-NLS-2$
		if(RodinBuilder.DEBUG_GRAPH)
			System.out.println(getClass().getName() + ": Added dependency: " +  //$NON-NLS-1$
					target.getTarget().getName() + " => " + link.source.getTarget().getName() + " instable = " + graph.isInstable()); //$NON-NLS-1$ //$NON-NLS-2$
	}

	protected boolean hasRemoveDelta(final List<Link> links, final List<Node> targets, String toolId) {
		
		// check if a target node has disappeared
		final HashSet<String> targetNames = new HashSet<String>(targets.size() * 4 / 3 + 1);
		for (Node target : targets)
			targetNames.add(target.getTarget().getName());
		for (Node node : current.getSuccessorNodes(toolId)) {
			String creatorName = node.getCreator().getName();
			if (creatorName != null 
					&& creatorName.equals(current.getTarget().getName()) 
					&& !targetNames.contains(node.getTarget().getName()))
				return true;
		}
		
		// check if a link has disappeared
		final HashSet<Node> targetSet = new HashSet<Node>(targets.size() * 4 / 3 + 1);
		targetSet.addAll(targets);
		for (Node target : targetSet) {
			List<Link> targetLinkList = target.getPredessorLinks();
			for (Link arc : targetLinkList)
				if (arc.id.equals(toolId) && !links.contains(arc)) {
					return true;
				}
		}
		
		return false;
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.builder.IGraph#removeDependencies(org.eclipse.core.runtime.IPath, java.lang.String)
	 */
	protected void removeDependencies(String toolId) {
		HashSet<Node> targets = current.getSuccessorNodes(toolId);
		for (Node node : targets) {
			node.removeAllLinks(toolId);
			node.setDated(true);
			if (RodinBuilder.DEBUG_GRAPH)
				System.out.println(getClass().getName()
						+ ": removed dependencies: " + //$NON-NLS-1$
						node.getTarget().getName());
			}
		graph.setInstable();
		if (RodinBuilder.DEBUG_GRAPH)
			System.out.println(getClass().getName()
					+ " instable = " + graph.isInstable()); //$NON-NLS-1$
	}

}
