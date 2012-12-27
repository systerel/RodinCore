/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.rodinp.core.builder;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

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
 * Requests to add nodes to the graph must be made
 * explicitly by methods <code>addNode()</code>.
 * Dependencies are managed by the facade. This saves clients from having 
 * to compute dependency graph deltas themselves. Dependency graph manipulation
 * uses a transaction mechanism behind the scenes. If the was any problem a
 * <code>CoreException</code> is thrown.
 * </p>
 * @see org.rodinp.core.builder.IExtractor
 *
 * @since 1.0
 */
public interface IGraph {

	/**
	 * Adds a a node "path" with associated tool into the graph. If a node with
	 * this path exists already only the tool is reassigned.
	 * 
	 * @param target
	 *            The file of the target node
	 * @throws CoreException if the graph could not be modified
	 */
	public void addTarget(IFile target) throws CoreException;
	
	/**
	 * Adds a dependency controlled by the user to the graph.
	 * Target must be a node added during the same extraction.
	 * 
	 * @param origin
	 *            The origin of this dependency.
	 *            This should be a resource visible to the user,
	 *            i.e. not a derived resource.
	 * @param source
	 *            The source of the dependency.
	 * @param target
	 *            The target of the dependency.
	 * @param prioritize
	 *            True if this dependency should be prioritized in the
	 *            topological order derived from the graph.
	 * @throws CoreException if the graph could not be modified
	 */
	public void addUserDependency(
			IFile origin, 
			IFile source, 
			IFile target,
			boolean prioritize) throws CoreException;
	
	/**
	 * Adds a dependency controlled by a tool to the graph.
	 * Target must be a node added during the same extraction.
	 * @param source
	 *            The source of the dependency.
	 * @param target
	 *            The target of the dependency.
	 * @param prioritize
	 *            True if this dependency should be prioritized in the
	 *            topological order derived from the graph.
	 * @throws CoreException if the graph could not be modified
	 */
	public void addToolDependency(
			IFile source, 
			IFile target, 
			boolean prioritize) throws CoreException;
	
}
