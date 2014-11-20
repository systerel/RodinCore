/*******************************************************************************
 * Copyright (c) 2007, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.rewriterTests;

import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.core.seqprover.rewriterTests.HideOriginalRewrites.HideOriginalRewriter;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AbstractAutoRewrites;

public class ShowOriginalRewrites extends AbstractAutoRewrites {

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
	protected IFormulaRewriter getRewriter() {
		return new HideOriginalRewriter(true);
	}

}
