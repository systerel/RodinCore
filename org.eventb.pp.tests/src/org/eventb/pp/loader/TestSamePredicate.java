/*******************************************************************************
 * Copyright (c) 2007, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.pp.loader;

import static org.eventb.internal.pp.core.elements.terms.AbstractPPTest.mTypeEnvironment;
import static org.junit.Assert.assertEquals;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.internal.pp.core.elements.terms.Util;
import org.eventb.internal.pp.loader.formula.AbstractFormula;
import org.eventb.internal.pp.loader.formula.SignedFormula;
import org.eventb.internal.pp.loader.formula.descriptor.LiteralDescriptor;
import org.eventb.internal.pp.loader.predicate.AbstractContext;
import org.junit.Test;

/**
 * This class tests that two predicates having the same terms as arguments 
 * with the same corresponding types are constructed with the same name. It
 * also tests that two different predicates are not constructed with the
 * same name. 
 *
 * @author François Terrier
 *
 */
public class TestSamePredicate {
	static FormulaFactory ff = FormulaFactory.getDefault();
	
	static ITypeEnvironmentBuilder env = mTypeEnvironment( //
			"S=ℙ(S); T=ℙ(T); a=S; b=S; S1=ℙ(S); S2=ℙ(S); c=T; d=T; T1=ℙ(T);" //
			+" T2=ℙ(T); e=BOOL; f=BOOL; SS1=S↔S; SS2=S↔S; TT1=T↔T;" //
			+" TT2=T↔T; k=ℤ; l=ℤ; NS=ℤ↔S; NN=ℤ↔ℤ; SN=S↔ℤ; STN=S×T↔ℤ;"
			, ff);
	
	String[][] test1 = new String[][]{
			new String[]{
				"a ∈ S1", "b ∈ S1", "a ∈ S2", "b ∈ S2"
			},
			new String[]{
				"c ∈ T1", "d ∈ T1", "c ∈ T2", "d ∈ T2"
			},
			new String[]{
				"a ↦ b ∈ SS1", "b ↦ a ∈ SS1", "a ↦ b ∈ SS2", "b ↦ a ∈ SS2"
			},
			new String[]{
				"c ↦ d ∈ TT1", "d ↦ c ∈ TT1", "c ↦ d ∈ TT2", "d ↦ c ∈ TT2"
			},
//			new String[]{
//				"k ↦ a ∈ NS", "k ↦ b ∈ NS", "k + 1 ↦ b ∈ NS", "k ∗ 1 ↦ b ∈ NS"
//			},
//			new String[]{
//				"k ↦ k ∈ NN", "k ↦ k + 1 ∈ NN"
//			},
//			new String[]{
//				"a ↦ k ∈ SN", "b ↦ k ∈ SN", "b ↦ k + 1 ∈ SN", "b ↦ k ∗ 1 ∈ SN"
//			},
//			new String[]{
//				"(a ↦ c) ↦ 1 ∈ STN", "(b ↦ d) ↦ k ∈ STN", "(a ↦ d) ↦ 1 ∈ STN", "(b ↦ c) ↦ k + 1 ∈ STN"
//			},

			new String[]{
				"e = f", "e = TRUE", "f = TRUE", "¬(e = TRUE)", "¬(f = TRUE)"
			},
			new String[]{
				"a = b", "b = a"
			},
			new String[]{
				"c = d", "d = c"
			},
			// TODO decide if it is the same literal
//			new String[]{
//				"k = 1", "k = 2", "k = 2 + k"
//			},
				
			// TODO clauses !
	};
	
	private SignedFormula<?> build(AbstractContext context, String test) {
		final Predicate pred = Util.parsePredicate(test, env);
		context.load(pred, false);
		return context.getLastResult().getSignature();
	}
	
    @Test
	public void testSamePredicate() {
		for (String[] tests : test1) {
			final AbstractContext context = new AbstractContext();
			LiteralDescriptor desc = null;
			for (String test : tests) {
				AbstractFormula<?> pp = build(context, test).getFormula();
				if (desc == null) desc = pp.getLiteralDescriptor();
				else assertEquals(desc, pp.getLiteralDescriptor());
			}
		}
	}
	
//	public void testDifferentPredicate() {
//		PredicateBuilder builder = new PredicateBuilder();
//		List<LiteralDescriptor> desc = new ArrayList<LiteralDescriptor>();
//		for (String[] tests : test1) {
//			String test = tests[0];
//			AbstractFormula<?> pp = ((SignedFormula<?>)build(builder, test)).getFormula();
//			// TODO: document while a loop below?
//			for (LiteralDescriptor lit : desc) {
//				assertNotSame(lit.toString() + " " + pp.getLiteralDescriptor(), lit, pp.getLiteralDescriptor());
//			}
//			desc.add(pp.getLiteralDescriptor());
//		}
//	}
	
}
