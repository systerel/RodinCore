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
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.datatype.IConstructorBuilder;
import org.eventb.core.ast.datatype.IDatatype;
import org.eventb.core.ast.datatype.IDatatypeBuilder;

public class InductiveDatatype {

	private static final IDatatype INSTANCE;
	static {
		final FormulaFactory ff = FormulaFactory.getDefault();
		final GivenType tyT = ff.makeGivenType("T");
		final GivenType tyInduc = ff.makeGivenType("Induc");
		final IDatatypeBuilder builder = ff.makeDatatypeBuilder("Induc", tyT);
		builder.addConstructor("ind0");
		final IConstructorBuilder ind1 = builder.addConstructor("ind1");
		ind1.addArgument("ind1_0", tyInduc);
		final IConstructorBuilder ind2 = builder.addConstructor("ind2");
		ind2.addArgument("ind2_0", tyInduc);
		ind2.addArgument("ind2_1", tyInduc);
		INSTANCE = builder.finalizeDatatype();
	}

	public static IDatatype getInstance() {
		return INSTANCE;
	}

}