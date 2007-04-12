package org.eventb.pp.core.elements;

import static org.eventb.pp.Util.cPlus;

import java.util.HashMap;

import org.eventb.internal.pp.core.elements.terms.AbstractVariable;
import org.eventb.internal.pp.core.elements.terms.Term;
import org.eventb.pp.AbstractPPTest;

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
		
		public HashMap<AbstractVariable, Term> getMap() {
			HashMap<AbstractVariable, Term> map = new HashMap<AbstractVariable, Term>();
			for (Substitution sub : substitutions) {
				map.put(sub.var, sub.term);
			}
			return map;
		}
	}
	
	static class Substitution {
		AbstractVariable var;
		Term term;
		
		Substitution (AbstractVariable var, Term term) {
			this.var = var;
			this.term = term;
		}
	}
	
	static Substitution sub(AbstractVariable var, Term term) {
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

	public void test() {
		for (TestPair test : tests) {
			assertEquals(test.input.substitute(test.getMap()), test.output);
		}
	}
	
}
