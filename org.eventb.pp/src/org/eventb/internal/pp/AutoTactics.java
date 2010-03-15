/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.pp;

import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.core.seqprover.eventbExtensions.AutoTactics.AbsractLazilyConstrTactic;
import org.eventb.pp.PPCore;

/**
 * This class file contains static classes that extend the autoTactics extension point in the sequent prover
 * 
 * 
 * @author Farhad Mehta
 *
 */
public class AutoTactics {

	
	/**
	 * This class is not meant to be instantiated
	 */
	private AutoTactics(){
	//	
	}
		
	/**
	 * Tries to discharge a sequent using PP, using only the selected hypotheses
	 * (timeout = .5 seconds)
	 * (maxSteps = 2000)
	 * 
	 * @author Farhad Mehta
	 *
	 */
	public static class PPrestricted extends AbsractLazilyConstrTactic{

		@Override
		protected ITactic getSingInstance() {
			return PPCore.newPP(true, 500, 2000);
		}
	}
	
	/**
	 * Tries to discharge a sequent using PP, using only hypotheses with free identifiers
	 * in common with the goal and selected hypotheses.
	 * (timeout = 2 seconds)
	 * (maxSteps = 3000)
	 * 
	 * @author Farhad Mehta
	 *
	 */
	public static class PPlasoo extends AbsractLazilyConstrTactic{

		@Override
		protected ITactic getSingInstance() {
			return Tactics.afterLasoo(PPCore.newPP(true, 2000, 3000));
		}
	}
	

	/**
	 * Tries to discharge a sequent using PP, using all visible hypotheses
	 * (timeout = 2 seconds)
	 * (maxSteps = 10000)
	 * 
	 * @author Farhad Mehta
	 *
	 */
	public static class PPunrestricted extends AbsractLazilyConstrTactic{

		@Override
		protected ITactic getSingInstance() {
			return PPCore.newPP(false, 2000, 3000);
		}
	}
	
	
}
