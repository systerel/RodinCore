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

import org.eventb.core.ast.BooleanType;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.extension.datatype.IArgument;
import org.eventb.core.ast.extension.datatype.IArgumentType;
import org.eventb.core.ast.extension.datatype.IConstructorMediator;
import org.eventb.core.ast.extension.datatype.IDatatypeExtension;
import org.eventb.core.ast.extension.datatype.ITypeConstructorMediator;

public class RecordDatatype implements IDatatypeExtension {

	private static final String TYPE_NAME = "RD";
	private static final String TYPE_IDENTIFIER = "Record DT Id";
	
	private static final RecordDatatype INSTANCE = new RecordDatatype();
	
	private RecordDatatype() {
		// singleton
	}
	
	public static RecordDatatype getInstance() {
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
		// no type parameter			
	}

	@Override
	public void addConstructors(IConstructorMediator mediator) {
		
		final IntegerType intType = mediator.makeIntegerType();
		final IArgumentType intArgType = mediator.newArgumentType(intType);
		final BooleanType booleanType = mediator.makeBooleanType();
		final IArgumentType boolArgType = mediator.newArgumentType(booleanType);
		
		final IArgument intArg = mediator.newArgument("intDestr", intArgType);
		final IArgument boolArg = mediator.newArgument("boolDestr", boolArgType);

		mediator.addConstructor("rd", "rdId", asList(intArg, boolArg));
	}

}