package org.eventb.internal.ui.eventbeditor.actions;

import org.eventb.core.IAxiom;

public class PrefixAxmName extends PrefixElementName {
	public PrefixAxmName() {
		super(IAxiom.ELEMENT_TYPE, "Axiom Label Prefix",
				"Please specify the prefix for axiom labels");
	}
}
