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
package org.eventb.internal.core.ast.extension;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.internal.core.parser.IParserPrinter;


/**
 * @author Nicolas Beauger
 * @since 2.0
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IToStringMediator {

	void append(String string);
	
	void appendImage(int kind);

	<T extends Formula<?>> void subPrint(T child, boolean isRight);

	<T extends Formula<?>> void forward(T child, boolean withTypes);

	<T extends Formula<?>> void subPrint(T child, boolean isRight, BoundIdentDecl[] boundDecls);
	
	<T extends Formula<?>> void subPrint(T child, boolean isRightOvr,
			BoundIdentDecl[] boundDecls, IParserPrinter<T> parser);
	
	<T extends Formula<?>> void forward(T child);
	
	FormulaFactory getFactory();

	void appendOperator();

	void appendBoundIdent(int boundIndex);
	
	boolean isWithTypes();
}
