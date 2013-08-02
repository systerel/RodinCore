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
import org.eventb.core.ast.extension.datatype2.IConstructorBuilder;
import org.eventb.core.ast.extension.datatype2.IDatatype2;
import org.eventb.core.ast.extension.datatype2.IDatatypeBuilder;

public class RecordDatatype {

	private static final IDatatype2 INSTANCE;
	static {
		final FormulaFactory ff = FormulaFactory.getDefault();
		final IDatatypeBuilder builder = ff.makeDatatypeBuilder("RD");
		final IConstructorBuilder rd = builder.addConstructor("rd");
		rd.addArgument("intDestr", ff.makeIntegerType());
		rd.addArgument("boolDestr", ff.makeBooleanType());
		INSTANCE = builder.finalizeDatatype();
	}

	public static IDatatype2 getInstance() {
		return INSTANCE;
	}

}