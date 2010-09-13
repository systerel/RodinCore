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

import static org.eventb.internal.core.parser.AbstractGrammar.*;
import static org.eventb.internal.core.parser.GenParser.ProgressDirection.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.eventb.core.ast.ASTProblem;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.ProblemKind;
import org.eventb.core.ast.ProblemSeverities;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.ast.Type;
import org.eventb.internal.core.lexer.Scanner;
import org.eventb.internal.core.lexer.Token;
import org.eventb.internal.core.lexer.Scanner.ScannerState;
import org.eventb.internal.core.parser.IParserPrinter.SubParseResult;
import org.eventb.internal.core.parser.OperatorRegistry.OperatorRelationship;

/**
 * @author Nicolas Beauger
 *
 */
public class GenParser {

	private static final boolean DEBUG = false;
	
	public static class OverrideException extends Exception {
	
		private static final long serialVersionUID = -1281802568424261959L;
	
		public OverrideException(String reason) {
			super(reason);
		}
	}

	public static class SyntaxError extends Exception {

		private static final long serialVersionUID = -8349775303104088736L;

		private final ASTProblem problem;
		
		public SyntaxError(ASTProblem problem) {
			this.problem = problem;
		}

		public ASTProblem getProblem() {
			return problem;
		}

	}

	private static class StackedValue<T> {
		T val;
		private final Stack<T> stack = new Stack<T>();
		
		public StackedValue(T initVal) {
			this.val = initVal;
		}
		
		public StackedValue(StackedValue<T> toCopy) {
			this.val = toCopy.val;
			this.stack.addAll(toCopy.stack);
		}
		
		public void push(T newVal) {
			stack.push(val);
			val = newVal;
		}

		public void pop() {
			val = stack.pop();
		}

		public T peekStack() {
			return stack.peek();
		}
		
		public boolean isStackEmpty() {
			return stack.isEmpty();
		}
		
		@Override
		public String toString() {
			return val.toString() + " " + stack.toString();
		}
	}
	
	private final Scanner scanner;
	
	// this field configures the parser to parse some kind of formula,
	// viz. Expression, Predicate, Assignment, or Type.
	private final Class<?> clazz;
	private final FormulaFactory factory;
	private final ParseResult result;
	private final boolean withPredVar;

	
	static enum ProgressDirection {
		LEFT, RIGHT
	}
	
	static class ParserContext {
		private static final Token INIT_TOKEN = new Token(IndexedSet.NOT_AN_INDEX, "", -1);

		private final Scanner scanner;
		protected final FormulaFactory factory;
		private final AbstractGrammar grammar;
		protected final ParseResult result;
		protected final boolean withPredVar;
		private StackedValue<Binding> binding = new StackedValue<Binding>(new Binding());
		private StackedValue<Integer> parentKind = new StackedValue<Integer>(_EOF); 
		private StackedValue<Integer> startPos = new StackedValue<Integer>(-1); 
		private int endPos = -1;
		private boolean parsingType;
		protected Token t;    // last recognized token
		protected Token la;   // lookahead token
		
		protected ParserContext(Scanner scanner, FormulaFactory factory, ParseResult result, boolean withPredVar) {
			this.scanner = scanner;
			this.factory = factory;
			this.grammar = factory.getGrammar();
			this.result = result;
			this.withPredVar = withPredVar;
		}

		/**
		 * Makes a source location starting from the position where the latest
		 * call to a subparse() method occurred, ending at the end position of
		 * the previous token (before current token t).
		 * 
		 * @return a source location
		 */
		public SourceLocation getSourceLocation() {
			if (startPos.val < 0) {
				throw new IllegalStateException("no start position set");
			}
			return makeSourceLocation(startPos.val, endPos);
		}
		
		public SourceLocation getEnclosingSourceLocation() {
			if (startPos.val < 0) {
				throw new IllegalStateException("no start position set");
			}
			return makeSourceLocation(startPos.peekStack(), t.getEnd());
		}

		public SourceLocation makeSourceLocation(Token token) {
			return makeSourceLocation(token.pos, token.getEnd());
		}

		public SourceLocation makeSourceLocation(int start, int end) {
			// a source location may occur at the very beginning of the formula
			// (empty formula for instance would be 0:-1)
			start = Math.max(0, start);
			end = Math.max(0, end);
			
			// a source location may occur at the very end of the formula
			// (EOF for instance would be fml.length:fml.length-1)
			start = Math.min(end, start);
			
			return new SourceLocation(start, end, result.getOrigin());
		}

		public void init() {
			t = INIT_TOKEN;
			la = scanner.Scan();
			accept();
		}
		
		private void accept() {
			if (grammar.isOpen(t.kind)) {
				pushParentKind(_OPEN);
			}
			if (grammar.isClose(la.kind)) {
				popParentKind();
			}
			endPos = t.getEnd();
			t = la;
			la = scanner.Scan();
		}
		
		private void pushParentKind(int newParentKind) {
			parentKind.push(newParentKind);
		}
		
		public void pushParentKind() {
			parentKind.push(t.kind);
		}

		public void popParentKind() {
			if (parentKind.isStackEmpty()) {
				// happens at least for ) without (
				// simple problem, error recovering => continue
				result.addProblem(new ASTProblem(makeSourceLocation(la),
						ProblemKind.UnmatchedTokens, ProblemSeverities.Error));
				// skip unmatched token
				t = la;
				la = scanner.Scan();
				return;
			}
			parentKind.pop();
		}
		
		public SavedContext save() {
			return new SavedContext(scanner.save(), t, la, parsingType,
					startPos, binding, parentKind);
		}
		
		public void restore(SavedContext sc) {
			scanner.restore(sc.scanState);
			t = sc.t;
			la = sc.la;
			parsingType = sc.parsingType;
			startPos = new StackedValue<Integer>(sc.startPos);
			binding = new StackedValue<Binding>(sc.binding);
			parentKind = new StackedValue<Integer>(sc.parentKind);
		}
		
		static class SavedContext {
			final ScannerState scanState;
			final Token t;
			final Token la;
			final boolean parsingType;
			final StackedValue<Integer> startPos;
			final StackedValue<Binding> binding;
			final StackedValue<Integer> parentKind;
			
			SavedContext(ScannerState scanState, Token t, Token la,
					boolean parsingType, StackedValue<Integer> startPos,
					StackedValue<Binding> binding,
					StackedValue<Integer> parentKind) {
				this.scanState = scanState;
				this.t = t;
				this.la = la;
				this.parsingType = parsingType;
				this.startPos = new StackedValue<Integer>(startPos);
				this.binding = new StackedValue<Binding>(binding);
				this.parentKind = new StackedValue<Integer>(parentKind);
			}
			
		}
		
		public boolean isParsingType() {
			return parsingType;
		}
		
		public void startParsingType() {
			this.parsingType = true;
		}
		
		public void stopParsingType() {
			this.parsingType = false;
		}

		/**
		 * Checks that the expected token with the given kind is ahead, then
		 * makes progress.
		 * 
		 * @param expectedKind
		 *            kind of the expected token
		 * @throws SyntaxError
		 *             in case an unexpected token is ahead
		 */
		public void accept(int expectedKind) throws SyntaxError {
			if (t.kind != expectedKind) {
				final String expected = grammar.getImage(expectedKind);
				throw new SyntaxError(new ASTProblem(makeSourceLocation(t),
						ProblemKind.UnexpectedSymbol, ProblemSeverities.Error,
						expected, grammar.getImage(t.kind)));
			}
			accept();
		}
		
		public void acceptOpenParen() throws SyntaxError {
			accept(_LPAR);
		}
		
		public void acceptCloseParen() throws SyntaxError {
			accept(_RPAR);
		}
		
		void scanUntilEOF() {
			while (t.kind != _EOF) {
				accept();
			}
		}
		
		public List<INudParser<? extends Formula<?>>> getNudParsers() {
			return grammar.getNudParsers(t);
		}
		
		public ILedParser<? extends Formula<?>> getLedParser() {
			return grammar.getLedParser(t);
		}
		
		public int getBoundIndex(String name) {
			return binding.val.getBoundIndex(name);
		}

		/**
		 * Returns whether the current token allows to progress right in the
		 * parsing.
		 * <p>
		 * It is <code>true</code> iff the current token:
		 * <li>is an operator</li>
		 * <li>is compatible with the current parent operator</li>
		 * <li>has a higher priority than the current parent operator</li>
		 * </p>
		 * 
		 * @return <code>true</code> if right progressing is allowed by the
		 *         grammar.
		 * @throws SyntaxError
		 *             if current operator is incompatible with current parent
		 *             operator
		 */
		public ProgressDirection giveProgressDirection() throws SyntaxError {
			final int leftKind = parentKind.val;
			final int rightKind = t.kind;
			if (!grammar.isOperator(rightKind)) {
				return LEFT;
			}
			final OperatorRelationship opRel = grammar.getOperatorRelationship(
					leftKind, rightKind);
			switch (opRel) {
			case INCOMPATIBLE:
				throw new SyntaxError(new ASTProblem(makeSourceLocation(t),
						ProblemKind.IncompatibleOperators,
						ProblemSeverities.Error, grammar.getImage(leftKind),
						grammar.getImage(rightKind)));
			case RIGHT_PRIORITY:
				return RIGHT;
			case COMPATIBLE:
				// process as left associative
			case LEFT_PRIORITY:
				return LEFT;
			default:
				return LEFT;
			}
		}
		
		private void pushPos() {
			startPos.push(t.pos);
		}
		
		private void popPos() {
			startPos.pop();
		}

		public <T> SubParseResult<T> subParseRes(INudParser<T> parser, boolean isRightChild) throws SyntaxError {
			final SubParseResult<T> parseRes = subParseNoCheckRes(parser);
			if (!parseRes.isClosed()) {
				final int childKind = parseRes.getKind();
				if (grammar.needsParentheses(isRightChild, childKind, parentKind.val)) {
					throw new SyntaxError(new ASTProblem(
							getSourceLocation(),
							ProblemKind.IncompatibleOperators,
							ProblemSeverities.Error, grammar
							.getImage(parentKind.val), grammar
							.getImage(childKind)));
				}
			}
			return parseRes;
		}
		
		public <T> T subParse(INudParser<T> parser, boolean isRightChild) throws SyntaxError {
			return subParseRes(parser, isRightChild).getParsed();
		}
		
		public <T> SubParseResult<T> subParseNoCheckRes(INudParser<T> parser)
				throws SyntaxError {
			pushPos();
			try {
				return parser.nud(this);
			} finally {
				popPos();
			}
		}
		
		public <T> T subParseNoCheck(INudParser<T> parser)
				throws SyntaxError {
			return subParseNoCheckRes(parser).getParsed();
		}

		public <T> T subParseNoCheck(INudParser<T> parser,
				List<BoundIdentDecl> newBoundIdents) throws SyntaxError {
			return subParse(parser, newBoundIdents, false, true);
		}
		
		public <T> T subParse(INudParser<T> parser,
				List<BoundIdentDecl> newBoundIdents, boolean isRightChild) throws SyntaxError {
			return subParse(parser, newBoundIdents, isRightChild, false);
		}
		
		private <T> T subParse(INudParser<T> parser,
				List<BoundIdentDecl> newBoundIdents, boolean isRightChild, boolean noCheck) throws SyntaxError {
			binding.push(new Binding(binding.val, newBoundIdents));
			try {
				if (noCheck) {
					return subParseNoCheck(parser);
				} else {
					return subParse(parser, isRightChild);
				}
			} finally {
				binding.pop();
			}
		}
		
		public <T> T subParseNoParent(INudParser<T> parser,List<BoundIdentDecl> newBoundIdents) throws SyntaxError {
			return subParseNoParent(parser, newBoundIdents, false);
		}
		
		// use it to avoid parent operator comparison
		// useful for parsing a predicate inside a non closed expression
		// (or conversely), as these operators have no relative priority
		private <T> T subParseNoParent(INudParser<T> parser,
				List<BoundIdentDecl> newBoundIdents, boolean noCheck) throws SyntaxError {
			pushParentKind(_NOOP);
			try {
				if (noCheck) {
					return subParseNoCheck(parser, newBoundIdents);
				} else {
					return subParse(parser, newBoundIdents, false); // TODO verify that false is always appropriate
				}
			} finally {
				popParentKind();
			}
		}
		
		public <T> T subParseNoParentNoCheck(INudParser<T> parser,
				List<BoundIdentDecl> newBoundIdents) throws SyntaxError {
			return subParseNoParent(parser, newBoundIdents, true);
		}

		private <T> T subParseSpecial(INudParser<T> parser, boolean noBinding, boolean noCheck) throws SyntaxError {
			if (noBinding) {
				binding.push(new Binding());
			}
			try {
				if (noCheck) {
					return subParseNoCheck(parser);
				} else {
					return subParse(parser, false); // TODO verify that false is always appropriate
				}
			} finally {
				if (noBinding) {
					binding.pop();
				}
			}
		}

		public <T> T subParseNoBinding(INudParser<T> parser) throws SyntaxError {
			return subParseSpecial(parser, true, false);
		}
		
		public <T> T subParseNoBindingNoCheck(INudParser<T> parser) throws SyntaxError {
			return subParseSpecial(parser, true, true);
		}
		
		public int getKind(String operatorImage) {
			return grammar.getKind(operatorImage);
		}

		/**
		 * Looks ahead for the given kind.
		 * <p>
		 * FIXME current implementation is not compatible with backtracking.
		 * MUST NOT be called after a call to {@link ParserContext#save()};
		 * @see Scanner#restore(ScannerState)
		 * </p>
		 * 
		 * @param searchedKind
		 *            a kind
		 * @return <code>true</code> iff the given kind has been found ahead
		 */
		public boolean lookAheadFor(int searchedKind) {
			if (la.kind == searchedKind) {
				return true;
			}
			return scanner.lookAheadFor(searchedKind);
		}
	}
	
    private static class Binding {
    	private Map<String, Integer> binders;
    	private int maxCount = -1;
 
    	// Creates an empty binding.
        Binding() {
        	binders = new HashMap<String, Integer>();
        }
    	
        // Creates a new binding based on <code>base</code> and extended
        // with <code>idents</code>
		Binding(Binding base, List<BoundIdentDecl> idents) {
        	binders = new HashMap<String, Integer>(base.binders);
			int index = base.maxCount;
    		for (BoundIdentDecl ident: idents) {
    			binders.put(ident.getName(), ++ index);
    		}
    		maxCount = index;
    	}

		// Returns the index to use for the identifier <code>name</code>
		// or -1 if the name is free under this binding.
		int getBoundIndex(String name) {
			Integer index = binders.get(name);
			if (index == null) {
				return -1;
			} else {
				return maxCount - index;
			}
		}
    }

	public GenParser(Class<?> clazz, Scanner scanner, ParseResult result,
			boolean withPredVar) {
		super();
		this.scanner = scanner;
		this.clazz = clazz;
		this.result = result;
		this.factory = result.factory;
		this.withPredVar = withPredVar;
	}

	/**
	 * Parses the tokens produced by the scanner associated to this parser.
	 * <p>
	 * The actual result of the parse is obtained via
	 * {@link #getResult() getResult()}.
	 */
	public void parse() {

		try {
			final ParserContext pc = new ParserContext(scanner, factory,
					result, withPredVar);
			pc.init();
			// separate parsed type in order to have
			// errors in case of unexpected result type
			if (clazz == Type.class) {
				final Type res = pc.subParse(MainParsers.TYPE_PARSER, false);
				result.setParsedType(res);
			} else if (clazz == Assignment.class) {
				final Assignment res = pc.subParse(MainParsers.ASSIGNMENT_PARSER, false);
				result.setParsedAssignment(res); 
			} else if (clazz == Predicate.class) {
				final Predicate res = pc.subParse(MainParsers.PRED_PARSER, false);
				result.setParsedPredicate(res); 
			} else if (clazz == Expression.class) {
				final Expression res = pc.subParse(MainParsers.EXPR_PARSER, false);
				result.setParsedExpression(res); 
			} else {
				throw new IllegalArgumentException(
						"Can only parse one of: Predicate, Expression, Assignment or Type.");
			}
			if (pc.t.kind != _EOF) {
				failUnmatchedTokens(pc);
			}
			// TODO remove above debug check when stable
			if (DEBUG) {
				if (pc.parentKind.val != _EOF) {
					throw new IllegalStateException("Improper parent stack: "
							+ pc.parentKind + " with " + pc.parentKind.val
							+ " = "
							+ factory.getGrammar().getImage(pc.parentKind.val));
				}
			}
		} catch (SyntaxError e) {
			processFailure(e.getProblem());
		}

	}

	private void failUnmatchedTokens(ParserContext pc) {
		final int startPos = pc.t.pos;
		pc.scanUntilEOF();
		final int endPos = pc.t.pos - 1;
		processFailure(new ASTProblem(pc.makeSourceLocation(startPos, endPos),
				ProblemKind.UnmatchedTokens, ProblemSeverities.Error));
	}

	private void processFailure(ASTProblem problem) {
		result.addProblem(problem);
		result.resetParsedFormula();
	}
	
	/**
	 * Returns the current parsing result.
	 *
	 * @return the current result.
	 */
	public ParseResult getResult() {
		return result;
	}

}
