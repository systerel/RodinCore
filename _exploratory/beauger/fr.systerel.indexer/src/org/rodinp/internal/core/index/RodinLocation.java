package org.rodinp.internal.core.index;

import static org.rodinp.internal.core.index.RodinLocationUtil.verifyRodinLocation;

import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinDBStatus;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.index.IRodinLocation;

public class RodinLocation implements IRodinLocation {

	/**
	 * This field contains the handle of the Rodin element where the concerned
	 * element is located.
	 * <p>
	 * This field cannot be <code>null</code>.
	 */
	private final IRodinElement element;

	/**
	 * This field contains the attribute type of the Rodin element where the
	 * concerned element is located.
	 * <p>
	 * This field is optional and can thus be <code>null</code>. However it
	 * must be set if either {@link #charStart} or {@link #charEnd} is
	 * specified.
	 */
	private final IAttributeType attributeType;

	/**
	 * An integer value indicating where the concerned element is located inside
	 * the attribute of type {@link #attributeType}. This field is
	 * zero-relative and inclusive.
	 * <p>
	 * This field is optional and can thus be set to
	 * {@link IRodinLocation#NULL_CHAR_POS}. However it must be set if
	 * {@link #charEnd} is set. Moreover, its value must be strictly less than
	 * that of {@link #charEnd}.
	 * 
	 */
	private final int charStart;

	/**
	 * An integer value indicating where the concerned element ends inside the
	 * attribute of type attributeType. This field is zero-relative and
	 * exclusive.
	 * <p>
	 * This attribute is optional and can thus be set to
	 * {@link IRodinLocation#NULL_CHAR_POS}. However it must be set if
	 * {@link #charStart} is set. Moreover, its value must be strictly greater
	 * than that of {@link #charStart}.
	 */
	private final int charEnd;

	public RodinLocation(IRodinElement element, IAttributeType attributeType,
			int charStart, int charEnd) {

		if (element == null) {
			throw new NullPointerException("null element");
		}

		IRodinDBStatus status = verifyRodinLocation(element,
				attributeType, charStart, charEnd);

		if (!status.isOK()) {
			throw new IllegalArgumentException(status.getMessage());
		}

		this.element = element;
		this.attributeType = attributeType;
		this.charStart = charStart;
		this.charEnd = charEnd;
	}

	public IRodinElement getElement() {
		return element;
	}

	public IAttributeType getAttributeType() {
		return attributeType;
	}

	public int getCharStart() {
		return charStart;
	}

	public int getCharEnd() {
		return charEnd;
	}

	public IRodinFile getRodinFile() {
		if (element instanceof IRodinFile) {
			return (IRodinFile) element;
		}
		if (element instanceof IInternalElement) {
			return ((IInternalElement) element).getRodinFile();
		}
		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + element.hashCode();
		result = prime * result
				+ ((attributeType == null) ? 0 : attributeType.hashCode());
		result = prime * result + charStart;
		result = prime * result + charEnd;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof RodinLocation))
			return false;
		final RodinLocation other = (RodinLocation) obj;
		if (!element.equals(other.element)) {
			return false;
		}
		if (attributeType == null) {
			return other.attributeType == null;
		} else if (!attributeType.equals(other.attributeType)) {
			return false;
		}
		if (charEnd != other.charEnd)
			return false;
		if (charStart != other.charStart)
			return false;
		return true;
	}

}
