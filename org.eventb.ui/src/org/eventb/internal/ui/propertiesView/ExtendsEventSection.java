/*******************************************************************************
 * Copyright (c) 2007, 2008 ETH Zurich and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - replaced inherited by extended
 *     Systerel - added history support
 *     Systerel - used IAttributeFactory
 *******************************************************************************/
package org.eventb.internal.ui.propertiesView;

import org.eventb.core.IEvent;
import org.eventb.internal.ui.eventbeditor.editpage.ExtendedAttributeFactory;
import org.eventb.internal.ui.eventbeditor.editpage.IAttributeFactory;

public class ExtendsEventSection extends CComboSection<IEvent> {

	@Override
	String getLabel() {
		return "Extended";
	}

	@Override
	protected IAttributeFactory<IEvent> createFactory() {
		return new ExtendedAttributeFactory();
	}

}
