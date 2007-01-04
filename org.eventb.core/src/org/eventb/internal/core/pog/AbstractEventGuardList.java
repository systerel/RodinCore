/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pog;

import java.util.List;

import org.eventb.core.ISCEvent;
import org.eventb.core.pog.state.IAbstractEventGuardList;
import org.eventb.core.pog.state.IAbstractEventGuardTable;
import org.eventb.internal.core.tool.state.ToolState;

/**
 * @author Stefan Hallerstede
 *
 */
public class AbstractEventGuardList extends ToolState implements IAbstractEventGuardList {

	private final IAbstractEventGuardTable[] abstractEventGuardTables;
	
	private final int refinementType;
	
	private final ISCEvent[] abstractEvents;
	
	/* (non-Javadoc)
	 * @see org.eventb.core.state.IState#getStateType()
	 */
	public String getStateType() {
		return STATE_TYPE;
	}

	public AbstractEventGuardList(
			final ISCEvent[] abstractEvents,
			final List<IAbstractEventGuardTable> abstractEventGuardTables) {
		
		assert abstractEvents.length == abstractEventGuardTables.size();
		
		this.abstractEvents = abstractEvents;
		
		this.abstractEventGuardTables = abstractEventGuardTables.toArray(
				new IAbstractEventGuardTable[abstractEventGuardTables.size()]);
		
		if (abstractEventGuardTables.size() == 0)
			refinementType = INTRO;
		else if (abstractEventGuardTables.size() == 1)
			refinementType = SPLIT;
		else
			refinementType = MERGE;
	}

	public IAbstractEventGuardTable[] getAbstractEventGuardTables() {
		IAbstractEventGuardTable[] agt = new IAbstractEventGuardTable[abstractEventGuardTables.length];
		System.arraycopy(abstractEventGuardTables, 0, agt, 0, abstractEventGuardTables.length);
		return agt;
	}

	public int getRefinementType() {
		return refinementType;
	}
	
	public ISCEvent[] getAbstractEvents() {
		ISCEvent[] ae = new ISCEvent[abstractEvents.length];
		System.arraycopy(abstractEvents, 0, ae, 0, abstractEvents.length);
		return ae;
	}

	public ISCEvent getFirstAbstractEvent() {
		if (abstractEvents.length == 0)
			return null;
		
		return abstractEvents[0];
	}

	public IAbstractEventGuardTable getFirstAbstractEventGuardTable() {
		if (abstractEventGuardTables.length == 0)
			return null;
		
		return abstractEventGuardTables[0];
	}

}
