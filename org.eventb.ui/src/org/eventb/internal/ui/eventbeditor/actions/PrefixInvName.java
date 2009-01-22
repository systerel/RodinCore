package org.eventb.internal.ui.eventbeditor.actions;

import org.eventb.core.IInvariant;

public class PrefixInvName extends PrefixElementName {
	public PrefixInvName() {
		super(IInvariant.ELEMENT_TYPE, "Invariant Label Prefix",
				"Please specify the prefix for invariant labels");
	}
}
