/*******************************************************************************
 * Copyright (c) 2008, 2010 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University of Southampton - initial API and implementation
 *     Systerel - published symbol factory
 *******************************************************************************/
package org.eventb.internal.core.sc.symbolTable;

import org.eventb.core.sc.IMarkerDisplay;
import org.eventb.core.sc.state.ISymbolInfo;
import org.rodinp.core.RodinDBException;

public interface ISymbolProblem {
	
	void createConflictError(ISymbolInfo<?,?> symbolInfo, IMarkerDisplay markerDisplay) 
	throws RodinDBException;
	
	void createConflictWarning(ISymbolInfo<?,?> symbolInfo, IMarkerDisplay markerDisplay) 
	throws RodinDBException;

}
