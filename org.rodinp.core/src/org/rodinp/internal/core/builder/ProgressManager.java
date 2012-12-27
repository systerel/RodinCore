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
	
	private int slices;
	
	private List<Integer> sList;
	
	public ProgressManager(IProgressMonitor monitor, IncrementalProjectBuilder builder) {
		this.monitor = new BuilderProgressMonitor(monitor, builder);
		monitor.beginTask(
				Messages.bind(Messages.build_building, builder.getProject().getName()), 
				MAX_EFFORT);
		remainingEffort = MAX_EFFORT;
		sList = new LinkedList<Integer>();
	}
	
	public void makeSlices(Graph graph) {
		slices = 0;
		for (Node node : graph) {
			if (!node.isDerived())
				slices++;
		}
		slices *= 1;
		
		if (graph.size() > slices)
			slices = graph.size();
	}
	
	IProgressMonitor getZeroProgressMonitor() {
		return new SubProgressMonitor(monitor, 0);
	}
	
	IProgressMonitor getSliceProgressMonitor() {
		if (slices > 0) {
			int newSlice = remainingEffort / slices--;
			remainingEffort -= newSlice;
			sList.add(remainingEffort);
			return new SubProgressMonitor(monitor, newSlice);
		} else
			return getZeroProgressMonitor();
	}
	
	IProgressMonitor getStepProgressMonitor() {
		if (remainingEffort > 0) {
			remainingEffort--;
			sList.add(remainingEffort);
			return new SubProgressMonitor(monitor, 1);
		} else
			return getZeroProgressMonitor();
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
