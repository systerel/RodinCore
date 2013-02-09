/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - hide original predicate (ver 0)
 *     Systerel - added SIMP_TYPE_SUBSETEQ and SIMP_TYPE_SUBSET_L (ver 1)
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.core.seqprover.IVersionedReasoner;
import org.eventb.core.seqprover.SequentProver;

public class TypeRewrites extends AbstractAutoRewrites implements IVersionedReasoner {
	
	private static final int VERSION = 1;
	
	public TypeRewrites() {
		super(true);
	}

	public static final String REASONER_ID = SequentProver.PLUGIN_ID
			+ ".typeRewrites";

	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	protected String getDisplayName() {
		return "type rewrites";
	}

	public int getVersion() {
		return VERSION;
	}

	@Override
	protected IFormulaRewriter getRewriter() {
		return new TypeRewriterImpl();
	}

}
