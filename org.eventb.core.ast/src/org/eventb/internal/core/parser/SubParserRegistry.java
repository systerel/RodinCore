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
import org.eventb.internal.core.lexer.Token;
import org.eventb.internal.core.parser.GenParser.OverrideException;

/**
 * @author Nicolas Beauger
 *
 */
public class SubParserRegistry {

	private static class KindParsers {
		private final List<ILedParser<? extends Formula<?>>> ledParsers = new ArrayList<ILedParser<? extends Formula<?>>>();
		private final List<INudParser<? extends Formula<?>>> nudParsers = new ArrayList<INudParser<? extends Formula<?>>>();

		public KindParsers() {
			// nothing to do
		}

		public void addLed(ILedParser<? extends Formula<?>> subParser) {
			if (!ledParsers.isEmpty()) {
				throw new IllegalArgumentException(
						"Cannot add several led parsers for one kind (led backtracking is not supported)");
			}
			ledParsers.add(subParser);
		}

		public void addNud(INudParser<? extends Formula<?>> subParser) {
			if (!nudParsers.contains(subParser)) {
				nudParsers.add(subParser);
			}
		}

		public List<ILedParser<? extends Formula<?>>> getLedParsers() {
			return ledParsers;
		}

		public List<INudParser<? extends Formula<?>>> getNudParsers() {
			return nudParsers;
		}

		public boolean isEmpty() {
			return ledParsers.isEmpty() && nudParsers.isEmpty();
		}
	}

	private final Map<Integer, KindParsers> kindParsers = new HashMap<Integer, KindParsers>();
	
	public List<INudParser<? extends Formula<?>>> getNudParsers(Token token) {
		final KindParsers parsers = kindParsers.get(token.kind);
		if (parsers == null) {
			return Collections.emptyList();
		}
		return parsers.getNudParsers(); 
	}
	
	public ILedParser<? extends Formula<?>> getLedParser(Token token) {
		final KindParsers parsers = kindParsers.get(token.kind);
		if (parsers == null || parsers.isEmpty()) {
			return null;
		}
		return parsers.getLedParsers().get(0); 
		// FIXME when backtracking there will be several subparsers for one kind
		// looks like backtracking is only required for nud parsers, could avoid it for led parsers.
	}
	
	public void addNud(int kind, INudParser<? extends Formula<?>> subParser) throws OverrideException {
		final KindParsers parsers = fetchParsers(kind);
		parsers.addNud(subParser);
	}

	public void addLed(int kind, ILedParser<? extends Formula<?>> subParser) throws OverrideException {
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

}
