package org.eventb.internal.ui.eventbeditor.actions;

import org.eventb.core.IVariable;

public class PrefixVarName extends PrefixElementName {
	public PrefixVarName() {
		super(IVariable.ELEMENT_TYPE, "Variable Identifier Prefix",
				"Please specify the prefix for variable identifiers");
	}
}
