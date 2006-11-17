/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.sc.symbolTable;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.sc.state.IEventRefinesInfo;

/**
 * @author Stefan Hallerstede
 *
 */
public interface IEventSymbolInfo extends ISymbolInfo {

	boolean isInherited();
	
	void setInherited() throws CoreException;
	
	void setRefinesInfo(IEventRefinesInfo refinesInfo);
	
	IEventRefinesInfo getRefinesInfo();

}
