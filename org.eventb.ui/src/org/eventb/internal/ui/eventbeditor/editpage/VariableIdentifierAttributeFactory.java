package org.eventb.internal.ui.eventbeditor.editpage;

public class VariableIdentifierAttributeFactory extends IdentifierAttributeFactory
		implements IAttributeFactory {

	@Override
	public void setPrefix() {
		defaultPrefix = "var";
	}

}
