package org.eventb.internal.ui.eventbeditor.editpage;

public class EventLabelAttributeFactory extends
		LabelAttributeFactory implements IAttributeFactory {

	@Override
	protected void setPrefix() {
		defaultPrefix = "evt";
	}

}
