package org.eventb.internal.ui.eventbeditor.editpage;

public class ActionLabelAttributeFactory extends
		LabelAttributeFactory implements IAttributeFactory {

	@Override
	protected void setPrefix() {
		defaultPrefix = "act";
	}

}
