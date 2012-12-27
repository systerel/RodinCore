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
package org.rodinp.internal.core.builder;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.rodinp.core.RodinCore;
import org.rodinp.core.builder.IGraph;
import org.rodinp.internal.core.util.Messages;

/**
 * @author Stefan Hallerstede
 *
 */
public class GraphTransaction implements IGraph {

	private boolean opened;
	private boolean closed;
	
	private final ArrayList<Link> links;
	private final ArrayList<Node> targets;
	private final GraphModifier handler;
	
	private final String toolId;
	
	public GraphTransaction(GraphModifier handler, String toolId) {
		
		opened = false;
		closed = false;
		
		this.handler = handler;
		this.toolId = toolId;
		
		links = new ArrayList<Link>(7);
		targets = new ArrayList<Node>(7);
	}
	
	/* (non-Javadoc)
	 * @see org.rodinp.core.builder.IGraph#putUserDependency(IFile, IFile, IFile, String, boolean)
	 */
	@Override
	public void addUserDependency(
			IFile origin, 
			IFile source, 
			IFile target,
			boolean prioritize) throws CoreException {
		if (!opened)
			throw makeGraphTransactionError();
		
		IPath originPath =  origin.getFullPath();
		IPath sourcePath =  source.getFullPath();
		IPath targetPath =  target.getFullPath();
		
		links.add(new Link(Link.Provider.USER, 
						prioritize ? Link.Priority.HIGH : Link.Priority.LOW, 
						toolId, 
						handler.getNodeOrPhantom(sourcePath), 
						handler.getNodeOrPhantom(originPath)));
		Node node = handler.getNodeOrPhantom(targetPath);
		targets.add(node);
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.builder.IGraph#putToolDependency(IFile, IFile, String, boolean)
	 */
	@Override
	public void addToolDependency(
			IFile source, 
			IFile target, 
			boolean prioritize) throws CoreException {
		if (!opened)
			throw makeGraphTransactionError();
		
		IPath sourcePath =  source.getFullPath();
		IPath targetPath =  target.getFullPath();
		
		links.add(new Link(Link.Provider.TOOL, 
				prioritize ? Link.Priority.HIGH : Link.Priority.LOW, 
				toolId, 
				handler.getNodeOrPhantom(sourcePath), 
				null));
		Node node = handler.getNodeOrPhantom(targetPath);
		targets.add(node);
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.builder.IGraph#addNode(org.eclipse.core.resources.IFile, java.lang.String)
	 */
	@Override
	public void addTarget(IFile file) throws CoreException {
		if (!opened)
			throw makeGraphTransactionError();
		
		handler.addNode(file.getFullPath(), toolId);
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.builder.IGraph#closeGraph()
	 */
	public void closeGraph() throws CoreException {
		if (opened && !closed) {
			opened = false;
			closed = true;
		} else
			throw makeGraphTransactionError();
		
		// first we compute if there is any change
//		boolean remove = false;
//		for (int i=0; i<links.size(); i++) {
//			Node target = targets.get(i);
//			List<Link> targetLinkList = target.getPredessorLinks();
//			for (Link link : targetLinkList)
//				if (link.id.equals(toolId) && !links.contains(link)) {
//					remove = true;
//					break;
//				}
//		}
		
		boolean remove = handler.hasRemoveDelta(links, targets, toolId);
		
		if (remove)
			// all links with toolId are removed
			handler.removeDependencies(toolId);
		
		// add all links
		for (int i=0; i<links.size(); i++) {
			handler.addDependency(links.get(i), targets.get(i));
		}
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.builder.IGraph#openGraph()
	 */
	public void openGraph() throws CoreException {
		if (!opened && !closed) 
			opened = true;
		else
			throw makeGraphTransactionError();
	}
	
	private CoreException makeGraphTransactionError() {
		return new CoreException(
				new Status(
						IStatus.ERROR, 
						RodinCore.PLUGIN_ID, 
						IStatus.OK, 
						Messages.build_graphTransactionError, 
						null));
	}

}
