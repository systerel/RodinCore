/*******************************************************************************
 * Copyright (c) 2007 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.internal.core.version;

import org.eclipse.core.runtime.IConfigurationElement;
import org.rodinp.core.IFileElementType;
import org.rodinp.core.IRodinFile;

/**
 * @author Stefan Hallerstede
 *
 */
public class SortedConversionSheet extends SimpleConversionSheet {
	
	private final String order;

	public SortedConversionSheet(IConfigurationElement configElement, IFileElementType<IRodinFile> type) {
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
