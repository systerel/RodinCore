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

import java.util.HashMap;
import java.util.Map;

import org.eventb.core.ast.Formula;
import org.eventb.internal.core.parser.AbstractGrammar.OverrideException;
import org.eventb.internal.core.parser.GenParser.SyntaxError;

/**
 * @author Nicolas Beauger
 *
 */
public class SubParserRegistry {

	// maps operator token kind to formula tag
	private final Map<Integer, Integer> operatorTag = new HashMap<Integer, Integer>();
		
	// maps operator token kind to a subparser
	// TODO when backtracking there will be several subparsers for one kind
	private final Map<Integer, ISubParser> subParsers = new HashMap<Integer, ISubParser>();

	
	public int getOperatorTag(Token token) throws SyntaxError {
		final Integer tag = operatorTag.get(token.kind);
		if (tag == null) {
			throw new SyntaxError("not an operator: " + token.val);
		}
		return tag;
	}
	
	public ISubParser getSubParser(int kind) {
		return subParsers.get(kind);
	}
	
	public void add(int tag, int kind, ISubParser subParser) throws OverrideException {
		final Integer oldTag = operatorTag.put(kind, tag);
		if (oldTag != null) {
			operatorTag.put(kind, oldTag);
			throw new OverrideException("Trying to override operator " + kind);
		}

		final ISubParser oldSubParser = subParsers.put(kind, subParser);
		if (oldSubParser != null) {
			subParsers.put(kind, oldSubParser);
			throw new OverrideException("Trying to override sub-parser of " + kind);
		}

	}

	public void addReserved(int kind, ISubParser subParser) {
		subParsers.put(kind, subParser);
	}

	public void addClosed(int openKind, int closeKind, ISubParser subParser) {
		operatorTag.put(closeKind, Formula.NO_TAG);
		subParsers.put(openKind, subParser);
	}

}
