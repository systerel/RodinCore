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
public class GraphHandler {

	private final Node current;
	
	private final Graph graph;
	
	public GraphHandler(Graph graph, Node current) {
		this.graph = graph;
		this.current = current;
	}
	
	/* (non-Javadoc)
	 * @see org.rodinp.core.builder.IGraph#addNode(org.eclipse.core.runtime.IPath, java.lang.String)
	 */
	public void addNode(IPath path, String producerId) { //throws CoreException {
		Node node = graph.getNode(path);

		if(node == null) {
			node = new Node();
			node.setPath(path);
			node.setToolId(producerId);
			graph.addNodeToGraph(node);
			graph.incN();
		} else if(node.isPhantom()) {
			node.setToolId(producerId);
			node.setDated(true);
			node.setPhantom(false);
			if (node.done)
				graph.setInstable(); // nodes depending on this phantom may already have been processed
		} else
			node.setToolId(producerId);
//			throw new CoreException(new Status(IStatus.ERROR,
//					RodinCore.PLUGIN_ID, 
//					Platform.PLUGIN_ERROR, "Node already exists: " + node.getName(), null)); //$NON-NLS-1$
		if(Graph.DEBUG)
			System.out.println(getClass().getName() + ": Node added: " + node.getName()); //$NON-NLS-1$
		node.setDated(true);
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.builder.IGraph#removeNode(org.eclipse.core.runtime.IPath)
	 */
	public void removeNode(IPath path) { //throws CoreException {
		Node node = graph.getNode(path);
		if(node == null || node.isPhantom())
			return;
		
		boolean notCurrentEqualsNode = !current.equals(node);
		boolean nodeIsSuccessor = current.hasSuccessor(node);
		boolean nodeIsRoot = node.getInCount() == 0;
		
		if( notCurrentEqualsNode || nodeIsSuccessor || nodeIsRoot) {
			node.markSuccessorsDated();
			graph.tryRemoveNode(node);
			graph.decN();
			
			// this could be optimized by checking if node.count < node.totalCount
			// if this is the case the node was not yet visited and the topsort stack
			// does not get invalidated, othewise we must restructure
			graph.setInstable();
			if(Graph.DEBUG)
				System.out.println(getClass().getName() + ": Node removed: " + node.getName()); //$NON-NLS-1$
		} else {
			if(Graph.DEBUG)
				System.out.println(getClass().getName() + ": Cannot remove node: " + node.getName()); //$NON-NLS-1$
		
//			throw new CoreException(new Status(IStatus.ERROR,
//					RodinCore.PLUGIN_ID, 
//					Platform.PLUGIN_ERROR, "Illegal attempt to remove node: " + node.getName(), null)); //$NON-NLS-1$
		}

	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.builder.IGraph#containsNode(org.eclipse.core.runtime.IPath)
	 */
	public boolean containsNode(IPath path) {
		Node node = graph.getNode(path);
		return node != null && !node.isPhantom();
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.builder.IGraph#addToolDependency(org.eclipse.core.runtime.IPath, org.eclipse.core.runtime.IPath, java.lang.String, boolean)
	 */
//	public void addToolDependency(IPath source, IPath target, String id,
//			boolean prioritize) throws CoreException {
//		addDependency(null, source, target, id, prioritize, Link.Provider.TOOL);
//	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.builder.IGraph#addUserDependency(org.eclipse.core.runtime.IPath, org.eclipse.core.runtime.IPath, org.eclipse.core.runtime.IPath, java.lang.String, boolean)
	 */
//	public void addUserDependency(IPath origin, IPath source, IPath target,
//			String id, boolean prioritize) throws CoreException {
//		addDependency(origin, source, target, id, prioritize, Link.Provider.USER);
//	}
	
	protected Node getNodeOrPhantom(IPath path) {
		Node node = graph.getNode(path);
		if(node == null) {
			node = new Node();
			node.setPath(path);
			node.setDated(false);
			node.setPhantom(true);
			graph.addNodeToGraph(node);
		}		
		return node;
	}

	protected void addDependency(Link link, Node target) { //throws CoreException {
		if(current == null && Graph.DEBUG)
			System.out.println("No current node"); //$NON-NLS-1$
		boolean currentEqualsSource = current.equals(link.source);
		boolean targetIsSuccessor = current.hasSuccessor(target);
		if(currentEqualsSource || targetIsSuccessor) {
			target.addLink(link);
			boolean instable = false;
			instable |= currentEqualsSource && target.done; 
				// the new target of the present node was already processed
			instable |= targetIsSuccessor && !link.source.done && !link.source.isPhantom();
				// the new source in the predecessor list of target has not been processed
			instable |= link.prio == Link.Priority.HIGH && link.source.getSuccPos() > 0; 
				// child nodes already traversed partially (and the new source is first in list)
			instable |= link.source.getSuccPos() > link.source.succSize(); 
				// the list of child nodes was already completely traversed
//			if(currentEqualsSource && targetNode.done)
//				graph.setInstable();
//				targetNode.setDated(false);
//			else if(targetIsSuccessor && !sourceNode.done)
//				graph.setInstable();
//				targetNode.setDated(false);
//			else if(prioritize && sourceNode.succPos > 0) // child nodes already traversed partially
//				graph.setInstable();
//			else if(sourceNode.succPos > sourceNode.succSize())
//				graph.setInstable();
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
//			throws CoreException {
		Node node = graph.getNode(target);
		if(node == null || node.isPhantom())
			return null;
//			throw new CoreException(new Status(IStatus.ERROR,
//					RodinCore.PLUGIN_ID, 
//					Platform.PLUGIN_ERROR, "Unknown node: " + target, null)); //$NON-NLS-1$
		Collection<IPath> deps = node.getSources(id);
		IPath[] paths = new IPath[deps.size()];
		deps.toArray(paths);
		return paths;
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.builder.IGraph#removeDependencies(org.eclipse.core.runtime.IPath, java.lang.String)
	 */
	public void removeDependencies(Collection<String> ids) { //throws CoreException {
		for (String id : ids) {
			for (Node node : graph) {
				node.removeLinks(id);
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
