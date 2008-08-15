package org.eventb.internal.core.sc.symbolTable;

import org.eventb.core.sc.IMarkerDisplay;
import org.eventb.core.sc.symbolTable.ISymbolInfo;
import org.rodinp.core.RodinDBException;

interface ISymbolProblem {
	
	void createConflictError(ISymbolInfo<?,?> symbolInfo, IMarkerDisplay markerDisplay) 
	throws RodinDBException;
	
	void createConflictWarning(ISymbolInfo<?,?> symbolInfo, IMarkerDisplay markerDisplay) 
	throws RodinDBException;

}
