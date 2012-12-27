/*******************************************************************************
 * Copyright (c) 2010, 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.parser;

import org.eventb.internal.core.parser.GenParser.SyntaxError;

/**
 * Nud stands for 'null-denoted', in contrast to 'left-denoted' (led parsers).
 * 
 * @param <R>
 *            type of the parsed object.
 * @author Nicolas Beauger
 */
public interface INudParser<R> extends IParserPrinter<R> {

	/**
	 * Parses a null-denoted formula with the given parser context.
	 * <p>
	 * The given start position corresponds to the current token. The current
	 * token is that of a symbol associated with this parser.
	 * </p>
	 * <p>
	 * When the method returns, current token is the one that immediately
	 * follows parsed formula.
	 * </p>
	 * 
	 * @param pc
	 *            the parser context
	 * @return the parsed formula
	 * @throws SyntaxError
	 *             if the there is a syntax error
	 */
	SubParseResult<R> nud(ParserContext pc) throws SyntaxError;

}
