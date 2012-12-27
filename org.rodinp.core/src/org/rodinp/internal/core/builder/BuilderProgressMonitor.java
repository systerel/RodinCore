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

import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author halstefa
 *
 */
public class BuilderProgressMonitor implements IProgressMonitor {

	private final IProgressMonitor monitor;
	private final IncrementalProjectBuilder builder;
	
	public BuilderProgressMonitor(IProgressMonitor monitor, IncrementalProjectBuilder builder) {
		this.monitor = monitor;
		this.builder = builder;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IProgressMonitor#beginTask(java.lang.String, int)
	 */
	@Override
	public void beginTask(String name, int totalWork) {
		monitor.beginTask(name, totalWork);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IProgressMonitor#done()
	 */
	@Override
	public void done() {
		monitor.done();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IProgressMonitor#internalWorked(double)
	 */
	@Override
	public void internalWorked(double work) {
		monitor.internalWorked(work);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IProgressMonitor#isCanceled()
	 */
	@Override
	public boolean isCanceled() {
		return monitor.isCanceled() || builder.isInterrupted();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IProgressMonitor#setCanceled(boolean)
	 */
	@Override
	public void setCanceled(boolean value) {
		monitor.setCanceled(value);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IProgressMonitor#setTaskName(java.lang.String)
	 */
	@Override
	public void setTaskName(String name) {
		monitor.setTaskName(name);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IProgressMonitor#subTask(java.lang.String)
	 */
	@Override
	public void subTask(String name) {
		monitor.subTask(name);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IProgressMonitor#worked(int)
	 */
	@Override
	public void worked(int work) {
		monitor.worked(work);
	}

}
