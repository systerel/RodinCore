/*******************************************************************************
 * Copyright (c) 2006, 2011 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - fixed NPE in SIMP_FUNIMAGE_LAMBDA (ver 0)
 *     Systerel - incremented to reasonerVersion 1 after OnePointRule v2 fix
 *     Systerel - incremented to reasonerVersion 2 after SIMP_FUNIMAGE_LAMBDA fix
 *     Systerel - incremented to reasonerVersion 3 after fixing bug #3025836
 *     Systerel - incremented to reasonerVersion 4 after adding datatype rules
 *     Systerel - introduction of new levels
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IVersionedReasoner;
import org.eventb.core.seqprover.SequentProver;

public abstract class AutoRewrites extends AbstractAutoRewrites implements
		IVersionedReasoner {

	/*
	 * HOWTO CREATE A NEW LEVEL OF THIS REASONER:
	 * 
	 * - First create a new Level in the enum below - Then create a new subclass
	 * of this class. - Don't forget to also change the value of DEFAULT below.
	 */

	/**
	 * Default instance of this family of reasoners.
	 */
	public static final IReasoner DEFAULT = new AutoRewritesL3();

	public static final String REASONER_ID = SequentProver.PLUGIN_ID
			+ ".autoRewrites";

	public static enum Level {

		// The parameter is the version of the associated level.
		// Names of existing levels must not be changed to ensure backward
		// compatibility of reasoner ids.
		L0(4), L1(1), L2(0), L3(0);

		public static final Level LATEST = Level.latest();

		private static final Level latest() {
			final Level[] values = Level.values();
			return values[values.length - 1];
		}

		public boolean from(Level other) {
			return this.ordinal() >= other.ordinal();
		}

		// Reasoner reasonerVersion for this level
		private final int reasonerVersion;
		private String reasonerId;

		private Level(int reasonerVersion) {
			this.reasonerVersion = reasonerVersion;
		}

		/*
		 * Pre-compute the reasoner ids. This cannot be done in the constructor
		 * due to visibility restrictions in enums.
		 */
		static {
			for (Level level : values()) {
				if (level == L0) {
					level.reasonerId = REASONER_ID;
				} else {
					level.reasonerId = REASONER_ID + level;
				}
			}
		}

		public String getReasonerId() {
			return reasonerId;
		}

		public int getReasonerVersion() {
			return reasonerVersion;
		}

	}

	private final Level level;

	protected AutoRewrites(Level level) {
		super(true);
		this.level = level;
	}

	public final String getReasonerID() {
		return level.getReasonerId();
	}

	@Override
	protected final String getDisplayName() {
		return "simplification rewrites";
	}

	public final int getVersion() {
		return level.getReasonerVersion();
	}

	@Override
	public final IFormulaRewriter getRewriter(FormulaFactory ff) {
		return new AutoRewriterImpl(ff, level);
	}

}
