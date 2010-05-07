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

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Identifier;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.InvalidExpressionException;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.UnaryExpression;
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

	private static abstract class AbstractSubParser<T> implements ISubParser<T> {

		protected final int tag;

		protected AbstractSubParser(int tag) {
			this.tag = tag;
		}
		
		public int getTag() {
			return tag;
		}
		
	}

	private static abstract class DefaultNudParser<T> extends AbstractSubParser<T> implements INudParser<T> {

		protected DefaultNudParser(int tag) {
			super(tag);
		}

		public final T parse(int parentTag, Formula<?> left, ParserContext pc) throws SyntaxError {
			return nud(pc);
		}
	}

	private static abstract class DefaultLedParser<T> extends AbstractSubParser<T> implements ILedParser<T> {

		protected DefaultLedParser(int tag) {
			super(tag);
		}
		
		public final T parse(int parentTag, Formula<?> left, ParserContext pc) throws SyntaxError {
			return led(left, pc);
		}

	}
	
	private static abstract class DefaultMainParser<T> extends DefaultNudParser<T> implements IMainParser<T> {

		protected DefaultMainParser() {
			super(NO_TAG);
		}

		public final T nud(ParserContext pc) throws SyntaxError {
			return parse(NO_TAG, pc);
		}
	}
	
	private static class FormulaParser extends DefaultMainParser<Formula<?>> {
		public FormulaParser() {
			// void constructor
		}
		
		public Formula<?> parse(int parentTag, ParserContext pc)
				throws SyntaxError {
		
			final List<INudParser<Formula<?>>> nudParsers = getNudParsers(pc);
			Formula<?> left = nudParse(pc, nudParsers);
			while (pc.canProgressRight(parentTag)) {
				final ILedParser<Formula<?>> ledParser = getLedParser(pc);
				pc.progress();
				left = ledParser.led(left, pc);
			}
			return left;
		}
		
		// TODO implement backtracking for led parsers as well 
	
		private static Formula<?> nudParse(ParserContext pc,
				List<INudParser<Formula<?>>> parsers) throws SyntaxError {
			// FIXME store current context and restore it for each sub-parser
			final List<SyntaxError> errors = new ArrayList<SyntaxError>();
			final Iterator<INudParser<Formula<?>>> iter = parsers.iterator();
			final SavedContext savedContext = pc.save();
			while(iter.hasNext()) {
				final INudParser<Formula<?>> nudParser = iter.next();
				try {
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
		
		public Type parse(int parentTag, ParserContext pc) throws SyntaxError {
			final int startPos = pc.t.pos;
			pc.startParsingType();
			final Expression expression = EXPR_PARSER.parse(NO_TAG, pc);
			if (!expression.isATypeExpression()) {
				final int endPos = pc.t.pos;
				throw new SyntaxError(
						"expected a type expression between positions "
								+ startPos + " and " + endPos);
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
		
		public Predicate parse(int parentTag, ParserContext pc) throws SyntaxError {
			final Formula<?> formula = FORMULA_PARSER.parse(parentTag, pc);
			if (!(formula instanceof Predicate)) {
				throw new SyntaxError("expected predicate");
			}
			return (Predicate) formula;
		}
	}

	static final PredicateParser PRED_PARSER = new PredicateParser();

	private static class ExpressionParser extends DefaultMainParser<Expression> {
		
		public ExpressionParser() {
			// void constructor
		}
		
		public Expression parse(int parentTag, ParserContext pc) throws SyntaxError {
			final Formula<?> formula = FORMULA_PARSER.parse(parentTag, pc);
			if (!(formula instanceof Expression)) {
				throw new SyntaxError("expected expression");
			}
			return (Expression) formula;
		}
	}

	static final ExpressionParser EXPR_PARSER = new ExpressionParser();

	// returns a non empty list
	static List<INudParser<Formula<?>>> getNudParsers(ParserContext pc)
			throws SyntaxError {
		final List<INudParser<Formula<?>>> subParsers = pc.getNudParsers();
		if (subParsers.isEmpty()) {
			throw new SyntaxError("don't know how to parse: " + pc.t.val);
		}
		return subParsers;
	}

	static ILedParser<Formula<?>> getLedParser(ParserContext pc)
	throws SyntaxError {
		final ILedParser<Formula<?>> subParser = pc.getLedParser();
		if (subParser == null) {
			throw new SyntaxError("don't know how to parse: " + pc.t.val);
		}
		return subParser;
	}

	static class IdentSubParser extends DefaultNudParser<Identifier> {

		protected IdentSubParser(int tag) {
			super(tag);
		}

		public Identifier nud(ParserContext pc) throws SyntaxError {
			final Identifier ident = makeIdent(pc);
			pc.progress();
			return ident;
		}
		
		// Creates an identifier for the given token.
		// Takes care of the bindings.
		private Identifier makeIdent(ParserContext pc) throws SyntaxError {
			final String name = pc.t.val;
			final int index = pc.getBoundIndex(name);
			final SourceLocation loc = pc.getSourceLocation();
			if (index == -1) {
				final Type type;
				if (pc.isParsingType()) {
					type = pc.factory.makePowerSetType(pc.factory.makeGivenType(name));
				} else {
					type = null;
				}
				return pc.factory.makeFreeIdentifier(name, loc, type);
			} else {
				return pc.factory.makeBoundIdentifier(index, loc);
			}
		}

		protected FreeIdentifier parseFreeIdent(ParserContext pc, String name,
				SourceLocation loc) throws SyntaxError {
			final Type type;
			if (pc.isParsingType()) {
				type = pc.factory.makePowerSetType(pc.factory
						.makeGivenType(name));
			} else {
				type = null;
			}
			return pc.factory.makeFreeIdentifier(name, loc, type);
		}

		protected BoundIdentifier parseBoundIdent(ParserContext pc,
				String name, int index, SourceLocation loc) throws SyntaxError {
			return pc.factory.makeBoundIdentifier(index, loc);
		}

	}
	
	static final IdentSubParser IDENT_SUBPARSER = new IdentSubParser(NO_TAG);
	
	static final INudParser<FreeIdentifier> FREE_IDENT_PARSER = new DefaultNudParser<FreeIdentifier>(
			FREE_IDENT) {

		public FreeIdentifier nud(ParserContext pc) throws SyntaxError {
			final Identifier ident = IDENT_SUBPARSER.nud(pc);
			if (!(ident instanceof FreeIdentifier)) {
				throw new IllegalStateException("expected a free identifier");
			}
			return (FreeIdentifier) ident;
		}
	};

	static final INudParser<IntegerLiteral> INTLIT_SUBPARSER = new DefaultNudParser<IntegerLiteral>(INTLIT) {
	
		public IntegerLiteral nud(ParserContext pc) throws SyntaxError {
			final BigInteger value = BigInteger.valueOf((Integer // FIXME NumberFormatException
					.valueOf(pc.t.val)));
			pc.progress();
			return pc.factory.makeIntegerLiteral(value, pc.getSourceLocation());
		}
	};

	// TODO verify that all parser calls are made with NO_TAG when parsing
	// formulae inside parentheses
	static final INudParser<Formula<?>> CLOSED_SUGAR = new DefaultNudParser<Formula<?>> (NO_TAG) {

		public Formula<?> nud(ParserContext pc) throws SyntaxError {
			pc.progressOpenParen();
			final Formula<?> formula = pc.subParse(NO_TAG, FORMULA_PARSER);
			pc.progressCloseParen();
			return formula;
		}
	};

	// FIXME current design does not allow subparsers with no tag
	static final ILedParser<Formula<?>> OFTYPE = new DefaultLedParser<Formula<?>>(OFTYPE_TAG) {
		
		public Formula<?> led(Formula<?> left, ParserContext pc)
				throws SyntaxError {
			final Type type = pc.subParse(NO_TAG, TYPE_PARSER);
			switch (left.getTag()) {
			case Formula.EMPTYSET:
				return pc.factory.makeEmptySet(type, pc.getSourceLocation());
			default:
				throw new SyntaxError("Unexpected oftype");
			}
		}
	};
	
	static class AtomicExpressionParser extends DefaultNudParser<AtomicExpression> {
	
		protected AtomicExpressionParser(int tag) {
			super(tag);
		}
	
		public AtomicExpression nud(ParserContext pc)
				throws SyntaxError {
			final AtomicExpression atomExpr = pc.factory.makeAtomicExpression(
					tag, pc.getSourceLocation());
			pc.progress();
			return atomExpr;
		}

	}

	static class BinaryExpressionInfix extends DefaultLedParser<Expression> {

		public BinaryExpressionInfix(int tag) {
			super(tag);
		}
		
		public Expression led(Formula<?> left, ParserContext pc)
				throws SyntaxError {
			final Expression right = pc.subParse(tag, EXPR_PARSER);
			if (!(left instanceof Expression)) {
				throw new SyntaxError("expected expressions");
			}
			final SourceLocation srcLoc = pc.getSourceLocation();
			return makeResult(pc.factory, (Expression) left,
					right, srcLoc);
		}
		
		protected Expression makeResult(FormulaFactory factory,
				Expression left, Expression right, SourceLocation srcLoc) {
			return factory.makeBinaryExpression(tag, left, right, srcLoc);
		}

	}
	
	static class ExtendedBinaryExpressionInfix extends BinaryExpressionInfix {

		public ExtendedBinaryExpressionInfix(int tag) {
			super(tag);
		}

		@Override
		protected Expression makeResult(FormulaFactory factory,
				Expression left, Expression right, SourceLocation srcLoc) {
			final IExpressionExtension extension = (IExpressionExtension) factory
					.getExtension(tag);

			return factory.makeExtendedExpression(extension,
					asList(left, right), Collections.<Predicate> emptySet(),
					srcLoc);
		}

	}

	static class AssociativeExpressionInfix extends DefaultLedParser<Expression> {

		public AssociativeExpressionInfix(int tag) {
			super(tag);
		}

		public Expression led(Formula<?> left, ParserContext pc)
				throws SyntaxError {
			final Expression right = pc.subParse(tag, EXPR_PARSER);
			if (!(left instanceof Expression)) {
				throw new SyntaxError("expected expressions");
			}
			final List<Expression> children = new ArrayList<Expression>();
			if (left.getTag() == tag) {
				children.addAll(asList(getChildren(left)));
			} else {
				children.add((Expression) left);
			}
			children.add(right);
			final SourceLocation srcLoc = pc.getSourceLocation();
			return makeResult(pc.factory, children, srcLoc);
		}
		
		protected Expression[] getChildren(Formula<?> exprWithSameTag) {
			return ((AssociativeExpression) exprWithSameTag).getChildren();
		}
		
		protected Expression makeResult(FormulaFactory factory,
				List<Expression> children, SourceLocation srcLoc) {
			return factory.makeAssociativeExpression(tag, children, srcLoc);
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
				List<Expression> children, SourceLocation srcLoc) {
			final IExpressionExtension extension = (IExpressionExtension) factory
					.getExtension(tag);
			return factory.makeExtendedExpression(extension, children, Collections
					.<Predicate> emptyList(), srcLoc);
		}
	}
	
	static class AssociativePredicateInfix extends DefaultLedParser<AssociativePredicate> {

		public AssociativePredicateInfix(int tag) {
			super(tag);
		}

		public AssociativePredicate led(Formula<?> left, ParserContext pc)
				throws SyntaxError {
			final Predicate right = pc.subParse(tag, PRED_PARSER);
			if (!(left instanceof Predicate)) {
				throw new SyntaxError("expected predicates");
			}
			final List<Predicate> children = new ArrayList<Predicate>();
			if (left.getTag() == tag) {
				children.addAll(asList(((AssociativePredicate) left)
						.getChildren()));
			} else {
				children.add((Predicate) left);
			}
			children.add(right);
			return pc.factory.makeAssociativePredicate(tag, children, pc
					.getSourceLocation());
		}
	}

	static class RelationalPredicateInfix extends DefaultLedParser<RelationalPredicate> {

		public RelationalPredicateInfix(int tag) {
			super(tag);
		}

		public RelationalPredicate led(Formula<?> left, ParserContext pc) throws SyntaxError {
			final Expression right = pc.subParse(tag, EXPR_PARSER);
			if (!(left instanceof Expression)) {
				throw new SyntaxError("expected expressions");
			}
			return pc.factory.makeRelationalPredicate(tag, (Expression) left,
					right, pc.getSourceLocation());
		}
	}

	static final ILedParser<BinaryExpression> FUN_IMAGE = new DefaultLedParser<BinaryExpression>(FUNIMAGE) {

		public BinaryExpression led(Formula<?> left, ParserContext pc) throws SyntaxError {
			final Expression right = pc.subParse(NO_TAG, EXPR_PARSER);
			if (!(left instanceof Expression)) {
				throw new SyntaxError("expected expressions");
			}
			pc.progressCloseParen();
			return pc.factory.makeBinaryExpression(tag, (Expression) left,
					right, pc.getSourceLocation());
		}
	};

	static class LiteralPredicateParser extends DefaultNudParser<LiteralPredicate> {

		public LiteralPredicateParser(int tag) {
			super(tag);
		}

		public LiteralPredicate nud(ParserContext pc) throws SyntaxError {
			final LiteralPredicate litPred = pc.factory.makeLiteralPredicate(
					tag, pc.getSourceLocation());
			pc.progress();
			return litPred;
		}
	}

	static class QuantifiedPredicateParser extends DefaultNudParser<QuantifiedPredicate> {

		public QuantifiedPredicateParser(int tag) {
			super(tag);
		}

		public QuantifiedPredicate nud(ParserContext pc) throws SyntaxError {
			pc.progress();
			final List<Identifier> identList = pc.subParse(IDENT_LIST_PARSER);
			pc.progress(_DOT);
			final Predicate pred = pc.subParse(tag, PRED_PARSER);

			final List<FreeIdentifier> freeIdentList = makeFreeIdentList(pc.factory, identList);
			final Predicate boundPred = pred.bindTheseIdents(freeIdentList, pc.factory);
			final List<BoundIdentDecl> boundIdentifiers = makeBoundIdentDeclList(
					pc.factory, freeIdentList);
			return pc.factory.makeQuantifiedPredicate(tag, boundIdentifiers,
					boundPred, null);
		}
	}

	static List<BoundIdentDecl> makeBoundIdentDeclList(FormulaFactory factory,
			final List<FreeIdentifier> identList) {
		final List<BoundIdentDecl> boundIdentifiers = new ArrayList<BoundIdentDecl>(identList.size());
		// TODO use Formula.bindTheseIdents instead ?
		for (FreeIdentifier ident: identList) {
			boundIdentifiers.add(ident.asDecl(factory));
		}
		return boundIdentifiers;
	}

	static List<FreeIdentifier> makeFreeIdentList(FormulaFactory factory,
			final List<Identifier> identList) throws SyntaxError {
		final List<FreeIdentifier> freeIdents = new ArrayList<FreeIdentifier>(identList.size());
		// TODO use Formula.bindTheseIdents instead ?
		for (Identifier ident: identList) {
			if (!(ident instanceof FreeIdentifier)) {
				throw new SyntaxError("expected a free identifier, but was "
						+ ident);
			}
			freeIdents.add((FreeIdentifier) ident);
		}
		return freeIdents;
	}

	static class UnaryExpressionParser extends DefaultNudParser<UnaryExpression> {

		public UnaryExpressionParser(int tag) {
			super(tag);
		}

		public UnaryExpression nud(ParserContext pc)
				throws SyntaxError {
			pc.progress();
			pc.progressOpenParen();
			final Expression child = pc.subParse(EXPR_PARSER);
			pc.progressCloseParen();
			return pc.factory.makeUnaryExpression(tag, child, pc.getSourceLocation());
		}

	}

	static class PatternParser extends DefaultMainParser<Pattern> {
		
		final Pattern pattern;
		
		public PatternParser(ParseResult result) {
			this.pattern = new Pattern(result);
		}

		public Pattern parse(int parentTag, ParserContext pc) throws SyntaxError {
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

			public Object parse(int parentTag, ParserContext pc) throws SyntaxError {
				if (pc.t.kind == _LPAR) {
					pc.progressOpenParen();
					pc.subParse(parser);
					pc.progressCloseParen();
				} else {
					final FreeIdentifier freeIdentifier = pc.subParse(
							FREE_IDENT_PARSER);
					pattern.declParsed(freeIdentifier.asDecl(pc.factory));
				}
				return null;
			}
		}
	}

	static class AbstListParser<T extends Expression> extends DefaultNudParser<List<T>> {
	
		private final INudParser<T> parser;
		
		public AbstListParser(INudParser<T> parser) {
			super(NO_TAG);
			this.parser = parser;
		}

		public List<T> nud(ParserContext pc) throws SyntaxError {
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
	
	static final AbstListParser<Identifier> IDENT_LIST_PARSER = new AbstListParser<Identifier>(IDENT_SUBPARSER);

	static final INudParser<SetExtension> SETEXT_PARSER = new DefaultNudParser<SetExtension>(SETEXT) {
		
		public SetExtension nud(ParserContext pc) throws SyntaxError {
			pc.progress();
			final List<Expression> exprs = pc.subParse(EXPR_LIST_PARSER);
			pc.progress(_RBRACE);
			
			return pc.factory.makeSetExtension(exprs, pc.getSourceLocation());
		}
	};
	
	static final INudParser<QuantifiedExpression> CSET_EXPLICIT = new DefaultNudParser<QuantifiedExpression>(CSET) {
		
		public QuantifiedExpression nud(ParserContext pc) throws SyntaxError {
			pc.progress();
			final List<Identifier> idents = pc.subParse(IDENT_LIST_PARSER);
			pc.progress(_DOT);
			final Predicate pred = pc.subParse(PRED_PARSER);
			pc.progress(_MID);
			final Expression expr = pc.subParse(EXPR_PARSER);
			pc.progress(_RBRACE);
			
			final List<FreeIdentifier> freeIdents = makeFreeIdentList(pc.factory, idents);
			final Predicate boundPred = pred.bindTheseIdents(freeIdents, pc.factory);
			final Expression boundExpr = expr.bindTheseIdents(freeIdents, pc.factory);
			final List<BoundIdentDecl> boundIdents = makeBoundIdentDeclList(pc.factory, freeIdents);
			return pc.factory.makeQuantifiedExpression(tag, boundIdents, boundPred,
					boundExpr, pc.getSourceLocation(), Form.Explicit);
		}
	};
	
	static final INudParser<QuantifiedExpression> CSET_IMPLICIT = new DefaultNudParser<QuantifiedExpression>(CSET) {
		
		public QuantifiedExpression nud(ParserContext pc) throws SyntaxError {
			pc.progress();
			final Expression expr = pc.subParse(EXPR_PARSER);
			pc.progress(_MID);
			final Predicate pred = pc.subParse(PRED_PARSER);
			pc.progress(_RBRACE);
			final List<FreeIdentifier> idents = asList(expr.getFreeIdentifiers());
			final Predicate boundPred = pred.bindTheseIdents(idents, pc.factory);
			final Expression boundExpr = expr.bindTheseIdents(idents, pc.factory);
			final List<BoundIdentDecl> boundIdents = makeBoundIdentDeclList(pc.factory, idents);
			return pc.factory.makeQuantifiedExpression(tag, boundIdents, boundPred,
					boundExpr, pc.getSourceLocation(), Form.Implicit);
		}
	};
	
	static final INudParser<QuantifiedExpression> CSET_LAMBDA = new DefaultNudParser<QuantifiedExpression>(CSET) {
		
		public QuantifiedExpression nud(ParserContext pc) throws SyntaxError {
			pc.progress();
			final PatternParser pattParser = new PatternParser(pc.result);
			final Pattern pattern = pc.subParse(pattParser);
//			PatternParser.parse(pc.subContext(), pattern);
			final List<FreeIdentifier> idents = getFreeIdents(pattern.getDecls(), pc.factory);
			pc.progress(_DOT);
			final Predicate pred = pc.subParse(PRED_PARSER);
			final Predicate boundPred = pred.bindTheseIdents(idents, pc.factory);
			pc.progress(_MID);
			final Expression expr = pc.subParse(EXPR_PARSER);
			final Expression boundExpr = expr.bindTheseIdents(idents, pc.factory);
			
			final Expression pair = pc.factory.makeBinaryExpression(MAPSTO,
					pattern.getPattern(), boundExpr, null);
			return pc.factory.makeQuantifiedExpression(tag, pattern.getDecls(), boundPred,
					pair, pc.getSourceLocation(), Form.Lambda);
		}

		// TODO avoid binding identifiers afterwards, better pass them
		// as arguments everywhere; reuse Binding from bmath.atg
		private List<FreeIdentifier> getFreeIdents(List<BoundIdentDecl> decls,
				FormulaFactory factory) {
			final List<FreeIdentifier> idents = new ArrayList<FreeIdentifier>(decls.size());
			for (BoundIdentDecl bid : decls) {
				idents.add(factory.makeFreeIdentifier(bid.getName(), bid
						.getSourceLocation()));
			}
			return idents;
		}
	};

}
