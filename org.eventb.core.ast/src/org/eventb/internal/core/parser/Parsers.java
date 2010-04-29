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
import static org.eventb.core.ast.Formula.FREE_IDENT;
import static org.eventb.core.ast.Formula.FUNIMAGE;
import static org.eventb.core.ast.Formula.INTLIT;
import static org.eventb.core.ast.Formula.NO_TAG;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.internal.core.parser.GenParser.ParserContext;
import org.eventb.internal.core.parser.GenParser.SyntaxError;

/**
 * @author Nicolas Beauger
 *
 */
public class Parsers {

	private static abstract class AbstractSubParser implements ISubParser {

		protected final int tag;

		protected AbstractSubParser(int tag) {
			this.tag = tag;
		}
		
		public int getTag() {
			return tag;
		}
		
	}

	private static abstract class DefaultNudParser extends AbstractSubParser implements INudParser {

		protected DefaultNudParser(int tag) {
			super(tag);
		}

	}

	private static abstract class DefaultLedParser extends AbstractSubParser implements ILedParser {

		protected DefaultLedParser(int tag) {
			super(tag);
		}

	}

	static final INudParser CLOSED_SUGAR = new DefaultNudParser(NO_TAG) {

		public Formula<?> nud(ParserContext pc, int startPos) throws SyntaxError {
			final Formula<?> formula = MainParser.parse(NO_TAG, pc, pc.la.pos);
			pc.progressCloseParen();
			return formula;
		}
	};

	static class BinaryExpressionInfix extends DefaultLedParser {

		public BinaryExpressionInfix(int tag) {
			super(Formula.NO_TAG);
		}
		
		public Expression led(Formula<?> left, ParserContext pc, int startPos)
				throws SyntaxError {
			final Expression right = MainParser.parseExpression(tag, pc, pc.la.pos);
			if (!(left instanceof Expression)) {
				throw new SyntaxError("expected expressions");
			}
			final SourceLocation srcLoc = pc.getSourceLocation(startPos);
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
	
	static class AssociativeExpressionInfix extends DefaultLedParser {

		public AssociativeExpressionInfix(int tag) {
			super(tag);
		}

		public Expression led(Formula<?> left, ParserContext pc, int startPos)
				throws SyntaxError {
			final Expression right = MainParser.parseExpression(tag, pc, pc.la.pos);
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
			final SourceLocation srcLoc = pc.getSourceLocation(startPos);
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
	
	static class AssociativePredicateInfix extends DefaultLedParser {

		public AssociativePredicateInfix(int tag) {
			super(tag);
		}

		public AssociativePredicate led(Formula<?> left, ParserContext pc, int startPos)
				throws SyntaxError {
			final Predicate right = MainParser.parsePredicate(tag, pc, pc.la.pos);
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
					.getSourceLocation(startPos));
		}
	}

	static class RelationalPredicateInfix extends DefaultLedParser {

		public RelationalPredicateInfix(int tag) {
			super(tag);
		}

		public RelationalPredicate led(Formula<?> left, ParserContext pc,
				int startPos) throws SyntaxError {
			final Expression right = MainParser.parseExpression(tag, pc, pc.la.pos);
			if (!(left instanceof Expression)) {
				throw new SyntaxError("expected expressions");
			}
			return pc.factory.makeRelationalPredicate(tag, (Expression) left,
					right, pc.getSourceLocation(startPos));
		}
	}

	static final ILedParser FUN_IMAGE = new DefaultLedParser(FUNIMAGE) {

		public BinaryExpression led(Formula<?> left, ParserContext pc,
				int startPos) throws SyntaxError {
			final Expression right = MainParser.parseExpression(tag, pc, pc.la.pos);
			if (!(left instanceof Expression)) {
				throw new SyntaxError("expected expressions");
			}
			pc.progressCloseParen();
			return pc.factory.makeBinaryExpression(tag, (Expression) left,
					right, pc.getSourceLocation(startPos));
		}
	};

	static class LiteralPredicateParser extends DefaultNudParser {

		public LiteralPredicateParser(int tag) {
			super(tag);
		}

		public Formula<?> nud(ParserContext pc, int startPos) throws SyntaxError {
			return pc.factory.makeLiteralPredicate(tag, pc.getSourceLocation(startPos));
		}
	}

	static class QuantifiedPredicateParser extends DefaultNudParser {

		private final IdentListParser identListParser;

		public QuantifiedPredicateParser(int tag, IdentListParser identListParser) {
			super(tag);
			this.identListParser = identListParser;
		}

		public Formula<?> nud(ParserContext pc, int startPos) throws SyntaxError {
			pc.progress();
			final List<FreeIdentifier> identList = identListParser.parse(pc, pc.t.pos);
			identListParser.progressEndList(pc);
			final Predicate pred = MainParser.parsePredicate(tag, pc, pc.la.pos);

			final Predicate boundPred = pred.bindTheseIdents(identList, pc.factory);
			final List<BoundIdentDecl> boundIdentifiers = new ArrayList<BoundIdentDecl>(identList.size());
			// TODO use Formula.bindTheseIdents instead ?
			for (FreeIdentifier ident: identList) {
				boundIdentifiers.add(pc.factory.makeBoundIdentDecl(ident.getName(), ident.getSourceLocation()));
			}
			return pc.factory.makeQuantifiedPredicate(tag, boundIdentifiers,
					boundPred, null);
		}
	}

	static class UnaryExpression extends DefaultNudParser {

		public UnaryExpression(int tag) {
			super(tag);
		}

		public Formula<?> nud(ParserContext pc, int startPos)
				throws SyntaxError {
			pc.progressOpenParen();
			final Expression child = MainParser.parseExpression(tag, pc, pc.la.pos);
			pc.progressCloseParen();
			return pc.factory.makeUnaryExpression(tag, child, pc.getSourceLocation(startPos));
		}

	}

	static class IdentListParser {
	
		private final int identSepKind;
		private final int endListKind;
		
		public IdentListParser(int identSepKind, int endListKind) {
			this.identSepKind = identSepKind;
			this.endListKind = endListKind;
		}
	
		public List<FreeIdentifier> parse(ParserContext pc, int startPos) throws SyntaxError {
			final List<FreeIdentifier> idents = new ArrayList<FreeIdentifier>();
			FreeIdentifier ident = (FreeIdentifier) FREE_IDENT_SUBPARSER.nud(pc, startPos);
			idents.add(ident);
			while (pc.la.kind == identSepKind) {
				pc.progress();
				pc.progress();
				ident = (FreeIdentifier) FREE_IDENT_SUBPARSER.nud(pc, pc.t.pos);
				idents.add(ident);
			}
			return idents;
		}
		
		public void progressEndList(ParserContext pc) throws SyntaxError {
			pc.progress(endListKind);
		}
	}

	static class MainParser {

		public static Formula<?> parse(int parentTag, ParserContext pc, int startPos)
				throws SyntaxError {
			final INudParser nudParser = nextNudParser(pc);
			Formula<?> left = nudParser.nud(pc, startPos);
			while (pc.canProgressRight(parentTag)) {
				final ILedParser ledParser = nextLedParser(pc);
				left = ledParser.led(left, pc, startPos);
			}
			return left;
		}

		public static Predicate parsePredicate(int parentTag, ParserContext pc, int startPos) throws SyntaxError {
			final Formula<?> formula = parse(parentTag, pc, startPos);
			if (!(formula instanceof Predicate)) {
				throw new SyntaxError("expected predicate");
			}
			return (Predicate) formula;
		}
		
		public static Expression parseExpression(int parentTag, ParserContext pc, int startPos) throws SyntaxError {
			final Formula<?> formula = parse(parentTag, pc, startPos);
			if (!(formula instanceof Expression)) {
				throw new SyntaxError("expected expression");
			}
			return (Expression) formula;
		}
		
		private static INudParser nextNudParser(ParserContext pc)
				throws SyntaxError {
			pc.progress();
			final INudParser subParser = pc.getNudParser();
			if (subParser == null) {
				throw new SyntaxError("don't know how to parse: " + pc.t.val);
			}
			return subParser;
		}
		
		private static ILedParser nextLedParser(ParserContext pc)
		throws SyntaxError {
			pc.progress();
			final ILedParser subParser = pc.getLedParser();
			if (subParser == null) {
				throw new SyntaxError("don't know how to parse: " + pc.t.val);
			}
			return subParser;
		}
	}

	static final INudParser FREE_IDENT_SUBPARSER = new DefaultNudParser(FREE_IDENT) {

		public FreeIdentifier nud(ParserContext pc, int startPos) throws SyntaxError {
			return pc.factory.makeFreeIdentifier(pc.t.val, pc
					.getSourceLocation(startPos));
		}
	};

	static final INudParser INTLIT_SUBPARSER = new DefaultNudParser(INTLIT) {

		public Formula<?> nud(ParserContext pc, int startPos) throws SyntaxError {
			final BigInteger value = BigInteger.valueOf((Integer
					.valueOf(pc.t.val)));
			return pc.factory.makeIntegerLiteral(value, pc.getSourceLocation(startPos));
		}
	};

}
