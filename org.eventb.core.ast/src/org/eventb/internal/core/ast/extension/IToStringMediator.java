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
package org.eventb.internal.core.ast.extension;

import org.eventb.core.ast.Formula;
import org.eventb.internal.core.parser.AbstractGrammar;
import org.eventb.internal.core.parser.AbstractGrammar.DefaultToken;

/**
 * Common protocol for pretty-printing. Implementations allows building the
 * result of <code>toString()</code> methods of formulas.
 * 
 * @author Nicolas Beauger
 * @since 2.0
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IToStringMediator {

	/*
	 * Appends the image of the given token to the buffer.
	 */
	void append(DefaultToken token);

	/*
	 * Appends the given string to the buffer.
	 */
	void append(String string);
	
	/*
	 * Appends the image associated to the given token kind to the buffer. If
	 * the given token kind corresponds to an operator, we ask the grammar
	 * whether it should be surrounded by spaces. By default, it is not.
	 */
	void appendImage(int kind);
	
	/*
	 * Appends the image associated to the given token kind to the buffer,
	 * specifying whether it should be surrounded by spaces.
	 */
	void appendImage(int lexKind, boolean withSpaces);
	
	int getKind();
	
	void subPrint(Formula<?> child, boolean isRightOvr);

	void subPrintNoPar(Formula<?> child, boolean isRightOvr,
			String[] addedBoundNames);
	
	void subPrint(Formula<?> child, boolean isRightOvr,
			String[] addedBoundNames, boolean withTypesOvr);
	
	void subPrintWithPar(Formula<?> child);

	void forward(Formula<?> formula);
	
	void appendBoundIdent(int boundIndex);
	
	boolean isWithTypes();

	String[] getBoundNames();
	
	/*
	 * Returns the grammar associated to this pretty-printer.
	 */
	AbstractGrammar getGrammar();

}
