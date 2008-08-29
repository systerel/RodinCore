/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core.version;

import org.rodinp.core.IRodinFile;
import org.rodinp.core.IConversionResult.IEntry;
import org.rodinp.core.basis.RodinFile;

class ConversionEntry implements IEntry {

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (byte b : buffer) {
			sb.append((char) b);
		}
		return sb.toString();
	}

	protected final RodinFile file;
	protected long version;
	protected long reqVersion;
	protected Exception error;
	protected String message;
	protected byte[] buffer;

	public IRodinFile getFile() {
		return file;
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

	public ConversionEntry(IRodinFile file) {
		this.file = (RodinFile) file;
	}
}