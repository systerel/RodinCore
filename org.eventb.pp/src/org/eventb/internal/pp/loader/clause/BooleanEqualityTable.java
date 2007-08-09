/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.loader.clause;

import java.util.HashMap;

import org.eventb.internal.pp.loader.formula.terms.TermSignature;

public class BooleanEqualityTable extends HashMap<TermSignature, Integer> {
	private static final long serialVersionUID = 2139618668777947328L;
	
	private int nextIdentifier;
	
	public BooleanEqualityTable(int nextIdentifier) {
		// do nothing
		this.nextIdentifier = nextIdentifier;
	}
	
	public int getNextLiteralIdentifier() {
		return nextIdentifier++;
	}
	
}
