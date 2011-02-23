/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.parser;

import java.util.Map;
import java.util.Map.Entry;

import org.eventb.internal.core.parser.ExternalViewUtils.Instantiator;

public class BracketCompactor {
	private final Map<Integer, Integer> closeOpen;

	public BracketCompactor(Map<Integer, Integer> closeOpen) {
		this.closeOpen = closeOpen;
	}
	
	public Brackets compact(Instantiator<Integer, Integer> opKindInst) {
		final int[] open = new int[closeOpen.size()];
		final int[] close = new int[closeOpen.size()];
		int i = 0;
		for (Entry<Integer, Integer> entry : closeOpen.entrySet()) {
			close[i] = opKindInst.instantiate(entry.getKey());
			open[i] = opKindInst.instantiate(entry.getValue());
			i++;
		}
		return new Brackets(open, close);
	}
}