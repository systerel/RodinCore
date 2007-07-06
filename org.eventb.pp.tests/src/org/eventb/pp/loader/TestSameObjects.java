package org.eventb.pp.loader;

import static org.eventb.pp.Util.cClause;
import static org.eventb.pp.Util.cPred;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.Sort;
import org.eventb.internal.pp.core.elements.terms.Variable;
import org.eventb.internal.pp.loader.clause.LoaderResult;
import org.eventb.pp.AbstractPPTest;
import org.eventb.pp.Util;

/**
 * This class verifies that the variables, constants, ...
 * have the same reference.
 *
 * @author François Terrier
 *
 */
public class TestSameObjects extends AbstractPPTest {

	private static FormulaFactory ff = FormulaFactory.getDefault();
	

	private static Variable var0 = Util.cVar(new Sort(ff.makeGivenType("S")));
	private static Variable var1 = Util.cVar(new Sort(ff.makeGivenType("T")));
	
	private static ITypeEnvironment env = ff.makeTypeEnvironment();
	static {
		env.addName("S", ff.makePowerSetType(ff.makeGivenType("S")));
		env.addName("T", ff.makePowerSetType(ff.makeGivenType("T")));
	}
	
	public void testSimpleConstant () {
		String formula = "∀x,y·a ∈ S ∨ b ∈ T ∨ x ∈ S ∨ y ∈ T";
		
		LoaderResult result = Util.doPhaseOneAndTwo(formula, env);
		
		// the expected result is a non-unit clause Sa ∨ Ta ∨ Sx ∨ Tx
		// let us assert this
		assertEquals(result.getClauses().size(), 1);
		Clause clause = result.getClauses().iterator().next();
		assertEquals(cClause(cPred(0, a),cPred(0, var0),cPred(1, b),cPred(1, var1)), clause);
		
	}
	
}
