/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.loader.formula.descriptor;

import java.util.List;

import org.eventb.internal.pp.core.elements.Sort;
import org.eventb.internal.pp.loader.predicate.IContext;
import org.eventb.internal.pp.loader.predicate.IIntermediateResult;

public class EqualityDescriptor extends LiteralDescriptor {

	private Sort sort;
	
	public Sort getSort() {
		return sort;
	}

	public EqualityDescriptor(IContext context, Sort sort) {
		super(context);
		this.sort = sort;
	}

	public EqualityDescriptor(IContext context, List<IIntermediateResult> termList, Sort sort) {
		super(context, termList);
		this.sort = sort;
	}
	
	@Override
	public String toString() {
		return "E" + sort.toString();
	}

}
