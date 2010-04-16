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
import java.util.List;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;

/**
 * @author Nicolas Beauger
 *
 */
public class Parsers {

	static class ClosedSugar implements ISubParser {
	
		private final int closeKind;
		
		public ClosedSugar(int closeKind) {
			this.closeKind = closeKind;
		}
	
		public Formula<?> led(Formula<?> left, GenParser.ParserContext pc)
				throws GenParser.SyntaxError {
			throw new GenParser.SyntaxError("unexpected symbol");
		}
	
		public Formula<?> nud(GenParser.ParserContext pc) throws GenParser.SyntaxError {
			final Formula<?> formula = Parsers.MainParser.parse(Formula.NO_TAG, pc);
			pc.progress(closeKind);
			return formula;
		}
		
	}
	static class AssociativeExpressionInfix implements ISubParser {
		
		private final int tag;
		
		public AssociativeExpressionInfix(int tag) {
			this.tag = tag;
		}
	
		public Formula<?> nud(GenParser.ParserContext pc) throws GenParser.SyntaxError {
			throw new GenParser.SyntaxError("unexpected symbol");
		}
		
		public AssociativeExpression led(Formula<?> left, GenParser.ParserContext pc)
				throws GenParser.SyntaxError {
			final Formula<?> right = Parsers.MainParser.parse(tag, pc);
			if (!(left instanceof Expression && right instanceof Expression)) {
				throw new GenParser.SyntaxError("expected expressions");
			}
			final List<Expression> children = new ArrayList<Expression>();
			if (left.getTag() == tag) {
				children.addAll(asList(((AssociativeExpression) left)
						.getChildren()));
			} else {
				children.add((Expression) left);
			}
			children.add((Expression) right);
			return pc.factory.makeAssociativeExpression(tag, children, null);
		}
	}
	static class MainParser {
	
		public static Formula<?> parse(int parentTag, GenParser.ParserContext pc) throws GenParser.SyntaxError {
			pc.progress();
			Formula<?> left = pc.getSubParser().nud(pc);
			while (pc.canProgressRight(parentTag)) {
				pc.progress();
				left = pc.getSubParser().led(left, pc);
			}
			return left;
		}
	}
	static final ISubParser FREE_IDENT_SUBPARSER = new ISubParser() {
		
		public Formula<?> led(Formula<?> left, GenParser.ParserContext pc)
				throws GenParser.SyntaxError {
			throw new GenParser.SyntaxError("no led for integer literals");
		}
	
		public Formula<?> nud(GenParser.ParserContext pc) throws GenParser.SyntaxError {
			return pc.factory.makeFreeIdentifier(pc.t.val, null);
		}
	};
	static final ISubParser INTLIT_SUBPARSER = new ISubParser() {
		
		public Formula<?> led(Formula<?> left, GenParser.ParserContext pc)
				throws GenParser.SyntaxError {
			throw new GenParser.SyntaxError("no led for integer literals");
		}
	
		public Formula<?> nud(GenParser.ParserContext pc) throws GenParser.SyntaxError {
			final BigInteger value = BigInteger.valueOf((Integer.valueOf(pc.t.val)));
			return pc.factory.makeIntegerLiteral(value, null);
		}
	};

}
