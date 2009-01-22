package org.eventb.internal.ui.eventbeditor.actions;

import org.eventb.core.IConstant;

public class PrefixCstName extends PrefixElementName {
	public PrefixCstName() {
		super(IConstant.ELEMENT_TYPE, "Constant Name Prefix",
				"Please specify the prefix for constant names");
	}
}
