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
 *******************************************************************************/
package org.eventb.internal.ui.propertiesView;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.editpage.ExtendedAttributeFactory;
import org.eventb.internal.ui.eventbeditor.editpage.IAttributeFactory;
import org.rodinp.core.RodinDBException;

public class ExtendsEventSection extends CComboSection {

	private static final IAttributeFactory factory = new ExtendedAttributeFactory();

	
	@Override
	String getLabel() {
		return "Extended";
	}

	@Override
	String getText() throws RodinDBException {
		return factory.getValue(element, null);

	}

	@Override
	void setData() {
		for(String value : factory.getPossibleValues(null, null))
			comboWidget.add(value);
	}

	@Override
	void setText(String text, IProgressMonitor monitor) throws RodinDBException {
		UIUtils.setStringAttribute(element, factory, text, null);
	}

}
