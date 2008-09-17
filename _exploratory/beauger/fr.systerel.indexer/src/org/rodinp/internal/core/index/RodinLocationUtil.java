package org.rodinp.internal.core.index;

import static org.rodinp.core.IRodinDBStatusConstants.*;

import org.rodinp.core.IAttributeType;
import org.rodinp.core.IAttributedElement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinDBStatus;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.index.RodinIndexer;
import org.rodinp.internal.core.RodinDBStatus;

public class RodinLocationUtil {

	public static IRodinDBStatus verifyRodinLocation(IRodinElement element,
			IAttributeType attributeType, int charStart, int charEnd) {
		final IAttributedElement attrElem;
		if (element instanceof IRodinFile
				|| element instanceof IInternalElement) {
			attrElem = (IAttributedElement) element;
		} else {
			return RodinIndexer
					.errorStatus("Bad element type: expected IRodinFile or IInternalElement");
		}

		if (!element.exists()) {
			return new RodinDBStatus(ELEMENT_DOES_NOT_EXIST, element);
		}
		if (attributeType == null) {
			return verifyNoPosition(charStart, charEnd);
		} else {
			return verifyWithPosition(attrElem, attributeType, charStart,
					charEnd);
		}
	}

	private static IRodinDBStatus verifyWithPosition(IAttributedElement element,
			IAttributeType attributeType, int charStart, int charEnd) {

		IRodinDBStatus status = verifyAttribute(element, attributeType,
				charStart >= 0);
		if (!status.isOK()) {
			return status;
		}
		if (charStart >= 0) {
			if (charStart >= charEnd) { // charEnd must be EXclusive
				return RodinIndexer
						.errorStatus("End position is before start position");
			}
		} else if (charEnd >= 0) {
			return RodinIndexer
					.errorStatus("End position without a start position");
		}
		return RodinDBStatus.VERIFIED_OK;
	}

	private static IRodinDBStatus verifyNoPosition(int charStart, int charEnd) {
		if (charStart >= 0) {
			return RodinIndexer
					.errorStatus("Start position without an attribute id");
		}
		if (charEnd >= 0) {
			return RodinIndexer
					.errorStatus("End position without an attribute id");
		}
		return RodinDBStatus.VERIFIED_OK;
	}

	private static IRodinDBStatus verifyAttribute(IAttributedElement element,
			IAttributeType attributeType, boolean withCharPos) {
		// Check that attribute exists
		try {
			if (withCharPos && !element.hasAttribute(attributeType)) {
				return new RodinDBStatus(ATTRIBUTE_DOES_NOT_EXIST, element,
						attributeType.getId());
			}
		} catch (RodinDBException rde) {
			return rde.getRodinDBStatus();
		}
		if (withCharPos) {
			// Check that it's an attribute of kind String
			if (!(attributeType instanceof IAttributeType.String)) {
				return new RodinDBStatus(INVALID_ATTRIBUTE_KIND, attributeType
						.getId());
				/** To be moved to {@link RodinCore} */

			}
		}
		return RodinDBStatus.VERIFIED_OK;
	}

}
