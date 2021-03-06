/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.pog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.pog.state.IMachineVariableTable;
import org.eventb.core.tool.IStateType;
import org.eventb.internal.core.tool.state.State;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineVariableTable extends State implements IMachineVariableTable {
	
	private List<FreeIdentifier> variables;
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
	@Override
	public IStateType<?> getStateType() {
		return STATE_TYPE;
	}

	@Override
	public boolean contains(FreeIdentifier variable) throws CoreException {
		assertImmutable();
		return cache.contains(variable);
	}

	public void add(FreeIdentifier variable, boolean preserved) throws CoreException {
		assertMutable();
		variables.add(variable);
		if (preserved)
			preservedVariables.add(variable);
		cache.add(variable);
	}
	
	@Override
	public List<FreeIdentifier> getPreservedVariables() throws CoreException {
		assertImmutable();
		return preservedVariables;
	}

	@Override
	public void makeImmutable() {
		variables = Collections.unmodifiableList(variables);
		preservedVariables = Collections.unmodifiableList(preservedVariables);
		super.makeImmutable();
	}

	@Override
	public List<FreeIdentifier> getVariables() throws CoreException {
		assertImmutable();
		return variables;
	}

}
