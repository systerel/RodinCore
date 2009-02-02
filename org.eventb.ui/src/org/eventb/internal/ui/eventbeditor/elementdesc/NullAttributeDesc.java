/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
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
import org.eventb.internal.ui.eventbeditor.manipulation.IAttributeManipulation;
import org.eventb.internal.ui.eventbeditor.manipulation.NullAttributeManipulation;
import org.rodinp.core.IAttributeType;

class NullAttributeDesc implements IAttributeDesc {

	public IEditComposite createWidget() {
		return null;
	}

	public IAttributeType getAttributeType() {
		return new IAttributeType() {

			public java.lang.String getId() {
				return "";
			}

			public java.lang.String getName() {
				return "";
			}
		};
	}

	public IAttributeManipulation getManipulation() {
		return new NullAttributeManipulation();
	}

	public String getSuffix() {
		return "";
	}

	public boolean isHorizontalExpand() {
		return false;
	}

	public String getPrefix() {
		return "";
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof NullAttributeDesc;
	}
}
