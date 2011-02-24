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
package org.eventb.internal.core.parser;

import org.eventb.core.ast.Formula;
import org.eventb.internal.core.parser.GenParser.SyntaxError;

/**
 * Led stands for 'left-denoted', in contrast to 'null-denoted' (nud parsers).
 * 
 * @param <R>
 *            type of the parsed object.
 * @author Nicolas Beauger
 */
public interface ILedParser<R> extends IParserPrinter<R> {

	/**
	 * Parses a left-denoted formula from the given parser context.
	 * <p>
	 * Current token is that of the operator symbol associated with this
	 * parser.
	 * </p>
	 * <p>
	 * When the method returns, current token is the one that immediately
	 * follows parsed formula.
	 * </p>
	 * 
	 * @param left
	 *            an already parsed formula that occurs at the left of the
	 *            operator symbol.
	 * @param pc
	 *            current parser context;
	 * @return the parsed formula
	 * @throws SyntaxError
	 *             if the there is a syntax error
	 */
	SubParseResult<R> led(Formula<?> left, ParserContext pc)
			throws SyntaxError;

}
