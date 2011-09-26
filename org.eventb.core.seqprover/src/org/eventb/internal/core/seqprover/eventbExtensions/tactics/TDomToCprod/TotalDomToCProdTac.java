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
package org.eventb.internal.core.seqprover.eventbExtensions.tactics.TDomToCprod;

import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.tactics.BasicTactics;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.TotalDomRewrites;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.TotalDomRewrites.Input;

/**
 * Applies TotalDomRewrites if the domain can be subsituted by a cartesian
 * product. It applies only in goal matching <code>x↦y∈dom(g)</code>.
 * 
 * @author Emmanuel Billaud
 */
public class TotalDomToCProdTac implements ITactic {

	@Override
	public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
		final IProverSequent sequent = ptNode.getSequent();
		final TotalDomToCprodTacImpl impl = new TotalDomToCprodTacImpl(sequent);
		final Input input = impl.computeInput();
		if (input == null) {
			return "Tactic unapplicable";
		}
		return BasicTactics.reasonerTac(new TotalDomRewrites(), input).apply(
				ptNode, pm);
	}

}
