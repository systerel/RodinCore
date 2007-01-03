/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc.symbolTable;

import org.rodinp.core.IInternalElement;

/**
 * @author Stefan Hallerstede
 *
 */
public class ConcreteVariableSymbolInfo extends MachineVariableSymbolInfo {

	public ConcreteVariableSymbolInfo(
			String symbol, 
			IInternalElement element, 
			org.rodinp.core.IAttributeType.String attribute, 
			String component) {
		super(symbol, false, element, attribute, component);
	}

}
