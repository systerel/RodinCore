/*******************************************************************************
 * Copyright (c) 2007 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui.eventbeditor.editpage;

import org.rodinp.core.IAttributeType;
import org.rodinp.core.IAttributedElement;

public class AttributeUISpec<E extends IAttributedElement> implements IAttributeUISpec<E> {

	private IAttributeFactory<E> factory;
	private String prefix;
	private String postfix;
	private boolean fillHorzontal;
	private IAttributeType type;
	
	public AttributeUISpec(IAttributeFactory<E> factory, IAttributeType type, String prefix,
			String postfix, boolean fillHorizontal) {
		assert factory != null;
		assert type != null;
		this.factory = factory;
		this.type = type;
		this.prefix = prefix;
		this.postfix = postfix;
		this.fillHorzontal = fillHorizontal;
	}

	public IAttributeFactory<E> getAttributeFactory() {
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

	public IAttributeType getAttributeType() {
		return type;
	}

}
