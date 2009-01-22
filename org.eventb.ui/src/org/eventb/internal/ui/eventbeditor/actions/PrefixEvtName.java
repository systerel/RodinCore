package org.eventb.internal.ui.eventbeditor.actions;

import org.eventb.core.IEvent;

public class PrefixEvtName extends PrefixElementName {
	public PrefixEvtName() {
		super(IEvent.ELEMENT_TYPE, "Event Label Prefix",
				"Please specify the prefix for event labels");
	}
}
