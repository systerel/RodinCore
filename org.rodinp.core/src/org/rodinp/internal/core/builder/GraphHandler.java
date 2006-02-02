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
import org.rodinp.core.builder.IGraph;

/**
 * @author Stefan Hallerstede
 *
 */
public class GraphHandler implements IGraph {

	private final Node current;
	
	private final Graph graph;
	
	public GraphHandler(Graph graph, Node current) {
		this.graph = graph;
		this.current = current;
	}
	
	/* (non-Javadoc)
	 * @see org.rodinp.core.builder.IGraph#addNode(org.eclipse.core.runtime.IPath, java.lang.String)
	 */
	public void addNode(IPath path, String producerId) throws CoreException {
		Node node = graph.getNode(path);

		if(node == null) {
			node = new Node();
			node.setPath(path);
			node.setProducerId(producerId);
			graph.addNodeToGraph(node);
			graph.incN();
		} else if(node.isPhantom()) {
			node.setProducerId(producerId);
			node.setDated(true);
			node.setPhantom(false);
		} else
			node.setProducerId(producerId);
//			throw new CoreException(new Status(IStatus.ERROR,
//					RodinCore.PLUGIN_ID, 
//					Platform.PLUGIN_ERROR, "Node already exists: " + node.getName(), null)); //$NON-NLS-1$
		if(RodinBuilder.DEBUG)
			System.out.println(getClass().getName() + ": Node added: " + node.getName()); //$NON-NLS-1$
		node.setDated(true);
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.builder.IGraph#removeNode(org.eclipse.core.runtime.IPath)
	 */
	public void removeNode(IPath path) throws CoreException {
		Node node = graph.getNode(path);
		if(node == null || node.isPhantom())
			return;
		
		boolean notCurrentEqualsSource = !current.equals(node);
		boolean nodeIsSuccessor = current.hasSuccessor(node);
		boolean nodeIsRoot = node.getInCount() == 0;
		
		if( notCurrentEqualsSource || nodeIsSuccessor || nodeIsRoot) {
			node.markSuccessorsDated();
			graph.tryRemoveNode(node);
			graph.decN();
			
			// this could be optimized by checking if node.count < node.totalCount
			// if this is the case the node was not yet visited and the topsort stack
			// does not get invalidated, othewise we must restructure
			graph.setInstable();
			if(RodinBuilder.DEBUG)
				System.out.println(getClass().getName() + ": Node removed: " + node.getName()); //$NON-NLS-1$
		
		} else {
			throw new CoreException(new Status(IStatus.ERROR,
					RodinCore.PLUGIN_ID, 
					Platform.PLUGIN_ERROR, "Illegal attempt to remove node: " + node.getName(), null)); //$NON-NLS-1$
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
	public void addToolDependency(IPath source, IPath target, String id,
			boolean prioritize) throws CoreException {
		addDependency(null, source, target, id, prioritize, Link.Provider.TOOL);
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.builder.IGraph#addUserDependency(org.eclipse.core.runtime.IPath, org.eclipse.core.runtime.IPath, org.eclipse.core.runtime.IPath, java.lang.String, boolean)
	 */
	public void addUserDependency(IPath origin, IPath source, IPath target,
			String id, boolean prioritize) throws CoreException {
		addDependency(origin, source, target, id, prioritize, Link.Provider.USER);
	}

	private void addDependency(IPath origin, IPath source, IPath target, String id, boolean prioritize, Link.Provider prov) throws CoreException {
		Node sourceNode = graph.getNode(source);
		Node targetNode = graph.getNode(target);
		Node originNode = (origin != null) ? graph.getNode(origin) : null;
		if(origin != null && originNode == null)
			throw new CoreException(new Status(IStatus.ERROR,
					RodinCore.PLUGIN_ID, 
					Platform.PLUGIN_ERROR, "Unknown node: " + origin.toString(), null)); //$NON-NLS-1$
		if(sourceNode == null) {
			sourceNode = new Node();
			sourceNode.setPath(source);
			sourceNode.setDated(false);
			sourceNode.setPhantom(true);
			graph.addNodeToGraph(sourceNode);
		}
		if(targetNode == null) {
			targetNode = new Node();
			targetNode.setPath(target);
			targetNode.setDated(false);
			targetNode.setPhantom(true);
			graph.addNodeToGraph(targetNode);
		}
		if(current == null && RodinBuilder.DEBUG)
			System.out.println("No current node"); //$NON-NLS-1$
		boolean currentEqualsSource = current.equals(sourceNode);
		boolean targetIsSuccessor = current.hasSuccessor(targetNode);
		if(currentEqualsSource || targetIsSuccessor) {
			targetNode.addLink(originNode, sourceNode, id, prov, prioritize ? Link.Priority.HIGH : Link.Priority.LOW);
			boolean instable = false;
			instable |= currentEqualsSource && targetNode.done; // the new target of the present node was already processed
			instable |= targetIsSuccessor && !sourceNode.done; // the new source in the predecessor list of target has not been processed
			instable |= prioritize && sourceNode.getSuccPos() > 0; // child nodes already traversed partially (and the new source is first in list)
			instable |= sourceNode.getSuccPos() > sourceNode.succSize(); // the list of child nodes was already completely traversed
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
				targetNode.setDated(true);
			}
		} else
			throw new CoreException(new Status(IStatus.ERROR,
					RodinCore.PLUGIN_ID, 
					Platform.PLUGIN_ERROR, 
					"Dependency [" + source.toString() + " / " + target.toString() + "] from " +  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					current.getName() + " not permitted", null)); //$NON-NLS-1$
		if(RodinBuilder.DEBUG)
			System.out.println(getClass().getName() + ": Added dependency: " +  //$NON-NLS-1$
					targetNode.getName() + " => " + sourceNode.getName() + " instable = " + graph.isInstable()); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.builder.IGraph#getDependencies(org.eclipse.core.runtime.IPath, java.lang.String)
	 */
	public IPath[] getDependencies(IPath target, String id)
			throws CoreException {
		Node node = graph.getNode(target);
		if(node == null || node.isPhantom())
			throw new CoreException(new Status(IStatus.ERROR,
					RodinCore.PLUGIN_ID, 
					Platform.PLUGIN_ERROR, "Unknown node: " + target, null)); //$NON-NLS-1$
		Collection<IPath> deps = node.getSources(id);
		IPath[] paths = new IPath[deps.size()];
		deps.toArray(paths);
		return paths;
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.builder.IGraph#removeDependencies(org.eclipse.core.runtime.IPath, java.lang.String)
	 */
	public void removeDependencies(IPath target, String id)
			throws CoreException {
		Node node = graph.getNode(target);
		if(node == null || node.isPhantom())
			throw new CoreException(new Status(IStatus.ERROR,
					RodinCore.PLUGIN_ID, 
					Platform.PLUGIN_ERROR, "Unknown node: " + target, null));
//		if(!current.hasSuccessor(node))
//			throw new CoreException(new Status(IStatus.ERROR,
//					RodinCore.PLUGIN_ID, 
//					Platform.PLUGIN_ERROR, "Cannot remove dependencies for target: " + node.getName(), null));
		node.removeLinks(id);
		node.setDated(true);
		graph.setInstable();
		if(RodinBuilder.DEBUG)
			System.out.println(getClass().getName() + ": removed dependencies: " +  //$NON-NLS-1$
					node.getName() + " instable = " + graph.isInstable()); //$NON-NLS-1$

	}

}
