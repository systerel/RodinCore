/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.internal.core.version;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.rodinp.core.IConversionResult;
import org.rodinp.core.IFileElementType;
import org.rodinp.core.IRodinDBStatusConstants;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;
import org.rodinp.internal.core.Buffer;
import org.rodinp.internal.core.ElementTypeManager;
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
		IRodinFile[] rodinFiles = project.getRodinFiles();
		entries = new ConversionEntry[rodinFiles.length];
		for (int i = 0; i < rodinFiles.length; i++) {
			entries[i] = new ConversionEntry(rodinFiles[i]);
		}
	}

	public void convert(boolean force, IProgressMonitor monitor) {
		VersionManager vManager = VersionManager.getInstance();
		ElementTypeManager eManager = ElementTypeManager.getInstance();

		if (monitor == null)
			monitor = new NullProgressMonitor();

		try {

			monitor.beginTask(Messages.converter_convertingFiles,
					entries.length);

			for (ConversionEntry entry : entries) {
				try {

					// TODO implement progress monitor
					getFileVersion(entry, null);
					IFileElementType<? extends IRodinFile> type = entry.file
							.getElementType();
					eManager.getFileElementType(entry.file.getResource());
					entry.reqVersion = vManager.getVersion(type);
					if (entry.version == entry.reqVersion) {
						entry.message = Messages.converter_fileUnchanged;
						continue;
					} else if (entry.version > entry.reqVersion) {
						entry.error = new RodinDBException(null,
								IRodinDBStatusConstants.FUTURE_VERSION);
						entry.message = Messages.converter_failedConversion;
						continue;
					} else {
						Converter converter = vManager.getConverter(type);
						byte[] contents = getBytes(entry, force);
						entry.buffer = converter.convert(contents,
								entry.version, entry.reqVersion);
					}
				} catch (Exception e) {
					entry.error = e;
					entry.message = Messages.converter_failedConversion;
				}

				monitor.worked(1);
			}

		} finally {
			monitor.done();
		}
	}

	private byte[] getBytes(ConversionEntry entry, boolean force)
			throws IOException, CoreException {
		InputStream s = entry.file.getResource().getContents(force);
		int size = s.available();
		byte[] contents = new byte[size];
		int read = s.read(contents);
		s.close();
		assert read == size;
		return contents;
	}

	private void getFileVersion(ConversionEntry entry, IProgressMonitor pm)
			throws RodinDBException {
		Buffer buffer = new Buffer(entry.file);
		try {
			buffer.load(pm);
			entry.version = buffer.getVersion();
		} catch (RodinDBException e) {
			if (buffer.getVersion() != -1) {
				entry.version = buffer.getVersion();
			} else {
				throw e;
			}
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
				if (entry.success() && entry.version != entry.reqVersion) {
					entry.file.revert();
					ByteArrayInputStream s = new ByteArrayInputStream(
							entry.buffer);
					try {
						entry.file.getResource().setContents(s, force,
								keepHistory, monitor);
					} catch (CoreException e) {
						throw new RodinDBException(e);
					}
					// remove following lines
					// RodinFileElementInfo info = (RodinFileElementInfo)
					// entry.file.getElementInfo();
					// info.setVersion(entry.reqVersion);
					entry.file.save(monitor, force, false);
				}
				monitor.worked(1);
			}

		} finally {
			monitor.done();
		}
	}
}
