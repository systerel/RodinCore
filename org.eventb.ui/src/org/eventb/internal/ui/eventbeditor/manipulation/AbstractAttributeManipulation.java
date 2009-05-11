package org.eventb.internal.ui.eventbeditor.manipulation;

import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.IAttributeType;

public abstract class AbstractAttributeManipulation implements
		IAttributeManipulation {

	protected void logCantGetPossibleValues(IAttributeType attribute) {
		UIUtils.log(null,
				"The method GetPossibleValues cannot be called for attribute "
						+ attribute);
	}

	protected void logNotPossibleValues(IAttributeType attribute, String value) {
		UIUtils.log(null, value + " is not a possible value for attribute "
				+ attribute);
	}

	protected void logCantRemove(IAttributeType attribute) {
		UIUtils.log(null, "Attribute " + attribute + " cannot be removed");
	}

}
