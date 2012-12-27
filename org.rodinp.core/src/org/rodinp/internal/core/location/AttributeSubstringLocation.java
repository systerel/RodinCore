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

import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.location.IAttributeSubstringLocation;
import org.rodinp.core.location.IRodinLocation;

public class AttributeSubstringLocation extends AttributeLocation implements
		IAttributeSubstringLocation {

	private final int charStart;
	private final int charEnd;

	public AttributeSubstringLocation(IInternalElement element,
			IAttributeType.String attributeType, int charStart, int charEnd) {
		super(element, attributeType);
		if (charStart < 0) {
			throw new IllegalArgumentException("Negative start position");
		}
		if (charEnd <= charStart) {
			throw new IllegalArgumentException(
					"End position must be greater than start position");
		}

		this.charStart = charStart;
		this.charEnd = charEnd;
	}

	@Override
	public int getCharStart() {
		return charStart;
	}

	@Override
	public int getCharEnd() {
		return charEnd;
	}

	@Override
	public boolean isIncludedIn(IRodinLocation other) {
		if (!(other instanceof IAttributeSubstringLocation)) {
			return super.isIncludedIn(other);
		}
		final IAttributeSubstringLocation otherSubs =
				(IAttributeSubstringLocation) other;
		final int otherStart = otherSubs.getCharStart();
		final int otherEnd = otherSubs.getCharEnd();
		return charStart >= otherStart && charEnd <= otherEnd;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + charStart;
		result = prime * result + charEnd;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof AttributeSubstringLocation))
			return false;
		final AttributeSubstringLocation other =
				(AttributeSubstringLocation) obj;
		return this.charStart == other.charStart
				&& this.charEnd == other.charEnd;
	}

	@Override
	public String toString() {
		return super.toString() + "[" + charStart + ".." + charEnd + "]";
	}

}
