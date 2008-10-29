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

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.index.IRodinLocation;

public class RodinLocation implements IRodinLocation {

	private final IInternalElement element;

	public RodinLocation(IInternalElement element) {
		if (element == null) {
			throw new NullPointerException("null element");
		}
		this.element = element;
	}

	public IInternalElement getElement() {
		return element;
	}

	public IRodinFile getRodinFile() {
		return element.getRodinFile();
	}

	@Override
	public int hashCode() {
		return element.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj.getClass() != this.getClass())
			return false;
		if (!(obj instanceof RodinLocation))
			return false;
		final RodinLocation other = (RodinLocation) obj;
		return element.equals(other.element);
	}

	@Override
	public String toString() {
		return element.toString();
	}

}
