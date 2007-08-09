package org.eventb.internal.ui.eventbeditor.editpage;

public class AttributeUISpec implements IAttributeUISpec {

	private IAttributeFactory factory;
	private String prefix;
	private String postfix;
	private boolean fillHorzontal;
	
	public AttributeUISpec(IAttributeFactory factory, String prefix,
			String postfix, boolean fillHorizontal) {
		assert factory != null;
		this.factory = factory;
		this.prefix = prefix;
		this.postfix = postfix;
		this.fillHorzontal = fillHorizontal;
	}

	public IAttributeFactory getAttributeFactory() {
		return factory;
	}

	public String getPostfix() {
		return postfix;
	}

	public String getPrefix() {
		return prefix;
	}

	public boolean isFillHorizontal() {
		return fillHorzontal;
	}

}
