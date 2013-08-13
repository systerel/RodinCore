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
package org.eventb.internal.core.seqprover.eventbExtensions.genmp;


/**
 * Enhancement of the reasoner GeneralizedModusPonens. The previous one search
 * only a occurnece of the predicate P or G. Now this reasoner search also
 * predicates equivalent to P or a sufficient condition.<br/>
 * 
 * This reasoner implements these rules:
 *
 * <ul>
 * <li>(P,φ(P†)⊢G) ≡ (P,φ(⊤)⊢G</li>
 * <li>(nP†,φ(P)⊢G) ≡ (nP†,φ(⊥)⊢G</li>
 * <li>(P⊢φ(P†)) ≡ (P⊢φ(⊤)</li>
 * <li>(nP†⊢φ(P)) ≡ (nP†⊢φ(⊥)</li>
 * <li>(H,φ(G†)⊢G) ≡ (H,φ(⊥)⊢G</li>
 * <li>(H,φ(G†)⊢¬G) ≡ (H,φ(⊤)⊢¬G</li>
 * <li>(H,φ(G†)⊢G1 ∨ ... ∨ Gi ∨ ... ∨ Gn) ≡ (H,φ(⊥)⊢G1 ∨ ... ∨ Gi ∨ ... ∨ Gn</li>
 * <li>(H,φ(G†)⊢G1 ∨ ... ∨ ¬Gi ∨ ... ∨ Gn) ≡ (H,φ(⊤)⊢G1 ∨ ... ∨ ¬Gi ∨ ... ∨ Gn</li>
 * </ul>
 *
 * @author Josselin Dolhen
 */
public class GeneralizedModusPonensL2 extends AbstractGenMP {

	public GeneralizedModusPonensL2() {
		super(Level.L2);
	}

}
