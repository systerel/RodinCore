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

import static org.eventb.internal.core.parser.OperatorRegistry.GROUP0;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.LanguageVersion;
import org.eventb.core.ast.extension.CycleError;
import org.eventb.internal.core.lexer.Token;
import org.eventb.internal.core.parser.GenParser.OverrideException;
import org.eventb.internal.core.parser.OperatorRegistry.OperatorRelationship;

/**
 * @author Nicolas Beauger
 *
 */
public abstract class AbstractGrammar {

	private static final String EOF_ID = "End of File";
	private static final String NOOP_ID = "No Operator";
	private static final String OPEN_ID = "Open";
	private static final String IDENT_IMAGE = "an identifier";
	private static final String INTLIT_IMAGE = "an integer literal";

	public static int _EOF;
	static int _NOOP;
	static int _OPEN;
	static int _LPAR;
	static int _RPAR;
	public static int _IDENT;
	public static int _INTLIT;
	static int _COMMA;

	protected final IndexedSet<String> tokens = new IndexedSet<String>();
	
	private final SubParserRegistry subParsers = new SubParserRegistry();
	
	protected final OperatorRegistry opRegistry = new OperatorRegistry();
	
	private final Map<Integer, Integer> closeOpenKinds = new HashMap<Integer, Integer>();
	
	private final Map<Integer, String> reservedImages = new HashMap<Integer, String>();
	
	public boolean isOperator(int kind) {
		return opRegistry.hasGroup(kind) && !tokens.isReserved(kind);
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
		_NOOP = tokens.reserved();
		_OPEN = tokens.reserved();
		_LPAR = tokens.getOrAdd("(");
		_RPAR = tokens.getOrAdd(")");
		_COMMA = tokens.getOrAdd(",");
		
		reservedImages.put(_EOF, "End Of Formula");
		opRegistry.addOperator(_EOF, EOF_ID, GROUP0);
		opRegistry.addOperator(_NOOP, NOOP_ID, GROUP0);
		opRegistry.addOperator(_OPEN, OPEN_ID, GROUP0);
		addOpenClose("(", ")");
		try {
			_INTLIT = addReservedSubParser(SubParsers.INTLIT_SUBPARSER, INTLIT_IMAGE);
			_IDENT = addReservedSubParser(SubParsers.IDENT_SUBPARSER, IDENT_IMAGE);
			subParsers.addNud(_LPAR, MainParsers.CLOSED_SUGAR);
		} catch (OverrideException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public List<INudParser<? extends Formula<?>>> getNudParsers(Token token) {
		return subParsers.getNudParsers(token);
	}
	
	public ILedParser<? extends Formula<?>> getLedParser(Token token) {
		return subParsers.getLedParser(token);
	}
	
	protected void addOperator(String token, String operatorId, String groupId,
			INudParser<? extends Formula<?>> subParser)
			throws OverrideException {
		final int kind = tokens.getOrAdd(token);
		opRegistry.addOperator(kind, operatorId, groupId);
		subParsers.addNud(kind, subParser);
	}
	
	protected void addOperator(String token, String operatorId, String groupId,
			ILedParser<? extends Formula<?>> subParser)
			throws OverrideException {
		final int kind = tokens.getOrAdd(token);
		opRegistry.addOperator(kind, operatorId, groupId);
		subParsers.addLed(kind, subParser);
	}

	protected void addOperator(int kind, String operatorId, String groupId,
			INudParser<? extends Formula<?>> subParser)
			throws OverrideException {
		opRegistry.addOperator(kind, operatorId, groupId);
		subParsers.addNud(kind, subParser);
	}

	protected void addOpenClose(String open, String close) {
		final int openKind = tokens.getOrAdd(open);
		final int closeKind = tokens.getOrAdd(close);
		closeOpenKinds.put(closeKind, openKind);
	}

	public boolean isOpen(int kind) {
		return closeOpenKinds.containsValue(kind);
	}

	public boolean isClose(int kind) {
		return closeOpenKinds.containsKey(kind);
	}

	private int addReservedSubParser(INudParser<? extends Formula<?>> subParser, String image)
			throws OverrideException {
		final int kind = tokens.reserved();
		reservedImages.put(kind, image);
		subParsers.addNud(kind, subParser);
		return kind;
	}
	
	protected void addLiteralOperator(String token, int tag,
			INudParser<? extends Formula<?>> subParser) throws OverrideException {
		final int kind = tokens.getOrAdd(token);
		subParsers.addNud(kind, subParser);
	}

	protected void addGroupPrioritySequence(String... groupIds) throws CycleError {
		for (int i = 0; i < groupIds.length - 1; i++) {
			opRegistry.addGroupPriority(groupIds[i], groupIds[i+1]);
		}
	}
	
	public OperatorRelationship getOperatorRelationship(int leftKind,
			int rightKind, LanguageVersion version) {
		return opRegistry.getOperatorRelationship(leftKind, rightKind, version);
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
	
	public String getImage(int kind) {
		String image = tokens.getKey(kind);
		if (image == null) {
			image = reservedImages.get(kind);
		}
		return image;
	}

}
