package org.rodinp.internal.core.index;

import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.index.IAttributeSubstringLocation;

public class AttributeSubstringLocation extends AttributeLocation implements
		IAttributeSubstringLocation {

	private final int charStart;
	private final int charEnd;

	public AttributeSubstringLocation(IInternalParent element,
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

	public int getCharStart() {
		return charStart;
	}

	public int getCharEnd() {
		return charEnd;
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
		final AttributeSubstringLocation other = (AttributeSubstringLocation) obj;
		return this.charStart == other.charStart
				&& this.charEnd == other.charEnd;
	}

	@Override
	public String toString() {
		return super.toString() + "[" + charStart + ".." + charEnd + "]";
	}

}
