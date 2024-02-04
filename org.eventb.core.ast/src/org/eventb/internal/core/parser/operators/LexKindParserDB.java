/*******************************************************************************
 * Copyright (c) 2010, 2024 Systerel and others.
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eventb.core.ast.Formula;
import org.eventb.internal.core.lexer.Token;
import org.eventb.internal.core.parser.GenParser.OverrideException;
import org.eventb.internal.core.parser.ILedParser;
import org.eventb.internal.core.parser.INudParser;
import org.eventb.internal.core.parser.operators.ExternalViewUtils.Instantiator;

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

		public void addNud(INudParser<? extends Formula<?>> subParser) throws OverrideException {
			if (!nudParsers.isEmpty()) {
				throw new OverrideException(
						"Cannot add several nud parsers for one kind (nud backtracking is not supported)");
			}
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
	
	public INudParser<? extends Formula<?>> getNudParser(Token token) {
		final KindParsers parsers = kindParsers.get(token.kind);
		if (parsers == null) {
			return null;
		}
		final List<INudParser<? extends Formula<?>>> nudParsers = parsers.getNudParsers();
		if (nudParsers.isEmpty()) {
			return null;
		}
		return nudParsers.get(0); 
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

	public void redistribute(Instantiator<Integer, Integer> opKindInst) {
		remapKinds(opKindInst, kindParsers);
	}

	private static <T> void remapKinds(Instantiator<Integer, Integer> opKinInst, Map<Integer, T> map) {
		final Map<Integer, T> newMap = new HashMap<Integer, T>();
		for (Entry<Integer, T> entry : map.entrySet()) {
			final Integer newKind = opKinInst.instantiate(entry.getKey());
			newMap.put(newKind, entry.getValue());
		}
		map.clear();
		map.putAll(newMap);
	}

}
