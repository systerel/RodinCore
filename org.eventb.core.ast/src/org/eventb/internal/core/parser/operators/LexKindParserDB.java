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
package org.eventb.internal.core.parser.operators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eventb.core.ast.Formula;
import org.eventb.internal.core.lexer.Token;
import org.eventb.internal.core.parser.GenParser.OverrideException;
import org.eventb.internal.core.parser.operators.ExternalViewUtils.Instantiator;
import org.eventb.internal.core.parser.ILedParser;
import org.eventb.internal.core.parser.INudParser;

/**
 * @author Nicolas Beauger
 *
 */
public class LexKindParserDB {

	private static class KindParsers {
		private final List<ILedParser<? extends Formula<?>>> ledParsers = new ArrayList<ILedParser<? extends Formula<?>>>();
		private final List<INudParser<? extends Formula<?>>> nudParsers = new ArrayList<INudParser<? extends Formula<?>>>();

		public KindParsers() {
			// nothing to do
		}

		public void addLed(ILedParser<? extends Formula<?>> subParser) throws OverrideException {
			if (!ledParsers.isEmpty()) {
				throw new OverrideException(
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
		if (parsers == null) {
			return null;
		}
		final List<ILedParser<? extends Formula<?>>> ledParsers = parsers
				.getLedParsers();
		if (ledParsers.isEmpty()) {
			return null;
		}
		return ledParsers.get(0);
	}
	
	public void addNud(int kind, INudParser<? extends Formula<?>> subParser) {
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

	public void redistribute(Instantiator<Integer, Integer> opKindInst) {
		final Map<Integer, KindParsers> newKindParsers = new HashMap<Integer, KindParsers>();
		for (Entry<Integer, KindParsers> entry : kindParsers.entrySet()) {
			final Integer newKind = opKindInst.instantiate(entry.getKey());
			newKindParsers.put(newKind, entry.getValue());
		}
		kindParsers.clear();
		kindParsers.putAll(newKindParsers);
	}

}
