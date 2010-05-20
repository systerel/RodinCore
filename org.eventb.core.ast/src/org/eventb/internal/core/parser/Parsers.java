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
import static org.eventb.internal.core.parser.AbstractGrammar.*;
import static org.eventb.internal.core.parser.BMath.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.AssociativeExpression;
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
import org.eventb.core.ast.InvalidExpressionException;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;
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
import org.eventb.internal.core.parser.GenParser.IMainParser;
import org.eventb.internal.core.parser.GenParser.ParserContext;
import org.eventb.internal.core.parser.GenParser.SyntaxError;
import org.eventb.internal.core.parser.GenParser.ParserContext.SavedContext;

/**
 * @author Nicolas Beauger
 *
 */
public class Parsers {

	private static abstract class AbstractSubParser<T> {

		// TODO move tag downwards to sub-classes that really require it
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

	private static abstract class ParenNudParser<T, U> extends PrefixNudParser<T> implements INudParser<T> {

		private final INudParser<U> childParser;
		
		protected ParenNudParser(int tag, INudParser<U> childParser) {
			super(tag);
			this.childParser = childParser;
		}

		@Override
		protected final T parseRight(ParserContext pc) throws SyntaxError {
			pc.progressOpenParen();
			final U child = pc.subParse(childParser);
			pc.progressCloseParen();
			return makeValue(pc, child, pc.getSourceLocation());
		}
		
		protected abstract T makeValue(ParserContext pc, U child, SourceLocation loc);
	}
	
	private static abstract class ValuedNudParser<T> extends AbstractSubParser<T> implements INudParser<T> {

		protected ValuedNudParser(int tag) {
			super(tag);
		}
		
		public final T nud(ParserContext pc) throws SyntaxError {
			final String tokenVal = pc.t.val;
			pc.progress();
			final SourceLocation loc = pc.getSourceLocation();
			return makeValue(pc, tokenVal, loc);
		}

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

	// TODO try to do the same as nud parsers, with generic types for left and right
	// TODO make assignment parser a led parser
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
			final Left typedLeft = asLeftType(left);
			final Right right = parseRight(pc);
			return makeValue(pc, typedLeft, right, pc.getSourceLocation());
		}

		protected abstract Left asLeftType(Formula<?> left) throws SyntaxError;
		
		protected abstract T makeValue(ParserContext pc, Left left,
				Right right, SourceLocation loc) throws SyntaxError;
	}
	
	private static abstract class DefaultLedExprParser<T> extends DefaultLedParser<T, Expression, Expression> implements ILedParser<T> {

		protected DefaultLedExprParser(int tag) {
			super(tag, EXPR_PARSER);
		}
		
		@Override
		protected final Expression asLeftType(Formula<?> left) throws SyntaxError {
			return asExpression(left);
		}
	}
	
	private static abstract class DefaultLedPredParser<T> extends DefaultLedParser<T, Predicate, Predicate> implements ILedParser<T> {

		protected DefaultLedPredParser(int tag) {
			super(tag, PRED_PARSER);
		}
		
		@Override
		protected Predicate asLeftType(Formula<?> left) throws SyntaxError {
			return asPredicate(left);
		}
	}
	
	private static String makeSynErrMessage(Formula<?> formula, Class<?> clazz) {
		return "expected a " + clazz.getCanonicalName() + ", but was "
				+ formula + " at position " + formula.getSourceLocation();
	}

	static Predicate asPredicate(Formula<?> formula) throws SyntaxError {
		if (!(formula instanceof Predicate)) {
			throw new SyntaxError(makeSynErrMessage(formula, Predicate.class));
		}
		return (Predicate) formula;
	}

	static Expression asExpression(Formula<?> formula) throws SyntaxError {
		if (!(formula instanceof Expression)) {
			throw new SyntaxError(makeSynErrMessage(formula, Expression.class));
		}
		return (Expression) formula;
	}

	private static abstract class DefaultMainParser<T> extends AbstractSubParser<T> implements IMainParser<T> {

		protected DefaultMainParser() {
			super(NO_TAG);
		}

		public final T nud(ParserContext pc) throws SyntaxError {
			return parse(pc);
		}
	}
	
	private static class FormulaParser extends DefaultMainParser<Formula<?>> {
		public FormulaParser() {
			// void constructor
		}
		
		public Formula<?> parse(ParserContext pc)
				throws SyntaxError {
		
			final List<INudParser<? extends Formula<?>>> nudParsers = getNudParsers(pc);
			Formula<?> left = nudParse(pc, nudParsers);
			while (pc.canProgressRight()) {
				final ILedParser<? extends Formula<?>> ledParser = getLedParser(pc);
				pc.pushParentKind(pc.t.kind);
				pc.progress();
				try {
					left = ledParser.led(left, pc);
				} finally {
					pc.popParentKind();
				}
			}
			return left;
		}
		
		// TODO implement backtracking for led parsers as well 
	
		private static Formula<?> nudParse(ParserContext pc,
				List<INudParser<? extends Formula<?>>> nudParsers) throws SyntaxError {
			final List<SyntaxError> errors = new ArrayList<SyntaxError>();
			final Iterator<INudParser<? extends Formula<?>>> iter = nudParsers.iterator();
			final SavedContext savedContext = pc.save();
			while(iter.hasNext()) {
				final INudParser<? extends Formula<?>> nudParser = iter.next();
				try {
					// FIXME the call to nud may add problems to pc.result
					// without throwing an exception
					return nudParser.nud(pc);
				} catch (SyntaxError e) {
					errors.add(e);
					pc.restore(savedContext);
				}
			}
			throw newCompoundError(errors);
		}
	
		// errors must be non empty 
		private static SyntaxError newCompoundError(List<SyntaxError> errors) {
			final StringBuilder reason = new StringBuilder(
					"Parse failed because");
			if (errors.size()>=2) {
				reason.append(" either: ");
			} else {
				reason.append(": ");
			}
			final Iterator<SyntaxError> iter = errors.iterator();
			while(iter.hasNext()) {
			final SyntaxError syntaxError = iter.next();
				reason.append(syntaxError.getMessage());
				if (iter.hasNext()) {
					reason.append(" OR ");
				}
			}
			return new SyntaxError(reason.toString());
		}
	}

	static final FormulaParser FORMULA_PARSER = new FormulaParser();

	private static class TypeParser extends DefaultMainParser<Type> {
		public TypeParser() {
			// void constructor
		}
		
		public Type parse(ParserContext pc) throws SyntaxError {
			pc.startParsingType();
			final Expression expression = pc.subParse(EXPR_PARSER);
			if (!expression.isATypeExpression()) {
				throw new SyntaxError(
						"expected a type expression at position "
								+ pc.getSourceLocation());
			}
			try {
				return expression.toType(pc.factory);
			} catch (InvalidExpressionException e) {
				// TODO should not happen (already checked)
				e.printStackTrace();
				return null;
			} finally {
				pc.stopParsingType();
			}
		}
	}

	static final TypeParser TYPE_PARSER = new TypeParser();

	private static class PredicateParser extends DefaultMainParser<Predicate> {
		public PredicateParser() {
			// void constructor
		}
		
		public Predicate parse(ParserContext pc) throws SyntaxError {
			final Formula<?> formula = FORMULA_PARSER.parse(pc);
			return asPredicate(formula);
		}
	}

	static final PredicateParser PRED_PARSER = new PredicateParser();

	private static class ExpressionParser extends DefaultMainParser<Expression> {
		
		public ExpressionParser() {
			// void constructor
		}
		
		public Expression parse(ParserContext pc) throws SyntaxError {
			final Formula<?> formula = FORMULA_PARSER.parse(pc);
			return asExpression(formula);
		}
	}

	static final ExpressionParser EXPR_PARSER = new ExpressionParser();

	// returns a non empty list
	static List<INudParser<? extends Formula<?>>> getNudParsers(ParserContext pc)
			throws SyntaxError {
		final List<INudParser<? extends Formula<?>>> subParsers = pc.getNudParsers();
		if (subParsers.isEmpty()) {
			throw new SyntaxError("don't know how to parse: " + pc.t.val);
		}
		return subParsers;
	}

	static ILedParser<? extends Formula<?>> getLedParser(ParserContext pc)
	throws SyntaxError {
		final ILedParser<? extends Formula<?>> subParser = pc.getLedParser();
		if (subParser == null) {
			throw new SyntaxError("don't know how to parse: " + pc.t.val);
		}
		return subParser;
	}

	// Takes care of the bindings.
	static final INudParser<Identifier> IDENT_SUBPARSER = new ValuedNudParser<Identifier>(NO_TAG) {

		@Override
		protected Identifier makeValue(ParserContext pc, String tokenVal,
				SourceLocation loc) {
			final int index = pc.getBoundIndex(tokenVal);
			if (index == -1) { // free identifier
				final Type type;
				if (pc.isParsingType()) {
					type = pc.factory.makePowerSetType(pc.factory.makeGivenType(tokenVal));
				} else {
					type = null;
				}
				return pc.factory.makeFreeIdentifier(tokenVal, loc, type);
			} else { // bound identifier
				return pc.factory.makeBoundIdentifier(index, loc);
			}
		}
		
	};
	
	static final IMainParser<FreeIdentifier> FREE_IDENT_SUBPARSER = new DefaultMainParser<FreeIdentifier>() {

		public FreeIdentifier parse(ParserContext pc) throws SyntaxError {
			final Identifier ident = IDENT_SUBPARSER.nud(pc);
			if (!(ident instanceof FreeIdentifier)) {
				throw new SyntaxError("expected a free identifier at position "
						+ pc.getSourceLocation());
			}
			return (FreeIdentifier) ident;
		}
	};

	static final IMainParser<BoundIdentDecl> BOUND_IDENT_DECL_SUBPARSER = new DefaultMainParser<BoundIdentDecl>() {

		public BoundIdentDecl parse(ParserContext pc) throws SyntaxError {
			final FreeIdentifier ident = FREE_IDENT_SUBPARSER.nud(pc);
			final FreeIdentifier typed;
			if (pc.t.kind == _TYPING) {
				pc.progress();
				typed = (FreeIdentifier) OFTYPE.led(ident, pc);
			} else {
				typed = ident;
			}
			return typed.asDecl(pc.factory);
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
				// TODO recover using ZERO ? add a problem instead (=> backtrack on problems)
				throw new SyntaxError("Expected a number, but was: "
						+ tokenVal);
			}
		}
	};

	static final INudParser<PredicateVariable> PRED_VAR_SUBPARSER = new ValuedNudParser<PredicateVariable>(
			PREDICATE_VARIABLE) {

		@Override
		protected PredicateVariable makeValue(ParserContext pc,
				String tokenVal, SourceLocation loc) throws SyntaxError {
			if (!pc.withPredVar) {
				throw new SyntaxError(
						"Predicate variables are forbidden in this context");
			}
			return pc.factory.makePredicateVariable(tokenVal, loc);
		}
	};

	// TODO verify that all formulae inside parentheses are parsed with a GROUP0
	// parent kind
	static final IMainParser<Formula<?>> CLOSED_SUGAR = new DefaultMainParser<Formula<?>> () {

		public Formula<?> parse(ParserContext pc) throws SyntaxError {
			pc.progressOpenParen();
			final Formula<?> formula = pc.subParse(FORMULA_PARSER);
			pc.progressCloseParen();
			return formula;
		}
	};

	// always returns an expression with the same tag as left
	static final DefaultLedParser<Expression, Expression, Type> OFTYPE = new DefaultLedParser<Expression, Expression, Type>(
			NO_TAG, TYPE_PARSER) {
		
		@Override
		protected Expression asLeftType(Formula<?> left) throws SyntaxError {
			return asExpression(left);
		}

		@Override
		protected Expression makeValue(ParserContext pc, Expression left,
				Type right, SourceLocation loc) throws SyntaxError {
			switch (left.getTag()) {
			case Formula.FREE_IDENT: // FIXME authorizes Oftype to appear at more places than specified
				final FreeIdentifier ident = (FreeIdentifier) left;
				return pc.factory.makeFreeIdentifier(ident.getName(), loc, right);
			case Formula.EMPTYSET:
			case Formula.KID_GEN:
			case Formula.KPRJ1_GEN:
			case Formula.KPRJ2_GEN:
				return pc.factory.makeAtomicExpression(left.getTag(), loc,
						right);
			default:
				throw new SyntaxError("Unexpected oftype");
			}
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

	static class BinaryExpressionInfix extends DefaultLedExprParser<BinaryExpression> {

		public BinaryExpressionInfix(int tag) {
			super(tag);
		}
		
		@Override
		protected BinaryExpression makeValue(ParserContext pc, Expression left,
				Expression right, SourceLocation loc) throws SyntaxError {
			return pc.factory.makeBinaryExpression(tag, left, right, loc);
		}

	}
	
	static class ExtendedBinaryExpressionInfix extends DefaultLedExprParser<ExtendedExpression> {

		public ExtendedBinaryExpressionInfix(int tag) {
			super(tag);
		}

		@Override
		protected ExtendedExpression makeValue(ParserContext pc,
				Expression left, Expression right, SourceLocation loc) throws SyntaxError {
			final IExpressionExtension extension = (IExpressionExtension) pc.factory
					.getExtension(tag);
			return pc.factory.makeExtendedExpression(extension, asList(left,
					right), Collections.<Predicate> emptySet(), loc);
		}

	}

	static class AssociativeExpressionInfix extends DefaultLedExprParser<Expression> {

		public AssociativeExpressionInfix(int tag) {
			super(tag);
		}

		@Override
		protected Expression makeValue(ParserContext pc, Expression left,
				Expression right, SourceLocation loc) throws SyntaxError {
			final List<Expression> children = new ArrayList<Expression>();
			if (left.getTag() == tag) {
				children.addAll(asList(getChildren(left)));
			} else {
				children.add(left);
			}
			children.add(right);
			return makeResult(pc.factory, children, loc);
		}
		
		protected Expression[] getChildren(Formula<?> exprWithSameTag) {
			return ((AssociativeExpression) exprWithSameTag).getChildren();
		}
		
		protected Expression makeResult(FormulaFactory factory,
				List<Expression> children, SourceLocation loc) {
			return factory.makeAssociativeExpression(tag, children, loc);
		}
		
	}

	static class ExtendedAssociativeExpressionInfix extends AssociativeExpressionInfix {

		public ExtendedAssociativeExpressionInfix(int tag) {
			super(tag);
		}
		
		@Override
		protected Expression[] getChildren(Formula<?> exprWithSameTag) {
			return ((ExtendedExpression) exprWithSameTag).getChildExpressions();
		}
		
		@Override
		protected ExtendedExpression makeResult(FormulaFactory factory,
				List<Expression> children, SourceLocation loc) {
			final IExpressionExtension extension = (IExpressionExtension) factory
					.getExtension(tag);
			return factory.makeExtendedExpression(extension, children, Collections
					.<Predicate> emptyList(), loc);
		}
	}
	
	static class AssociativePredicateInfix extends DefaultLedPredParser<AssociativePredicate> {

		public AssociativePredicateInfix(int tag) {
			super(tag);
		}

		@Override
		protected AssociativePredicate makeValue(ParserContext pc,
				Predicate left, Predicate right, SourceLocation loc)
				throws SyntaxError {
			final List<Predicate> children = new ArrayList<Predicate>();
			if (left.getTag() == tag) {
				children.addAll(asList(((AssociativePredicate) left)
						.getChildren()));
			} else {
				children.add(left);
			}
			children.add(right);
			return pc.factory.makeAssociativePredicate(tag, children, loc);
		}
	}

	static class RelationalPredicateInfix extends DefaultLedExprParser<RelationalPredicate> {

		public RelationalPredicateInfix(int tag) {
			super(tag);
		}

		@Override
		protected RelationalPredicate makeValue(ParserContext pc,
				Expression left, Expression right, SourceLocation loc) throws SyntaxError {
			return pc.factory.makeRelationalPredicate(tag, left, right, loc);
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
			// parse inner expression without caring about the parent kind
			// else f(f(a)) would be rejected as it is a right-associative AST
			final Expression right = super.parseRight(pc);
			pc.progress(closeKind);
			return right;
		}
		
		@Override
		protected BinaryExpression makeValue(ParserContext pc, Expression left,
				Expression right, SourceLocation loc) throws SyntaxError {
			return pc.factory.makeBinaryExpression(tag, left, right, loc);
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
		protected BinaryPredicate makeValue(ParserContext pc, Predicate left,
				Predicate right, SourceLocation loc) throws SyntaxError {
			return pc.factory.makeBinaryPredicate(tag, left, right, loc);
		}
	}

	static class QuantifiedPredicateParser extends PrefixNudParser<QuantifiedPredicate> {

		public QuantifiedPredicateParser(int tag) {
			super(tag);
		}

		@Override
		public QuantifiedPredicate parseRight(ParserContext pc) throws SyntaxError {
			final List<BoundIdentDecl> boundIdentifiers = pc.subParse(BOUND_IDENT_DECL_LIST_PARSER);
			pc.progress(_DOT);
			final Predicate pred = pc.subParse(PRED_PARSER, boundIdentifiers);

			return pc.factory.makeQuantifiedPredicate(tag, boundIdentifiers,
					pred, null);
		}
	}

	static class UnaryExpressionParser extends ParenNudParser<UnaryExpression, Expression> {

		public UnaryExpressionParser(int tag) {
			super(tag, EXPR_PARSER);
		}

		@Override
		protected UnaryExpression makeValue(ParserContext pc, Expression child,
				SourceLocation loc) {
			return pc.factory.makeUnaryExpression(tag, child, loc);
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
		protected UnaryExpression makeValue(ParserContext pc, Expression left,
				Expression right, SourceLocation loc) throws SyntaxError {
			return pc.factory.makeUnaryExpression(tag, left, loc);
		}
		
		@Override
		protected Expression parseRight(ParserContext pc) throws SyntaxError {
			// nothing to parse at right
			return null;
		}
	};
	
	static final INudParser<BoolExpression> KBOOL_PARSER = new ParenNudParser<BoolExpression, Predicate>(KBOOL, PRED_PARSER) {

		@Override
		protected BoolExpression makeValue(ParserContext pc, Predicate child,
				SourceLocation loc) {
			return pc.factory.makeBoolExpression(child, loc);
		}

	};

	static class PatternParser extends DefaultMainParser<Pattern> {
		
		final Pattern pattern;
		
		public PatternParser(ParseResult result) {
			this.pattern = new Pattern(result);
		}

		public Pattern parse(ParserContext pc) throws SyntaxError {
			final PatternAtomParser atomParser = new PatternAtomParser(pattern, this);
			pc.subParse(atomParser);
			while (pc.t.kind == _MAPSTO) {
				pc.progress();
				pc.subParse(atomParser);
				pattern.mapletParsed(pc.getSourceLocation());
			}
			return pattern;
		}

		private static class PatternAtomParser extends DefaultMainParser<Object> {

			private final Pattern pattern;
			private final PatternParser parser;
			
			public PatternAtomParser(Pattern pattern, PatternParser parser) {
				this.pattern = pattern;
				this.parser = parser;
			}

			public Object parse(ParserContext pc) throws SyntaxError {
				if (pc.t.kind == _LPAR) {
					pc.progressOpenParen();
					pc.subParse(parser);
					pc.progressCloseParen();
				} else {
					final FreeIdentifier freeIdentifier = pc.subParse(
							FREE_IDENT_SUBPARSER);
					pattern.declParsed(freeIdentifier.asDecl(pc.factory));
				}
				return null;
			}
		}
	}

	// parses a non empty list of T
	static class AbstListParser<T> extends DefaultMainParser<List<T>> {
	
		private final INudParser<T> parser;
		
		public AbstListParser(INudParser<T> parser) {
			this.parser = parser;
		}

		public List<T> parse(ParserContext pc) throws SyntaxError {
			final List<T> list = new ArrayList<T>();
			T next = pc.subParse(parser);
			list.add(next);
			while (pc.t.kind == _COMMA) {
				pc.progress();
				next = pc.subParse(parser);
				list.add(next);
			}
			return list;
		}
		
	}

	static final AbstListParser<Expression> EXPR_LIST_PARSER = new AbstListParser<Expression>(EXPR_PARSER);
	
	static final AbstListParser<FreeIdentifier> FREE_IDENT_LIST_PARSER = new AbstListParser<FreeIdentifier>(FREE_IDENT_SUBPARSER);
	
	static final AbstListParser<BoundIdentDecl> BOUND_IDENT_DECL_LIST_PARSER = new AbstListParser<BoundIdentDecl>(BOUND_IDENT_DECL_SUBPARSER);
	
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
			final List<BoundIdentDecl> boundIdents = pc.subParse(BOUND_IDENT_DECL_LIST_PARSER);
			pc.progress(_DOT);
			final Predicate pred = pc.subParse(PRED_PARSER, boundIdents);
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
			final Expression expr = pc.subParse(EXPR_PARSER);
			pc.progress(_MID);
			final List<FreeIdentifier> idents = asList(expr.getFreeIdentifiers());
			final List<BoundIdentDecl> boundIdents = makeBoundIdentDeclList(pc.factory, idents);
			final Expression boundExpr = expr.bindTheseIdents(idents, pc.factory);

			final Predicate pred = pc.subParse(PRED_PARSER, boundIdents);
			progressClose(pc);

			return pc.factory.makeQuantifiedExpression(tag, boundIdents, pred,
					boundExpr, pc.getSourceLocation(), Form.Implicit);
		}
		
		private static <T extends Identifier> List<BoundIdentDecl> makeBoundIdentDeclList(
				FormulaFactory factory, List<FreeIdentifier> identList) {
			final List<BoundIdentDecl> boundIdentifiers = new ArrayList<BoundIdentDecl>(
					identList.size());
			for (FreeIdentifier ident : identList) {
				boundIdentifiers.add(ident.asDecl(factory));
			}
			return boundIdentifiers;
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
			final Pattern pattern = pc.subParse(pattParser);
			pc.progress(_DOT);
			final List<BoundIdentDecl> boundDecls = pattern.getDecls();
			final Predicate pred = pc.subParse(PRED_PARSER, boundDecls);
			pc.progress(_MID);
			final Expression expr = pc.subParse(EXPR_PARSER, boundDecls);
			
			final Expression pair = pc.factory.makeBinaryExpression(MAPSTO,
					pattern.getPattern(), expr, null);
			return pc.factory.makeQuantifiedExpression(tag, boundDecls, pred,
					pair, pc.getSourceLocation(), Form.Lambda);
		}
	};


	static List<BoundIdentDecl> makePrimedDecl(List<FreeIdentifier> lhsList, FormulaFactory factory) {
		final List<BoundIdentDecl> decls = new ArrayList<BoundIdentDecl>(lhsList.size());
	    for (FreeIdentifier ident: lhsList) {
			decls.add(ident.asPrimedDecl(factory));
		}
		return decls;
	}

	// used as a main parser; directly called by the general parser.
	/** @see GenParser#parse() */
	static final IMainParser<Assignment> ASSIGNMENT_PARSER = new DefaultMainParser<Assignment>() {

		public Assignment parse(ParserContext pc) throws SyntaxError {
			final List<FreeIdentifier> idents = pc.subParse(FREE_IDENT_LIST_PARSER);
			final Token tokenAfterIdents = pc.t;
			final int tokenKind = tokenAfterIdents.kind;
			pc.progress();

			if (tokenKind == _LPAR) { // FUNIMAGE assignment
				if (idents.size() != 1) {
					throw new SyntaxError("Assignment to function images applies to exactly one function.");
				}
				final FreeIdentifier ident = idents.get(0);
				final Expression index = pc.subParse(EXPR_PARSER);
				pc.progressCloseParen();
				pc.progress(_BECEQ);
				final Expression value = pc.subParse(EXPR_PARSER);
				final Expression overriding = makeFunctionOverriding(ident, index, value, pc.factory);
				return pc.factory.makeBecomesEqualTo(ident, overriding, pc.getSourceLocation());
			} else if (tokenKind == _BECEQ) {
				final List<Expression> values = pc.subParse(EXPR_LIST_PARSER);
				if (idents.size() != values.size()) {
					throw new SyntaxError("incompatible size of left and right parts of assignment");
				}
				return pc.factory.makeBecomesEqualTo(idents, values, pc.getSourceLocation());
			} else if (tokenKind == _BECMO) {
				if (idents.size() != 1) {
					throw new SyntaxError("\'Becomes Member Of\' applies to only one identifier");
				}
				final Expression expr = pc.subParse(EXPR_PARSER);
				return pc.factory.makeBecomesMemberOf(idents.get(0), expr, pc.getSourceLocation());
			} else if (tokenKind == _BECST) {
				final List<BoundIdentDecl> primed = makePrimedDecl(idents, pc.factory);
				final Predicate condition = pc.subParse(PRED_PARSER, primed);
				return pc.factory.makeBecomesSuchThat(idents, primed, condition, pc.getSourceLocation());
			} else {
				throw new SyntaxError("Unknown assignment operator: "
						+ tokenAfterIdents.val);
			}
		}
		
	};
	
	static Expression makeFunctionOverriding(FreeIdentifier ident,
			Expression index, Expression value, FormulaFactory factory) {
		
		Expression pair = factory.makeBinaryExpression(Formula.MAPSTO, index, value, null);
		Expression singletonSet = factory.makeSetExtension(pair, null);
		return factory.makeAssociativeExpression(Formula.OVR, 
				new Expression[] {ident, singletonSet}, null);
	}
	
	static final INudParser<MultiplePredicate> PARTITION_PARSER = new ParenNudParser<MultiplePredicate, List<Expression>>(KPARTITION, EXPR_LIST_PARSER) {

		@Override
		protected MultiplePredicate makeValue(ParserContext pc,
				List<Expression> child, SourceLocation loc) {
			return pc.factory.makeMultiplePredicate(tag, child, loc);
		}

	};
	
	static final INudParser<SimplePredicate> FINITE_PARSER = new ParenNudParser<SimplePredicate, Expression>(KFINITE, EXPR_PARSER) {

		@Override
		protected SimplePredicate makeValue(ParserContext pc,
				Expression child, SourceLocation loc) {
			return pc.factory.makeSimplePredicate(tag, child, loc);
		}

	};
	
	static final INudParser<UnaryExpression> UNMINUS_PARSER = new PrefixNudParser<UnaryExpression>(UNMINUS) {

		@Override
		protected UnaryExpression parseRight(ParserContext pc)
				throws SyntaxError {
			final Expression child = pc.subParse(EXPR_PARSER);
			return pc.factory.makeUnaryExpression(UNMINUS, child, pc.getSourceLocation());
		}

	};
	
}
