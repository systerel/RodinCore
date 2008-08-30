/*******************************************************************************
 * Copyright (c) 2007, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - refactoring and various improvements
 *******************************************************************************/
package org.rodinp.internal.core.version;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.rodinp.core.IConversionResult;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;
import org.rodinp.internal.core.util.Messages;

/**
 * @author Stefan Hallerstede
 * 
 */
public class ConversionResult implements IConversionResult {

	private final IRodinProject project;
	private final ConversionEntry[] entries;

	public ConversionResult(IRodinProject project) throws RodinDBException {
		this.project = project;
		final IRodinFile[] rodinFiles = project.getRodinFiles();
		final int len = rodinFiles.length;
		entries = new ConversionEntry[len];
		for (int i = 0; i < rodinFiles.length; i++) {
			entries[i] = new ConversionEntry(rodinFiles[i]);
		}
	}

	public void convert(boolean force, IProgressMonitor monitor) {
		final VersionManager vManager = VersionManager.getInstance();
		if (monitor == null)
			monitor = new NullProgressMonitor();

		try {
			monitor.beginTask(Messages.converter_convertingFiles,
					2 * entries.length);
			for (ConversionEntry entry : entries) {
				entry.upgrade(vManager, force, monitor);
			}
		} finally {
			monitor.done();
		}
	}

	public IEntry[] getEntries() {
		return entries.clone();
	}

	public IRodinProject getProject() {
		return project;
	}

	public void accept(boolean force, boolean keepHistory,
			IProgressMonitor monitor) throws RodinDBException {
		if (monitor == null)
			monitor = new NullProgressMonitor();

		try {
			monitor.beginTask(Messages.converter_savingFiles, entries.length);
			for (ConversionEntry entry : entries) {
				entry.accept(force, keepHistory, monitor);
			}

		} finally {
			monitor.done();
		}
	}

}
