/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.autoTacticExtentionTests;

import static org.junit.Assert.assertEquals;

import org.eventb.core.seqprover.IParameterValuation;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.ITacticParameterizer;

/**
 * @author Nicolas Beauger
 *
 */
public class ParameterizedTactics {
	
	public static class FakeTactic implements ITactic {
	
		private final boolean bool1;
		private final boolean bool2;
		private final int int1;
		private final long long1;
		private final String string;
	
		
		public FakeTactic(boolean bool1, boolean bool2, int int1, long long1,
				String string) {
			super();
			this.bool1 = bool1;
			this.bool2 = bool2;
			this.int1 = int1;
			this.long1 = long1;
			this.string = string;
		}
	
	
		@Override
		public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
			throw new IllegalStateException("not supposed to be called");
		}
		
		public void assertParameterValues(boolean expBool1, boolean expBool2,
				int expInt1, long expLong1, String expString) {
			assertEquals(expBool1, bool1);
			assertEquals(expBool2, bool2);
			assertEquals(expInt1, int1);
			assertEquals(expLong1, long1);
			assertEquals(expString, string);
		}
	
	}

	public static class TacWithParams implements ITacticParameterizer {

		public static final String TACTIC_ID = "org.eventb.core.seqprover.tests.tacParam";

		private static final String PRM_BOOL1 = "bool1";
		private static final String PRM_BOOL2 = "bool2";
		private static final String PRM_INT = "int1";
		private static final String PRM_LONG = "long1";
		private static final String PRM_STRING = "string1";

		@Override
		public ITactic getTactic(IParameterValuation parameters) {
			return new FakeTactic(parameters.getBoolean(PRM_BOOL1),
					parameters.getBoolean(PRM_BOOL2), parameters.getInt(PRM_INT),
					parameters.getLong(PRM_LONG), parameters.getString(PRM_STRING));
		}
	}
	
	public static class Both implements ITactic, ITacticParameterizer {

		@Override
		public ITactic getTactic(IParameterValuation parameters) {
			return null;
		}

		@Override
		public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
			return null;
		}
		
	}
	
	public static class TacWithParam implements ITactic {

		@Override
		public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
			return null;
		}
		
	}
	
	public static class ParamTacNoParam implements ITacticParameterizer {

		@Override
		public ITactic getTactic(IParameterValuation parameters) {
			return null;
		}
		
	}
}
