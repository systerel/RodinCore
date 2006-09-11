package org.eventb.internal.ui;

import org.eclipse.swt.graphics.Image;

public class ElementUI {

	private Image image;

	private Class clazz;

	public ElementUI(Image image, Class clazz) {
		this.image = image;
		this.clazz = clazz;
	}

	public Class getElementClass() {
		return clazz;
	}

	public Image getImage() {
		return image;
	}

}
