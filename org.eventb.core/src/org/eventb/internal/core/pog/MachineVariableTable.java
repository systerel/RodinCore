/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.pog.state.IMachineVariableTable;
import org.eventb.core.tool.state.IToolStateType;
import org.eventb.internal.core.tool.state.ToolState;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineVariableTable extends ToolState implements IMachineVariableTable {
	
	final private ArrayList<FreeIdentifier> variables;
	private List<FreeIdentifier> preservedVariables;
	final private Set<FreeIdentifier> cache;

	public MachineVariableTable(int size) {
		variables = new ArrayList<FreeIdentifier>(size);
		preservedVariables = new ArrayList<FreeIdentifier>(size);
		cache = new HashSet<FreeIdentifier>(size * 4 / 3 + 1);
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IState#getStateType()
	 */
	public IToolStateType<?> getStateType() {
		return STATE_TYPE;
	}

	public boolean contains(FreeIdentifier variable) {
		return cache.contains(variable);
	}

	public void add(FreeIdentifier variable, boolean preserved) {
		variables.add(variable);
		if (preserved)
			preservedVariables.add(variable);
		cache.add(variable);
	}
	
	public Iterator<FreeIdentifier> iterator() {
		return variables.iterator();
	}
	
	public List<FreeIdentifier> getPreservedVariables() {
		return preservedVariables;
	}

	@Override
	public void makeImmutable() {
		variables.trimToSize();
		preservedVariables = Collections.unmodifiableList(preservedVariables);
	}

}
