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
package org.eventb.pp.core.elements.terms;

import static org.eventb.internal.pp.core.elements.terms.Util.cPlus;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import org.eventb.internal.pp.core.elements.terms.AbstractPPTest;
import org.eventb.internal.pp.core.elements.terms.SimpleTerm;
import org.eventb.internal.pp.core.elements.terms.Term;
import org.junit.Test;

public class TestSubstitution extends AbstractPPTest {
	
	static class TestPair {
		Substitution[] substitutions;
		Term input;
		Term output;
		
		public TestPair(Term input, Term output, Substitution... substitutions) {
			this.input = input;
			this.output = output;
			this.substitutions = substitutions;
		}
		
		public HashMap<SimpleTerm, SimpleTerm> getMap() {
			HashMap<SimpleTerm, SimpleTerm> map = new HashMap<SimpleTerm, SimpleTerm>();
			for (Substitution sub : substitutions) {
				map.put(sub.var, sub.term);
			}
			return map;
		}
	}
	
	static class Substitution {
		SimpleTerm var;
		SimpleTerm term;
		
		Substitution (SimpleTerm var, SimpleTerm term) {
			this.var = var;
			this.term = term;
		}
	}
	
	static Substitution sub(SimpleTerm var, SimpleTerm term) {
		return new Substitution(var, term);
	}
	

	// substitutions are done one after the other
	TestPair[] tests = new TestPair[]{
			new TestPair(
					var0,
					a,
					sub(var0, a)
			),
			
			new TestPair(
					var0,
					var0,
					sub(var1, var1)
			),
			
			new TestPair(
					cPlus(var0, var1, var2),
					cPlus(a, var1, var2),
					sub(var0, a)
			),
			
			new TestPair(
					cPlus(var0, var1, var2),
					cPlus(a, b, var2),
					sub(var0, a),
					sub(var1, b)
			),
			
			new TestPair(
					cPlus(evar0, fvar0),
					cPlus(a, b),
					sub(evar0, a),
					sub(fvar0, b)
			),
	};

    @Test
	public void test() {
		for (TestPair test : tests) {
			assertEquals(Term.substituteSimpleTerms(test.getMap(),test.input), test.output);
		}
	}
	
}
