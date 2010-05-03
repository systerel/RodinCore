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
import org.eventb.internal.core.parser.GenParser.ParserContext;
import org.eventb.internal.core.parser.GenParser.SyntaxError;

/**
 * @author Nicolas Beauger
 * 
 */
public interface ILedParser extends ISubParser {

	/**
	 * Parses a left-denoted formula from the given parser context. Current
	 * token is that just after the operator symbol associated with this parser
	 * (start of the right part).
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
	 * @param startPos
	 *            current position
	 * @return the parsed formula
	 * @throws SyntaxError
	 *             if the there is a syntax error
	 */
	Formula<?> led(Formula<?> left, ParserContext pc, int startPos)
			throws SyntaxError;

}
