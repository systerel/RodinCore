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

import static java.util.Arrays.asList;
import static org.eventb.core.ast.Formula.*;
import static org.eventb.internal.core.parser.BMath.*;
import static org.eventb.internal.core.parser.MainParsers.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;

import org.eventb.core.ast.ASTProblem;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Identifier;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.LanguageVersion;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.ProblemKind;
import org.eventb.core.ast.ProblemSeverities;
import org.eventb.core.ast.ProductType;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;
import org.eventb.core.ast.QuantifiedExpression.Form;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.internal.core.parser.GenParser.ParserContext;
import org.eventb.internal.core.parser.GenParser.SyntaxError;
import org.eventb.internal.core.parser.MainParsers.PatternParser;

/**
 * Sub-parsers are specialized parsers; they are usually bound to an operator.
 * 
 * @author Nicolas Beauger
 * 
 */
public class SubParsers {

	private static final Predicate[] NO_PRED = new Predicate[0];

	static abstract class AbstractSubParser<T> {

		protected final int tag;

		protected AbstractSubParser(int tag) {
			this.tag = tag;
		}
		
	}

	private static abstract class PrefixNudParser<T> extends AbstractSubParser<T> implements INudParser<T> {

		protected PrefixNudParser(int tag) {
			super(tag);
		}
		
		public final T nud(ParserContext pc) throws SyntaxError {
			pc.progress();
			return parseRight(pc);
		}
		
		/**
		 * Current token is the one that immediately follows the one on which
		 * nud() applies.
		 * 
		 * @param pc
		 *            the parser context
		 * @return the value to be returned by nud()
		 * @throws SyntaxError
		 */
		protected abstract T parseRight(ParserContext pc) throws SyntaxError;
	}

	private static abstract class ParenNudParser<T, U> extends PrefixNudParser<T> {

		private final INudParser<U> childParser;
		
		protected ParenNudParser(int tag, INudParser<U> childParser) {
			super(tag);
			this.childParser = childParser;
		}

		@Override
		protected final T parseRight(ParserContext pc) throws SyntaxError {
			// FIXME parsing this way prevents priority and compatibility checks
			// with operators that follow the closing parenthesis
			pc.progressOpenParen();
			final U child = pc.subParse(childParser);
			pc.progressCloseParen();
			return makeValue(pc.factory, child, pc.getSourceLocation());
		}
		
		protected abstract T makeValue(FormulaFactory factory, U child, SourceLocation loc) throws SyntaxError;
	}
	
	private static abstract class ValuedNudParser<T> extends AbstractSubParser<T> implements INudParser<T> {

		protected ValuedNudParser(int tag) {
			super(tag);
		}
		
		public final T nud(ParserContext pc) throws SyntaxError {
			final String tokenVal = pc.t.val;
			pc.progress(getKind());
			final SourceLocation loc = pc.getSourceLocation();
			return makeValue(pc, tokenVal, loc);
		}

		protected abstract int getKind();
		
		/**
		 * Makes the value to be returned by nud().
		 * <p>
		 * Current token is the one that immediately follows the given token
		 * value.
		 * </p>
		 * 
		 * @param pc
		 *            a parser context
		 * @param tokenVal
		 *            the value of the token on which nud() applies
		 * @param loc
		 *            the location of the token on which nud() applies
		 * @return the value to be returned by nud().
		 * @throws SyntaxError 
		 */
		protected abstract T makeValue(ParserContext pc, String tokenVal, SourceLocation loc) throws SyntaxError;

	}

	private static class VersionConditionalNudParser extends AbstractSubParser<Formula<?>> implements INudParser<Formula<?>> {

		private final EnumMap<LanguageVersion, INudParser<? extends Formula<?>>> parsers = new EnumMap<LanguageVersion, INudParser<? extends Formula<?>>>(
				LanguageVersion.class);
		
		protected VersionConditionalNudParser(INudParser<? extends Formula<?>>... parsers ) {
			super(NO_TAG);
			if (parsers.length != LanguageVersion.values().length) {
				throw new IllegalArgumentException("A parser is required for every language version");
			}
			for(LanguageVersion version: LanguageVersion.values()) {
				this.parsers.put(version, parsers[version.ordinal()]);
			}
		}

		public Formula<?> nud(ParserContext pc) throws SyntaxError {
			return parsers.get(pc.version).nud(pc);
		}
		
	}

	private static abstract class DefaultLedParser<T, Left, Right> extends AbstractSubParser<T> implements ILedParser<T> {

		private final INudParser<Right> rightParser;
		
		protected DefaultLedParser(int tag, INudParser<Right> rightParser) {
			super(tag);
			this.rightParser = rightParser;
		}
		
		protected Right parseRight(ParserContext pc) throws SyntaxError {
			return pc.subParse(rightParser);
		}

		public final T led(Formula<?> left, ParserContext pc) throws SyntaxError {
			pc.progress();
			final Left typedLeft = asLeftType(left);
			final Right right = parseRight(pc);
			return makeValue(pc.factory, typedLeft, right, pc.getSourceLocation());
		}

		protected abstract Left asLeftType(Formula<?> left) throws SyntaxError;
		
		protected abstract T makeValue(FormulaFactory factory, Left left,
				Right right, SourceLocation loc) throws SyntaxError;
	}
	
	private static abstract class DefaultLedExprParser<T> extends DefaultLedParser<T, Expression, Expression> {

		protected DefaultLedExprParser(int tag) {
			super(tag, EXPR_PARSER);
		}
		
		@Override
		protected final Expression asLeftType(Formula<?> left) throws SyntaxError {
			return asExpression(left);
		}
	}
	
	private static abstract class DefaultLedPredParser<T> extends DefaultLedParser<T, Predicate, Predicate> {

		protected DefaultLedPredParser(int tag) {
			super(tag, PRED_PARSER);
		}
		
		@Override
		protected Predicate asLeftType(Formula<?> left) throws SyntaxError {
			return asPredicate(left);
		}
	}
	
	private static abstract class AssociativeLedParser<T, Child> extends AbstractSubParser<T> implements ILedParser<T> {

		private final INudParser<Child> childParser;
		
		protected AssociativeLedParser(int tag, INudParser<Child> childParser) {
			super(tag);
			this.childParser = childParser;
		}

		public T led(Formula<?> left, ParserContext pc) throws SyntaxError {
			final Child typedLeft = asChildType(left);
			
			final List<Child> children = new ArrayList<Child>();
			children.add(typedLeft);
			
			final int kind = pc.t.kind;
			do {
				pc.progress();
				final Child next = pc.subParse(childParser);
				children.add(next);
			} while (pc.t.kind == kind);
			
			return makeResult(pc.factory, children, pc.getSourceLocation());
		}
		
		protected abstract Child asChildType(Formula<?> left) throws SyntaxError;
		
		protected abstract T makeResult(FormulaFactory factory,
				List<Child> children, SourceLocation loc) throws SyntaxError;

	}

	// TODO move ident parsers to MainParsers as they are imported there
	// Takes care of the bindings.
	static final INudParser<Identifier> IDENT_SUBPARSER = new ValuedNudParser<Identifier>(NO_TAG) {

		@Override
		protected Identifier makeValue(ParserContext pc, String tokenVal,
				SourceLocation loc) {
			if (pc.isParsingType()) { // make a type expression
				final Type type = pc.factory.makePowerSetType(pc.factory.makeGivenType(tokenVal));
				return pc.factory.makeFreeIdentifier(tokenVal, loc, type);
			}
			final int index = pc.getBoundIndex(tokenVal);
			if (index == -1) { // free identifier
				return pc.factory.makeFreeIdentifier(tokenVal, loc);
			} else { // bound identifier
				return pc.factory.makeBoundIdentifier(index, loc);
			}
		}

		@Override
		protected int getKind() {
			return B_MATH.getIDENT();
		}
		
	};
	
	static final INudParser<FreeIdentifier> FREE_IDENT_SUBPARSER = new INudParser<FreeIdentifier>() {

		public FreeIdentifier nud(ParserContext pc) throws SyntaxError {
			final Identifier ident = IDENT_SUBPARSER.nud(pc);
			if (!(ident instanceof FreeIdentifier)) {
				throw new SyntaxError(new ASTProblem(ident.getSourceLocation(),
						ProblemKind.FreeIdentifierExpected,
						ProblemSeverities.Error));
			}
			return (FreeIdentifier) ident;
		}
	};

	static final INudParser<BoundIdentDecl> BOUND_IDENT_DECL_SUBPARSER = new ValuedNudParser<BoundIdentDecl>(BOUND_IDENT_DECL) {

		@Override
		protected BoundIdentDecl makeValue(ParserContext pc, String tokenVal,
				SourceLocation loc) throws SyntaxError {
			Type type = null;
			if (pc.t.kind == _TYPING) {
				pc.pushParentKind();
				pc.progress();
				try {
					type = pc.subParse(TYPE_PARSER);
				} finally {
					pc.popParentKind();
				}
			}
			return pc.factory.makeBoundIdentDecl(tokenVal, pc.getSourceLocation(), type);
		}

		@Override
		protected int getKind() {
			return B_MATH.getIDENT();
		}
	};

	static final INudParser<IntegerLiteral> INTLIT_SUBPARSER = new ValuedNudParser<IntegerLiteral>(INTLIT) {
	
		@Override
		protected IntegerLiteral makeValue(ParserContext pc, String tokenVal,
				SourceLocation loc) throws SyntaxError {
			try {
				final BigInteger value = BigInteger.valueOf((Integer
						.valueOf(tokenVal)));
				return pc.factory.makeIntegerLiteral(value, loc);
			} catch (NumberFormatException e) {
				// FIXME this is rather a problem with the lexer: it should
				// never have returned a _INTLIT token kind
				throw new SyntaxError(new ASTProblem(loc,
						ProblemKind.IntegerLiteralExpected,
						ProblemSeverities.Error));
			}
		}

		@Override
		protected int getKind() {
			return B_MATH.getINTLIT();
		}
	};

	static final INudParser<Predicate> PRED_VAR_SUBPARSER = new ValuedNudParser<Predicate>(
			PREDICATE_VARIABLE) {

		@Override
		protected Predicate makeValue(ParserContext pc,
				String tokenVal, SourceLocation loc) throws SyntaxError {
			if (!pc.withPredVar) {
				pc.result.addProblem(new ASTProblem(loc,
						ProblemKind.PredicateVariableNotAllowed,
						ProblemSeverities.Error, tokenVal));
				return pc.factory.makeLiteralPredicate(Formula.BTRUE, loc);
			}
			return pc.factory.makePredicateVariable(tokenVal, loc);
		}

		@Override
		protected int getKind() {
			return B_MATH.getPREDVAR();
		}
	};

	/**
	 * Parses expressions outside bound identifier declarations. Always returns
	 * an expression with the same tag as left.
	 */
	static final ILedParser<Expression> OFTYPE = new ILedParser<Expression>() {
		
		private static final String POW_ALPHA = "\u2119(alpha)";
		private static final String POW_ALPHA_ALPHA = "\u2119(alpha \u00d7 alpha)";
		private static final String POW_ALPHA_BETA_ALPHA = "\u2119(alpha \u00d7 beta \u00d7 alpha)";
		private static final String POW_ALPHA_BETA_BETA = "\u2119(alpha \u00d7 beta \u00d7 beta)";
		
		public Expression led(Formula<?> left, ParserContext pc) throws SyntaxError {
			final int tag = left.getTag();
			if (!pc.isParenthesized()) {
				throw newMissingParenError(pc);
			}
			if (!isTypedGeneric(tag)) {
				throw newUnexpectedOftype(pc);
			}
			pc.progress();
			
			Type type = pc.subParse(TYPE_PARSER);
			final SourceLocation typeLoc = pc.getSourceLocation();
			if (pc.t.kind != _RPAR) {
				throw newMissingParenError(pc);
			}
			if (!checkValidTypedGeneric(tag, type, typeLoc, pc.result)) {
				type = null;
			}
			return pc.factory.makeAtomicExpression(tag, pc
					.getEnclosingSourceLocation(), type);
		}

		private boolean isTypedGeneric(int tag) {
			switch (tag) {
			case Formula.EMPTYSET:
			case Formula.KID_GEN:
			case Formula.KPRJ1_GEN:
			case Formula.KPRJ2_GEN:
				return true;
			default:
				return false;
			}
		}
		
		private SyntaxError newUnexpectedOftype(ParserContext pc) {
			return new SyntaxError(new ASTProblem(pc.makeSourceLocation(pc.t),
					ProblemKind.UnexpectedOftype, ProblemSeverities.Error));
		}
		
		private SyntaxError newMissingParenError(ParserContext pc) {
			return new SyntaxError(new ASTProblem(pc.makeSourceLocation(pc.t),
					ProblemKind.OftypeMissingParentheses,
					ProblemSeverities.Error));
		}

		
		private boolean checkValidTypedGeneric(int tag, Type type,
				SourceLocation typeLoc, ParseResult result) throws SyntaxError {
			switch (tag) {
			case Formula.EMPTYSET:
				if (!(type instanceof PowerSetType)) {
					result.addProblem(newInvalidGenType(typeLoc, POW_ALPHA));
					return false;
				}
				break;
			case Formula.KID_GEN:
				final Type source = type.getSource();
				if (!(source != null && source.equals(type.getTarget()))) {
					result.addProblem(newInvalidGenType(typeLoc, POW_ALPHA_ALPHA));
					return false;
				}
				break;
			case Formula.KPRJ1_GEN:
				if (!isValidPrjType(type, true)) {
					result.addProblem(newInvalidGenType(typeLoc, POW_ALPHA_BETA_ALPHA));
					return false;
				}
				break;
			case Formula.KPRJ2_GEN:
				if (!isValidPrjType(type, false)) {
					result.addProblem(newInvalidGenType(typeLoc, POW_ALPHA_BETA_BETA));
					return false;
				}
				break;
			default:
				// tag has already been checked
				assert false;
			}
			return true;
		}

		private ASTProblem newInvalidGenType(SourceLocation loc, String expected) {
			return new ASTProblem(loc,
					ProblemKind.InvalidGenericType,
					ProblemSeverities.Error,
					expected);
		}
		
		private boolean isValidPrjType(Type type, boolean left) {
			final Type source = type.getSource();
			final Type target = type.getTarget();
			if (!(source instanceof ProductType)) {
				return false;
			}

			final ProductType prodSource = (ProductType) source;
			final Type child;
			if (left) {
				child = prodSource.getLeft();
			} else {
				child = prodSource.getRight();
			}
			return target.equals(child);
		}

	};
	
	static class AtomicExpressionParser extends PrefixNudParser<AtomicExpression> {
	
		protected AtomicExpressionParser(int tag) {
			super(tag);
		}
	
		@Override
		protected AtomicExpression parseRight(ParserContext pc)
				throws SyntaxError {
			return pc.factory.makeAtomicExpression(tag, pc.getSourceLocation());
		}

	}

	static class ExtendedAtomicExpressionParser extends PrefixNudParser<ExtendedExpression> {
		
		protected ExtendedAtomicExpressionParser(int tag) {
			super(tag);
		}
	
		@Override
		protected ExtendedExpression parseRight(ParserContext pc)
				throws SyntaxError {
			return checkAndMakeExtendedExpr(pc.factory, tag, Collections
					.<Expression> emptyList(), pc.getSourceLocation());
		}

	}

	static class BinaryExpressionInfix extends DefaultLedExprParser<BinaryExpression> {

		public BinaryExpressionInfix(int tag) {
			super(tag);
		}
		
		@Override
		protected BinaryExpression makeValue(FormulaFactory factory, Expression left,
				Expression right, SourceLocation loc) throws SyntaxError {
			return factory.makeBinaryExpression(tag, left, right, loc);
		}

	}
	
	static class ExtendedBinaryExpressionInfix extends DefaultLedExprParser<ExtendedExpression> {

		public ExtendedBinaryExpressionInfix(int tag) {
			super(tag);
		}

		@Override
		protected ExtendedExpression makeValue(FormulaFactory factory,
				Expression left, Expression right, SourceLocation loc) throws SyntaxError {
			return checkAndMakeExtendedExpr(factory, tag, asList(left,
					right), loc);
		}

	}

	static class AssociativeExpressionInfix extends AssociativeLedParser<Expression, Expression> {


		protected AssociativeExpressionInfix(int tag) {
			super(tag, EXPR_PARSER);
		}

		@Override
		protected Expression makeResult(FormulaFactory factory,
				List<Expression> children, SourceLocation loc) throws SyntaxError {
			return factory.makeAssociativeExpression(tag, children, loc);
		}

		@Override
		protected Expression asChildType(Formula<?> left) throws SyntaxError {
			return asExpression(left);
		}
		
	}

	static class ExtendedAssociativeExpressionInfix extends AssociativeExpressionInfix {

		public ExtendedAssociativeExpressionInfix(int tag) {
			super(tag);
		}
		
		@Override
		protected ExtendedExpression makeResult(FormulaFactory factory,
				List<Expression> children, SourceLocation loc) throws SyntaxError {
			return checkAndMakeExtendedExpr(factory, tag, children, loc);
		}
	}
	
	static class AssociativePredicateInfix extends AssociativeLedParser<AssociativePredicate, Predicate> {

		public AssociativePredicateInfix(int tag) {
			super(tag, PRED_PARSER);
		}

		@Override
		protected Predicate asChildType(Formula<?> left) throws SyntaxError {
			return asPredicate(left);
		}

		@Override
		protected AssociativePredicate makeResult(FormulaFactory factory,
				List<Predicate> children, SourceLocation loc)
				throws SyntaxError {
			return factory.makeAssociativePredicate(tag, children, loc);
		}
	}

	static class RelationalPredicateInfix extends DefaultLedExprParser<RelationalPredicate> {

		public RelationalPredicateInfix(int tag) {
			super(tag);
		}

		@Override
		protected RelationalPredicate makeValue(FormulaFactory factory,
				Expression left, Expression right, SourceLocation loc) throws SyntaxError {
			return factory.makeRelationalPredicate(tag, left, right, loc);
		}
	}

	static class LedImage extends DefaultLedExprParser<BinaryExpression> {

		private final int closeKind;
		
		protected LedImage(int tag, int closeKind) {
			super(tag);
			this.closeKind = closeKind;
		}

		@Override
		protected Expression parseRight(ParserContext pc) throws SyntaxError {
			// FIXME parsing this way prevents priority and compatibility checks
			// with operators that follow the closing parenthesis
			final Expression right = super.parseRight(pc);
			pc.progress(closeKind);
			return right;
		}
		
		@Override
		protected BinaryExpression makeValue(FormulaFactory factory, Expression left,
				Expression right, SourceLocation loc) throws SyntaxError {
			return factory.makeBinaryExpression(tag, left, right, loc);
		}
		
	}

	static class LiteralPredicateParser extends PrefixNudParser<LiteralPredicate> {

		public LiteralPredicateParser(int tag) {
			super(tag);
		}

		@Override
		protected LiteralPredicate parseRight(ParserContext pc)
				throws SyntaxError {
			return pc.factory.makeLiteralPredicate(tag, pc.getSourceLocation());
		}
	}

	static final INudParser<UnaryPredicate> NOT_PARSER = new PrefixNudParser<UnaryPredicate>(NOT) {

		@Override
		protected UnaryPredicate parseRight(ParserContext pc)
				throws SyntaxError {
			final Predicate pred = pc.subParse(PRED_PARSER);
			return pc.factory.makeUnaryPredicate(tag, pred, pc.getSourceLocation());
		}
	};

	static class BinaryPredicateParser extends DefaultLedPredParser<BinaryPredicate> {

		public BinaryPredicateParser(int tag) {
			super(tag);
		}

		@Override
		protected BinaryPredicate makeValue(FormulaFactory factory, Predicate left,
				Predicate right, SourceLocation loc) throws SyntaxError {
			return factory.makeBinaryPredicate(tag, left, right, loc);
		}
	}

	static class QuantifiedPredicateParser extends PrefixNudParser<QuantifiedPredicate> {

		public QuantifiedPredicateParser(int tag) {
			super(tag);
		}

		@Override
		public QuantifiedPredicate parseRight(ParserContext pc) throws SyntaxError {
			final List<BoundIdentDecl> boundIdentifiers = pc.subParseNoBinding(BOUND_IDENT_DECL_LIST_PARSER);
			pc.progress(_DOT);
			final Predicate pred = pc.subParse(PRED_PARSER, boundIdentifiers);

			return pc.factory.makeQuantifiedPredicate(tag, boundIdentifiers,
					pred, pc.getSourceLocation());
		}
	}

	static class UnaryExpressionParser extends ParenNudParser<UnaryExpression, Expression> {

		public UnaryExpressionParser(int tag) {
			super(tag, EXPR_PARSER);
		}

		@Override
		protected UnaryExpression makeValue(FormulaFactory factory, Expression child,
				SourceLocation loc) {
			return factory.makeUnaryExpression(tag, child, loc);
		}

	}

	static class GenExpressionParser implements INudParser<Expression> {
		
		private final INudParser<UnaryExpression> parserV1;
		private final INudParser<AtomicExpression> parserV2;
		
		public GenExpressionParser(int unaryTagV1, int atomicTagV2) {
			this.parserV1 = new UnaryExpressionParser(unaryTagV1);
			this.parserV2 = new AtomicExpressionParser(atomicTagV2);
		}
		
		public Expression nud(ParserContext pc) throws SyntaxError {
			switch (pc.version) {
			case V1:
				return parserV1.nud(pc);
			case V2:
				return parserV2.nud(pc);
			default:
				throw new IllegalArgumentException(
						"Unsupported language version: " + pc.version);
			}
		}
	}
	
	static final ILedParser<UnaryExpression> CONVERSE_PARSER = new DefaultLedExprParser<UnaryExpression>(CONVERSE) {

		@Override
		protected UnaryExpression makeValue(FormulaFactory factory, Expression left,
				Expression right, SourceLocation loc) throws SyntaxError {
			return factory.makeUnaryExpression(tag, left, loc);
		}
		
		@Override
		protected Expression parseRight(ParserContext pc) throws SyntaxError {
			// nothing to parse at right
			return null;
		}
	};
	
	static final INudParser<BoolExpression> KBOOL_PARSER = new ParenNudParser<BoolExpression, Predicate>(KBOOL, PRED_PARSER) {

		@Override
		protected BoolExpression makeValue(FormulaFactory factory, Predicate child,
				SourceLocation loc) {
			return factory.makeBoolExpression(child, loc);
		}

	};

	static final INudParser<SetExtension> SETEXT_PARSER = new PrefixNudParser<SetExtension>(SETEXT) {
		
		@Override
		public SetExtension parseRight(ParserContext pc) throws SyntaxError {
			final List<Expression> exprs;
			if (pc.t.kind == _RBRACE) { // only place where a list may be empty
				exprs = Collections.emptyList();
			} else {
				exprs = pc.subParse(EXPR_LIST_PARSER);
			}
			pc.progress(_RBRACE);
			return pc.factory.makeSetExtension(exprs, pc.getSourceLocation());
		}
	};
	
	static class ExplicitQuantExpr extends PrefixNudParser<QuantifiedExpression> {
		
		protected ExplicitQuantExpr(int tag) {
			super(tag);
		}

		@Override
		protected QuantifiedExpression parseRight(ParserContext pc) throws SyntaxError {
			final List<BoundIdentDecl> boundIdents = pc.subParseNoBinding(BOUND_IDENT_DECL_LIST_PARSER);
			pc.progress(_DOT);
			final Predicate pred = pc.subParseNoParent(PRED_PARSER, boundIdents);
			pc.progress(_MID);
			final Expression expr = pc.subParse(EXPR_PARSER, boundIdents);
			progressClose(pc);

			return pc.factory.makeQuantifiedExpression(tag, boundIdents, pred,
					expr, pc.getSourceLocation(), Form.Explicit);
		}
	
		protected void progressClose(ParserContext pc) throws SyntaxError {
			// do nothing by default
		}
	}
	
	static final ExplicitQuantExpr CSET_EXPLICIT = new ExplicitQuantExpr(CSET) {
		@Override
		protected void progressClose(ParserContext pc) throws SyntaxError {
			pc.progress(_RBRACE);
		}
	};
	
	static class ImplicitQuantExpr extends PrefixNudParser<QuantifiedExpression> {
		
		protected ImplicitQuantExpr(int tag) {
			super(tag);
		}

		@Override
		protected final QuantifiedExpression parseRight(ParserContext pc)
		throws SyntaxError {
			final Expression expr = pc.subParseNoBinding(EXPR_PARSER);
			pc.progress(_MID);
			final List<BoundIdentDecl> boundIdents = new ArrayList<BoundIdentDecl>();
			final Expression boundExpr = expr.bindAllFreeIdents(boundIdents, pc.factory);

			final Predicate pred = pc.subParseNoParent(PRED_PARSER, boundIdents);
			progressClose(pc);

			return pc.factory.makeQuantifiedExpression(tag, boundIdents, pred,
					boundExpr, pc.getSourceLocation(), Form.Implicit);
		}
		
		protected void progressClose(ParserContext pc) throws SyntaxError {
			// do nothing by default
		}
		
	}
	
	static final ImplicitQuantExpr CSET_IMPLICIT = new ImplicitQuantExpr(CSET) {
		@Override
		protected void progressClose(ParserContext pc) throws SyntaxError {
			pc.progress(_RBRACE);
		}
	};
	
	static final INudParser<QuantifiedExpression> CSET_LAMBDA = new PrefixNudParser<QuantifiedExpression>(CSET) {
		
		@Override
		public QuantifiedExpression parseRight(ParserContext pc) throws SyntaxError {
			final PatternParser pattParser = new PatternParser(pc.result);
			final Pattern pattern = pc.subParseNoBinding(pattParser);
			pc.progress(_DOT);
			final List<BoundIdentDecl> boundDecls = pattern.getDecls();
			final Predicate pred = pc.subParseNoParent(PRED_PARSER, boundDecls);
			pc.progress(_MID);
			final Expression expr = pc.subParse(EXPR_PARSER, boundDecls);
			
			final Expression pair = pc.factory.makeBinaryExpression(MAPSTO,
					pattern.getPattern(), expr, null);
			return pc.factory.makeQuantifiedExpression(tag, boundDecls, pred,
					pair, pc.getSourceLocation(), Form.Lambda);
		}
	};

	private static final INudParser<MultiplePredicate> MULTIPLE_PREDICATE_PARSER = new ParenNudParser<MultiplePredicate, List<Expression>>(KPARTITION, EXPR_LIST_PARSER) {

		@Override
		protected MultiplePredicate makeValue(FormulaFactory factory,
				List<Expression> child, SourceLocation loc) {
			return factory.makeMultiplePredicate(tag, child, loc);
		}

	};
	
	static final INudParser<? extends Formula<?>> PARTITION_PARSER = new VersionConditionalNudParser(IDENT_SUBPARSER, MULTIPLE_PREDICATE_PARSER);

	static final INudParser<SimplePredicate> FINITE_PARSER = new ParenNudParser<SimplePredicate, Expression>(KFINITE, EXPR_PARSER) {

		@Override
		protected SimplePredicate makeValue(FormulaFactory factory,
				Expression child, SourceLocation loc) {
			return factory.makeSimplePredicate(tag, child, loc);
		}

	};
	
	static final INudParser<Expression> UNMINUS_PARSER = new INudParser<Expression>() {

		public Expression nud(ParserContext pc) throws SyntaxError {
			final int minusPos = pc.t.pos;
			pc.progress();
			final Expression expr = pc.subParse(EXPR_PARSER);
			final SourceLocation loc = pc.getSourceLocation();
	        if (expr instanceof IntegerLiteral
	        		&& expr.getSourceLocation().getStart() == minusPos + 1) {
				// A unary minus followed by an integer literal, glued together,
				// this is a negative integer literal
	        	final IntegerLiteral lit = (IntegerLiteral) expr;
	        	return pc.factory.makeIntegerLiteral(lit.getValue().negate(), loc);
	        }
	  		return pc.factory.makeUnaryExpression(UNMINUS, expr, loc);
		}

	};
	
	static class ExtendedExprParen extends ParenNudParser<ExtendedExpression, List<Expression>> {

		protected ExtendedExprParen(int tag) {
			super(tag, EXPR_LIST_PARSER);
		}

		@Override
		protected ExtendedExpression makeValue(FormulaFactory factory,
				List<Expression> children, SourceLocation loc) throws SyntaxError {
			return checkAndMakeExtendedExpr(factory, tag, children, loc);
		}

	}
	
	static ExtendedExpression checkAndMakeExtendedExpr(FormulaFactory factory, int tag,
			List<Expression> children, SourceLocation loc) throws SyntaxError {
		final IExpressionExtension extension = (IExpressionExtension) factory
				.getExtension(tag);
		if (!extension.getKind().checkPreconditions(
				children.toArray(new Expression[children.size()]), NO_PRED)) {
			throw new SyntaxError(new ASTProblem(loc,
					ProblemKind.ExtensionPreconditionError,
					ProblemSeverities.Error));
		}
		return factory.makeExtendedExpression(extension, children, Collections
				.<Predicate> emptyList(), loc);
	}
}
