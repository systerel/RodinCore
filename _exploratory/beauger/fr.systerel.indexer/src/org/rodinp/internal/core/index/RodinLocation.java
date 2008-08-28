package org.rodinp.internal.core.index;

import org.rodinp.core.IAttributeType;
import org.rodinp.core.IRodinDBStatus;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.index.IRodinLocation;
import org.rodinp.internal.core.RodinDBStatus;

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

		IRodinDBStatus status = RodinLocationUtil.verifyRodinLocation(element,
				attributeType, charStart, charEnd);

		if (status != RodinDBStatus.VERIFIED_OK) {
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

}
