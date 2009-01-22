package org.eventb.internal.ui.eventbeditor.actions;

import org.eventb.core.IAction;

public class PrefixActName extends PrefixElementName {
	public PrefixActName() {
		super(IAction.ELEMENT_TYPE, "Action Label Prefix",
				"Please specify the prefix for action labels");
	}
}
