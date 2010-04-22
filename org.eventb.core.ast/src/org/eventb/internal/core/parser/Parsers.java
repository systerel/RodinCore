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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.internal.core.parser.GenParser.ParserContext;
import org.eventb.internal.core.parser.GenParser.SyntaxError;

/**
 * @author Nicolas Beauger
 *
 */
public class Parsers {

	private static class DefaultSubParser implements ISubParser {

		protected DefaultSubParser() {
			// avoid synthetic accessors emulation
		}

		public Formula<?> led(Formula<?> left, ParserContext pc, int startPos)
				throws SyntaxError {
			throw new SyntaxError("unexpected symbol: " + pc.t.val);
		}

		public Formula<?> nud(ParserContext pc, int startPos) throws SyntaxError {
			throw new SyntaxError("unexpected symbol: " + pc.t.val);
		}

	}

	static class ClosedSugar extends DefaultSubParser {

		private final int closeKind;

		public ClosedSugar(int closeKind) {
			this.closeKind = closeKind;
		}

		@Override
		public Formula<?> nud(ParserContext pc, int startPos) throws GenParser.SyntaxError {
			final Formula<?> formula = MainParser.parse(Formula.NO_TAG,
					pc, pc.la.pos);
			pc.progress(closeKind);
			return formula;
		}
		
	}

	static class AssociativeExpressionInfix extends DefaultSubParser {

		protected final int tag;

		public AssociativeExpressionInfix(int tag) {
			this.tag = tag;
		}

		@Override
		public Expression led(Formula<?> left, ParserContext pc, int startPos)
				throws GenParser.SyntaxError {
			final Formula<?> right = MainParser.parse(tag, pc, pc.la.pos);
			if (!(left instanceof Expression && right instanceof Expression)) {
				throw new GenParser.SyntaxError("expected expressions");
			}
			final List<Expression> children = new ArrayList<Expression>();
			if (left.getTag() == tag) {
				children.addAll(asList(getChildren(left)));
			} else {
				children.add((Expression) left);
			}
			children.add((Expression) right);
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
	
	static class AssociativePredicateInfix extends DefaultSubParser {

		private final int tag;

		public AssociativePredicateInfix(int tag) {
			this.tag = tag;
		}

		@Override
		public AssociativePredicate led(Formula<?> left, ParserContext pc, int startPos)
				throws GenParser.SyntaxError {
			final Formula<?> right = MainParser.parse(tag, pc, pc.la.pos);
			if (!(left instanceof Predicate && right instanceof Predicate)) {
				throw new GenParser.SyntaxError("expected predicates");
			}
			final List<Predicate> children = new ArrayList<Predicate>();
			if (left.getTag() == tag) {
				children.addAll(asList(((AssociativePredicate) left)
						.getChildren()));
			} else {
				children.add((Predicate) left);
			}
			children.add((Predicate) right);
			return pc.factory.makeAssociativePredicate(tag, children, pc
					.getSourceLocation(startPos));
		}
	}

	static class LiteralPredicateParser extends DefaultSubParser {

		private final int tag;

		public LiteralPredicateParser(int tag) {
			this.tag = tag;
		}

		@Override
		public Formula<?> nud(ParserContext pc, int startPos) throws SyntaxError {
			return pc.factory.makeLiteralPredicate(tag, pc.getSourceLocation(startPos));
		}
	}

	static class MainParser {

		public static Formula<?> parse(int parentTag, ParserContext pc, int startPos)
				throws SyntaxError {
			ISubParser subParser = nextSubParser(pc);
			Formula<?> left = subParser.nud(pc, startPos);
			while (pc.canProgressRight(parentTag)) {
				subParser = nextSubParser(pc);
				left = subParser.led(left, pc, startPos);
			}
			return left;
		}

		private static ISubParser nextSubParser(ParserContext pc)
				throws SyntaxError {
			pc.progress();
			final ISubParser subParser = pc.getSubParser();
			if (subParser == null) {
				throw new SyntaxError("don't know how to parse: " + pc.t.val);
			}
			return subParser;
		}
	}

	static final ISubParser FREE_IDENT_SUBPARSER = new DefaultSubParser() {

		@Override
		public Formula<?> nud(ParserContext pc, int startPos) throws GenParser.SyntaxError {
			return pc.factory.makeFreeIdentifier(pc.t.val, pc
					.getSourceLocation(startPos));
		}
	};

	static final ISubParser INTLIT_SUBPARSER = new DefaultSubParser() {

		@Override
		public Formula<?> nud(ParserContext pc, int startPos) throws SyntaxError {
			final BigInteger value = BigInteger.valueOf((Integer
					.valueOf(pc.t.val)));
			return pc.factory.makeIntegerLiteral(value, pc.getSourceLocation(startPos));
		}
	};

}
