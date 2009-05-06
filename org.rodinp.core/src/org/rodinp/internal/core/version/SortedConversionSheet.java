/*******************************************************************************
 * Copyright (c) 2007, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     Systerel - used XSLWriter
 *******************************************************************************/
package org.rodinp.internal.core.version;

import org.eclipse.core.runtime.IConfigurationElement;
import org.rodinp.core.IInternalElementType;

/**
 * @author Stefan Hallerstede
 *
 */
public class SortedConversionSheet extends SimpleConversionSheet {
	
	private final String order;

	public SortedConversionSheet(IConfigurationElement configElement,
			IInternalElementType<?> type) {
		super(configElement, type);
		order = configElement.getAttribute("order");
	}

	@Override
	public boolean hasSorter() {
		return true;
	}

	@Override
	public void addSorter(XSLWriter writer) {
		writer.sort(XSLConstants.XSL_EN, XSLConstants.XSL_CURRENT_NAME, order);
	}

}
