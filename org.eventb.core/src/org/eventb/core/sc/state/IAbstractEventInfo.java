/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.sc.state;

import java.util.List;

import org.eventb.core.ISCEvent;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.sc.symbolTable.IEventSymbolInfo;

/**
 * @author Stefan Hallerstede
 *
 */
public interface IAbstractEventInfo extends Comparable {

	ISCEvent getEvent();
	String getEventLabel();
	
	FreeIdentifier getIdentifier(String name);
	FreeIdentifier[] getIdentifiers();
	
	Predicate[] getGuards();
	Assignment[] getActions();
	
	void setInherited(IEventSymbolInfo eventSymbolInfo);
	IEventSymbolInfo getInherited();
	
	boolean isRefined();
	
	void addMergeSymbolInfo(IEventSymbolInfo symbolInfo);
	void addSplitSymbolInfo(IEventSymbolInfo symbolInfo);
	
	List<IEventSymbolInfo> getMergeSymbolInfos();
	List<IEventSymbolInfo> getSplitSymbolInfos();
	
	void setRefineError(boolean value);
	boolean hasRefineError();

}
