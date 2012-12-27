/*******************************************************************************
 * Copyright (c) 2008, 2012 Systerel and others.
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
public abstract class Location <T extends IRodinElement> {

	protected final T element;

	public Location(T element) {
		if (element == null) {
			throw new NullPointerException("null element");
		}
		this.element = element;
	}
	
	public T getElement() {
		return element;
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
		if (obj.getClass() != this.getClass())
			return false;
		Location<?> other = (Location<?>) obj;
		if (!element.equals(other.element))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return element.toString();
	}

	public IRodinFile getRodinFile() {
		if (element instanceof IRodinFile) {
			return (IRodinFile) element;
		} else if (element instanceof IInternalElement) {
			return ((IInternalElement) element).getRodinFile();
		}
		return null;
	}



}
