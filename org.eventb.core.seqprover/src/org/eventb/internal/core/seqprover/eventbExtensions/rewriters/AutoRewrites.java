/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - fixed NPE in SIMP_FUNIMAGE_LAMBDA (ver 0)
 *     Systerel - incremented to version 1 after OnePointRule v2 fix
 *     Systerel - incremented to version 2 after SIMP_FUNIMAGE_LAMBDA fix
 *     Systerel - incremented to version 3 after fixing bug #3025836
 *     Systerel - incremented to version 4 after adding datatype rules
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.core.seqprover.IVersionedReasoner;
import org.eventb.core.seqprover.SequentProver;

public class AutoRewrites extends AbstractAutoRewrites implements
		IVersionedReasoner {

	public static enum Level {
		L0, L1, L2, L3;
		
		public static final Level LATEST = Level.latest();
		
		private static final Level latest() {
			final Level[] values = Level.values();
			return values[values.length-1];
		}
		
		public boolean from(Level other){
			return this.ordinal() >= other.ordinal();
		}		
		
	}
	
	private static final int REASONER_VERSION = 4;

	public AutoRewrites() {
		super(true);
	}

	public static final String REASONER_ID = SequentProver.PLUGIN_ID
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

	@Override
	public IFormulaRewriter getRewriter(FormulaFactory ff) {
		return new AutoRewriterImpl(ff, Level.L0);
	}

}
