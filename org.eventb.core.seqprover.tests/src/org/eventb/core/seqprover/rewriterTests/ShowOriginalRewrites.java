/*******************************************************************************
 * Copyright (c) 2007, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.rewriterTests;

import org.eventb.core.ast.DefaultRewriter;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AbstractAutoRewrites;

public class ShowOriginalRewrites extends AbstractAutoRewrites implements
		IReasoner {

	private static class HideOriginalRewriter extends DefaultRewriter {

		private Expression number0 = Lib.parseExpression("0");
		private Expression number1 = Lib.parseExpression("1");
		private Expression number2 = Lib.parseExpression("2");
		
		public HideOriginalRewriter(boolean autoFlattening, FormulaFactory ff) {
			super(autoFlattening, ff);
			number0.typeCheck(ff.makeTypeEnvironment());
			number1.typeCheck(ff.makeTypeEnvironment());
			number2.typeCheck(ff.makeTypeEnvironment());
		}

		@Override
		public Expression rewrite(IntegerLiteral literal) {
			if (literal.equals(number0))
				return number1;
			if (literal.equals(number1))
				return number2;
			return super.rewrite(literal);
		}

		@Override
		public Predicate rewrite(SimplePredicate predicate) {
			return Lib.True;
		}
		
	}
	
	public ShowOriginalRewrites() {
		super(false);
	}

	@Override
	protected String getDisplayName() {
		return "Test show original rewrites";
	}

	public String getReasonerID() {
		return "org.eventb.core.seqprover.tests.showOriginalRewrites";
	}

	@Override
	protected IFormulaRewriter getRewriter(FormulaFactory formulaFactory) {
		return new HideOriginalRewriter(true, formulaFactory);
	}

}
