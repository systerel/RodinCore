/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.internal.core.builder;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.rodinp.internal.core.util.Messages;

/**
 * @author Stefan Hallerstede
 *
 */
public class ProgressManager {

	private final IProgressMonitor monitor;
	
	private final static int MAX_EFFORT = 1000;
	
	private int remainingEffort;
	
	private int cnt;
	private List<Integer> cntL;
	
	private HashSet<String> nodes;
	private int expected;
	
	public ProgressManager(IProgressMonitor monitor, IncrementalProjectBuilder builder) {
		this.monitor = new BuilderProgressMonitor(monitor, builder);
		monitor.beginTask(
				Messages.bind(Messages.build_building, builder.getProject().getName()), 
				MAX_EFFORT);
		remainingEffort = MAX_EFFORT;
		nodes = null;
		expected = 0;
		cnt = 0;
		cntL = new LinkedList<Integer>();
	}
	
	public void anticipateSlice(Node node) {
		if (node.isDated()) {
			nodes.add(node.getName());
		}
	}
	
	public void decreaseSliceAdjustment(Graph graph, Node node) {
		if (expected > 0)
			expected--;
	}
	
	public void anticipateSlices(Graph graph) {
		nodes = new HashSet<String>(graph.size() * 4 / 3 + 1);
		expected = 0;
		for (Node node : graph) {
			if (node.getToolId() == null || node.getToolId() == "")
				expected++;
			else
				anticipateSlice(node);
		}
	}
	
	IProgressMonitor getZeroProgressMonitor() {
		return new SubProgressMonitor(monitor, 0);
	}
	
	IProgressMonitor getProgressMonitor(int percent) {
		int slice = MAX_EFFORT * (percent / 100);
		remainingEffort = slice > remainingEffort ? 0 : remainingEffort - slice;
		return new SubProgressMonitor(monitor, slice);
	}
	
	IProgressMonitor getProgressMonitorForNode(Node node) {
		int nodeCount = nodes == null ? 0 : nodes.size();
		int nodeEffort = remainingEffort / (nodeCount + expected);
		remainingEffort = remainingEffort - nodeEffort;
		if (nodeCount > 0)
			nodes.remove(node.getName());
		cnt += nodeEffort;
		cntL.add(nodeEffort);
		return new SubProgressMonitor(monitor, nodeEffort);
	}
	
	void subTask(String name) {
		monitor.subTask(name);
	}
	
	public boolean isCanceled() {
		return monitor.isCanceled();
	}
	
	public void done() {
		monitor.done();
	}
	
}
