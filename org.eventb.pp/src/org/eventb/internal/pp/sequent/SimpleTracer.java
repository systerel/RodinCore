/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.pp.sequent;

import java.util.Collections;
import java.util.List;

import org.eventb.core.ast.Predicate;
import org.eventb.pp.ITracer;

public class SimpleTracer implements ITracer {
	private final Predicate hypothesis;
	private final boolean goalNeeded;

	public SimpleTracer(InputPredicate ip) {
		if (ip.isGoal) {
			this.hypothesis = null;
			this.goalNeeded = true;
		} else {
			this.hypothesis = ip.originalPredicate;
			this.goalNeeded = false;
		}
	}

	public List<Predicate> getNeededHypotheses() {
		if (hypothesis != null) {
			return Collections.singletonList(hypothesis);
		}
		return Collections.emptyList();
	}

	public boolean isGoalNeeded() {
		return goalNeeded;
	}
}