package org.eventb.internal.ui.eventbeditor.actions;

import org.eventb.core.ICarrierSet;

public class PrefixSetName extends PrefixElementName {
	public PrefixSetName() {
		super(ICarrierSet.ELEMENT_TYPE, "Carrier Set Identifier Prefix",
				"Please specify the prefix for carrier set identifiers");
	}
}
