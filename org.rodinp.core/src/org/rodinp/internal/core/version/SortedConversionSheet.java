/*******************************************************************************
 * Copyright (c) 2007, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
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

	private static String T1 = 
		"\t\t\t<" + XSLConstants.XSL_SORT + " " + XSLConstants.XSL_LANG + "=\"" + XSLConstants.XSL_EN + "\" " + 
		XSLConstants.XSL_SELECT + "=\"" + XSLConstants.XSL_CURRENT_NAME + "\" " +
		XSLConstants.XSL_ORDER + "=\"";
	private static String T2 = "\"/>\n";
	
	@Override
	public void addSorter(StringBuffer document) {
		document.append(T1);
		document.append(order);
		document.append(T2);
	}

}
