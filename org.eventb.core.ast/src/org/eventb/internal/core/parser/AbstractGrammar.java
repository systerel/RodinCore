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

/**
 * @author Nicolas Beauger
 *
 */
public abstract class AbstractGrammar {

	static int _EOF;
	static int _LPAR;
	static int _RPAR;
	static int _IDENT;
	static int _INTLIT;
	static int _COMMA;

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

	/**
	 * Initialises tokens, parsers and operator relationships.
	 * <p>
	 * Subclasses are expected to override and call this method first.
	 * </p>
	 */
	// TODO split into several init methods, one for each data (?)
	public void init() {
		_EOF = tokens.reserved();
		_LPAR = tokens.getOrAdd("(");
		_RPAR = tokens.getOrAdd(")");
		_COMMA = tokens.getOrAdd(",");
		try {
			_INTLIT = addReservedSubParser(Parsers.INTLIT_SUBPARSER);
			_IDENT = addReservedSubParser(Parsers.FREE_IDENT_SUBPARSER);
			addClosedSugar(_LPAR, _RPAR);
		} catch (OverrideException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

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
	
	private int addReservedSubParser(INudParser subParser)
			throws OverrideException {
		final int kind = tokens.reserved();
		subParsers.addReserved(kind, subParser);
		return kind;
	}
	
	private void addClosedSugar(int openKind, int closeKind)
			throws OverrideException {
		subParsers.addClosed(_LPAR, Parsers.CLOSED_SUGAR);
	}
	
	protected void addLiteralOperator(String token, int tag,
			INudParser subParser) throws OverrideException {
		final int kind = tokens.getOrAdd(token);
		subParsers.addNud(kind, subParser);
	}

	public int getEOF() {
		return _EOF;
	}
	
	public int getIDENT() {
		return _IDENT;
	}
	
	public int getINTLIT() {
		return _INTLIT;
	}
	

}
