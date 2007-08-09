package org.eventb.internal.ui.eventbeditor.editpage;

public interface IAttributeUISpec {
	public String getPrefix();

	public String getPostfix();

	public boolean isFillHorizontal();

	public IAttributeFactory getAttributeFactory();

}
