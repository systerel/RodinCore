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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eventb.core.ast.Formula;
import org.eventb.internal.core.parser.AbstractGrammar.OverrideException;
import org.eventb.internal.core.parser.GenParser.SyntaxError;

/**
 * @author Nicolas Beauger
 *
 */
public class SubParserRegistry {

	private static class KindParsers {
		private final List<ILedParser<Formula<?>>> ledParsers = new ArrayList<ILedParser<Formula<?>>>();
		private final List<INudParser<Formula<?>>> nudParsers = new ArrayList<INudParser<Formula<?>>>();

		public KindParsers() {
			// nothing to do
		}

		public void addLed(ILedParser ledParser) {
			if (!ledParsers.contains(ledParser)) {
				ledParsers.add(ledParser);
			}
		}

		public void addNud(INudParser ledParser) {
			if (!nudParsers.contains(ledParser)) {
				nudParsers.add(ledParser);
			}
		}

		public List<ILedParser<Formula<?>>> getLedParsers() {
			return ledParsers;
		}

		public List<INudParser<Formula<?>>> getNudParsers() {
			return nudParsers;
		}

		public boolean isEmpty() {
			return ledParsers.isEmpty() && nudParsers.isEmpty();
		}
	}

	private final Map<Integer, KindParsers> kindParsers = new HashMap<Integer, KindParsers>();
	
	// TODO move calls to subparser and remove method
	public int getOperatorTag(Token token) throws SyntaxError {
		return getFirstSubParser(token).getTag();
	}

	// FIXME remove
	private ISubParser getFirstSubParser(Token token) {
		final KindParsers parsers = kindParsers.get(token.kind);
		if (parsers == null) {
			return null;
		}
		final List<ILedParser<Formula<?>>> ledParsers = parsers.getLedParsers();
		if (!ledParsers.isEmpty()) {
			return ledParsers.get(0);
		}
		final List<INudParser<Formula<?>>> nudParsers = parsers.getNudParsers();
		if (!nudParsers.isEmpty()) {
			return nudParsers.get(0);
		}
		return null;
	}

	public boolean isOperator(Token token) {
		final KindParsers parsers = kindParsers.get(token.kind);
		return parsers != null && !parsers.isEmpty();
	}
	
	
	public List<INudParser<Formula<?>>> getNudParsers(Token token) {
		final KindParsers parsers = kindParsers.get(token.kind);
		if (parsers == null) {
			return Collections.emptyList();
		}
		return parsers.getNudParsers(); 
	}
	
	public ILedParser<Formula<?>> getLedParser(Token token) {
		final KindParsers parsers = kindParsers.get(token.kind);
		if (parsers == null || parsers.isEmpty()) {
			return null;
		}
		return parsers.getLedParsers().get(0); 
		// FIXME
		// when backtracking there will be several subparsers for one kind
	}
	
	public void addNud(int kind, INudParser subParser) throws OverrideException {
		final KindParsers parsers = fetchParsers(kind);
		parsers.addNud(subParser);
	}

	public void addLed(int kind, ILedParser subParser) throws OverrideException {
		final KindParsers parsers = fetchParsers(kind);
		parsers.addLed(subParser);
	}

	private KindParsers fetchParsers(int kind) {
		KindParsers parsers = kindParsers.get(kind);
		if (parsers == null) {
			parsers = new KindParsers();
			kindParsers.put(kind, parsers);
		}
		return parsers;
	}


	public void addReserved(int kind, INudParser subParser) {
		final KindParsers parsers = fetchParsers(kind);
		parsers.addNud(subParser);
	}

	public void addClosed(int openKind, INudParser subParser) throws OverrideException {
		addNud(openKind, subParser);
	}

}
