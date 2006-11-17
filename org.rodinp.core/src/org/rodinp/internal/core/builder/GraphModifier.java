/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.rodinp.internal.core.builder;

import java.util.Collection;

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
	
	private final ProgressManager manager;
	
	public GraphModifier(Graph graph, Node current, ProgressManager manager) {
		this.graph = graph;
		this.current = current;
		this.manager = manager;
	}
	
	/* (non-Javadoc)
	 * @see org.rodinp.core.builder.IGraph#addNode(org.eclipse.core.runtime.IPath, java.lang.String)
	 */
	public void addNode(IPath path, String producerId) { //throws CoreException {
		Node node = graph.getNode(path);

		if(node == null) {
			node = graph.builderAddNodeToGraph(path);
			node.setToolId(producerId);
		} else if(node.isPhantom()) {
			node.setToolId(producerId);
			node.setDated(true);
			node.setPhantom(false);
			if (node.done)
				graph.setInstable(); // nodes depending on this phantom may already have been processed
		} else {
			node.setToolId(producerId);
			node.setDated(true);
		}
		
		manager.anticipateSlice(node);
		
		if(Graph.DEBUG)
			System.out.println(getClass().getName() + ": Node added: " + node.getName()); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.builder.IGraph#containsNode(org.eclipse.core.runtime.IPath)
	 */
	public boolean containsNode(IPath path) {
		Node node = graph.getNode(path);
		return node != null && !node.isPhantom();
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

	protected void addDependency(Link link, Node target) { //throws CoreException {
		if(current == null && Graph.DEBUG)
			System.out.println("No current node"); //$NON-NLS-1$
		boolean currentEqualsSource = current.equals(link.source);
		boolean targetIsSuccessor = current.hasSuccessor(target);
		if(currentEqualsSource || targetIsSuccessor) {
			target.addPredecessorLink(link);
			boolean instable = false;
			instable |= currentEqualsSource && target.done; 
				// the new target of the present node was already processed
			instable |= targetIsSuccessor && !link.source.done && !link.source.isPhantom();
				// the new source in the predecessor list of target has not been processed
			instable |= link.prio == Link.Priority.HIGH && link.source.getSuccessorPos() > 0; 
				// child nodes already traversed partially (and the new source is first in list)
			instable |= link.source.getSuccessorPos() > link.source.getSuccessorCount(); 
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
					current.getName() + " not permitted", null)), " while modifying dependency graph"); //$NON-NLS-1$
		if(Graph.DEBUG)
			System.out.println(getClass().getName() + ": Added dependency: " +  //$NON-NLS-1$
					target.getName() + " => " + link.source.getName() + " instable = " + graph.isInstable()); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.builder.IGraph#getDependencies(org.eclipse.core.runtime.IPath, java.lang.String)
	 */
	public IPath[] getDependencies(IPath target, String id) {
		Node node = graph.getNode(target);
		if(node == null || node.isPhantom())
			return null;
		Collection<IPath> deps = node.getSources(id);
		IPath[] paths = new IPath[deps.size()];
		deps.toArray(paths);
		return paths;
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.builder.IGraph#removeDependencies(org.eclipse.core.runtime.IPath, java.lang.String)
	 */
	public void removeDependencies(Collection<String> ids) {
		for (String id : ids) {
			for (Node node : graph) {
				node.removeAllLinks(id);
				node.setDated(true);
				if (Graph.DEBUG)
					System.out.println(getClass().getName()
							+ ": removed dependencies: " + //$NON-NLS-1$
							node.getName());
			}
		}
		if (!ids.isEmpty())
			graph.setInstable();
		if (Graph.DEBUG)
			System.out.println(getClass().getName()
					+ " instable = " + graph.isInstable()); //$NON-NLS-1$
	}

}
