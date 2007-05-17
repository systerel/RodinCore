package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.jface.action.IAction;
import org.eventb.core.IConstant;
import org.eventb.internal.ui.eventbeditor.editpage.EditSectionRegistry;

public class PrefixCstName extends PrefixElementName<IConstant> {

	public static final String DEFAULT_PREFIX = "cst";

	public void run(IAction action) {
		setPrefix(IConstant.ELEMENT_TYPE,
				EditSectionRegistry.IDENTIFIER_ATTRIBUTE_ID,
				"Constant Name Prefix",
				"Please specify the prefix for constant names");
	}

}
