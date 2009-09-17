/*******************************************************************************
 * Copyright (c) 2008 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc.symbolTable;

import org.eventb.core.sc.IMarkerDisplay;
import org.eventb.core.sc.state.ISymbolInfo;
import org.rodinp.core.RodinDBException;

interface ISymbolProblem {
	
	void createConflictError(ISymbolInfo<?,?> symbolInfo, IMarkerDisplay markerDisplay) 
	throws RodinDBException;
	
	void createConflictWarning(ISymbolInfo<?,?> symbolInfo, IMarkerDisplay markerDisplay) 
	throws RodinDBException;

}
