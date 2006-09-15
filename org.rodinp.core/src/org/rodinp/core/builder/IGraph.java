/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.rodinp.core.builder;

import org.eclipse.core.runtime.IPath;

/**
 * @author Stefan Hallerstede
 * 
 * <p>
 * This interface is used by the extractors registers with the builder
 * to manipulate the dependency graph of all Rodin resources of a Rodin
 * project. It is a Facade to the more complicated ways of manipulating
 * the dependency graph inside the builder.
 * </p><p>
 * Some information is cached in the corresponding object so the contents
 * of the facade must be synchronised with the graph at the end of an extraction.
 * </p><p>
 * Requests to add and remove nodes to resp. from the graph must be made
 * explicitly by methods <code>addNode()</code> and <code>removeNode()</code>.
 * Dependencies are managed by the facade. This saves clients from having 
 * to compute dependency graph deltas themselves.
 * </p>
 * @see org.rodinp.core.builder.IExtractor
 *
 */
public interface IGraph {

	/**
	 * Adds a a node "path" with associated tool into the graph. If a node with
	 * this path exists already only the tool is reassigned.
	 * 
	 * @param path
	 *            The path of the node
	 * @param toolId
	 *            The path of the tool
	 */
	public void addNode(IPath path, String toolId);
	
//	/**
//	 * Remove the node "path" from the graph. If the node does not exist,
//	 * nothing happens. All dependencies are removed with the node.
//	 * 
//	 * @param path
//	 *            The path of the node to be removed.
//	 */
//	public void removeNode(IPath path);
	
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
	 */
	public void putUserDependency(IPath origin, IPath source, IPath target,
			String id, boolean prioritize);
	
	/**
	 * Adds a dependency controlled by a tool to the graph.
	 * @param source
	 *            The source of the dependency.
	 * @param target
	 *            The target of the dependency.
	 * @param id
	 *            The kind identifier of the dependency.
	 * @param prioritize
	 *            True if this dependency should be prioritized in the
	 *            topological order derived from the graph.
	 */
	public void putToolDependency(IPath source, IPath target, String id,
			boolean prioritize);

	/**
	 * When all manipulations have been done method <code>updateGraph()</code>
	 * must be called to synchronise the state of the graph.
	 */
	public void updateGraph();
}
