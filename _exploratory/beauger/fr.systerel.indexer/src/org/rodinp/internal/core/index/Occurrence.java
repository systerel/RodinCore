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
package org.rodinp.internal.core.index;

import org.rodinp.core.IRodinFile;
import org.rodinp.core.index.IOccurrence;
import org.rodinp.core.index.IOccurrenceKind;
import org.rodinp.core.index.IInternalLocation;

public class Occurrence implements IOccurrence {

	private final IOccurrenceKind kind;
	private final IInternalLocation location;

	public Occurrence(IOccurrenceKind kind, IInternalLocation location) {
		if (kind == null) {
			throw new NullPointerException("null kind");
		}
		if (location == null) {
			throw new NullPointerException("null location");
		}
		this.kind = kind;
		this.location = location;
	}

	public IOccurrenceKind getKind() {
		return kind;
	}

	public IInternalLocation getLocation() {
		return location;
	}

	public IRodinFile getRodinFile() {
		return location.getRodinFile();
	}
	
	// DEBUG
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("kind: " + kind.getName());
		sb.append(" ");
		sb.append("location: " + location);
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + kind.hashCode();
		result = prime * result + location.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (! (obj instanceof Occurrence))
			return false;
		final Occurrence other = (Occurrence) obj;
		if (!kind.equals(other.kind))
			return false;
		if (!location.equals(other.location))
			return false;
		return true;
	}

}
