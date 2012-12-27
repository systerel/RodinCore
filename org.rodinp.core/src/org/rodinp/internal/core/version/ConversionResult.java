/*******************************************************************************
 * Copyright (c) 2007, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - refactoring and various improvements
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.rodinp.internal.core.version;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
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
		try {
			final SubMonitor spm = SubMonitor.convert(monitor,
					Messages.converter_convertingFiles, entries.length);
			for (ConversionEntry entry : entries) {
				entry.upgrade(vManager, force, spm.newChild(1));
			}
		} finally {
			if (monitor != null) {
				monitor.done();
			}
		}
	}

	@Override
	public IEntry[] getEntries() {
		return entries.clone();
	}

	@Override
	public IRodinProject getProject() {
		return project;
	}

	@Override
	public void accept(boolean force, boolean keepHistory,
			IProgressMonitor monitor) throws RodinDBException {
		try {
			final SubMonitor spm = SubMonitor.convert(monitor,
					Messages.converter_savingFiles, entries.length);
			for (ConversionEntry entry : entries) {
				entry.accept(force, keepHistory, spm.newChild(1));
			}

		} finally {
			if (monitor != null) {
				monitor.done();
			}
		}
	}

}
