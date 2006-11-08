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
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.rodinp.core.IFileElementType;
import org.rodinp.internal.core.ElementTypeManager;
import org.rodinp.internal.core.util.Messages;

/**
 * @author Stefan Hallerstede
 *
 */
public class Node implements Serializable {

	private static final long serialVersionUID = 2715764862822077579L;
	private String name; // name of the resource (full name in workspace!)
	private IFileElementType fileElementType; // the extension of the resource
	private LinkedList<Link> pred; // the predecessor list
	private String toolId; // toolId to be run to produce the resource of this node
	private boolean dated; // true if the resource of this node needs to be (re-)created
	private boolean phantom; // a node that was created by a dependency requirement
	private boolean cycle; // node is on a cycle
	
	// temporary data for construction of topological order
	private int totalCount; // number of predecessors of this node (for topological sort)
	private ArrayList<Node> succNodes; // successors of this node (for topological sort)
	private ArrayList<Link> succLinks; // successors of this node (for topological sort)
	private HashMap<String, Node> targets; // the set of names of the successors
	
	transient private int succPos; // Position in succ* during graph traversal
	
	transient protected int count; // number of predecessors of this node remaining in the unprocessed top sort
	transient protected boolean done; // nodes with count zero and done are already in the ordered list
	
	transient private IPath path; // the path corresponding to name (cache)
	
	/**
	 * cache for the file resource
	 */
	transient private IFile file;
	
	public Node() {
		name = null;
		fileElementType = null;
		pred = new LinkedList<Link>();
		toolId = null;
		dated = true;
		totalCount = 0;
		succNodes = new ArrayList<Node>(3);
		succLinks = new ArrayList<Link>(3);
		targets = new HashMap<String, Node>(3);
		done = false;
	}
	
	@Override
	public String toString() {
		return printNode();
	}
	
	@Override
	public boolean equals(Object o) {
		return name.equals(((Node) o).name);
	}
	
	protected List<Link> getLinks() {
		return pred;
	}
	
	protected void addLink(Link link) { 
		if(pred.contains(link))
			return;
		link.source.targets.put(this.name, this);
		pred.add(link);
		totalCount++;
		if(link.source.succPos <= link.source.succSize())
			count++;
		
		if(link.prio == Link.Priority.LOW) {
			link.source.succNodes.add(this);
			link.source.succLinks.add(link);
		} else {
			link.source.succNodes.add(0, this);
			link.source.succLinks.add(0, link);
		}
	}

	protected void addLink(Node origin, Node source, String id, Link.Provider prov, Link.Priority prio) { 
		Link link = new Link(prov, prio, id, source, origin);
		addLink(link);
	}
	
	protected void removeLinks(String id) {
		LinkedList<Link> predCopy = new LinkedList<Link>(pred);
		for(Link link : predCopy) {
			if(link.id.equals(id)) {
				link.source.targets.remove(this.name);
				pred.remove(link);
				totalCount--;
				count--;
				
				link.source.succNodes.remove(this);
				link.source.succLinks.remove(link);
			}
		}
	}
	
	protected Collection<IPath> getSources(String id) {
		ArrayList<IPath> sources = new  ArrayList<IPath>(pred.size());
		for(Link link : pred) {
			if(link.id.equals(id))
				sources.add(link.source.getPath());
		}
		return sources;
	}
	
	protected IPath getPath() {
		if(path == null)
			path = new Path(name);
		return path;
	}

	protected String getName() {
		return name;
	}

	protected void setPath(IPath path) {
		this.path = path;
		this.name = path.toString();
		
		// TODO fix code below.
		final ElementTypeManager manager = ElementTypeManager.getInstance();
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		this.file = workspace.getRoot().getFile(path);
		if (file != null)
			this.fileElementType = manager.getFileElementType(file);
		else
			this.fileElementType = null;
	}
	
	protected int getInCount() {
		return totalCount;
	}
	
	protected boolean isDerived() {
		return toolId != null && !toolId.equals("");
	}

	protected boolean isNotDerived() {
		return toolId == null || toolId.equals("");
	}

	protected void setToolId(String tool) {
		this.toolId = tool;
	}
	
	protected String getToolId() {
		return toolId;
	}
	
	// after removal of a node from the graph
	// the successors of the node must be recreated;
	// this requires also recreating the origin of the
	// outgoing edges of the node! 
	protected void markSuccessorsDated() {
		for(Node suc : succNodes) {
			suc.setDated(true);
//			suc.markOriginDated(this);
		}
	}
	
//	private void markOriginDated(Node node) {
//		for(Link link : pred) {
//			if(link.source == node && link.origin != null)
//				link.origin.setDated(true);
//		}
//	}
	
	protected boolean hasSuccessor(Node node) {
		return succNodes.contains(node);
	}
	
	protected void nextSucc() {
		succPos++;
	}
	
	protected int getSuccPos() {
		return succPos;
	}

	protected Node succNode() {
		return (succPos < succNodes.size()) ? succNodes.get(succPos) : null;
	}

	protected Link succLink() {
		return (succPos < succLinks.size()) ? succLinks.get(succPos) : null;
	}

	protected IFile getFile() {
		if (file != null)
			return file;
		file = ResourcesPlugin.getWorkspace().getRoot().getFile(getPath());
		return file;
	}
	
	protected void setDated(boolean value) {
		dated = value;
	}
	
	protected boolean isDated() {
		return dated;
	}
	
	protected void initForSort() {
		count = totalCount;
		done = false;
		succPos = 0;
	}
	
	protected int succSize() {
		return succNodes.size();
	}
	
	protected String printNode() {
		String res = name  + "[";
		res += isDated() ? "D" : "N";
		res += isPhantom() ? "-P" : "-N";
		res += "] :";
		for(Node node : succNodes) {
			res = res + " " + node.name;
		}
		return res;
	}
	
	protected void unlink() {
		for(Link link : pred) {
			link.source.targets.remove(this.name);
			
			link.source.succNodes.remove(this);
			link.source.succLinks.remove(link);
		}
		int size = succNodes.size();
		for(int pos = 0; pos < size; pos++) {
			Node node = succNodes.get(pos);
			node.dated = true;
			node.pred.remove(succLinks.get(pos));
			node.totalCount--;
			node.count--;
		}
	}
	
	protected void removeSuccessorToolCount() {
		for(int pos = 0; pos < succNodes.size(); pos++) {
			if(succLinks.get(pos).prov == Link.Provider.TOOL) {
				succNodes.get(pos).count--;
			}
		}
	}
	
	protected void markReachable() {
		if(!done)
			return;
		done = false;
		for(Node node : succNodes)
			node.markReachable();
	}
	
	protected void addOriginToCycle() {
		for(Link link : pred) {
			if(link.source.count > 0) {
				IFile originFile = link.origin.getFile();
				link.origin.dated = true;
				if(originFile != null)
					MarkerHelper.addMarker(
							originFile,
							true,
							Messages.build_resourceInCycle
					);
				else if(Graph.DEBUG)
					System.out.println(getClass().getName() + ": File not found: " + link.origin.getName()); //$NON-NLS-1$
			}
		}
	}
	
	protected boolean dependsOnPhantom() {
		for(Link link : pred) {
			if(link.source.isPhantom())
				return true;
		}
		return false;
	}
	
	protected void printPhantomProblem() {
		for(Link link : pred) {
			if(link.source.isPhantom())
				if(link.source.toolId == null || link.source.toolId.equals("")) {
					IFile originFile = link.origin.getFile();
					if(originFile != null)
						MarkerHelper.addMarker(
								originFile, 
								false,
								Messages.build_resourceDoesNotExist,
								link.source.getName()
						);
				}
		}
	}
	
	/**
	 * @return Returns the phantom.
	 */
	protected boolean isPhantom() {
		return phantom;
	}

	/**
	 * @param phantom The phantom to set.
	 */
	protected void setPhantom(boolean phantom) {
		this.phantom = phantom;
	}

	/**
	 * @return Returns the cycle.
	 */
	protected boolean isCycle() {
		return cycle;
	}

	/**
	 * @param cycle The cycle to set.
	 */
	protected void setCycle(boolean cycle) {
		this.cycle = cycle;
	}

	/**
	 * @return Returns the fileElementType.
	 */
	public IFileElementType getFileElementType() {
		return fileElementType;
	}

}
