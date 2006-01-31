/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.core.builder;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

public interface IGraph {

	/**
	 * Adds a a node "path" with associated tool into the graph. If a node with
	 * this path exists already only the tool is reassigned.
	 * 
	 * @param path
	 *            The path of the node
	 * @param producerId
	 *            The path of the tool
	 */
	public void addNode(IPath path, String producerId)
			throws CoreException;

	/**
	 * Remove the node "path" from the graph. If the node does not exist,
	 * nothing happens. All dependencies are removed with the node.
	 * 
	 * @param path
	 *            The path of the node to be removed.
	 */
	public void removeNode(IPath path) throws CoreException;

	/**
	 * Checks whether the graph contains a particular node.
	 * 
	 * @param path
	 *            The path of the node.
	 * @return true if the node exists in the graph, false otherwise
	 */
	public boolean containsNode(IPath path);

	/**
	 * Adds a dependency controlled by a tool to the graph.
	 * 
	 * @param source
	 *            The source of the dependency.
	 * @param target
	 *            The target of the dependency.
	 * @param id
	 *            The kind identifier of the dependency.
	 * @param prioritize
	 *            True if this dependency should be prioritized in the
	 *            topological order derived from the graph.
	 * @throws CoreException
	 *             If the target node does not exist or if there is already a
	 *             dependency with the same source.
	 */
	public void addToolDependency(IPath source, IPath target, String id,
			boolean prioritize) throws CoreException;

	/**
	 * Adds a dependency controlled by the user to the graph.
	 * 
	 * @param origin
	 *            The origin of this dependency.
	 *            This should be a resource visible to the user,
	 *            i.e. not a derived resource.
	 * @param source
	 *            The source of the dependency.
	 * @param target
	 *            The target of the dependency.
	 * @param id
	 *            The kind identifier of the dependency.
	 * @param prioritize
	 *            True if this dependency should be prioritized in the
	 *            topological order derived from the graph.
	 * @throws CoreException
	 *             If the target node does not exist or if there is already a
	 *             dependency with the same source.
	 */
	public void addUserDependency(IPath origin, IPath source, IPath target,
			String id, boolean prioritize) throws CoreException;

	/**
	 * Returns the sources of the dependencies of target.
	 * 
	 * @param target
	 *            The target of the sources.
	 * @return The array of sources.
	 * @throws CoreException
	 *             If the target node does not exist.
	 */
	public IPath[] getDependencies(IPath target, String id)
			throws CoreException;

	/**
	 * Removes dependencies of kind "id" for the target from the graph
	 * 
	 * @param target
	 *            The target of the dependency.
	 * @param id
	 *            The kind of dependency to be removed.
	 * @throws CoreException
	 *             If the target node does not exist.
	 */
	public void removeDependencies(IPath target, String id)
			throws CoreException;
}
