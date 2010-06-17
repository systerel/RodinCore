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
package org.eventb.core.ast.extension.samples;

import java.util.Arrays;

import org.eventb.core.ast.extension.datatype.IConstructorMediator;
import org.eventb.core.ast.extension.datatype.IDatatypeExtension;
import org.eventb.core.ast.extension.datatype.IDestructorMediator;
import org.eventb.core.ast.extension.datatype.ITypeConstructorMediator;
import org.eventb.core.ast.extension.datatype.ITypeParameter;

/**
 * @author Nicolas Beauger
 * 
 */
public class ListType implements IDatatypeExtension {

	private static final String TYPE_NAME = "List";
	private static final String TYPE_IDENTIFIER = "List Id";
	private static final String GROUP_IDENTIFIER = "List Group";
	
	
	public String getTypeName() {
		return TYPE_NAME;
	}

	public String getId() {
		return TYPE_IDENTIFIER;
	}
	
	public String getGroupId() {
		return GROUP_IDENTIFIER;
	}

	public void addTypeParameters(ITypeConstructorMediator mediator) {
		mediator.addTypeParam("S");			
	}

	public void addConstructors(IConstructorMediator mediator) {
		mediator.addConstructor("nil", "NIL", GROUP_IDENTIFIER);
		final ITypeParameter typeS = mediator.getTypeParameter("S");
		final ITypeParameter listS = mediator.newTypeConstructor(typeS);
		mediator.addConstructor("cons", "CONS", GROUP_IDENTIFIER, Arrays
				.asList(typeS, listS));
	}

	public void addDestructors(IDestructorMediator mediator) {
		final ITypeParameter typeS = mediator.getTypeParameter("S");
		mediator.addDestructor("head", "HEAD", typeS);
		final ITypeParameter listS = mediator.newTypeConstructor(typeS);
		mediator.addDestructor("tail", "TAIL", listS);
	}

}
