/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
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
public final class ConversionSheetFactory {

	private static final String SORTED_SHEET = "sorted";
	private static final String SIMPLE_SHEET = "simple";

	public static ConversionSheet makeConversionSheet(
			IConfigurationElement configElement, 
			IFileElementType<IRodinFile> type) {
		
		String sheetType = configElement.getName();
		
		ConversionSheet sheet = null;
		
		if (sheetType.equals(SIMPLE_SHEET)) {
			sheet = new SimpleConversionSheet(configElement, type);
		} else if (sheetType.equals(SORTED_SHEET)) {
			sheet = new SortedConversionSheet(configElement, type);
		} else {
			throw new IllegalStateException("Unknown type of conversion: " + sheetType);
		}
		
		sheet.checkBundle(type.toString(), sheet);	
		return sheet;

	}

}
