package org.eventb.internal.ui.eventbeditor.actions;

import org.eventb.core.IWitness;

public class PrefixWitName extends PrefixElementName {
	public PrefixWitName() {
		super(IWitness.ELEMENT_TYPE, "Witness Name Prefix",
				"Please specify the prefix for witness names");
	}
}
