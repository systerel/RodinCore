package org.rodinp.internal.core.index;

import org.rodinp.core.IRodinElement;
import org.rodinp.core.index.IRodinLocation;

public class RodinLocation implements IRodinLocation {

	private final IRodinElement element;
	private final String attributeId;
	private final int charStart;
	private final int charEnd;

	public RodinLocation(IRodinElement element, String attributeId, int charStart,
			int charEnd) {
		this.element = element;
		this.attributeId = attributeId;
		this.charStart = charStart;
		this.charEnd = charEnd;
	}


	public IRodinElement getElement() {
		return element;
	}


	public String getAttributeId() {
		return attributeId;
	}


	public int getCharStart() {
		return charStart;
	}


	public int getCharEnd() {
		return charEnd;
	}

}
///**
//* Element marker attribute (value <code>"element"</code>). This
//* attribute contains the handle identifier of the Rodin internal element
//* where the problem is located.
//* <p>
//* This attribute is optional, however it must be set if
//* {@link #ATTRIBUTE_ID} is specified.
//* </p>
//*/
//public static final String ELEMENT = "element"; //$NON-NLS-1$
//
///**
//* Attribute id marker attribute (value <code>"attributeId"</code>). This
//* attribute contains the identifier of the Rodin attribute where the
//* problem is located.
//* <p>
//* This marker attribute is optional, however it must be set if either
//* {@link #CHAR_START} or {@link #CHAR_END} is specified.
//* </p>
//*/
//public static final String ATTRIBUTE_ID = "attributeId"; //$NON-NLS-1$
//
///**
//* Character start marker attribute (value <code>"charStart"</code>).
//* <p>
//* An integer value indicating where a text marker starts. This attribute is
//* zero-relative and inclusive. The position stored in this attribute is
//* relative to the String representation of the Rodin attribute where the
//* error is located (see {@link #ATTRIBUTE_ID}). That latter attribute must
//* thus be of kind <code>String</code>.
//* </p>
//* <p>
//* This attribute is optional, however it must be set if {@link #CHAR_END}
//* is set. Moreover, its value must be strictly less than that of
//* <code>CHAR_END</code>.
//* </p>
//*/
//public static final String CHAR_START = "charStart"; //$NON-NLS-1$
//
///**
//* Character end marker attribute (value <code>"charEnd"</code>).
//* <p>
//* An integer value indicating where a text marker ends. This attribute is
//* zero-relative and exclusive. The position stored in this attribute is
//* relative to the String representation of the Rodin attribute where the
//* error is located (see {@link #ATTRIBUTE_ID}). That latter attribute must
//* thus be of kind <code>String</code>.
//* </p>
//* <p>
//* This attribute is optional, however it must be set if {@link #CHAR_START}
//* is set. Moreover, its value must be strictly greater than that of
//* <code>CHAR_START</code>.
//* </p>
//*/
//public static final String CHAR_END = "charEnd"; //$NON-NLS-1$
