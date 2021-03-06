/*******************************************************************************
 * Copyright (c) 2009, 2014 Systerel and others.
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
import org.eventb.internal.ui.eventbeditor.manipulation.NullAttributeManipulation;
import org.eventb.ui.manipulation.IAttributeManipulation;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElementType;

public class NullAttributeDesc extends AttributeDesc {

	static IAttributeManipulation manipulation = new NullAttributeManipulation();

	private static IAttributeType type = getTypeInstance();

	public NullAttributeDesc() {
		super(manipulation, "", "", false, type);
	}

	@Override
	public IEditComposite createWidget() {
		return null;
	}

	private static IAttributeType getTypeInstance() {
		return new IAttributeType() {

			@Override
			public java.lang.String getId() {
				return "";
			}

			@Override
			public java.lang.String getName() {
				return "";
			}

			@Override
			public boolean equals(Object obj) {
				if (!(obj instanceof IAttributeType))
					return false;
				final IAttributeType at = (IAttributeType) obj;
				return this.getId().equals(at.getId())
						&& this.getName().equals(at.getName());
			}

			@Override
			public IInternalElementType<?>[] getElementTypes() {
				return new IInternalElementType[0];
			}

			@Override
			public boolean isAttributeOf(IInternalElementType<?> elementType) {
				return false;
			}

			@Override
			public boolean isUbiquitous() {
				return false;
			}
		};
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof NullAttributeDesc);
	}

	public boolean isUbiquitous() {
		return false;
	}
}
