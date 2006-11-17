/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.internal.core.builder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.rodinp.core.RodinCore;
import org.rodinp.core.builder.IAutomaticTool;
import org.rodinp.internal.core.util.Messages;
import org.rodinp.internal.core.util.Util;

/**
 * @author Stefan Hallerstede
 * 
 * Class <code>Graph</code> keeps the dependency graph of a Rodin project.
 * It implements methods for adding and removing nodes, <code>addNodeToGraph()</code> and
 * <code>removeNodeFromGraph</code>.
 * <p>
 * Removing a node from the graph is not straightforward because it has two semantics
 * depending on what kind of node is to removed.
 * <ul>
 * <li>If the node represents a derived resource, i.e. created by a tool, it can not
 * simply be removed and unlinked from the graph because this could invalidate the graph.
 * Instead, such nodes are turned into phantoms, if other nodes denpend on them. There
 * is a separate step <code>removePhantoms</code> that will eventually remove all phantom nodes.
 * </li>
 * <li>
 * If a not is not derived, then it can only be deleted on request of the user.
 * So it is removed permanently from the graph including all nodes derived from it.
 * Whether the resources corresponding to the files are deleted depends on the
 * implementation of the <code>clean()</code> method provided for that resource
 * (@see org.rodinp.core.builder.IAutomaticTool).
 * </li>
 * </ul>
 * </p>
 */
public class Graph implements Serializable, Iterable<Node> {
	
	private static final long serialVersionUID = 8944017873234553160L;

	public static boolean DEBUG = false;
	
	private HashMap<String,Node> nodes;
	
	transient private HashMap<IPath, Node> nodeCache;
	transient private LinkedList<Node> nodePreList; // contains the sorted nodes
	transient private LinkedList<Node> nodePostList; // contains the unsorted nodes
	
	transient private Stack<Node> nodeStack;
	
	transient private boolean instable; // true if the top order must be changed during build
	
	transient private ToolManager toolManager; // = GraphManager.getGraphManager();
	
	public Node getNode(String name) {
		return nodes.get(name);
	}

	public Node getNode(IPath path) {
		Node node = nodeCache.get(path);
		if(node == null) {
			node = nodes.get(path.toString());
			if(node == null)
				return null;
			nodeCache.put(path, node);
		}
		return node;
	}

	public Node builderAddNodeToGraph(IPath path) {
		Node node = new Node();
		node.setPath(path);
		nodes.put(node.getName(),node);
		nodePostList.add(node);
		return node;
	}

	public void builderRemoveNodeFromGraph(Node node, ProgressManager manager) {
		
		if (nodes.size() == 0)
			return;
		
		Collection<Node> values = new ArrayList<Node>(nodes.values());
		for(Node n : values)
			n.done = true;
		node.markReachableSuccessorsUndone();
		
		manager.subTask(Messages.bind(Messages.build_removing, node.getName()));
		
		for(Node n : values) {
			if(!n.done) {
				removeNode(n);
				try {
					removeNode(n, node, manager.getZeroProgressMonitor());
				} catch(CoreException e) {
					if(Graph.DEBUG)
						System.out.println(getClass().getName() + ": Error during remove&clean"); //$NON-NLS-1$
				}
			}
		}
		initCaches();
	}

	public void builderCleanGraph(IProject project, int percent, ProgressManager manager) throws CoreException {
			
			if (nodes.size() == 0)
				return;
			
			ArrayList<IStatus> vStats = null; // lazy initialized
			Collection<Node> values = new ArrayList<Node>(nodes.values());
			
			int slice = percent / values.size();
			
			manager.subTask(Messages.bind(Messages.build_cleaning, project.getName()));
			
			for(Node node : values) {
				try {
					cleanNode(node, manager.getProgressMonitor(slice));
					if(node.isDerived()) 
						tryRemoveNode(node);
					
				} catch(CoreException e) {
					if (vStats == null)
						vStats= new ArrayList<IStatus>();
					vStats.add(e.getStatus());
				}
			}
			initCaches();
			if (vStats != null) {
				IStatus[] stats= new IStatus[vStats.size()];
				vStats.toArray(stats);
				throw new CoreException(new MultiStatus(RodinCore.BUILDER_ID, IStatus.ERROR, stats, "Error while cleaning", null)); //$NON-NLS-1$
			}
		}

	/**
		 * This method implements the incremental build for Rodin projects.
		 * The building process terminates when all tools (@see IAutomaticTool) have run
		 * and the the graph is stable. As dependencies are added, changed, or removed during the build,
		 * it can happen that the build would have been started with wrong dependencies, hence,
		 * the topological order would be invalid for the Rodin project. In this case
		 * the build is restarted, recreating all derived resources that may have been invalidated.
		 * @param manager
		 * 		The progress manager to use
		 * @throws CoreException
		 * 		If any problem occurred during build.
		 */
		public void builderBuildGraph(ProgressManager manager) throws CoreException {
			if(Graph.DEBUG)
				System.out.print(getClass().getName() + ": IN Graph:\n" + printGraph()); //$NON-NLS-1$
			instable = true;
			while(instable) {
				topSortInit();
				topSortNodes(nodePreList, true, manager);
				if(Graph.DEBUG)
					System.out.print(getClass().getName() + ": OUT Graph:\n" + printGraph()); //$NON-NLS-1$
				if(Graph.DEBUG)
					System.out.println(getClass().getName() + ": Build Order: " + nodePreList.toString()); //$NON-NLS-1$
				if(instable) {
					if(Graph.DEBUG)
						System.out.println(getClass().getName() + ": Graph structure may have changed. Reordering ..."); //$NON-NLS-1$
					continue;
				}
				commit();
			}
			removePhantoms();
		}

	public void builderExtractNode(Node node, ProgressManager manager) throws CoreException {
			extract(node, new GraphModifier(this, node, manager), manager);
			if (node.getToolId() == null || node.getToolId() == "")
				node.setDated(false);
		}

	public Graph() {
		nodes = new HashMap<String,Node>(11);
		
		nodeCache = new HashMap<IPath, Node>(11);
		nodePreList = new LinkedList<Node>();
		nodePostList = new LinkedList<Node>();

	}
	
	public void initCaches() {
		nodeCache = new HashMap<IPath, Node>(nodes.size());
		nodePreList = new LinkedList<Node>();
		nodePostList = new LinkedList<Node>(nodes.values());
	}
	
	private ToolManager getManager() {
		if(toolManager == null)
		  	toolManager = ToolManager.getToolManager();
		return toolManager;
	}
	
	private String printGraph() {
		String res = ""; //$NON-NLS-1$
		for(Node node : nodes.values()) {
			res = res + node.printNode() + "\n"; //$NON-NLS-1$
			
		}
		return res;
	}
	
	@Override
	public String toString() {
		return printGraph();
	}
		
	private void runTool(Node node, ProgressManager manager) {
		if(node.isPhantom())
			return;
		if(node.dependsOnPhantom()) {
			node.printPhantomProblem();
			return;
		}
		String toolName = node.getToolId();
		IFile file = node.getFile();
		if (file == null) {// resource is not a file
			Util.log(null, "Builder resource not a file" + file.getName()); //$NON-NLS-1$
			if (Graph.DEBUG)
				System.out.println(getClass().getName() + ": Builder resource not a file!"); //$NON-NLS-1$
			return;
		}
		
		boolean changed = false;
		
		if (toolName == null || toolName.equals("")) {
			if(Graph.DEBUG)
				System.out.println(getClass().getName() + ": Root node changed: " + node.getName());
			
			changed = true;
			
		} else {
			if(Graph.DEBUG)
			 System.out.println(getClass().getName() + 
					 ": Running tool: " + toolName + " on node: " + node.getName()); //$NON-NLS-1$ //$NON-NLS-2$
			ToolDescription toolDescription = getManager().getToolDescription(toolName);
			IAutomaticTool tool = toolDescription.getTool();
			if(tool == null) {
				Util.log(null, "Unknown tool: " + toolName + " for node " + node.getName()); //$NON-NLS-1$ //$NON-NLS-2$
				return;
			}
			try {
				
				FileRunnable runnable = new FileRunnable(tool, file);
				RodinCore.run(runnable, manager.getProgressMonitorForNode(node));
				changed = runnable.targetHasChanged();
				
			} catch (OperationCanceledException e) {
				throw e;
			} catch (CoreException e) {
				issueToolError(node, file, toolDescription, toolName, e);
				return;
			} catch (Throwable e) {
				issueToolError(node, file, toolDescription, toolName, e);
				return;
			}
		}
		
		// we can ignore the rest of this method on cancelation
		// the updated file only becomes committed after node.dated is set to false
		
		node.setDated(false);
		
		if(changed) {
			node.markSuccessorsDated();
			extract(node, new GraphModifier(this, node, manager), manager);
		}
	}

	private void issueToolError(
			Node node, 
			IFile file, 
			ToolDescription toolDescription, 
			String toolName, 
			Throwable e) {
		Util.log(e, " while running tool " + toolName + " on " + file.getName()); //$NON-NLS-1$
		MarkerHelper.deleteAllProblemMarkers(file);
		MarkerHelper.addMarker(
				file, 
				false,
				Messages.build_ToolError, 
				toolDescription.getName()
		);
		node.setDated(false); // do not run defect tools unnecessarily often
		node.setPhantom(true);
	}
	
	private void issueExtractionError(
			Node node, 
			IFile file, 
			ExtractorDescription extractorDescription, 
			Exception e) {
		Util.log(e, " while extracting from " + file.getName()); //$NON-NLS-1$
		MarkerHelper.deleteAllProblemMarkers(file);
		MarkerHelper.addMarker(
				file, 
				false,
				Messages.build_ExtractorError,
				extractorDescription.getName()
		);
		node.setPhantom(true);
	}
	
	private void extract(Node node, GraphModifier handler, ProgressManager manager) {
		ExtractorDescription[] descriptions = 
			getManager().getExtractorDescriptions(node.getFileElementType());
		if(descriptions == null)
			return;
		for(int j = 0; j < descriptions.length; j++) {
			IFile file = node.getFile();
			try {
				descriptions[j].getExtractor().extract(
						file, 
						new GraphTransaction(handler), 
						manager.getZeroProgressMonitor());
			} catch (Exception e) {
				issueExtractionError(node, file, descriptions[j], e);
			}
		}
	}

	public void setPreferredNode(Node pNode) {
		
		if (pNode == null || pNode.isPreferred())
			return;
		
		resetNodeLists();
		
		for (Node node : nodePostList)
			node.setPreferred(false);
		
		pNode.markReachablePredecessorsPreferred();
		
		LinkedList<Node> nodeTempList = new LinkedList<Node>();
		
		for (Node node : nodePostList)
			if (node.isPreferred())
				nodePreList.add(node);
			else
				nodeTempList.add(node);
		
		nodePostList.clear();
		nodePostList.addAll(nodeTempList);
	}
	
	private void removeNode(Node node) {
		node.unlinkNode();
		nodes.remove(node.getName());
	}
	
	/**
	 * This is an optimized version for node removal during sorting without cache invalidation
	 * @param node The node to be removed
	 */
	protected void tryRemoveNode(Node node) {
		if (node.getSuccessorCount() > 0) {
			node.setDated(false);
			node.setPhantom(true);
		} else {
			removeNode(node);
			if (node.done)
				nodePreList.remove(node);
			else
				nodePostList.remove(node);
			nodeCache.remove(node.getPath());
		}
	}
	
	private void removePhantoms() {
		Collection<Node> values = new ArrayList<Node>(nodes.values());
		for(Node node : values) {
			if(node.isPhantom() && node.getSuccessorCount() == 0)
				removeNode(node);
		}
	}
	
	private void cleanNode(Node node, IProgressMonitor monitor) throws CoreException {
		node.setDated(true);
		if(node.isNotDerived())
			return;
		IAutomaticTool tool = getManager().getToolDescription(node.getToolId()).getTool(); 
		if (tool != null)
			tool.clean(node.getFile(), monitor);
	}
	
	private void removeNode(Node node, Node origin, IProgressMonitor monitor) throws CoreException {
		node.setDated(true);
		if(node.isNotDerived())
			return;
		IAutomaticTool tool = getManager().getToolDescription(node.getToolId()).getTool(); 
		if (tool != null)
			tool.remove(node.getFile(), origin.getFile(), monitor);
	}
	
	private void topSortInit() {
		// create topological order for graph of resources
		
		// initialize
		nodeStack = new Stack<Node>();
		
		resetNodeLists();
		
		for(Node node : nodePostList) {
			node.initForSort();
		}
		
		instable = false;
	}

	private void resetNodeLists() {
		
		if (nodePreList.isEmpty())
			return;
		
		nodePreList.addAll(nodePostList);
		nodePostList = nodePreList;
		nodePreList = new LinkedList<Node>();
	}

	private void topSortNodes(LinkedList<Node> sorted, boolean toolLinks, ProgressManager manager) throws CoreException {
		// sort all undone nodes in nodes append to sorted
		// tools are only run if run is true
		// if toolLinks is false only user links are considered (for cycle analysis)
		while(!instable) {
			if(manager != null && manager.isCanceled())
				throw new OperationCanceledException();
			if(nodeStack.isEmpty()) {
				Node firstNode = null;
				for(Node node : nodePostList)
					if(node.count == 0) {
						firstNode = node;
						break;
					}
				if(firstNode == null)
					break;
				topSortStep(sorted, firstNode, manager);
			} else {
				Node node = nodeStack.peek();
				Node succNode = node.getCurrentSuccessorNode();	
				Link succLink = node.getCurrentSuccessorLink();
				node.advanceSuccessorPos();
				if(succNode != null) {
					if(toolLinks || succLink.prov == Link.Provider.USER) {
						succNode.count--;
						if(succNode.count == 0) {
							topSortStep(sorted, succNode, manager);
						}
					}
				} else {
					nodeStack.pop();
				}
			}
		}
	}

	private void topSortStep(LinkedList<Node> sorted, Node node, ProgressManager manager) {
		nodePostList.remove(node);
		nodeStack.push(node);
		sorted.add(node);
		node.done = true;
		if(manager != null) {
			manager.decreaseSliceAdjustment(this, node);
			if(node.isDated())
				runTool(node, manager);
		}
	}
	
	private void commit() throws CoreException {
		// the purpose of this method is analyze the cycles more closely
		// in order to avoid faulty error messages. In particular, when
		// an error message is shown to the user, the user should be responsible
		// for the error and not some plug-in that has introduced a cyclic
		// dependency.
		
		if(Graph.DEBUG)
			System.out.print(getClass().getName() + ": Checking:"); //$NON-NLS-1$
		
		// first we modify the graph to find out more about the cause of the cycle
		for(Node node : nodePreList) {
				node.setCycle(false);
				if(Graph.DEBUG)
					System.out.print(" " + node.getName()); //$NON-NLS-1$
		}
		
		if(Graph.DEBUG)
			System.out.println();

		for(Node node : nodePostList) { // node could not be ordered (cycle!)
				// !node.done is equivalent to node.count > 0 at this point
				
				// remove all tool edges from the graph (virtually!)
				node.removeSuccessorToolCount();
				
				// the must must be updated as soon as the cycle disappears!
				node.setDated(true);
				node.setCycle(true);
				
				//	delete markers of all resources that could not be ordered
				IFile file = node.getFile();
				if(file != null)
					MarkerHelper.deleteAllProblemMarkers(file);
				else if(Graph.DEBUG)
					System.out.println(getClass().getName() + ": File not found: " + node.getPath().toString()); //$NON-NLS-1$

		}

		if(nodePostList.size() == 0) // N == 0 means: no cycles!
			return;
		
		// all nodes that can now be sorted into the list spurious were in a cycle
		// that depended on a tool (so it must be a faulty tool and not the user)
		// if spurious is empty everything is ok from the point of view of the tools:
		// it's the user's fault!
		LinkedList<Node> spurious = new LinkedList<Node>();
		topSortNodes(spurious, false, null);
		
		// print to error console all spurious errors
		if(spurious.size() > 0)
			spuriousErrors(spurious);
		
		// attach messages to resources for user errors
		userErrors();
		
		nodePostList.addAll(spurious);
	}
	
	private void spuriousErrors(LinkedList<Node> spurious) {
		String message = new String("Spurious dependency cycle"); //$NON-NLS-1$
		String cycle = new String("Cycle:\n"); //$NON-NLS-1$
		for(Node node : spurious) {
			cycle += "\nName: " + node.getPath().toOSString() + " Tool: " + node.getToolId(); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		// N == 0 means there is no cycle left. So it's a bug in a plug-in.
		if (nodePostList.size() == 0)
			Util.log(null, message + "\n" + cycle); //$NON-NLS-1$
	}
	
	private void userErrors() {
		// if simple is true we don't do reachability analysis
		// this improves performance from quadratic to linear
		// in the number of nodes connected to a cycle
		for(Node node : nodePostList) {
				node.addOriginToCycle();
		}
	}

	/**
	 * Mark the graph instable. This causes the topological sort to be repeated 
	 * (and the tools to be re-run)
	 */
	protected void setInstable() {
		this.instable = true;
	}
	
	protected boolean isInstable() {
		return instable;
	}

	public Iterator<Node> iterator() {
		return nodes.values().iterator();
	}
	
	public int size() {
		return nodes.size();
	}

}
