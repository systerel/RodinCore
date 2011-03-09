/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pog;

import java.util.Arrays;
import java.util.List;

import org.eventb.core.ISCEvent;
import org.eventb.core.pog.state.IAbstractEventGuardList;
import org.eventb.core.pog.state.IAbstractEventGuardTable;
import org.eventb.core.tool.IStateType;
import org.eventb.internal.core.tool.state.State;

/**
 * @author Stefan Hallerstede
 *
 */
public class AbstractEventGuardList extends State implements IAbstractEventGuardList {

	@Override
	public String toString() {
		return abstractEventGuardTables.toString();
	}

	private List<IAbstractEventGuardTable> abstractEventGuardTables;
	
	private final int refinementType;
	
	private List<ISCEvent> abstractEvents;
	
	/* (non-Javadoc)
	 * @see org.eventb.core.state.IState#getStateType()
	 */
	@Override
	public IStateType<?> getStateType() {
		return STATE_TYPE;
	}

	public AbstractEventGuardList(
			final ISCEvent[] abstractEvents,
			final List<IAbstractEventGuardTable> abstractEventGuardTables) {
		
		assert abstractEvents.length == abstractEventGuardTables.size();
		
		this.abstractEvents = Arrays.asList(abstractEvents);
		
		this.abstractEventGuardTables = abstractEventGuardTables;
		
		if (abstractEventGuardTables.size() == 0)
			refinementType = INTRO;
		else if (abstractEventGuardTables.size() == 1)
			refinementType = SPLIT;
		else
			refinementType = MERGE;
	}

	@Override
	public List<IAbstractEventGuardTable> getAbstractEventGuardTables() {
		return abstractEventGuardTables;
	}

	@Override
	public int getRefinementType() {
		return refinementType;
	}
	
	@Override
	public List<ISCEvent> getAbstractEvents() {
		return abstractEvents;
	}

	@Override
	public ISCEvent getFirstAbstractEvent() {
		if (abstractEvents.size() == 0)
			return null;
		
		return abstractEvents.get(0);
	}

	@Override
	public IAbstractEventGuardTable getFirstAbstractEventGuardTable() {
		if (abstractEventGuardTables.size() == 0)
			return null;
		
		return abstractEventGuardTables.get(0);
	}

}
