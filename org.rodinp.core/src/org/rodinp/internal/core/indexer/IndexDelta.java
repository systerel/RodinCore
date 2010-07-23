/*******************************************************************************
 * Copyright (c) 2008, 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core.indexer;

import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;

/**
 * @author Nicolas Beauger
 * 
 */
public class IndexDelta implements IIndexDelta {

	private final IRodinElement element;
	private final Kind kind;

	public IndexDelta(IRodinElement element, Kind kind) {
		validate(element, kind);
		this.element = element;
		this.kind = kind;
	}

	private void validate(IRodinElement elem, Kind k) {
		if (k == Kind.FILE_CHANGED) {
			if (!(elem instanceof IRodinFile)) {
				throw new IllegalArgumentException("IRodinFile expected");
			}
		} else {
			if (!(elem instanceof IRodinProject)) {
				throw new IllegalArgumentException("IRodinProject expected");
			}
		}
	}

	@Override
	public IRodinElement getElement() {
		return element;
	}

	@Override
	public Kind getKind() {
		return kind;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = prime + element.hashCode();
		result = prime * result + kind.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof IndexDelta))
			return false;
		final IndexDelta other = (IndexDelta) obj;
		if (!element.equals(other.element))
			return false;
		if (!kind.equals(other.kind))
			return false;
		return true;
	}

}
