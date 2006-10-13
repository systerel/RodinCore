/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc.symbolTable;

import org.eventb.core.EventBAttributes;
import org.eventb.core.sc.symbolTable.ILabelSymbolInfo;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class LabelSymbolInfo 
	extends SymbolInfo 
	implements ILabelSymbolInfo {

	public LabelSymbolInfo(
			String symbol, 
			IRodinElement element, 
			String component) {
		super(symbol, element, component);
		
	}

	@Override
	public String getSymbolAttributeId() {
		return EventBAttributes.LABEL_ATTRIBUTE;
	}
}
