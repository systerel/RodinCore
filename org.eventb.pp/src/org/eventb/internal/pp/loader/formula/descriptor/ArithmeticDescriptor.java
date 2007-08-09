/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.loader.formula.descriptor;

import org.eventb.internal.pp.loader.predicate.IContext;

public class ArithmeticDescriptor extends LiteralDescriptor {

	public ArithmeticDescriptor(IContext context) {
		super(context);
	}

	@Override
	public String toString() {
		return "A";
	}


}
