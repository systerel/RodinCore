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
package org.eventb.internal.pp.loader.formula;

import org.eventb.internal.pp.core.elements.PredicateTable;
import org.eventb.internal.pp.core.elements.terms.VariableTable;
import org.eventb.internal.pp.loader.clause.BooleanEqualityTable;

public class ClauseContext extends AbstractContext {
	private int startOffset;
	private int endOffset;
	private VariableTable variableTable;
	private BooleanEqualityTable booleanTable;
	private PredicateTable predicateTable;
	
	public ClauseContext(VariableTable variableTable, BooleanEqualityTable booleanTable,
			PredicateTable predicateTable) {
		this.variableTable = variableTable;
		this.booleanTable = booleanTable;
		this.predicateTable = predicateTable;
	}
	
	public int getStartOffset() {
		return startOffset;
	}
	
	void setStartOffset(int startOffset) {
		this.startOffset = startOffset;
	}
	
	public int getEndOffset() {
		return endOffset;
	}
	
	void setEndOffset(int endOffset) {
		this.endOffset = endOffset;
	}
	
	public VariableTable getVariableTable() {
		return variableTable;
	}
	
	public BooleanEqualityTable getBooleanTable() {
		return booleanTable;
	}
	
	public PredicateTable getPredicateTable() {
		return predicateTable;
	}
	
}
