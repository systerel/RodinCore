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
package org.eventb.internal.core.seqprover.eventbExtensions.genmp;


/**
 * Enhancement of the reasoner GeneralizedModusPonens. This reasoner implements
 * 4 more rules than the previous one :
 * <ul>
 * <li>(H,φ(G)⊢G) ≡ (H,φ(⊥)⊢G</li>
 * <li>(H,φ(G)⊢¬G) ≡ (H,φ(⊤)⊢¬G</li>
 * <li>(H,φ(Gi)⊢G1 ∨ ... ∨ Gi ∨ ... ∨ Gn) ≡ (H,φ(⊥)⊢G1 ∨ ... ∨ Gi ∨ ... ∨ Gn</li>
 * <li>(H,φ(Gi)⊢G1 ∨ ... ∨ ¬Gi ∨ ... ∨ Gn) ≡ (H,φ(⊤)⊢G1 ∨ ... ∨ ¬Gi ∨ ... ∨ Gn</li>
 * </ul>
 * 
 * @author Emmanuel Billaud
 */
public class GeneralizedModusPonensL1 extends AbstractGenMP {

	public GeneralizedModusPonensL1() {
		super(Level.L1);
	}

}
