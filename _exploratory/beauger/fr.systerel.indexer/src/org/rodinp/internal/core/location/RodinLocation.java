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
package org.rodinp.internal.core.location;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.location.IRodinLocation;

/**
 * @author Nicolas Beauger
 * 
 */
public class RodinLocation implements IRodinLocation {

	private final IRodinElement element;

	public RodinLocation(IRodinElement element) {
		this.element = element;
	}

	public IRodinElement getElement() {
		return element;
	}

	public IRodinFile getRodinFile() {
		if (element instanceof IRodinFile) {
			return (IRodinFile) element;
		} else if (element instanceof IInternalElement) {
			return ((IInternalElement) element).getRodinFile();
		}
		return null;
	}

	public boolean isIncludedIn(IRodinLocation other) {
		final IRodinElement otherElement = other.getElement();
		return otherElement.equals(element)
				|| otherElement.isAncestorOf(element);
	}

	@Override
	public int hashCode() {
		return 31 + element.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof RodinLocation))
			return false;
		final RodinLocation other = (RodinLocation) obj;
		if (!element.equals(other.element))
			return false;
		return true;
	}

}
