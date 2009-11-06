/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - fixed NPE in SIMP_FUNIMAGE_LAMBDA (ver 0)
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.core.seqprover.IVersionedReasoner;
import org.eventb.core.seqprover.SequentProver;

public class AutoRewrites extends AbstractAutoRewrites implements
		IVersionedReasoner {

	private static final int REASONER_VERSION = 0;
	private static final IFormulaRewriter rewriter = new AutoRewriterImpl();

	public AutoRewrites() {
		super(rewriter, true);
	}

	public static String REASONER_ID = SequentProver.PLUGIN_ID
			+ ".autoRewrites";

	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	protected String getDisplayName() {
		return "simplification rewrites";
	}

	public int getVersion() {
		return REASONER_VERSION;
	}

}
