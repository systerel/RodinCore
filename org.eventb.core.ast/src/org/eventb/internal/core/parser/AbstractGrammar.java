/*******************************************************************************
 * Copyright (c) 2010, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.parser;

import static org.eventb.core.ast.extension.StandardGroup.ARITHMETIC;
import static org.eventb.core.ast.extension.StandardGroup.GROUP_0;
import static org.eventb.core.ast.extension.StandardGroup.TYPED;
import static org.eventb.internal.core.parser.AbstractGrammar.DefaultToken.EOF;
import static org.eventb.internal.core.parser.AbstractGrammar.DefaultToken.LBRACE;
import static org.eventb.internal.core.parser.AbstractGrammar.DefaultToken.LBRACKET;
import static org.eventb.internal.core.parser.AbstractGrammar.DefaultToken.LPAR;
import static org.eventb.internal.core.parser.AbstractGrammar.DefaultToken.NEG_LIT;
import static org.eventb.internal.core.parser.AbstractGrammar.DefaultToken.NOOP;
import static org.eventb.internal.core.parser.AbstractGrammar.DefaultToken.OFTYPE;
import static org.eventb.internal.core.parser.AbstractGrammar.DefaultToken.OPEN;
import static org.eventb.internal.core.parser.AbstractGrammar.DefaultToken.RBRACE;
import static org.eventb.internal.core.parser.AbstractGrammar.DefaultToken.RBRACKET;
import static org.eventb.internal.core.parser.AbstractGrammar.DefaultToken.RPAR;
import static org.eventb.internal.core.parser.SubParsers.OFTYPE_PARSER;
import static org.eventb.internal.core.parser.operators.OperatorRelationship.COMPATIBLE;
import static org.eventb.internal.core.parser.operators.OperatorRelationship.LEFT_PRIORITY;
import static org.eventb.internal.core.parser.operators.OperatorRelationship.RIGHT_PRIORITY;

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
import org.eventb.core.ast.extension.StandardGroup;
import org.eventb.internal.core.lexer.Token;
import org.eventb.internal.core.parser.GenParser.OverrideException;
import org.eventb.internal.core.parser.operators.BracketCompactor;
import org.eventb.internal.core.parser.operators.Brackets;
import org.eventb.internal.core.parser.operators.ExternalViewUtils;
import org.eventb.internal.core.parser.operators.ExternalViewUtils.Instantiator;
import org.eventb.internal.core.parser.operators.LexKindParserDB;
import org.eventb.internal.core.parser.operators.OpRegistryCompactor;
import org.eventb.internal.core.parser.operators.OperatorRegistry;
import org.eventb.internal.core.parser.operators.OperatorRegistryCompact;
import org.eventb.internal.core.parser.operators.OperatorRelationship;
import org.eventb.internal.core.parser.operators.PropertyParserDB;

/**
 * @author Nicolas Beauger
 *
 */
public abstract class AbstractGrammar {

	public static enum DefaultToken {
		EOF("End of Formula", true),
		NOOP("No Operator", true),
		OPEN("Open", true),
		IDENT("an identifier", true),
		INT_LIT("an integer literal", true),
		NEG_LIT("a negative integer literal", true),
		PRED_VAR("Predicate Variable", true),
		LPAR("(", false),
		RPAR(")", false),
		COMMA(",", false),
		LBRACKET("[", false),
		RBRACKET("]", false),
		LBRACE("{", false),
		RBRACE("}", false),
		MAPS_TO("\u21a6", false),
		MID("\u2223", false),
		DOT("\u00b7", false),
		OFTYPE("\u2982", false);

		private final String image;
		private final boolean isReserved;
		
		private DefaultToken(String image, boolean isReserved) {
			this.image = image;
			this.isReserved = isReserved;
		}
		
		public String getImage() {
			return image;
		}
		
		public boolean isReserved() {
			return isReserved;
		}
	}
	
	private static final String OFTYPE_ID = "Oftype";
	
	protected TokenSet tokens = new TokenSet();
	
	private final LexKindParserDB subParsers = new LexKindParserDB();
	
	private OperatorRegistry initOpRegistry = new OperatorRegistry();
	private OperatorRegistryCompact opRegistry = null;
	
	private List<IOperatorInfo<? extends Formula<?>>> deferredOperators = new ArrayList<IOperatorInfo<? extends Formula<?>>>();
	
	// used by extended grammar to fetch appropriate parser
	// and by extended formulae to fetch appropriate printers
	// TODO try to generalise to standard language operators
	private final PropertyParserDB propParsers = new PropertyParserDB();
	
	private Map<Integer, Integer> initCloseOpenKinds = new HashMap<Integer, Integer>();
	private Brackets brackets = null;
	
	private final int[] defaultTokenKinds = new int[DefaultToken.values().length];
	
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
		return kind == getKind(NEG_LIT)
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

			initOpRegistry.addOperator(getKind(EOF), EOF.getImage(), GROUP_0.getId(), false);
			initOpRegistry.addOperator(getKind(NOOP), NOOP.getImage(), GROUP_0.getId(), false);
			initOpRegistry.addOperator(getKind(OPEN), OPEN.getImage(), GROUP_0.getId(), false);
			initOpRegistry.addOperator(getKind(NEG_LIT), NEG_LIT.getImage(), ARITHMETIC.getId(),
					false);
			
			// TODO move to Expression.init() called from BMath
			// Undefined Operators
			addOperator(OFTYPE, OFTYPE_ID, TYPED.getId(), OFTYPE_PARSER, true);

			addOpenClose(LPAR.getImage(), RPAR.getImage());
			addOpenClose(LBRACE.getImage(), RBRACE.getImage());
			addOpenClose(LBRACKET.getImage(), RBRACKET.getImage());

			IntegerLiteral.init(this);
			Identifier.init(this);
			subParsers.addNud(getKind(LPAR), MainParsers.CLOSED_SUGAR);
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
		final DefaultToken[] defTokens = DefaultToken.values();
		for (int i = 0; i < defTokens.length; i++) {
			final DefaultToken token = defTokens[i];
			if (token.isReserved()) {
				defaultTokenKinds[i] = tokens.reserved(token.getImage());
			} else {
				defaultTokenKinds[i] = tokens.getOrAdd(token.getImage());
			}
		}
	}

	private void updateDefaultKinds() {
		final DefaultToken[] defTokens = DefaultToken.values();
		for (int i = 0; i < defTokens.length; i++) {
			final DefaultToken token = defTokens[i];
			if (token.isReserved()) {
				defaultTokenKinds[i] = tokens.getReserved(token.getImage());
			} else {
				defaultTokenKinds[i] = tokens.getKind(token.getImage());
			}
		}
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
		
		final BracketCompactor brkCompactor = new BracketCompactor(initCloseOpenKinds);
		brackets = brkCompactor.compact(opKindInst);
		initCloseOpenKinds = null;
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
	public void addOperator(DefaultToken token, String operatorId, String groupId,
			IParserPrinter<? extends Formula<?>> subParser, boolean isSpaced)
			throws OverrideException {
		final int kind = getKind(token);
		initOpRegistry.addOperator(kind, operatorId, groupId, isSpaced);
		if (subParser instanceof INudParser) {
			subParsers.addNud(kind,
					(INudParser<? extends Formula<?>>) subParser);
		} else {
			subParsers.addLed(kind,
					(ILedParser<? extends Formula<?>>) subParser);
		}
	}

	protected void addOpenClose(String open, String close) {
		final int openKind = tokens.getOrAdd(open);
		final int closeKind = tokens.getOrAdd(close);
		initCloseOpenKinds.put(closeKind, openKind);
	}

	public boolean isOpen(int kind) {
		return brackets.isOpen(kind);
	}

	public boolean isClose(int kind) {
		return brackets.isClose(kind);
	}

	public void addReservedSubParser(DefaultToken token,
			INudParser<? extends Formula<?>> subParser) {
		assert token.isReserved();
		final int reservedKind = getKind(token);
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
	
	public int getKind(DefaultToken token) {
		return defaultTokenKinds[token.ordinal()];
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
		if (parentKind == getKind(EOF)) { // TODO maybe not needed
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
