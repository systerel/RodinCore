/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pog;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.pog.state.IMachineVariableTable;
import org.eventb.internal.core.state.State;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineVariableTable extends State implements IMachineVariableTable {
	
	final private ArrayList<FreeIdentifier> variables;
	final private ArrayList<FreeIdentifier> preservedVariables;
	final private HashSet<FreeIdentifier> hashSet;

	public MachineVariableTable(int size) {
		variables = new ArrayList<FreeIdentifier>(size);
		preservedVariables = new ArrayList<FreeIdentifier>(size);
		hashSet = new HashSet<FreeIdentifier>(size * 4 / 3 + 1);
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IState#getStateType()
	 */
	public String getStateType() {
		return STATE_TYPE;
	}

	public boolean contains(FreeIdentifier variable) {
		return hashSet.contains(variable);
	}

	public void add(FreeIdentifier variable, boolean preserved) {
		variables.add(variable);
		if (preserved)
			preservedVariables.add(variable);
		hashSet.add(variable);
	}
	
	public Iterator<FreeIdentifier> iterator() {
		return variables.iterator();
	}
	
	public ArrayList<FreeIdentifier> getPreservedVariables() {
		return preservedVariables;
	}

	public void trimToSize() {
		variables.trimToSize();
		preservedVariables.trimToSize();
	}

}
