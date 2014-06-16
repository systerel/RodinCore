/*******************************************************************************
 * Copyright (c) 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.sc.symbolTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eventb.core.sc.IMarkerDisplay;
import org.eventb.core.sc.state.IReservedNameTable;
import org.eventb.core.sc.state.ISymbolWarning;
import org.eventb.core.tool.IStateType;
import org.eventb.internal.core.tool.state.State;
import org.rodinp.core.RodinDBException;

/**
 * Simple implementation of the reserved name table.
 * 
 * @author Laurent Voisin
 */
public class ReservedNameTable extends State implements IReservedNameTable {

	private final Map<String, List<ISymbolWarning>> table;

	public ReservedNameTable() {
		this.table = new HashMap<String, List<ISymbolWarning>>();
	}

	@Override
	public IStateType<IReservedNameTable> getStateType() {
		return STATE_TYPE;
	}

	@Override
	public void add(String name, ISymbolWarning warning) {
		List<ISymbolWarning> list = table.get(name);
		if (list == null) {
			list = new ArrayList<ISymbolWarning>();
			table.put(name, list);
		}
		list.add(warning);
	}

	@Override
	public boolean isInConflict(String name) {
		return table.containsKey(name);
	}

	@Override
	public void issueWarningFor(String name, IMarkerDisplay display)
			throws RodinDBException {
		final List<ISymbolWarning> list = table.get(name);
		if (list == null) {
			return;
		}
		for (final ISymbolWarning warning : list) {
			warning.createConflictWarning(display);
		}
	}

	@Override
	public String toString() {
		return table.toString();
	}

}
