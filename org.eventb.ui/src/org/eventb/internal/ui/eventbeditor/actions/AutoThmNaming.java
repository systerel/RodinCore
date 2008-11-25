package org.eventb.internal.ui.eventbeditor.actions;

import org.eventb.core.EventBPlugin;

public class AutoThmNaming extends AutoElementNaming {

	@Override
	public String getAttributeRelationshipID() {
		return EventBPlugin.PLUGIN_ID + ".theoremLabel";
	}

}