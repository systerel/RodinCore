/*******************************************************************************
 * Copyright (c) 2007, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added history support
 *     Systerel - separation of file and root element
 *     Systerel - used IAttributeFactory
 *******************************************************************************/
package org.eventb.internal.ui.propertiesView;

import org.eventb.core.IExtendsContext;
import org.eventb.internal.ui.eventbeditor.editpage.ExtendsContextAbstractContextNameAttributeFactory;
import org.eventb.internal.ui.eventbeditor.editpage.IAttributeFactory;

public class ExtendsContextSection extends CComboSection<IExtendsContext> {

	@Override
	String getLabel() {
		return "Abs. Ctx.";
	}

	@Override
	protected IAttributeFactory<IExtendsContext> createFactory() {
		return new ExtendsContextAbstractContextNameAttributeFactory();
	}

}
