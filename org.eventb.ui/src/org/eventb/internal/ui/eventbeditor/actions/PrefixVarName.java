package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.jface.action.IAction;
import org.eventb.core.IVariable;
import org.eventb.internal.ui.eventbeditor.editpage.EditSectionRegistry;

public class PrefixVarName extends PrefixElementName<IVariable> {

	public static final String DEFAULT_PREFIX = "var";

	public void run(IAction action) {
		setPrefix(IVariable.ELEMENT_TYPE,
				EditSectionRegistry.IDENTIFIER_ATTRIBUTE_ID,
				"Variable Identifier Prefix",
				"Please specify the prefix for variable identifiers");
	}

}
