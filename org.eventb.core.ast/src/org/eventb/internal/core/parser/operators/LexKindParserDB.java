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

import java.util.HashMap;
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

	private final Map<Integer, INudParser<? extends Formula<?>>> nudParsers = new HashMap<>();
	private final Map<Integer, ILedParser<? extends Formula<?>>> ledParsers = new HashMap<>();

	public INudParser<? extends Formula<?>> getNudParser(Token token) {
		return nudParsers.get(token.kind);
	}

	public ILedParser<? extends Formula<?>> getLedParser(Token token) {
		return ledParsers.get(token.kind);
	}

	public void addNud(int kind, INudParser<? extends Formula<?>> subParser) throws OverrideException {
		var old = nudParsers.put(kind, subParser);
		if (old != null) {
			throw new OverrideException(
					"Cannot add several nud parsers for one kind (nud backtracking is not supported)");
		}
	}

	public void addLed(int kind, ILedParser<? extends Formula<?>> subParser) throws OverrideException {
		var old = ledParsers.put(kind, subParser);
		if (old != null) {
			throw new OverrideException(
					"Cannot add several led parsers for one kind (led backtracking is not supported)");
		}
	}

	public void redistribute(Instantiator<Integer, Integer> opKindInst) {
		remapKinds(opKindInst, nudParsers);
		remapKinds(opKindInst, ledParsers);
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
