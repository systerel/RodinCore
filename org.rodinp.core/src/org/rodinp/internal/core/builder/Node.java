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

	private static final long serialVersionUID = -4755682487326821513L;
	private String name; // name of the resource (full name in workspace!)
	private LinkedList<Link> predessorLinks; // the predecessor list
	private String toolId; // toolId to be run to produce the resource of this node
	private boolean dated; // true if the resource of this node needs to be (re-)created
	private boolean phantom; // a node that was created by a dependency requirement
	private boolean cycle; // node is on a cycle
	
	// temporary data for construction of topological order
	private ArrayList<Node> successorNodes; // successors of this node (for topological sort)
	private ArrayList<Link> successorLinks; // successors of this node (for topological sort)
	private HashMap<String, Node> successorTargets; // the set of names of the successors
	
	transient private int successorPos; // Position in successor lists during graph traversal	
	transient protected int count; // number of predecessors of this node remaining in the unprocessed top sort
	transient protected boolean done; // nodes with count zero and done are already in the ordered list
	
	transient private IPath path; // the path corresponding to name (cache)
	transient private IFileElementType fileElementType; // the element type of the resource (cache)
	transient private IFile file; // the file corresponding to name (cache)
	
	public Node() {
		name = null;
		toolId = null;
		dated = true;
		done = false;
		predessorLinks = new LinkedList<Link>();
		successorNodes = new ArrayList<Node>(3);
		successorLinks = new ArrayList<Link>(3);
		successorTargets = new HashMap<String, Node>(3);
	}
	
	@Override
	public String toString() {
		return printNode();
	}
	
	@Override
	public boolean equals(Object o) {
		return name.equals(((Node) o).name);
	}
	
	protected List<Link> getPredessorLinks() {
		return predessorLinks;
	}
	
	protected void addPredecessorLink(Link link) { 
		if(predessorLinks.contains(link))
			return;
		link.source.successorTargets.put(this.name, this);
		predessorLinks.add(link);
		if(link.source.successorPos <= link.source.getSuccessorCount())
			count++;
		
		if(link.prio == Link.Priority.LOW) {
			link.source.successorNodes.add(this);
			link.source.successorLinks.add(link);
		} else {
			link.source.successorNodes.add(0, this);
			link.source.successorLinks.add(0, link);
		}
	}

	protected void addPredecessorLink(Node origin, Node source, String id, Link.Provider prov, Link.Priority prio) { 
		Link link = new Link(prov, prio, id, source, origin);
		addPredecessorLink(link);
	}
	
	protected void removeAllLinks(String id) {
		LinkedList<Link> predCopy = new LinkedList<Link>(predessorLinks);
		for(Link link : predCopy) {
			if(link.id.equals(id)) {
				link.source.successorTargets.remove(this.name);
				predessorLinks.remove(link);
				count--;
				
				link.source.successorNodes.remove(this);
				link.source.successorLinks.remove(link);
			}
		}
	}
	
	protected Collection<IPath> getSources(String id) {
		ArrayList<IPath> sources = new  ArrayList<IPath>(predessorLinks.size());
		for(Link link : predessorLinks) {
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
	}
	
	protected int getPredecessorCount() {
		return predessorLinks.size();
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
	// the successors of the node must be recreated
	protected void markSuccessorsDated() {
		for(Node suc : successorNodes) {
			suc.setDated(true);
		}
	}

	protected boolean hasSuccessor(Node node) {
		return successorNodes.contains(node);
	}
	
	protected void advanceSuccessorPos() {
		successorPos++;
	}
	
	protected int getSuccessorPos() {
		return successorPos;
	}

	protected Node getCurrentSuccessorNode() {
		return (successorPos < successorNodes.size()) ? successorNodes.get(successorPos) : null;
	}

	protected Link getCurrentSuccessorLink() {
		return (successorPos < successorLinks.size()) ? successorLinks.get(successorPos) : null;
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
		count = getPredecessorCount();
		done = false;
		successorPos = 0;
	}
	
	protected int getSuccessorCount() {
		return successorNodes.size();
	}
	
	protected String printNode() {
		String res = name  + "[";
		res += isDated() ? "D" : "N";
		res += isPhantom() ? "-P" : "-N";
		res += "] :";
		for(Node node : successorNodes) {
			res = res + " " + node.name;
		}
		return res;
	}
	
	protected void unlinkNode() {
		for(Link link : predessorLinks) {
			link.source.successorTargets.remove(this.name);
			
			link.source.successorNodes.remove(this);
			link.source.successorLinks.remove(link);
		}
		int size = successorNodes.size();
		for(int pos = 0; pos < size; pos++) {
			Node node = successorNodes.get(pos);
			node.dated = true;
			node.predessorLinks.remove(successorLinks.get(pos));
			node.count--;
		}
	}
	
	protected void removeSuccessorToolCount() {
		for(int pos = 0; pos < successorNodes.size(); pos++) {
			if(successorLinks.get(pos).prov == Link.Provider.TOOL) {
				successorNodes.get(pos).count--;
			}
		}
	}
	
	protected void markReachable() {
		if(!done)
			return;
		done = false;
		for(Node node : successorNodes)
			node.markReachable();
	}
	
	protected void addOriginToCycle() {
		for(Link link : predessorLinks) {
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
		for(Link link : predessorLinks) {
			if(link.source.isPhantom())
				return true;
		}
		return false;
	}
	
	protected void printPhantomProblem() {
		for(Link link : predessorLinks) {
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
		
		if (fileElementType == null && getFile() != null) {
			final ElementTypeManager manager = ElementTypeManager.getInstance();

			this.fileElementType = manager.getFileElementType(getFile());
		}

		return fileElementType;
	}

}
