/*******************************************************************************
 * Copyright (c) 2007, 2009 ETH Zurich and others.
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

import static org.eclipse.core.runtime.IStatus.INFO;
import static org.rodinp.core.IRodinDBStatusConstants.CONVERSION_ERROR;
import static org.rodinp.core.IRodinDBStatusConstants.FUTURE_VERSION;
import static org.rodinp.core.RodinCore.PLUGIN_ID;
import static org.rodinp.core.RodinCore.getPlugin;
import static org.rodinp.internal.core.RodinDBStatus.VERIFIED_OK;
import static org.rodinp.internal.core.util.Messages.bind;
import static org.rodinp.internal.core.util.Messages.converter_failedConversion;
import static org.rodinp.internal.core.util.Messages.converter_fileUnchanged;
import static org.rodinp.internal.core.util.Messages.converter_successfulConversion;
import static org.rodinp.internal.core.util.Messages.status_upgradedFile;
import static org.rodinp.internal.core.version.VersionManager.UNKNOWN_VERSION;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.IConversionResult.IEntry;
import org.rodinp.internal.core.Buffer;
import org.rodinp.internal.core.RodinDBStatus;
import org.rodinp.internal.core.RodinFile;

public class ConversionEntry implements IEntry {

	private final RodinFile file;

	private long version;
	private long reqVersion;
	private IStatus status;
	private String message;
	private byte[] buffer;

	public ConversionEntry(IRodinFile file) {
		this.file = (RodinFile) file;
		this.status = VERIFIED_OK;
		this.version = UNKNOWN_VERSION;
	}

	public ConversionEntry(IRodinFile file, long version) {
		this.file = (RodinFile) file;
		this.status = VERIFIED_OK;
		this.version = version;
	}

	public void accept(boolean force, boolean keepHistory,
			IProgressMonitor monitor) throws RodinDBException {
		final SubMonitor sm = SubMonitor.convert(monitor, 100);
		if (success() && buffer != null) {
			file.revert();
			sm.worked(10);
			final ByteArrayInputStream s = new ByteArrayInputStream(buffer);
			final SubMonitor ssm = sm.newChild(90);
			try {
				file.getResource().setContents(s, force, keepHistory, ssm);
			} catch (CoreException e) {
				throw new RodinDBException(e);
			}
			final IStatus st = new Status(INFO, PLUGIN_ID, bind(
					status_upgradedFile, file.getPath(), reqVersion));
			log(st);
		}
	}

	public IRodinFile getFile() {
		return file;
	}

	private long computeFileVersion(IProgressMonitor pm) throws RodinDBException {
		final Buffer fileBuffer = new Buffer(file);
		fileBuffer.attemptLoad(pm);
		return fileBuffer.getVersion();
	}

	public String getMessage() {
		return message;
	}

	public long getSourceVersion() {
		return version;
	}

	public long getTargetVersion() {
		return reqVersion;
	}

	public boolean success() {
		return status.isOK();
	}

	@Override
	public String toString() {
		return new String(buffer);
	}

	public void upgrade(VersionManager vManager, boolean force,
			IProgressMonitor pm) {
		final IInternalElementType<?> type = file.getRoot().getElementType();
		final SubMonitor sm = SubMonitor.convert(pm, 100);
		try {
			if (version == UNKNOWN_VERSION) {
				version = computeFileVersion(sm.newChild(10));
			}
			reqVersion = vManager.getVersion(type);
			if (version == reqVersion) {
				message = converter_fileUnchanged;
				return;
			}
			if (version > reqVersion) {
				status = new RodinDBStatus(FUTURE_VERSION, file, "" + version);
				message = converter_failedConversion;
				return;
			}
			final Converter converter = vManager.getConverter(type);
			final InputStream contents = file.getResource().getContents(force);
			buffer = converter.convert(contents, version, reqVersion);
			message = converter_successfulConversion;
			sm.worked(90);
		} catch (CoreException e) {
			status = e.getStatus();
			message = converter_failedConversion;
		} catch (Exception e) {
			status = new RodinDBStatus(CONVERSION_ERROR, e);
			message = converter_failedConversion;
		} finally {
			if (! status.isOK()) {
				log(status);
			}
		}
	}

	private void log(IStatus st) {
		getPlugin().getLog().log(st);
	}

}