package org.eventb.internal.ui.eventbeditor.actions;

import org.eventb.core.IGuard;

public class PrefixGrdName extends PrefixElementName {
	public PrefixGrdName() {
		super(IGuard.ELEMENT_TYPE, "Guard Label Prefix",
				"Please specify the prefix for guard labels");
	}
}
