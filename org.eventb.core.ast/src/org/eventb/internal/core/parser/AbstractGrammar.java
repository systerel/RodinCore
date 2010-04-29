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

import org.eventb.internal.core.parser.GenParser.SyntaxError;
import org.eventb.internal.core.parser.Parsers.IdentListParser;

/**
 * @author Nicolas Beauger
 *
 */
public abstract class AbstractGrammar {

	protected static class SyntaxCompatibleError extends SyntaxError {

		private static final long serialVersionUID = -6230478311681172354L;

		public SyntaxCompatibleError(String reason) {
			super(reason);
		}
	}

	public static class OverrideException extends Exception {
	
		private static final long serialVersionUID = -1281802568424261959L;
	
		public OverrideException(String reason) {
			super(reason);
		}
	}

	protected final IndexedSet<String> tokens = new IndexedSet<String>();
	
	private final SubParserRegistry subParsers = new SubParserRegistry();
	
	protected final OperatorRegistry opRegistry = new OperatorRegistry();
	
	public OperatorRegistry getOperatorRegistry() {
		return opRegistry;
	}
	
	public int getOperatorTag(Token token) throws SyntaxError {
		return subParsers.getOperatorTag(token);
	}
	
	public boolean isOperator(Token token) {
		return subParsers.isOperator(token);
	}
	
	public IndexedSet<String> getTokens() {
		return tokens;
	}

	// TODO split into several init methods, one for each data
	public abstract void init();

	public INudParser getNudParser(Token token) {
		return subParsers.getNudParser(token);
	}
	
	public ILedParser getLedParser(Token token) {
		return subParsers.getLedParser(token);
	}
	
	protected void addOperator(String token, int tag, String operatorId, String groupId,
			INudParser subParser) throws OverrideException {
		opRegistry.addOperator(tag, operatorId, groupId);
		final int kind = tokens.getOrAdd(token);
		subParsers.addNud(kind, subParser);
	}
	
	protected void addOperator(String token, int tag, String operatorId, String groupId,
			ILedParser subParser) throws OverrideException {
		opRegistry.addOperator(tag, operatorId, groupId);
		final int kind = tokens.getOrAdd(token);
		subParsers.addLed(kind, subParser);
	}
	
	protected int addReservedSubParser(INudParser subParser)
			throws OverrideException {
		final int kind = tokens.reserved();
		subParsers.addReserved(kind, subParser);
		return kind;
	}
	
	protected void addClosedSugar(int openKind, int closeKind)
			throws OverrideException {
		subParsers.addClosed(openKind, new Parsers.ClosedSugar(closeKind));
	}
	
	protected void addLiteralOperator(String token, int tag,
			INudParser subParser) throws OverrideException {
		final int kind = tokens.getOrAdd(token);
		subParsers.addNud(kind, subParser);
	}

	protected void addQuantifiedOperator(String token, String identSeparator,
			String endList, int tag, String operatorId, String groupId)
			throws OverrideException {
		final int identSepKind = tokens.getOrAdd(identSeparator);
		final int endListKind = tokens.getOrAdd(endList);
		final IdentListParser quantIdentListParser = new IdentListParser(
				identSepKind, endListKind);
		final Parsers.QuantifiedPredicateParser quantParser = new Parsers.QuantifiedPredicateParser(
				tag, quantIdentListParser);
		addOperator(token, tag, operatorId, groupId, quantParser);
	}

}
