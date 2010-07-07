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
import static org.eventb.internal.core.parser.OperatorRegistry.OperatorRelationship.INCOMPATIBLE;
import static org.eventb.internal.core.parser.OperatorRegistry.OperatorRelationship.LEFT_PRIORITY;
import static org.eventb.internal.core.parser.SubParsers.IDENT_SUBPARSER;
import static org.eventb.internal.core.parser.SubParsers.INTLIT_SUBPARSER;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.LanguageVersion;
import org.eventb.core.ast.extension.CycleError;
import org.eventb.core.ast.extension.IOperatorProperties;
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

	protected static final IndexedSet<String> reservedTokens = new IndexedSet<String>();
	
	
	public static final int _EOF = reservedTokens.reserved("End Of Formula");
	static final int _NOOP = reservedTokens.reserved("No Operator");
	static final int _OPEN = reservedTokens.reserved("Open");
	static int _LPAR;
	public static int _RPAR;
	public static final int _IDENT = reservedTokens.reserved(IDENT_IMAGE);
	public static final int _INTLIT = reservedTokens.reserved(INTLIT_IMAGE);
	static int _COMMA;

	protected final IndexedSet<String> tokens = new IndexedSet<String>(reservedTokens);
	
	private final LexKindParserDB subParsers = new LexKindParserDB();
	
	private final OperatorRegistry opRegistry = new OperatorRegistry();
	
	// used by extended grammar to fetch appropriate parser
	// and by extended formulae to fetch appropriate printers
	// TODO try to generalise to standard language operators
	private final PropertyParserDB propParsers = new PropertyParserDB();
	
	private final Map<Integer, Integer> closeOpenKinds = new HashMap<Integer, Integer>();
	
	public boolean isOperator(int kind) {
		// TODO could be replaced by 'there exists a tag for the given kind'
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
	public final void init() {
		
		_LPAR = tokens.getOrAdd("(");
		_RPAR = tokens.getOrAdd(")");
		_COMMA = tokens.getOrAdd(",");
		opRegistry.addOperator(_EOF, EOF_ID, GROUP0);
		opRegistry.addOperator(_NOOP, NOOP_ID, GROUP0);
		opRegistry.addOperator(_OPEN, OPEN_ID, GROUP0);
		addOpenClose("(", ")");
		try {
			subParsers.addNud(_INTLIT, INTLIT_SUBPARSER);
			subParsers.addNud(_IDENT, IDENT_SUBPARSER);
			subParsers.addNud(_LPAR, MainParsers.CLOSED_SUGAR);
		} catch (OverrideException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		addOperators();
		addOperatorRelationships();
	}

	protected abstract void addOperators();
	protected abstract void addOperatorRelationships();
	
	public void addCompatibility(String leftOpId, String rightOpId) {
		opRegistry.addCompatibility(leftOpId, rightOpId);
	}
	
	public void addCompatibility(String leftOpId, String rightOpId, LanguageVersion version) {
		opRegistry.addCompatibility(leftOpId, rightOpId, version);
	}
	
	public void addPriority(String lowOpId, String highOpId) throws CycleError {
		opRegistry.addPriority(lowOpId, highOpId);
	}
	
	public void addGroupPriority(String lowGroupId, String highGroupId) throws CycleError {
		opRegistry.addGroupPriority(lowGroupId, highGroupId);
	}

	public List<INudParser<? extends Formula<?>>> getNudParsers(Token token) {
		return subParsers.getNudParsers(token);
	}
	
	public ILedParser<? extends Formula<?>> getLedParser(Token token) {
		return subParsers.getLedParser(token);
	}
	
	public IParserPrinter<? extends Formula<?>> getParser(IOperatorProperties operProps, int kind,
			int tag) {
		return propParsers.getParser(operProps, kind, tag);
	}

	public void addParser(IPropertyParserInfo<? extends Formula<?>> parserInfo)
			throws OverrideException {
		propParsers.add(parserInfo);
	}
	
	// TODO remove all other addOperator() methods
	public void addOperator(IOperatorInfo<? extends Formula<?>> operInfo)
			throws OverrideException {
		final int kind = tokens.getOrAdd(operInfo.getImage());
		opRegistry.addOperator(kind, operInfo.getId(), operInfo.getGroupId());
		final IParserPrinter<? extends Formula<?>> parser = operInfo.makeParser(kind);
		if (parser instanceof INudParser<?>) {
			subParsers.addNud(kind, (INudParser<? extends Formula<?>>) parser);
		} else {
			subParsers.addLed(kind, (ILedParser<? extends Formula<?>>) parser);
		}
	}
	
	public void addOperator(String token, String operatorId, String groupId,
			INudParser<? extends Formula<?>> subParser)
			throws OverrideException {
		final int kind = tokens.getOrAdd(token);
		opRegistry.addOperator(kind, operatorId, groupId);
		subParsers.addNud(kind, subParser);
	}

	// FIXME remove method after correctly refactoring so as not to need it
	public void addOperator(String token, int tag, String operatorId,
			String groupId, INudParser<? extends Formula<?>> subParser)
			throws OverrideException {
		final int kind = tokens.getOrAdd(token);
		opRegistry.addOperator(kind, operatorId, groupId);
		subParsers.addNud(kind, subParser);
	}

	public void addOperator(String token, String operatorId, String groupId,
			ILedParser<? extends Formula<?>> subParser)
			throws OverrideException {
		final int kind = tokens.getOrAdd(token);
		opRegistry.addOperator(kind, operatorId, groupId);
		subParsers.addLed(kind, subParser);
	}

	public void addOperator(int kind, String operatorId, String groupId,
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

	public void addReservedSubParser(int reservedKind,
			INudParser<? extends Formula<?>> subParser)
			throws OverrideException {
		subParsers.addNud(reservedKind, subParser);
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
		return tokens.getElem(kind);
	}

	public int getKind(String image) {
		final int kind = tokens.getIndex(image);
		if (kind == IndexedSet.NOT_AN_INDEX) {
			throw new IllegalArgumentException("No such token: " + image);
		}
		return kind;
	}

	/**
	 * Returns whether parentheses are needed around a formula tag when it
	 * appears as a child of formula parentTag.
	 * 
	 * @param isRightChild
	 *            <code>true</code> if tag node is the right child parentTag,
	 *            <code>false</code> if it is the left child or a unique child
	 * @param childKind
	 * @param parentKind
	 * @param version
	 * @return <code>true</code> iff parentheses are needed
	 * @since 2.0
	 */
	public boolean needsParentheses(boolean isRightChild, int childKind,
			int parentKind, LanguageVersion version) {
		if (childKind == parentKind) {
			// FIXME false for maplets
			// FIXME missing case for 1 + - 2 (PLUS UNMINUS)
			return true;
		}
		if (parentKind == _EOF) { // TODO maybe not needed
			return false;
		}
		if (!isOperator(parentKind) || !isOperator(childKind)) {
			return false; // IDENT for instance
		}
		final OperatorRelationship opRel = getOperatorRelationship(parentKind,
				childKind, version);
		// FIXME wrong rule: take isRightChild into account
		return (opRel == LEFT_PRIORITY || opRel == INCOMPATIBLE);
	}

}
