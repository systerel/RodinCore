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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eventb.internal.core.parser.AbstractGrammar.OverrideException;
import org.eventb.internal.core.parser.GenParser.SyntaxError;

/**
 * @author Nicolas Beauger
 *
 */
public class SubParserRegistry {

	private static class KindParsers {
		//		private final List<ISubParser> ledParsers = new ArrayList<ISubParser>();
		//		private final List<ISubParser> nudParsers = new ArrayList<ISubParser>();
		private final List<ISubParser> parsers = new ArrayList<ISubParser>();

		public KindParsers() {
			// nothing to do
		}
		
		//		public void addLed(ISubParser ledParser) {
		//			if (!ledParsers.contains(ledParser)) {
		//				ledParsers.add(ledParser);
		//			}
		//		}
		//
		//		public void addNud(ISubParser ledParser) {
		//			if (!nudParsers.contains(ledParser)) {
		//				nudParsers.add(ledParser);
		//			}
		//		}

		public void add(ISubParser parser) {
			if (!parsers.contains(parser)) {
				parsers.add(parser);
			}
		}

		//		public List<ISubParser> getLedParsers() {
		//			return ledParsers;
		//		}
		//		
		//		public List<ISubParser> getNudParsers() {
		//			return nudParsers;
		//		}

		public List<ISubParser> getParsers() {
			return parsers;
		}
	}

	private final Map<Integer, KindParsers> kindParsers = new HashMap<Integer, KindParsers>();
	
	// TODO move calls to subparser and remove method
	public int getOperatorTag(Token token) throws SyntaxError {
		return getSubParser(token).getTag();
	}

	public boolean isOperator(Token token) {
		final KindParsers parsers = kindParsers.get(token.kind);
		return parsers != null && !parsers.getParsers().isEmpty();
	}
	
	// TODO
	public ISubParser getSubParser(Token token) {
		final KindParsers parsers = kindParsers.get(token.kind);
		if (parsers == null || parsers.getParsers().isEmpty()) {
//			throw new SyntaxError("not an operator: " + token.val);
			return null;
		}
		return parsers.getParsers().get(0); 
		// FIXME
		// when backtracking there will be several subparsers for one kind
	}
	
	public void add(int kind, ISubParser subParser) throws OverrideException {
		final KindParsers parsers = fetchParsers(kind);
		parsers.add(subParser);
	}

	private KindParsers fetchParsers(int kind) {
		KindParsers parsers = kindParsers.get(kind);
		if (parsers == null) {
			parsers = new KindParsers();
			kindParsers.put(kind, parsers);
		}
		return parsers;
	}


	public void addReserved(int kind, ISubParser subParser) {
		final KindParsers parsers = fetchParsers(kind);
		parsers.add(subParser);
	}

	public void addClosed(int openKind, int closeKind, ISubParser subParser) throws OverrideException {
		add(openKind, subParser);
	}

}
