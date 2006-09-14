/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.internal.core.builder;

import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.rodinp.core.IRodinDBMarker;
import org.rodinp.internal.core.ElementTypeManager;
import org.rodinp.internal.core.util.Messages;
import org.rodinp.internal.core.util.Util;

import com.sun.org.apache.bcel.internal.generic.ISUB;

/**
 * @author Stefan Hallerstede
 *
 */
public class RodinBuilder extends IncrementalProjectBuilder {
	
	public static boolean DEBUG = false;
	
	BuildState state;
	
	ElementTypeManager elementTypeManager;
	
	@Override
	protected void startupOnInitialize() {
        // add builder init logic here
		
		state = null;
		
		elementTypeManager = ElementTypeManager.getElementTypeManager();
     }
	
	class RodinBuilderDeltaVisitor implements IResourceDeltaVisitor {

		final IProgressMonitor monitor;
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
		 */
		public boolean visit(IResourceDelta delta) throws CoreException {
			IResource resource = delta.getResource();
			switch (delta.getKind()) {
			case IResourceDelta.ADDED:
				// handle added resource
				
				if(createNode(resource) != null)
					markNodeDated(resource, true, monitor);
				break;
			case IResourceDelta.REMOVED:
				// handle removed resource
				Node node = state.graph.getNode(resource.getFullPath());
				if(node == null)
					break;
				state.graph.removeNodeFromGraph(node, makeProgressMonitor(monitor));
				break;
			case IResourceDelta.CHANGED:
				// handle changed resource
				markNodeDated(resource, true, monitor);
				break;
			}
			//return true to continue visiting children.
			return true;
		}

		public RodinBuilderDeltaVisitor(IProgressMonitor monitor) {
			this.monitor = monitor;
		}
	}
	
	// this is hardcoded for now but bust be replaced by a
	// request to the repository!
	Node createNode(IResource resource) {
		if(resource instanceof IFile) {
// TODO the builder should only react to creation of files whose content type
//		is recognised as 'RODIN'. For the moment we use file extensions.
			IFile file = (IFile) resource;
			String elementType = elementTypeManager.getFileElementType(file);
			if(elementType == null)
				return null;
			Node node = state.graph.getNode(resource.getFullPath());
			
			if(node == null) {
				node = new Node();
				node.setPath(resource.getFullPath());
				state.graph.addNodeToGraph(node);
			}
			return node;

		}
		return null;
	}

	class RodinBuilderResourceVisitor implements IResourceVisitor {
		
		final IProgressMonitor monitor;
		
		public boolean visit(IResource resource) {
			markNodeDated(resource, true, monitor);
			//return true to continue visiting children.
			return true;
		}

		public RodinBuilderResourceVisitor(IProgressMonitor monitor) {
			this.monitor = monitor;
		}
	}

	protected static void deleteMarkers(IFile file) {
		try {
			if(file.exists())
				file.deleteMarkers(IRodinDBMarker.RODIN_PROBLEM_MARKER, false, IResource.DEPTH_ZERO);
		} catch (CoreException e) {
			Util.log(e, "when deleting markers");
		}
	}

	@Override
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
			throws CoreException {
		
		if (monitor == null)
			monitor = new NullProgressMonitor();
	
		try {
		
			monitor.beginTask(
					Messages.bind(Messages.build_building, getProject().getName()), 
					IProgressMonitor.UNKNOWN);
		
			if (DEBUG) {
				String kindImage = 
					kind == FULL_BUILD ? "full" :
						kind == AUTO_BUILD ? "auto" :
							kind == INCREMENTAL_BUILD ? "incremental" :
								"unknown";
				System.out.println("##############################################");
				System.out.println("BUILDER: Starting " + kindImage + " build.");
			}
		
			if (state == null)
				state = BuildState.getBuildState(getProject(), monitor);
			if (kind == FULL_BUILD) {
				fullBuild(monitor);
			} else {
				IResourceDelta delta = getDelta(getProject());
				if (delta == null) {
					fullBuild(monitor);
				} else {
					incrementalBuild(delta, monitor);
				}
			}
		} finally {
			monitor.done();
			if (DEBUG) {
				System.out.println("BUILDER: Finished build.");
				System.out.println("##############################################");
			}
		}
		return null;
	}
	
	IProgressMonitor makeProgressMonitor(IProgressMonitor monitor) {
		return new SubProgressMonitor(makeBuilderProgressMonitor(monitor), 1);
	}

	IProgressMonitor makeBuilderProgressMonitor(IProgressMonitor monitor) {
		return new BuilderProgressMonitor(monitor, this);
	}

	/**
	 * Hook allowing to reset some static state after a complete build iteration.
	 * This hook is invoked during POST_AUTO_BUILD notification
	 */
	public static void buildFinished() {
		// build has finished
    }
    
	private void buildGraph(IProgressMonitor monitor) throws CoreException {
		state.graph.buildGraph(makeBuilderProgressMonitor(monitor));
	}
	
	/**
	 * Hook allowing to initialize some static state before a complete build iteration.
	 * This hook is invoked during PRE_AUTO_BUILD notification
	 */
	public static void buildStarting() {
		// build is about to start
	}

	private void cleanGraph(IProgressMonitor monitor) throws CoreException {
		if (state == null)
			state = BuildState.getBuildState(getProject(), monitor);
		state.graph.cleanGraph(makeProgressMonitor(monitor), getProject());
		state.graph = new Graph();
	}
	
	void markNodeDated(IResource resource, boolean changed, IProgressMonitor monitor) {
		if (resource instanceof IFile) {
			Node node = state.graph.getNode(resource.getFullPath());
			if(node == null) {
				if(DEBUG)
					System.out.println(getClass().getName() + ": Node not in dependency graph " + resource.getName()); //$NON-NLS-1$
				node = createNode(resource);
			}
			if(node != null) {
				// TODO management of markers should be connected to extraction
				deleteMarkers((IFile) resource);
				// TODO implement editable and non-editable resource options in builder graph.
				// some resources, e.g., files maintained by the prover are derived and editable;
				// other resources may be editable or non-editable, e.g., Event-B models that may
				// have been entered directly by the user, or created by a tool such as u2b;
				// in the first case it is editable, in the second it isn't.
				// TODO the distinction based on changed is not really necessary because only the
				// extractors are run on the first element of a non-tool node of the graph.
				// So by default we could always implement for the case "changed = true".
				if (changed)
					try {
						state.graph.extractNode(node, makeProgressMonitor(monitor));
					} catch (CoreException e) {
						Util.log(e, "during extraction after change");
					}
			    else
					node.setDated(true);
			} else if(DEBUG)
				System.out.println(getClass().getName() + ": Cannot create node " + resource.getName()); //$NON-NLS-1$
		}
	}

	protected void fullBuild(final IProgressMonitor monitor) throws CoreException {
		try {
			cleanGraph(monitor);
			getProject().accept(new RodinBuilderResourceVisitor(monitor));
		} catch (CoreException e) {
			Util.log(e, "during builder full build");
		}
		try {
			buildGraph(monitor);
		} catch (OperationCanceledException e) {
			if(isInterrupted())
				return;
			else throw e;
		}
	}
	
	@Override
	protected void clean(IProgressMonitor monitor) {
        try {
			if (DEBUG) {
				System.out.println("BUILDER: Starting cleaning");
			}
        	cleanGraph(monitor);
        } catch (CoreException e) {
			Util.log(e, "during builder clean");
		} catch (OperationCanceledException e) {
			if (isInterrupted())
				return;
			throw e;
		} finally {
			if (DEBUG) {
				System.out.println("BUILDER: Finished cleaning");
			}
		}
     }

	protected void incrementalBuild(IResourceDelta delta,
			IProgressMonitor monitor) throws CoreException {
		// the visitor does the work.
		delta.accept(new RodinBuilderDeltaVisitor(monitor));
		try {
			buildGraph(monitor);
		} catch (OperationCanceledException e) {
			if(isInterrupted())
				return;
			else throw e;
		}
	}

}
