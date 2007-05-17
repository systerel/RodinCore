package org.eventb.internal.ui.eventbeditor.editpage;

public class InvariantLabelAttributeFactory extends
		LabelAttributeFactory implements IAttributeFactory {

	@Override
	protected void setPrefix() {
		defaultPrefix = "inv";
	}

}
