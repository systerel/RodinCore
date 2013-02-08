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
package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.core.seqprover.SequentProver;

public class InclusionSetMinusLeftRewrites extends InclusionSetMinusRewrites {

	public static final String REASONER_ID = SequentProver.PLUGIN_ID + ".inclusionSetMinusLeftRewrites";

	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	protected IFormulaRewriter makeRewriter() {
		return new InclusionSetMinusLeftRewriterImpl();
	}

}
