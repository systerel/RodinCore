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
package org.eventb.internal.pp.loader.clause;

import java.util.HashMap;
import java.util.Map;

import org.eventb.internal.pp.loader.formula.terms.TermSignature;

public class BooleanEqualityTable {
	
	private final Map<TermSignature, Integer> map = new HashMap<TermSignature, Integer>();
	private int nextIdentifier;
	
	public BooleanEqualityTable(int nextIdentifier) {
		// do nothing
		this.nextIdentifier = nextIdentifier;
	}
	
	public int getIntegerForTermSignature(TermSignature sig) {
		int result;
		if (map.containsKey(sig)) result = map.get(sig);
		else {
			result = nextIdentifier++;
			map.put(sig, result);
		}
		return result;
	}
}
