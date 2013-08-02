/*******************************************************************************
 * Copyright (c) 2010, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.reasonerExtentionTests;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.datatype2.IConstructorBuilder;
import org.eventb.core.ast.extension.datatype2.IDatatype2;
import org.eventb.core.ast.extension.datatype2.IDatatypeBuilder;

public class SimpleDatatype {

	private static final IDatatype2 INSTANCE;
	static {
		final FormulaFactory ff = FormulaFactory.getDefault();
		final Type integerType = ff.makeIntegerType();
		final IDatatypeBuilder builder = ff.makeDatatypeBuilder("SD");
		builder.addConstructor("cons0");
		final IConstructorBuilder cons1 = builder.addConstructor("cons1");
		cons1.addArgument("destr1", integerType);
		final IConstructorBuilder cons2 = builder.addConstructor("cons2");
		cons2.addArgument("destr2_0", integerType);
		cons2.addArgument("destr2_1", integerType);
		INSTANCE = builder.finalizeDatatype();
	}

	public static IDatatype2 getInstance() {
		return INSTANCE;
	}

}