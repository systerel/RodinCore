package org.eventb.pp.loader;

import static org.eventb.internal.pp.core.elements.terms.Util.cClause;
import static org.eventb.internal.pp.core.elements.terms.Util.cCons;
import static org.eventb.internal.pp.core.elements.terms.Util.cPred;
import static org.eventb.internal.pp.core.elements.terms.Util.cVar;
import static org.eventb.internal.pp.core.elements.terms.Util.descriptor;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.PredicateLiteralDescriptor;
import org.eventb.internal.pp.core.elements.Sort;
import org.eventb.internal.pp.core.elements.terms.AbstractPPTest;
import org.eventb.internal.pp.core.elements.terms.Constant;
import org.eventb.internal.pp.core.elements.terms.Util;
import org.eventb.internal.pp.core.elements.terms.Variable;
import org.eventb.internal.pp.core.elements.terms.VariableContext;
import org.eventb.internal.pp.core.elements.terms.VariableTable;
import org.eventb.internal.pp.loader.clause.ClauseBuilder;
import org.junit.Test;

/**
 * This class verifies that the variables, constants, ...
 * have the same reference.
 *
 * @author François Terrier
 *
 */
public class TestSameObjects extends AbstractPPTest {

	private static final FormulaFactory ff = FormulaFactory.getDefault();
	
	private static final Constant aS = cCons("a", S);
	private static final Constant bT = cCons("b", T);

	private static final Variable var0 = cVar(1, S);
	private static final Variable var1 = cVar(2, T);

	private static final PredicateLiteralDescriptor d0S = descriptor(0, S);
	private static final PredicateLiteralDescriptor d1T = descriptor(1, T);

	private static ITypeEnvironment env = ff.makeTypeEnvironment();
	static {
		env.addGivenSet("S");
		env.addGivenSet("T");
	}
	
    @Test
	public void testSimpleConstant() {
		final String formula = "∀x,y·a ∈ S ∨ b ∈ T ∨ x ∈ S ∨ y ∈ T";
		final ClauseBuilder result = Util.doPhaseOneAndTwo(formula, env,
				new MyVariableTable());

		// the expected result is a non-unit clause Sa ∨ Ta ∨ Sx ∨ Tx
		final List<Clause> clauses = result.getClauses();
		assertEquals(clauses.size(), 1);
		final Clause clause = clauses.iterator().next();
		assertEquals(cClause(cPred(d0S, aS), cPred(d0S, var0), cPred(d1T, bT),
				cPred(d1T, var1)), clause);
	}
	
	static class MyVariableTable extends VariableTable {

		public MyVariableTable() {
			super(new VariableContext());
		}
		
		@Override
		@SuppressWarnings("synthetic-access")
		public Constant getConstant(String name, Sort sort) {
			if (name.equals("a")) return aS;
			if (name.equals("b")) return bT;
			return super.getConstant(name, sort);
		}

	}
	
}
