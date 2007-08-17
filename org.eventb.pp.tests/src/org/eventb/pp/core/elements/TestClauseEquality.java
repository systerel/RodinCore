package org.eventb.pp.core.elements;

import static org.eventb.internal.pp.core.elements.terms.Util.cClause;
import static org.eventb.internal.pp.core.elements.terms.Util.cEqClause;
import static org.eventb.internal.pp.core.elements.terms.Util.cEqual;
import static org.eventb.internal.pp.core.elements.terms.Util.cNEqual;
import static org.eventb.internal.pp.core.elements.terms.Util.cNotPred;
import static org.eventb.internal.pp.core.elements.terms.Util.cNotProp;
import static org.eventb.internal.pp.core.elements.terms.Util.cPred;
import static org.eventb.internal.pp.core.elements.terms.Util.cProp;

import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.terms.AbstractPPTest;

public class TestClauseEquality extends AbstractPPTest {
	
	
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

//		testEqual(
//				cClause(cPred(1,cPlus(cPlus(a,y),y))),cClause(cPred(1,cPlus(cPlus(a,x),x)))
//		);

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

	public void testEqual(Clause... clauses) {
		for (int i = 0; i < clauses.length-1; i++) {
			Clause clause1 = clauses[i];
			Clause clause2 = clauses[i+1];
			assertTrue(clause1.equals(clause2));
			assertTrue(clause2.equals(clause1));

			assertEquals(""+clause1+","+clause2,clause1.hashCode(),clause2.hashCode());
		}
	}
	
	public void testUnequal(Clause... clauses) {
		for (int i = 0; i < clauses.length-1; i++) {
			Clause clause1 = clauses[i];
			Clause clause2 = clauses[i+1];
			assertFalse(clause1.equals(clause2));
			assertFalse(clause2.equals(clause1));
		}
	}
}
