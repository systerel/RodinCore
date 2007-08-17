package org.eventb.pp.loader;

import static org.eventb.internal.pp.core.elements.terms.Util.cClause;
import static org.eventb.internal.pp.core.elements.terms.Util.cPred;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.Sort;
import org.eventb.internal.pp.core.elements.terms.AbstractPPTest;
import org.eventb.internal.pp.core.elements.terms.Constant;
import org.eventb.internal.pp.core.elements.terms.Util;
import org.eventb.internal.pp.core.elements.terms.Variable;
import org.eventb.internal.pp.core.elements.terms.VariableContext;
import org.eventb.internal.pp.core.elements.terms.VariableTable;
import org.eventb.internal.pp.loader.clause.ClauseBuilder;

/**
 * This class verifies that the variables, constants, ...
 * have the same reference.
 *
 * @author François Terrier
 *
 */
public class TestSameObjects extends AbstractPPTest {

	private static FormulaFactory ff = FormulaFactory.getDefault();
	

	private static Variable var0 = Util.cVar(1,S);
	private static Variable var1 = Util.cVar(2,T);
	
	private static ITypeEnvironment env = ff.makeTypeEnvironment();
	static {
		env.addName("S", ff.makePowerSetType(ff.makeGivenType("S")));
		env.addName("T", ff.makePowerSetType(ff.makeGivenType("T")));
	}
	
	public void testSimpleConstant () {
		String formula = "∀x,y·a ∈ S ∨ b ∈ T ∨ x ∈ S ∨ y ∈ T";
		
		ClauseBuilder result = Util.doPhaseOneAndTwo(formula, env, new MyVariableTable());
		
		// the expected result is a non-unit clause Sa ∨ Ta ∨ Sx ∨ Tx
		assertEquals(result.getClauses().size(), 1);
		Clause clause = result.getClauses().iterator().next();
		assertEquals(cClause(cPred(0, a),cPred(0, var0),cPred(1, b),cPred(1, var1)), clause);
		
	}
	
	static class MyVariableTable extends VariableTable {

		public MyVariableTable() {
			super(new VariableContext());
		}
		
		@Override
		public Constant getConstant(String name, Sort sort) {
			if (name.equals("a")) return a;
			if (name.equals("b")) return b;
			return super.getConstant(name, sort);
		}

	}
	
}
