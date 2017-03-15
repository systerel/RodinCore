/*******************************************************************************
 * Copyright (c) 2017 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions.genmp;


/**
 * Level of the reasoner GeneralizedModusPonens that ignores hidden hypotheses.
 * It is otherwise identical to GeneralizedModusPonensL2.
 *
 * @author Laurent Voisin
 */
public class GeneralizedModusPonensL3 extends AbstractGenMP {

	public GeneralizedModusPonensL3() {
		super(Level.L3);
	}

}
