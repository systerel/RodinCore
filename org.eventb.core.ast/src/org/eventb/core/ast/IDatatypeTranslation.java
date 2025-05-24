/*******************************************************************************
 * Copyright (c) 2012, 2025 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast;

import java.util.List;

/**
 * Common protocol for translating Event-B formulas containing datatype
 * extensions to plain set theory. Instances of this interface allow to
 * translate several Event-B formulas sharing a common type environment from an
 * instance of the Event-B mathematical language extended with some datatype
 * extensions to the plain Event-B mathematical language.
 * <p>
 * This interface should be used in the following manner:
 * <ul>
 * <li>From the common type environment, create an instance of this interface
 * with method {@link ITypeEnvironment#makeDatatypeTranslation()}.</li>
 * <li>Translate as many formulas as needed using this instance by calling
 * method {@link Formula#translateDatatype(IDatatypeTranslation)}.</li>
 * <li>Optionally, retrieve the formula factory of the translated formulas
 * and/or the axioms giving the properties of the fresh identifiers introduced
 * by the translation.</li>
 * </ul>
 * </p>
 * 
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * 
 * @since 2.7
 */
public interface IDatatypeTranslation {

	/**
	 * Returns the formula factory of the target language.
	 */
	FormulaFactory getTargetFormulaFactory();

	/**
	 * Returns the axioms that specify the properties of the fresh identifiers
	 * (i.e. translated datatypes) introduced by the translation so far.
	 * 
	 * Let the parameterized datatype DT for a given set of formal type
	 * parameters <tt>T1, ... Tn</tt> be defined as follows:
	 * 
	 * <pre>
	 *  DT(T1,...,Tn) ::=
	 *          c1(d11: α11,..., d1k1: α1k1)
	 *          c2(d21: α21,..., d2k2: α2k2)
	 *          .
	 *          .
	 *          .
	 *          cm(dm1: αm1,..., dmkm: αmkm)
	 * </pre>
	 * 
	 * Each instance of a parameterized datatype DT is translated to a
	 * fresh given set τ. The following constants and axioms are created
	 * to characterize it:
	 * 
	 * <ul>
	 * <li>each value constructor <code>ci</code> is translated to a fresh
	 * identifier <code>ɣi</code> and the following axiom is added:
	 * 
	 * <pre>
	 * (A) ɣi ∈ αi1 × ... × αiki ↣ τ
	 * </pre>
	 * 
	 * </li>
	 * <li>an axiom tells that the datatype is partitioned by the
	 * constructors:
	 * 
	 * <pre>
	 * (B) partition(τ, ran(ɣ1), ..., ran(ɣm))
	 * </pre>
	 * 
	 * </li>
	 * <li>each destructor dij is translated to a fresh identifier δij and the following
	 * typing axiom is created:
	 * 
	 * <pre>
	 * (C) δij ∈ ran(ɣi) ↠ αij
	 * </pre>
	 * 
	 * </li>
	 * <li>the connection between destructors and the corresponding value constructor
	 * is defined by the following axiom:
	 * 
	 * <pre>
	 * (D) δi1 ⊗ ... ⊗ δiki = ɣi∼
	 * </pre>
	 * 
	 * </li>
	 * <li>
	 * the set constructor <code>DT</code> is translated to a fresh constant
	 * <code>Γ</code> and an axiom defines it as a fixed-point other the constructed values:
	 * 
	 * <pre>
	 * (E) Γ = (λ t1 ↦ t2 ↦ … ↦ tn · ⊤ ∣ (⋂ Γ ∣ ɣ1[ϕ11 × … × ϕ1k1] ⊆ Γ ∧ … ∧ ɣn[ϕn1 × … × ϕnkm] ⊆ Γ)
	 * </pre>
	 * 
	 * where ϕiki is obtained by substituting t1, t2, …, tn for the formal type
	 * parameters T1, T2, ..., Tn and Γ for DT in the set expression corresponding
	 * to the type αiki.
	 * </li>
	 * <li>
	 * the completeness axiom for the set datatype constructor:
	 * <pre>
	 * (F) Γ(T1 ↦ T2 ↦ … ↦ Tn) = τ
	 * </pre>
	 * </li>
	 * </ul>
	 * 
	 * Moreover, there are four special cases where these predicates are
	 * slightly modified:
	 * <ul>
	 * <li>when there is no type parameter, predicates (E) and (F) are not
	 * generated</li>
	 * <li>when all value constructors are basic, axiom (E) gets simplified to
	 * <pre>
	 * (E) Γ = (λ t1 ↦ t2 ↦ … ↦ tn · ⊤ ∣ ɣ1[ϕ11 × … × ϕ1k1] ∪ … ∪ ɣn[ϕn1 × … × ϕnkm])
	 * </pre></li>
	 * <li>when a constructor ɣi has no destructor, predicates (A), (C), and (D)
	 * are not generated and the singleton "{ɣi}" replaces "ran(ɣi)" in (B) and
	 * ɣi[ϕi1 × … × ϕikm] in (E)</li>
	 * <li>when there is only one constructor with arguments, (A) becomes
	 * ɣi ∈ αi1 × ... × αiki ⤖ τ and the predicate (B) becomes superfluous</li>
	 * </ul>
	 */
	List<Predicate> getAxioms();

}
