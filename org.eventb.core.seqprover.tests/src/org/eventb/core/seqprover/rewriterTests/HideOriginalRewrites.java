/*******************************************************************************
 * Copyright (c) 2007, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.rewriterTests;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;

import java.math.BigInteger;

import org.eventb.core.ast.DefaultRewriter;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.eventbExtensions.DLib;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AbstractAutoRewrites;

public class HideOriginalRewrites extends AbstractAutoRewrites implements
		IReasoner {

	static class HideOriginalRewriter extends DefaultRewriter {

		private static final BigInteger TWO = BigInteger.valueOf(2L);
		
		public HideOriginalRewriter(boolean autoFlattening) {
			super(autoFlattening);
		}

		@Override
		public Expression rewrite(IntegerLiteral literal) {
			final FormulaFactory ff = literal.getFactory();
			final BigInteger value = literal.getValue();
			if (value.equals(ZERO)) {
				return ff.makeIntegerLiteral(ONE, null);
			}
			if (value.equals(ONE)) {
				return ff.makeIntegerLiteral(TWO, null);
			}
			return super.rewrite(literal);
		}

		@Override
		public Predicate rewrite(SimplePredicate predicate) {
			return DLib.True(predicate.getFactory());
		}
		
	}

	public HideOriginalRewrites() {
		super(true);
	}

	@Override
	protected String getDisplayName() {
		return "Test hide original rewrites";
	}

	@Override
	protected IFormulaRewriter getRewriter() {
		return new HideOriginalRewriter(true);
	}

	public String getReasonerID() {
		return "org.eventb.core.seqprover.tests.hideOriginalRewrites";
	}

}
