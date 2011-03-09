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

import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.extension.datatype.IArgument;
import org.eventb.core.ast.extension.datatype.IArgumentType;
import org.eventb.core.ast.extension.datatype.IConstructorMediator;
import org.eventb.core.ast.extension.datatype.IDatatypeExtension;
import org.eventb.core.ast.extension.datatype.ITypeConstructorMediator;

public class SimpleDatatype implements IDatatypeExtension {

	private static final String TYPE_NAME = "SD";
	private static final String TYPE_IDENTIFIER = "Simple DT Id";
	
	private static final SimpleDatatype INSTANCE = new SimpleDatatype();
	
	private SimpleDatatype() {
		// singleton
	}
	
	public static SimpleDatatype getInstance() {
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
		final IArgument destr1 = mediator.newArgument("destr1", intArgType);
		final IArgument destr2_0 = mediator.newArgument("destr2_0", intArgType);
		final IArgument destr2_1 = mediator.newArgument("destr2_1", intArgType);
		
		mediator.addConstructor("cons0", "CONS_0");
		mediator.addConstructor("cons1", "CONS_1", asList(destr1));
		mediator.addConstructor("cons2", "CONS_2",
				asList(destr2_0, destr2_1));
	}

}