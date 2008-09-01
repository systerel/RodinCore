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
import org.eclipse.core.runtime.SubMonitor;
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

	public void upgrade(VersionManager vManager, boolean force,
			IProgressMonitor pm) {
		final IFileElementType<?> type = file.getElementType();
		final SubMonitor sm = SubMonitor.convert(pm, 100);
		try {
			version = getFileVersion(sm.newChild(10));
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
			sm.worked(90);
		} catch (Exception e) {
			error = e;
			message = Messages.converter_failedConversion;
		}
	}

}