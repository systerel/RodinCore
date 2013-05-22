/*******************************************************************************
 * Copyright (c) 2009, 2012 Systerel and others.
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
import org.eventb.internal.ui.eventbeditor.manipulation.AbstractBooleanManipulation;
import org.rodinp.core.IAttributeType;

public class ToggleDesc extends AttributeDesc {

	public ToggleDesc(AbstractBooleanManipulation manipulation,
			IAttributeType attrType) {
		super(manipulation, "", "", false, attrType);
	}

	@Override
	public IEditComposite createWidget() {
		return new ToggleEditComposite(this);
	}

	@Override
	public boolean isToggleAttribute() {
		return true;
	}

}
