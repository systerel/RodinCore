/*******************************************************************************
 * Copyright (c) 2007, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.rewriterTests;

import org.eventb.core.seqprover.SequentProver;

public class TypeRewriterReasonerTests extends AbstractAutomaticReasonerTests {

	@Override
	protected SuccessfulTest[] getSuccessfulTests() {
		// No need to test this. This should be guaranteed by testing the
		// abstract automatic rewrite reasoner and the formula rewriter itself.
		return new SuccessfulTest [] {
		};
	}

	@Override
	protected String[] getUnsuccessfulTests() {
		// No need to test this. This should be guaranteed by testing the
		// abstract automatic rewrite reasoner and the formula rewriter itself.
		return new String [] {
		};	
	}

	@Override
	public String getReasonerID() {
		return SequentProver.PLUGIN_ID + ".typeRewrites";
	}

}
