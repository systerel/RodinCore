/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc.symbolTable;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.internal.core.Util;
import org.eventb.internal.core.sc.Messages;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalParent;

/**
 * @author Stefan Hallerstede
 *
 */
public class AbstractConstantSymbolInfo extends ConstantSymbolInfo {

	public AbstractConstantSymbolInfo(
			String symbol, 
			String link, 
			IInternalElement element, 
			IAttributeType.String attribute, 
			String component) {
		super(symbol, link, element, attribute, component);
	}

	public void createSCElement(IInternalParent parent, IProgressMonitor monitor) throws CoreException {
		throw Util.newCoreException(Messages.symtab_cannotCreateAbstractConstant);
	}

}
