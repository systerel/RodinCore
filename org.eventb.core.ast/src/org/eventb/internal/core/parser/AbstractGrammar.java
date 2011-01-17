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

import static org.eventb.internal.core.parser.BMath.StandardGroup.ARITHMETIC;
import static org.eventb.internal.core.parser.BMath.StandardGroup.GROUP_0;
import static org.eventb.internal.core.parser.BMath.StandardGroup.TYPED;
import static org.eventb.internal.core.parser.OperatorRelationship.COMPATIBLE;
import static org.eventb.internal.core.parser.OperatorRelationship.LEFT_PRIORITY;
import static org.eventb.internal.core.parser.OperatorRelationship.RIGHT_PRIORITY;
import static org.eventb.internal.core.parser.SubParsers.OFTYPE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.Identifier;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.extension.CycleError;
import org.eventb.core.ast.extension.IGrammar;
import org.eventb.core.ast.extension.IOperator;
import org.eventb.core.ast.extension.IOperatorProperties;
import org.eventb.internal.core.lexer.Token;
import org.eventb.internal.core.parser.BMath.StandardGroup;
import org.eventb.internal.core.parser.ExternalViewUtils.Instantiator;
import org.eventb.internal.core.parser.GenParser.OverrideException;

/**
 * @author Nicolas Beauger
 *
 */
public abstract class AbstractGrammar {

	private static final String OFTYPE_IMAGE = "\u2982";
	private static final String EOF_ID = "End of Formula";
	private static final String NOOP_ID = "No Operator";
	private static final String OPEN_ID = "Open";
	private static final String IDENT_IMAGE = "an identifier";
	private static final String INTLIT_IMAGE = "an integer literal";
	protected static final String NEGLIT_ID = "a negative integer literal";
	private static final String PREDVAR_ID = "Predicate Variable";

	private static final String LPAR_IMAGE = "(";
	private static final String RPAR_IMAGE = ")";
	private static final String LBRACKET_IMAGE = "[";
	private static final String RBRACKET_IMAGE = "]";
	private static final String LBRACE_IMAGE = "{";
	private static final String RBRACE_IMAGE = "}";
	private static final String COMMA_IMAGE = ",";
	private static final String DOT_IMAGE = "\u00b7";
	private static final String MID_IMAGE = "\u2223";
	private static final String MAPSTO_IMAGE = "\u21a6";
	private static final String PARTITION_IMAGE = "partition";
	private static final String OFTYPE_ID = "Oftype";
	
	// TODO make an enum
	private int _EOF;
	private int _NOOP;
	private int _OPEN;
	private int _IDENT;
	private int _INTLIT;
	private int _NEGLIT;
	private int _LPAR;
	private int _RPAR;
	private int _COMMA;
	private int _RBRACKET;
	private int _RBRACE;
	private int _MAPSTO;
	private int _MID;
	private int _DOT;
	private int _PREDVAR;
	private int _KPARTITION;
	private int _OFTYPE;

	protected TokenSet tokens = new TokenSet();
	
	private final LexKindParserDB subParsers = new LexKindParserDB();
	
	private OperatorRegistry initOpRegistry = new OperatorRegistry();
	private OperatorRegistryCompact opRegistry = null;
	
	private List<IOperatorInfo<? extends Formula<?>>> deferredOperators = new ArrayList<IOperatorInfo<? extends Formula<?>>>();
	
	// used by extended grammar to fetch appropriate parser
	// and by extended formulae to fetch appropriate printers
	// TODO try to generalise to standard language operators
	private final PropertyParserDB propParsers = new PropertyParserDB();
	
	private final Map<Integer, Integer> closeOpenKinds = new HashMap<Integer, Integer>();
	
	public IGrammar asExternalView() {
		final Instantiator<Integer, IOperator> instantiator = new Instantiator<Integer, IOperator>();
		final Map<Integer, String> kindIds = opRegistry.getKindIds();
		for (Entry<Integer, String> kindId : kindIds.entrySet()) {
			final Integer kind = kindId.getKey();
			final String id = kindId.getValue();
			final String syntaxSymbol = tokens.getImage(kind);
			final IOperator operator = new ExternalViewUtils.ExternalOperator(id, syntaxSymbol);
			instantiator.setInst(kind, operator);
		}
		return opRegistry.asExternalView(instantiator);
	}

	public boolean isOperator(int kind) {
		// TODO could be replaced by 'there exists a tag for the given kind'
		return kind == getNEGLIT()
				|| (opRegistry.hasGroup(kind) && (!tokens.isReserved(kind)));
	}
	
	protected boolean isInitOperator(int kind) {
		// TODO could be replaced by 'there exists a tag for the given kind'
		return initOpRegistry.hasGroup(kind) && (!tokens.isReserved(kind));
	}
	
	public TokenSet getTokens() {
		return tokens;
	}

	/**
	 * Initialises tokens, parsers and operator relationships.
	 * <p>
	 * Subclasses are expected to override and call this method first.
	 * </p>
	 */
	public final void init() {
		try {
			initDefaultKinds();

			initOpRegistry.addOperator(_EOF, EOF_ID, GROUP_0.getId(), false);
			initOpRegistry.addOperator(_NOOP, NOOP_ID, GROUP_0.getId(), false);
			initOpRegistry.addOperator(_OPEN, OPEN_ID, GROUP_0.getId(), false);
			initOpRegistry.addOperator(_NEGLIT, NEGLIT_ID, ARITHMETIC.getId(),
					false);
			// Undefined Operators
			addOperator("\u2982", OFTYPE_ID, TYPED.getId(), OFTYPE, true);

			addOpenClose(LPAR_IMAGE, RPAR_IMAGE);
			addOpenClose(LBRACE_IMAGE, RBRACE_IMAGE);
			addOpenClose(LBRACKET_IMAGE, RBRACKET_IMAGE);

			IntegerLiteral.init(this);
			Identifier.init(this);
			subParsers.addNud(_LPAR, MainParsers.CLOSED_SUGAR);
			addOperators();
			addOperatorRelationships();

			// the following redistributes all kinds
			compact();

			updateDefaultKinds();

			for (IOperatorInfo<? extends Formula<?>> operInfo : deferredOperators) {
				populateSubParsers(operInfo);
			}
			deferredOperators = null;
		} catch (OverrideException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void initDefaultKinds() {
		_EOF = tokens.reserved(EOF_ID);
		_NOOP = tokens.reserved(NOOP_ID);
		_OPEN = tokens.reserved(OPEN_ID);
		_IDENT = tokens.reserved(IDENT_IMAGE);
		_INTLIT = tokens.reserved(INTLIT_IMAGE);
		_NEGLIT = tokens.reserved(NEGLIT_ID);
		_PREDVAR = tokens.reserved(PREDVAR_ID);
		_LPAR = tokens.getOrAdd(LPAR_IMAGE);
		_RPAR = tokens.getOrAdd(RPAR_IMAGE);
		_COMMA = tokens.getOrAdd(COMMA_IMAGE);
		_RBRACKET = tokens.getOrAdd(RBRACKET_IMAGE);
		_RBRACE = tokens.getOrAdd(RBRACE_IMAGE);
		_MAPSTO = tokens.getOrAdd(MAPSTO_IMAGE);
		_MID = tokens.getOrAdd(MID_IMAGE);
		_DOT = tokens.getOrAdd(DOT_IMAGE);
		_KPARTITION = tokens.getOrAdd(PARTITION_IMAGE);
		_OFTYPE = tokens.getOrAdd(OFTYPE_IMAGE);
	}

	private void updateDefaultKinds() {
		_EOF = tokens.getReserved(EOF_ID);
		_NOOP = tokens.getReserved(NOOP_ID);
		_OPEN = tokens.getReserved(OPEN_ID);
		_IDENT = tokens.getReserved(IDENT_IMAGE);
		_INTLIT = tokens.getReserved(INTLIT_IMAGE);
		_NEGLIT = tokens.getReserved(NEGLIT_ID);
		_PREDVAR = tokens.getReserved(PREDVAR_ID);
		_LPAR = tokens.getKind(LPAR_IMAGE);
		_RPAR = tokens.getKind(RPAR_IMAGE);
		_COMMA = tokens.getKind(COMMA_IMAGE);
		_RBRACKET = tokens.getKind(RBRACKET_IMAGE);
		_RBRACE = tokens.getKind(RBRACE_IMAGE);
		_MAPSTO = tokens.getKind(MAPSTO_IMAGE);
		_MID = tokens.getKind(MID_IMAGE);
		_DOT = tokens.getKind(DOT_IMAGE);
		_KPARTITION = tokens.getKind(PARTITION_IMAGE);
		_OFTYPE = tokens.getKind(OFTYPE_IMAGE);
	}

	private void compact() {
		final OpRegistryCompactor regCompactor = new OpRegistryCompactor(
				initOpRegistry);
		final Instantiator<Integer, Integer> opKindInst = new Instantiator<Integer, Integer>();
		opRegistry = regCompactor.compact(opKindInst);
		initOpRegistry = null;
		
		final TokenSetRedist tokenCompactor = new TokenSetRedist();
		tokens = tokenCompactor.redistribute(tokens, opKindInst);
		subParsers.redistribute(opKindInst);
		
		final Map<Integer, Integer> newCloseOpen = new HashMap<Integer, Integer>();
		for (Entry<Integer, Integer> entry : closeOpenKinds.entrySet()) {
			newCloseOpen.put(opKindInst.instantiate(entry.getKey()),
					opKindInst.instantiate(entry.getValue()));
		}
		closeOpenKinds.clear();
		closeOpenKinds.putAll(newCloseOpen);
	}

	private void populateSubParsers(
			IOperatorInfo<? extends Formula<?>> operInfo)
			throws OverrideException {
		final int kind = tokens.getKind(operInfo.getImage());
		final IParserPrinter<? extends Formula<?>> parser = operInfo.makeParser(kind);
		if (parser instanceof INudParser<?>) {
			subParsers.addNud(kind, (INudParser<? extends Formula<?>>) parser);
		} else {
			subParsers.addLed(kind, (ILedParser<? extends Formula<?>>) parser);
		}
	}

	protected abstract void addOperators();
	protected abstract void addOperatorRelationships();
	
	public void addCompatibility(String leftOpId, String rightOpId) {
		initOpRegistry.addCompatibility(leftOpId, rightOpId);
	}
	
	public void addAssociativity(String opId) {
		initOpRegistry.addAssociativity(opId);
	}
	
	public void addPriority(String lowOpId, String highOpId) throws CycleError {
		initOpRegistry.addPriority(lowOpId, highOpId);
	}
	
	public void addGroupPriority(String lowGroupId, String highGroupId) throws CycleError {
		initOpRegistry.addGroupPriority(lowGroupId, highGroupId);
	}

	public List<INudParser<? extends Formula<?>>> getNudParsers(Token token) {
		return subParsers.getNudParsers(token);
	}
	
	public ILedParser<? extends Formula<?>> getLedParser(Token token) {
		return subParsers.getLedParser(token);
	}
	
	// for now, used only for extension parsers
	public IOperatorInfo<? extends Formula<?>> getParser(
			IOperatorProperties operProps, String image, int tag, String opId,
			String groupId) {
		return propParsers.getParser(operProps, image, tag, opId, groupId);
	}

	public void addParser(IPropertyParserInfo<? extends Formula<?>> parserInfo)
			throws OverrideException {
		propParsers.add(parserInfo);
	}
	
	// TODO remove all other addOperator() methods
	public void addOperator(IOperatorInfo<? extends Formula<?>> operInfo)
			throws OverrideException {
		final int kind = tokens.getOrAdd(operInfo.getImage());
		initOpRegistry.addOperator(kind, operInfo.getId(),
				operInfo.getGroupId(), operInfo.isSpaced());
		// kind is unstable at this stage, subParsers are populated later
		deferredOperators.add(operInfo);
	}

	// must be called only with subparsers getting their kind dynamically from a
	// grammar while parsing, the kind must not be stored inside the subparser
	protected void addOperator(String token, String operatorId, String groupId,
			ILedParser<? extends Formula<?>> subParser, boolean isSpaced)
			throws OverrideException {
		final int kind = tokens.getOrAdd(token);
		initOpRegistry.addOperator(kind, operatorId, groupId, isSpaced);
		subParsers.addLed(kind, subParser);
	}

	// must be called only with subparsers getting their kind dynamically from a
	// grammar while parsing, the kind must not be stored inside the subparser
	public void addOperator(int kind, String operatorId, String groupId,
			INudParser<? extends Formula<?>> subParser, boolean isSpaced)
			throws OverrideException {
		initOpRegistry.addOperator(kind, operatorId, groupId, isSpaced);
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
			INudParser<? extends Formula<?>> subParser) {
		subParsers.addNud(reservedKind, subParser);
	}
	
	protected void addGroupPrioritySequence(StandardGroup... groups) throws CycleError {
		for (int i = 0; i < groups.length - 1; i++) {
			initOpRegistry.addGroupPriority(groups[i].getId(), groups[i+1].getId());
		}
	}
	
	public OperatorRelationship getOperatorRelationship(int leftKind,
			int rightKind) {
		return opRegistry.getOperatorRelationship(leftKind, rightKind);
	}
	
	public int getEOF() {
		return _EOF;
	}
	
	public int getNOOP() {
		return _NOOP;
	}
	
	public int getOPEN() {
		return _OPEN;
	}

	public int getIDENT() {
		return _IDENT;
	}
	
	public int getINTLIT() {
		return _INTLIT;
	}
	
	public int getNEGLIT() {
		return _NEGLIT;
	}
	
	public int getLPAR() {
		return _LPAR;
	}
	
	public int getRPAR() {
		return _RPAR;
	}
	
	public int getCOMMA() {
		return _COMMA;
	}
	
	public int getRBRACKET() {
		return _RBRACKET;
	}
	
	public int getRBRACE() {
		return _RBRACE;
	}
	
	public int getMAPSTO() {
		return _MAPSTO;
	}

	public int getMID() {
		return _MID;
	}
	
	public int getDOT() {
		return _DOT;
	}
	
	public int getPREDVAR() {
		return _PREDVAR;
	}

	public int getPARTITION() {
		return _KPARTITION;
	}
	
	public int getOFTYPE() {
		return _OFTYPE;
	}
	
	public String getImage(int kind) {
		return tokens.getImage(kind);
	}

	public int getKind(String image) {
		final int kind = tokens.getKind(image);
		if (kind == TokenSet.UNKNOWN_KIND) {
			// TODO consider throwing a caught exception (for extensions to manage)
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
	 * @return <code>true</code> iff parentheses are needed
	 * @since 2.0
	 */
	public boolean needsParentheses(boolean isRightChild, int childKind,
			int parentKind) {
		if (parentKind == getEOF()) { // TODO maybe not needed
			return false;
		}
		if (!isOperator(parentKind) || !isOperator(childKind)) {
			return false; // IDENT for instance
		}
		if (childKind == parentKind && opRegistry.isAssociative(parentKind)) {
			return true;
		}
		final OperatorRelationship relParentChild = getOperatorRelationship(parentKind,
				childKind);
		if (relParentChild == LEFT_PRIORITY) {
			// Rule 1: parent priority => parentheses
			return true;
		}
		if (relParentChild == RIGHT_PRIORITY) {
			// Rule 2: child priority => no parentheses
			return false;
		}
		// no priority is defined, now it is only a matter of left/right compatibility
		if (isRightChild && relParentChild == COMPATIBLE) {
			// parent on the left, child on the right
			// Rule 3: compatible right child => parentheses
			return true;
		}
		if (!isRightChild && getOperatorRelationship(childKind, parentKind) == COMPATIBLE) {
			// child on the left, parent on the right
			// Rule 4: compatible left child => no parentheses
			return false;
		}
		return true; // Other cases => parentheses
	}

	public boolean isSpaced(int kind)	 {
		return opRegistry.isSpaced(kind);
	}
	
	public boolean isDeclared(String operatorId) {
		return opRegistry.isDeclared(operatorId);
	}
}
