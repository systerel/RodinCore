/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.loader.formula.descriptor;

import java.util.List;

import org.eventb.internal.pp.loader.predicate.IContext;
import org.eventb.internal.pp.loader.predicate.IIntermediateResult;

public class EquivalenceClauseDescriptor extends IndexedDescriptor {

	public EquivalenceClauseDescriptor(IContext context, List<IIntermediateResult> termList, int index) {
		super(context, termList, index);
	}

	public EquivalenceClauseDescriptor(IContext context, int index) {
		super(context, index);
	}

	@Override
	public String toString() {
		return "Le"+index;
	}

//	@Override
//	public int hashCode() {
//		return super.hashCode();
//	}
//	
//	@Override
//	public boolean equals(Object obj) {
//		if (obj instanceof EquivalenceClauseDescriptor) {
//			EquivalenceClauseDescriptor temp = (EquivalenceClauseDescriptor) obj;
//			return super.equals(temp);
//		}
//		return false;
//	}
}
