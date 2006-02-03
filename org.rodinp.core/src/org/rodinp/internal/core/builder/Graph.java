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
import java.util.LinkedList;
import java.util.Stack;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.rodinp.core.RodinCore;
import org.rodinp.core.builder.IExtractor;
import org.rodinp.core.builder.IInterrupt;
import org.rodinp.core.builder.IAutomaticTool;
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
public class Graph implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7032499313483369519L;
	private HashMap<String,Node> nodes;
	private Node active; // begin list with this node if it is a root node otherwise ignore
	
	transient private HashMap<IPath, Node> nodeCache;
	transient private LinkedList<Node> nodePreList; // contains the sorted nodes
	transient private LinkedList<Node> nodePostList; // contains the unsorted nodes
	
	transient private Stack<Node> nodeStack;
	
	transient private boolean instable; // true if the top order must be changed during build
	
	transient private ToolManager manager; // = GraphManager.getGraphManager();
	
	transient private int N;
	
	public void incN() {
		N++;
	}
	
	public void decN() {
		N--;
	}
	
//	transient private IInterrupt progress;
//	transient private IProgressMonitor monitor;
	
	public Graph() {
		nodes = new HashMap<String,Node>(11);
		active = null;
		
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
		if(manager == null)
		  	manager = ToolManager.getToolManager();
		return manager;
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
		
	private void runTool(Node node, IInterrupt interrupt, IProgressMonitor monitor) {
		if(node.isPhantom() || node.dependsOnPhantom())
			return;
		String toolName = node.getProducerId();
		IFile file = node.getFile();
		if (file == null) {// resource is not a file
			Util.log(null, "Builder resource not a file" + file.getName()); //$NON-NLS-1$
			if (RodinBuilder.DEBUG)
				System.out.println(getClass().getName() + ": Builder resource not a file!"); //$NON-NLS-1$
			return;
		}
		if (toolName == null || toolName.equals("")) {
			if(RodinBuilder.DEBUG)
				System.out.println(getClass().getName() + ": Root node changed: " + node.getName());
			node.setDated(false);

			RodinBuilder.deleteMarkers(file);
			node.markSuccessorsDated();
			try {
				extract(node, new GraphHandler(this, node), null, null);
			} catch (CoreException e){
				Util.log(e, "while extracting from " + file.getFullPath()); //$NON-NLS-1$
			}
			return; // no associated tool
		} else if(RodinBuilder.DEBUG)
			System.out.println(getClass().getName() + ": Running tool: " + toolName + " on node: " + node.getName()); //$NON-NLS-1$ //$NON-NLS-2$
		IAutomaticTool tool = getManager().getTool(toolName);
		if(tool == null) {
			Util.log(null, "Unknown tool: " + toolName + " for node " + node.getName()); //$NON-NLS-1$ //$NON-NLS-2$
			return;
		}
		RodinBuilder.deleteMarkers(file);
		boolean changed = false;
		try {
			changed = tool.run(file, interrupt, monitor);
		} catch (OperationCanceledException e) {
			throw e;
		} catch (CoreException e) {
			Util.log(e, "while running producer" + file.getName()); //$NON-NLS-1$
			return;
		} catch (RuntimeException e) {
			Util.log(e, "while running producer" + file.getName()); //$NON-NLS-1$
			return;
		}
		
		// check if not interruped before accepting the output
		if(interrupt.isInterrupted())
			return;
		// we can ignore the rest of this method on interrupt
		// the updated file only becomes committed after node.dated is set to false
		
		node.setDated(false);
		
		if(changed) {
			node.markSuccessorsDated();
			try {
				extract(node, new GraphHandler(this, node), interrupt, monitor);
			} catch (CoreException e) {
				Util.log(e, "while extracting dependencies"); //$NON-NLS-1$
			}
		}
	}
	
	private void extract(Node node, GraphHandler handler, IInterrupt interrupt, IProgressMonitor monitor) throws CoreException {
		IExtractor[] extractor = getManager().getExtractors(node.getFileElementTypeId());
		if(extractor == null)
			return;
		for(int j = 0; j < extractor.length; j++)
			extractor[j].extract(node.getFile(), handler);
	}

//	private void extract(Node node, IInterrupt progress, IProgressMonitor monitor) throws CoreException {
//		node.markSuccessorsDated();
//		IFile file = node.getFile();
//		extract(file, new GraphHandler(this, node), progress, monitor);
//	}
//
	public void activate(String name) {
		// make the node called name preferred
		Node a = nodes.get(name);
		if(a != active) {
			active = a;
		}
	}
	
	private void removeNode(Node node) {
		node.unlink();
		nodes.remove(node.getName());
	}
	
	/**
	 * This is an optimized version for node removal during sorting without cache invalidation
	 * @param node The node to be removed
	 */
	protected void tryRemoveNode(Node node) {
		if (node.succSize() > 0) {
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
	
	public void removePhantoms() {
		Collection<Node> values = new ArrayList<Node>(nodes.values());
		for(Node node : values) {
			if(node.isPhantom() && node.succSize() == 0)
				removeNode(node);
		}
	}
	
	public void addNodeToGraph(Node node) {
		nodes.put(node.getName(),node);
		nodePostList.add(node);
	}
	
	public void removeNodeFromGraph(Node node, IInterrupt interrupt, IProgressMonitor monitor) {
		Collection<Node> values = new ArrayList<Node>(nodes.values());
		for(Node n : values)
			n.done = true;
		node.markReachable();
		for(Node n : values) {
			if(!n.done) {
				removeNode(n);
				try {
					cleanNode(n, interrupt, monitor);
				} catch(CoreException e) {
					if(RodinBuilder.DEBUG)
						System.out.println(getClass().getName() + ": Error during remove&clean"); //$NON-NLS-1$
				}
			}
		}
		initCaches();
	}
	
	private void cleanNode(Node node, IInterrupt interrupt, IProgressMonitor monitor) throws CoreException {
		node.setDated(true);
		if(node.isNotDerived())
			return;
		IAutomaticTool tool = getManager().getTool(node.getProducerId());
		tool.clean(node.getFile(), interrupt, monitor);
	}
	
	public void cleanGraph(IInterrupt interrupt, IProgressMonitor monitor) throws CoreException {
		ArrayList<IStatus> vStats = null; // lazy initialized
		Collection<Node> values = new ArrayList<Node>(nodes.values());
		for(Node node : values) {
			try {
				cleanNode(node, interrupt, monitor);
				if(node.isDerived()) //$NON-NLS-1$
					tryRemoveNode(node);
				
			} catch(CoreException e) {
				if (vStats == null)
					vStats= new ArrayList<IStatus>();
				vStats.add(e.getStatus());
			}
		}
//		for(Node node : values) {
//			String toolName = node.getProducerId();
//			if(toolName != null && !toolName.equals("")) //$NON-NLS-1$
//				removeNode(node);
//		}
		initCaches();
		if (vStats != null) {
			IStatus[] stats= new IStatus[vStats.size()];
			vStats.toArray(stats);
			throw new CoreException(new MultiStatus(RodinCore.BUILDER_ID, IStatus.ERROR, stats, "Error while cleaning", null)); //$NON-NLS-1$
		}
	}
	
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
	
	private void topSortInit() {
		// create topological order for graph of resources
		
		// initialize
		nodeStack = new Stack<Node>();
//		succStack = new Stack<Position>();
		
		nodePreList.addAll(nodePostList);
		nodePostList = nodePreList;
		nodePreList = new LinkedList<Node>();
		
		for(Node node : nodePostList) {
			node.initForSort();
		}
		N = nodes.size();
		
		// only root nodes can be preferred
		if(active != null && active.count == 0 && nodePostList.getFirst() != active) {
			nodePostList.remove(active);
			nodePostList.add(0, active);
		}
		
		instable = false;
	}

	/**
	 * This method implements the incremental build for Rodin projects.
	 * The building process terminates when all tools (@see IAutomaticTool) have run
	 * and the the graph is stable. As dependencies are added, changed, or removed during the build,
	 * it can happen that the build would have been started with wrong dependencies, hence,
	 * the topological order would be invalid for the Rodin project. In this case
	 * the build is restarted, recreating all derived resources that may have been invalidated.
	 * @param interrupt
	 * 		The interrupt request
	 * @param monitor
	 * 		The progress monitor to use
	 * @throws CoreException
	 * 		If any problem occurred during build.
	 */
	public void buildGraph(IInterrupt interrupt, IProgressMonitor monitor) throws CoreException {
		if(RodinBuilder.DEBUG)
			System.out.print(getClass().getName() + ": IN Graph:\n" + printGraph()); //$NON-NLS-1$
//		this.progress = progress;
//		this.monitor = monitor;
		instable = true;
		while(instable) {
			topSortInit();
			topSortNodes(nodePreList, true, true, interrupt, monitor);
			if(RodinBuilder.DEBUG)
				System.out.print(getClass().getName() + ": OUT Graph:\n" + printGraph()); //$NON-NLS-1$
			if(RodinBuilder.DEBUG)
				System.out.println(getClass().getName() + ": Build Order: " + nodePreList.toString()); //$NON-NLS-1$
			if(instable) {
				if(RodinBuilder.DEBUG)
					System.out.println(getClass().getName() + ": Graph structure may have changed. Reordering ..."); //$NON-NLS-1$
				continue;
			}
			commit(interrupt, monitor);
		}
		removePhantoms();
	}
	
	private void topSortNodes(LinkedList<Node> sorted, boolean run, boolean toolLinks, IInterrupt interrupt, IProgressMonitor monitor) throws CoreException {
		// sort all undone nodes in nodes append to sorted
		// tools are only run if run is true
		// if toolLinks is false only user links are considered (for cycle analysis)
		while(!instable) {
			if(interrupt.isInterrupted())
				return;
			if(monitor.isCanceled())
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
				nodePostList.remove(firstNode);
				nodeStack.push(firstNode);
//				firstNode.succPos = 0;
//				succStack.push(new Position());
				sorted.add(firstNode);
				N--;
				firstNode.done = true;
				if(run)
					if(firstNode.isDated())
						runTool(firstNode, interrupt, monitor);
//				else if(node.isCycle())
//				RodinBuilderX.deleteMarkers(node.getFile());
			} else {
				Node node = nodeStack.peek();
				Node succNode = node.succNode();	
				Link succLink = node.succLink();
				node.nextSucc();
				if(succNode != null) {
					if(toolLinks || succLink.prov == Link.Provider.USER) {
						succNode.count--;
						if(succNode.count == 0) {
							nodeStack.push(succNode);
//							succNode.succPos = 0;
//							succStack.push(new Position());
							nodePostList.remove(succNode);
							sorted.add(succNode);
							N--;
							succNode.done = true;
							if(run)
								if(succNode.isDated())
									runTool(succNode, interrupt, monitor);
//							else if(succNode.isCycle())
//							RodinBuilderX.deleteMarkers(succNode.getFile());
						}
					}
				} else {
					nodeStack.pop();
//					succStack.pop();
				}
			}
		}
	}
	
	private void commit(IInterrupt interrupt, IProgressMonitor monitor) throws CoreException {
		// the purpose of this method is analyze the cycles more closely
		// in order to avoid faulty error messages. In particular, when
		// an error message is shown to the user, the user should be responsible
		// for the error and not some plug-in that has introduced a cyclic
		// dependency.
		
		if(RodinBuilder.DEBUG)
			System.out.print(getClass().getName() + ": Committing:"); //$NON-NLS-1$
		
		// first we modify the graph to find out more about the cause of the cycle
		for(Node node : nodePreList) {
//				node.setDated(false);
				node.setCycle(false);
				if(RodinBuilder.DEBUG)
					System.out.print(" " + node.getName()); //$NON-NLS-1$
		}
		
		if(RodinBuilder.DEBUG)
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
					RodinBuilder.deleteMarkers(file);
				else if(RodinBuilder.DEBUG)
					System.out.println(getClass().getName() + ": File not found: " + node.getPath().toString()); //$NON-NLS-1$

		}

		if(N == 0) // N == 0 means: no cycles!
			return;
		
		// all nodes that can now be sorted into the list spurious were in a cycle
		// that depended on a tool (so it must be a faulty tool and not the user)
		// if spurious is empty everything is ok from the point of view of the tools:
		// it's the user's fault!
		LinkedList<Node> spurious = new LinkedList<Node>();
		topSortNodes(spurious, false, false, interrupt, monitor);
		
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
			cycle += "\nName: " + node.getPath().toOSString() + " Tool: " + node.getProducerId(); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		// N == 0 means there is no cycle left. So it's a bug in a plug-in.
		if (N == 0)
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
	
}
