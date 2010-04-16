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
public interface ISubParser {

	Formula<?> led(Formula<?> left, ParserContext pc) throws SyntaxError;

	Formula<?> nud(ParserContext pc) throws SyntaxError;
}
