/*******************************************************************************
 * Copyright (c) 2009, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.elementdesc;

import org.eventb.internal.ui.eventbeditor.editpage.IEditComposite;
import org.eventb.ui.manipulation.IAttributeManipulation;
import org.rodinp.core.IAttributeType;

public class ComboDesc extends AttributeDesc {

	private boolean required;

	public ComboDesc(IAttributeManipulation factory, String prefix,
			String suffix, boolean isHorizontalExpand, IAttributeType attrType,
			boolean required) {
		super(factory, prefix, suffix, isHorizontalExpand, attrType);
		this.required = required;
	}

	@Override
	public IEditComposite createWidget() {
		return new CComboEditComposite(this);
	}

	public boolean isRequired() {
		return required;
	}
}
