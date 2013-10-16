/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.ui.prover;

import java.util.Collection;

import org.eventb.core.IPOSequent;
import org.eventb.core.seqprover.IProofTreeNode;

/**
 * Common protocol for contributors of dynamic global tactics to the prover UI.
 * <p>
 * Implementors are meant to be referenced in extension point
 * <code>org.eventb.ui.proofTactics</code> as <code>tacticProvider</code> of
 * <code>dynamic_dropdown</code>.
 * </p>
 * 
 * @author Nicolas Beauger
 * @since 3.0
 */
public interface IUIDynTacticProvider {

	/**
	 * Returns available tactics for the given context.
	 * 
	 * @param ptNode
	 *            current proof tree node
	 * @param poSequent
	 *            current PO sequent
	 * @return a collection of UI dynamic tactics
	 */
	Collection<IUIDynTactic> getDynTactics(IProofTreeNode ptNode,
			IPOSequent poSequent);

}
