package org.eventb.pp.core.elements;

import static org.eventb.pp.Util.cClause;
import static org.eventb.pp.Util.cCons;
import static org.eventb.pp.Util.cELocVar;
import static org.eventb.pp.Util.cEqClause;
import static org.eventb.pp.Util.cEqual;
import static org.eventb.pp.Util.cFLocVar;
import static org.eventb.pp.Util.cNEqual;
import static org.eventb.pp.Util.cNotPred;
import static org.eventb.pp.Util.cNotProp;
import static org.eventb.pp.Util.cPlus;
import static org.eventb.pp.Util.cPred;
import static org.eventb.pp.Util.cProp;
import static org.eventb.pp.Util.cVar;
import junit.framework.TestCase;

import org.eventb.internal.pp.core.elements.IClause;
import org.eventb.internal.pp.core.elements.terms.Constant;
import org.eventb.internal.pp.core.elements.terms.LocalVariable;
import org.eventb.internal.pp.core.elements.terms.Variable;

public class TestClauseEquality extends TestCase{
	
	private static Variable x = cVar();
	private static Variable y = cVar();
	private static Variable z = cVar();
	private static Constant a = cCons("a");
	private static Constant b = cCons("b");
	private static LocalVariable evar0 = cELocVar(0);
	private static LocalVariable evar1 = cELocVar(1);
	private static LocalVariable fvar0 = cFLocVar(0);
	private static LocalVariable fvar1 = cFLocVar(1);
	
	public void testPropositionalEqual() {
		testEqual(
				cClause(cProp(0)),cClause(cProp(0))	
		);
	}

	public void testPredicateEqual() {
		testEqual(
				cClause(cPred(0,x)),cClause(cPred(0,y))	
		);
		testEqual(
				cClause(cPred(0,x,y)),cClause(cPred(0,y,x))	
		);
		testEqual(
				cClause(cPred(0,x,y,z)),cClause(cPred(0,y,x,z))	
		);
		testEqual(
				cClause(cPred(0,x,y,z)),cClause(cPred(0,z,y,x))	
		);
		testEqual(
				cClause(cPred(0,x),cPred(1,y)),cClause(cPred(0,y),cPred(1,x))
		);
		testEqual(
				cClause(cPred(0,x),cPred(1,y),cPred(2,z)),cClause(cPred(0,y),cPred(1,x),cPred(2,z))
		);
		testEqual(
				cClause(cPred(0,x),cPred(1,x),cPred(2,x)),cClause(cPred(0,y),cPred(1,y),cPred(2,y))
		);
		testEqual(
				cClause(cPred(0,x),cPred(1,y),cPred(2,x)),cClause(cPred(0,z),cPred(1,x),cPred(2,z))
		);

		testEqual(
				cClause(cPred(0,x),cEqual(x,y)),cClause(cPred(0,x),cEqual(y,x))
		);
		testEqual(
				cClause(cPred(0,y),cEqual(a,y)),cClause(cPred(0,x),cEqual(a,x))
		);
		testEqual(
				cClause(cPred(0,y),cEqual(a,y)),cClause(cPred(0,x),cEqual(x,a))
		);

		testEqual(
				cClause(cPred(0,a)),cClause(cPred(0,a))
		);
		testEqual(
				cClause(cPred(0,evar0)),cClause(cPred(0,evar1))
		);
		testEqual(
				cClause(cPred(0,x,evar0)),cClause(cPred(0,y,evar1))
		);
		testEqual(
				cEqClause(cProp(0),cPred(0,fvar0)),cEqClause(cProp(0),cPred(0,fvar1))
		);

		testEqual(
				cClause(cPred(1,cPlus(cPlus(a,y),y))),cClause(cPred(1,cPlus(cPlus(a,x),x)))
		);

		testEqual(
				cEqClause(cPred(3,x),cPred(0,x),cNotPred(1,x,evar0)),cEqClause(cPred(3,x),cPred(0,x),cNotPred(1,x,evar1))
		);
	}


	public void testUnequalClauses() {
		testUnequal(
				cClause(cProp(0)),cClause(cNotProp(0))
		);
		testUnequal(
				cClause(cPred(1,a)),cClause(cNotPred(1,a))
		);
		testUnequal(
				cClause(cEqual(a,a)),cClause(cNEqual(a,a))
		);

		testUnequal(
				cClause(cProp(0)),cClause(cProp(1))	
		);
		testUnequal(
				cClause(cPred(0,x)),cClause(cPred(0,x,x))	
		);
		testUnequal(
				cClause(cPred(0,x)),cClause(cPred(1,x))	
		);

		testUnequal(
				cClause(cPred(0,x,y)),cClause(cPred(0,y,y))	
		);

		testUnequal(
				cClause(cPred(0,x,y,z)),cClause(cPred(0,y,x,x))	
		);
		testUnequal(
				cClause(cPred(0,x,y,z)),cClause(cPred(0,x,x,x))	
		);
		testUnequal(
				cClause(cPred(0,x,y,z)),cClause(cPred(0,x,x,y))	
		);

		testUnequal(
				cClause(cPred(0,x),cPred(1,y)),cClause(cPred(0,x),cPred(1,x))
		);
		testUnequal(
				cClause(cPred(0,x),cPred(1,y),cPred(2,z)),cClause(cPred(0,x),cPred(1,x),cPred(2,z))
		);
		testUnequal(
				cClause(cPred(0,x),cPred(1,x),cPred(2,x)),cClause(cPred(0,x),cPred(1,y),cPred(2,y))
		);
		testUnequal(
				cClause(cPred(0,x),cPred(1,y),cPred(2,x)),cClause(cPred(0,z),cPred(1,x),cPred(2,x))
		);

		testUnequal(
				cClause(cPred(0,a)),cClause(cPred(0,b))
		);


		testUnequal(
				cEqClause(cProp(1),cPred(0,evar0)),cEqClause(cProp(1),cPred(0,fvar1))
		);
		testUnequal(
				cEqClause(cProp(1),cPred(0,fvar0)),cEqClause(cProp(1),cPred(0,evar1))
		);

		testUnequal(
				cEqClause(cProp(0),cProp(0)),cClause(cProp(0),cProp(0))	
		);

		testUnequal(
				cEqClause(cProp(0),cPred(1,a)),cClause(cProp(0),cPred(1,b))	
		);

		testUnequal(
				cClause(cPred(0,x),cEqual(a,y)),cClause(cPred(0,x),cEqual(a,x))
		);			
		testUnequal(
				cClause(cPred(0,x),cEqual(a,y)),cClause(cPred(0,x),cEqual(a,a))
		);
	}
	
	public void testUnequalDisjAndEqClauses() {
		testUnequal(cClause(cProp(0),cProp(0)),cEqClause(cProp(0),cProp(0)));
	}
	
	public void testEqualClauseWithEquality() {
		// this is unequal
		testUnequal(cClause(cPred(0,x),cEqual(x,y)),cClause(cPred(0,y),cEqual(x,y)));
	}

	public void testEqual(IClause... clauses) {
		for (int i = 0; i < clauses.length-1; i++) {
			IClause clause1 = clauses[i];
			IClause clause2 = clauses[i+1];
			assertTrue(clause1.equals(clause2));
			assertTrue(clause2.equals(clause1));

			assertEquals(""+clause1+","+clause2,clause1.hashCode(),clause2.hashCode());
		}
	}
	
	public void testUnequal(IClause... clauses) {
		for (int i = 0; i < clauses.length-1; i++) {
			IClause clause1 = clauses[i];
			IClause clause2 = clauses[i+1];
			assertFalse(clause1.equals(clause2));
			assertFalse(clause2.equals(clause1));
		}
	}
}
