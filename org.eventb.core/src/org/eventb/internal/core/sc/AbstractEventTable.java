/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.sc.state.IAbstractEventTable;
import org.eventb.core.tool.state.IToolStateType;
import org.eventb.internal.core.tool.state.ToolState;

/**
 * @author Stefan Hallerstede
 *
 */
public class AbstractEventTable extends ToolState implements IAbstractEventTable {
	
	@Override
	public void makeImmutable() {
		super.makeImmutable();
		table = Collections.unmodifiableList(table);
	}

	private List<AbstractEventInfo> table;
	private final List<String> labels;
	private final HashSet<String> localVariables;

	public AbstractEventTable(int size) {
		table = new ArrayList<AbstractEventInfo>(size);
		labels = new ArrayList<String>(size);
		localVariables = new HashSet<String>(size * 6 + 1);
	}

	public IToolStateType<?> getStateType() {
		return STATE_TYPE;
	}

	public void putAbstractEventInfo(AbstractEventInfo info) throws CoreException {
		assert !table.contains(info);
		
		assertMutable();
		
		table.add(info);
		labels.add(info.getEventLabel());
			
		for (FreeIdentifier identifier : info.getVariables()) {
			localVariables.add(identifier.getName());
		}
		
	}

	public AbstractEventInfo getAbstractEventInfo(String label) {
		int index = labels.indexOf(label);
		
		return index == -1 ? null : table.get(index);
	}

	public boolean isLocalVariable(String name) {
		return localVariables.contains(name);
	}
	
	public List<AbstractEventInfo> getAbstractEventInfos() {
		return table;
	}

}
