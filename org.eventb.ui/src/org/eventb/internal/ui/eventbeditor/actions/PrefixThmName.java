package org.eventb.internal.ui.eventbeditor.actions;

import org.eventb.core.ITheorem;

public class PrefixThmName extends PrefixElementName {
	public PrefixThmName() {
		super(ITheorem.ELEMENT_TYPE, "Theorem Label Prefix",
				"Please specify the prefix for theorem labels");
	}
}
