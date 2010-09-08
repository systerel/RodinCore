/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.reasonerExtentionTests;

import static java.util.Arrays.asList;

import org.eventb.core.ast.extension.datatype.IArgument;
import org.eventb.core.ast.extension.datatype.IArgumentType;
import org.eventb.core.ast.extension.datatype.IConstructorMediator;
import org.eventb.core.ast.extension.datatype.IDatatypeExtension;
import org.eventb.core.ast.extension.datatype.ITypeConstructorMediator;
import org.eventb.core.ast.extension.datatype.ITypeParameter;

public class InductiveDatatype implements IDatatypeExtension {

	private static final String TYPE_NAME = "Induc";
	private static final String TYPE_IDENTIFIER = "Induc DT Id";
	
	private static final InductiveDatatype INSTANCE = new InductiveDatatype();
	
	private InductiveDatatype() {
		// singleton
	}
	
	public static InductiveDatatype getInstance() {
		return INSTANCE;
	}
	
	@Override
	public String getTypeName() {
		return TYPE_NAME;
	}

	@Override
	public String getId() {
		return TYPE_IDENTIFIER;
	}
	
	@Override
	public void addTypeParameters(ITypeConstructorMediator mediator) {
		mediator.addTypeParam("T");
	}

	@Override
	public void addConstructors(IConstructorMediator mediator) {
		final ITypeParameter prmT = mediator.getTypeParameter("T");
		final IArgumentType argTypeT = mediator.newArgumentType(prmT);
		final IArgumentType argTypeInducT = mediator.newArgumentTypeConstr(asList(argTypeT));
		
		final IArgument destr1 = mediator.newArgument("ind1_0", argTypeInducT);
		final IArgument destr2_0 = mediator.newArgument("ind2_0", argTypeInducT);
		final IArgument destr2_1 = mediator.newArgument("ind2_1", argTypeInducT);
		
		mediator.addConstructor("ind0", "IND_0");
		mediator.addConstructor("ind1", "IND_1", asList(destr1));
		mediator.addConstructor("ind2", "IND_2",
				asList(destr2_0, destr2_1));
	}

}