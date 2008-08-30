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

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.rodinp.core.IFileElementType;
import org.rodinp.core.IRodinDBStatusConstants;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.IConversionResult.IEntry;
import org.rodinp.core.basis.RodinFile;
import org.rodinp.internal.core.Buffer;
import org.rodinp.internal.core.util.Messages;

class ConversionEntry implements IEntry {

	protected final RodinFile file;

	protected long version;
	protected long reqVersion;
	protected Exception error;
	protected String message;
	protected byte[] buffer;

	public ConversionEntry(IRodinFile file) {
		this.file = (RodinFile) file;
	}

	// Consumes one tick of given monitor
	public void accept(boolean force, boolean keepHistory,
			IProgressMonitor monitor) throws RodinDBException {
		if (success() && buffer != null) {
			file.revert();
			final ByteArrayInputStream s = new ByteArrayInputStream(buffer);
			final SubProgressMonitor spm = new SubProgressMonitor(monitor, 1);
			try {
				file.getResource().setContents(s, force, keepHistory, spm);
			} catch (CoreException e) {
				throw new RodinDBException(e);
			}
		} else {
			monitor.worked(1);
		}
	}

	public IRodinFile getFile() {
		return file;
	}

	private long getFileVersion(IProgressMonitor pm) throws RodinDBException {
		final Buffer fileBuffer = new Buffer(file);
		try {
			fileBuffer.load(pm);
			return fileBuffer.getVersion();
		} catch (RodinDBException e) {
			final long v = fileBuffer.getVersion();
			if (v != -1) {
				return v;
			} else {
				throw e;
			}
		}
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
		return error == null;
	}

	@Override
	public String toString() {
		return new String(buffer);
	}

	// Consumes two ticks of given monitor
	public void upgrade(VersionManager vManager, boolean force,
			IProgressMonitor pm) {
		final IFileElementType<?> type = file.getElementType();
		try {
			version = getFileVersion(new SubProgressMonitor(pm, 1));
			reqVersion = vManager.getVersion(type);
			if (version == reqVersion) {
				message = Messages.converter_fileUnchanged;
				return;
			}
			if (version > reqVersion) {
				error = new RodinDBException(null,
						IRodinDBStatusConstants.FUTURE_VERSION);
				message = Messages.converter_failedConversion;
				return;
			}
			final Converter converter = vManager.getConverter(type);
			final InputStream contents = file.getResource().getContents(force);
			buffer = converter.convert(contents, version, reqVersion);
		} catch (Exception e) {
			error = e;
			message = Messages.converter_failedConversion;
		} finally {
			pm.worked(1);
		}
	}

}