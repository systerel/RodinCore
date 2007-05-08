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
	
	IClause[][] equalClauses = new IClause[][]{
			new IClause[]{
					cClause(cProp(0)),cClause(cProp(0))	
			},
			new IClause[]{
					cClause(cPred(0,x)),cClause(cPred(0,y))	
			},
			new IClause[]{
					cClause(cPred(0,x,y)),cClause(cPred(0,y,x))	
			},
			new IClause[]{
					cClause(cPred(0,x,y,z)),cClause(cPred(0,y,x,z))	
			},
			new IClause[]{
					cClause(cPred(0,x,y,z)),cClause(cPred(0,z,y,x))	
			},
			new IClause[]{
					cClause(cPred(0,x),cPred(1,y)),cClause(cPred(0,y),cPred(1,x))
			},
			new IClause[]{
					cClause(cPred(0,x),cPred(1,y),cPred(2,z)),cClause(cPred(0,y),cPred(1,x),cPred(2,z))
			},
			new IClause[]{
					cClause(cPred(0,x),cPred(1,x),cPred(2,x)),cClause(cPred(0,y),cPred(1,y),cPred(2,y))
			},
			new IClause[]{
					cClause(cPred(0,x),cPred(1,y),cPred(2,x)),cClause(cPred(0,z),cPred(1,x),cPred(2,z))
			},

			new IClause[]{
					cClause(cPred(0,x),cEqual(x,y)),cClause(cPred(0,x),cEqual(y,x))
			},
			new IClause[]{
					cClause(cPred(0,y),cEqual(a,y)),cClause(cPred(0,x),cEqual(a,x))
			},
			new IClause[]{
					cClause(cPred(0,y),cEqual(a,y)),cClause(cPred(0,x),cEqual(x,a))
			},
			
			new IClause[]{
					cClause(cPred(0,a)),cClause(cPred(0,a))
			},
			new IClause[]{
					cClause(cPred(0,evar0)),cClause(cPred(0,evar1))
			},
			new IClause[]{
					cClause(cPred(0,x,evar0)),cClause(cPred(0,y,evar1))
			},
			new IClause[]{
					cEqClause(cProp(0),cPred(0,fvar0)),cEqClause(cProp(0),cPred(0,fvar1))
			},
			
			new IClause[]{
					cClause(cPred(1,cPlus(cPlus(a,y),y))),cClause(cPred(1,cPlus(cPlus(a,x),x)))
			},
			
			
	};

	
	IClause[][] unequalClauses = new IClause[][]{
			new IClause[]{
					cClause(cProp(0)),cClause(cNotProp(0))
			},
			new IClause[]{
					cClause(cPred(1,a)),cClause(cNotPred(1,a))
			},
			new IClause[]{
					cClause(cEqual(a,a)),cClause(cNEqual(a,a))
			},
			
			new IClause[]{
					cClause(cProp(0)),cClause(cProp(1))	
			},
			new IClause[]{
					cClause(cPred(0,x)),cClause(cPred(0,x,x))	
			},
			new IClause[]{
					cClause(cPred(0,x)),cClause(cPred(1,x))	
			},
			
			new IClause[]{
					cClause(cPred(0,x,y)),cClause(cPred(0,y,y))	
			},
			
			new IClause[]{
					cClause(cPred(0,x,y,z)),cClause(cPred(0,y,x,x))	
			},
			new IClause[]{
					cClause(cPred(0,x,y,z)),cClause(cPred(0,x,x,x))	
			},
			new IClause[]{
					cClause(cPred(0,x,y,z)),cClause(cPred(0,x,x,y))	
			},
			
			new IClause[]{
					cClause(cPred(0,x),cPred(1,y)),cClause(cPred(0,x),cPred(1,x))
			},
			new IClause[]{
					cClause(cPred(0,x),cPred(1,y),cPred(2,z)),cClause(cPred(0,x),cPred(1,x),cPred(2,z))
			},
			new IClause[]{
					cClause(cPred(0,x),cPred(1,x),cPred(2,x)),cClause(cPred(0,x),cPred(1,y),cPred(2,y))
			},
			new IClause[]{
					cClause(cPred(0,x),cPred(1,y),cPred(2,x)),cClause(cPred(0,z),cPred(1,x),cPred(2,x))
			},

			new IClause[]{
					cClause(cPred(0,a)),cClause(cPred(0,b))
			},

			
			new IClause[]{
					cEqClause(cProp(1),cPred(0,evar0)),cEqClause(cProp(1),cPred(0,fvar1))
			},
			new IClause[]{
					cEqClause(cProp(1),cPred(0,fvar0)),cEqClause(cProp(1),cPred(0,evar1))
			},
			
			new IClause[]{
					cEqClause(cProp(0),cProp(0)),cClause(cProp(0),cProp(0))	
			},

			new IClause[]{
					cEqClause(cProp(0),cPred(1,a)),cClause(cProp(0),cPred(1,b))	
			},

			new IClause[]{
					cClause(cPred(0,x),cEqual(a,y)),cClause(cPred(0,x),cEqual(a,x))
			},			
			new IClause[]{
					cClause(cPred(0,x),cEqual(a,y)),cClause(cPred(0,x),cEqual(a,a))
			},
						
	};
	

	public void testEqual() {
		for (IClause[] clauses : equalClauses) {
			for (int i = 0; i < clauses.length-1; i++) {
				IClause clause1 = clauses[i];
				IClause clause2 = clauses[i+1];
				assertTrue(clause1.equals(clause2));
				assertTrue(clause2.equals(clause1));
				
				assertEquals(""+clause1+","+clause2,clause1.hashCode(),clause2.hashCode());
			}
		}
	}
	
	public void testUnequal() {
		for (IClause[] clauses : unequalClauses) {
			for (int i = 0; i < clauses.length-1; i++) {
				IClause clause1 = clauses[i];
				IClause clause2 = clauses[i+1];
				assertFalse(clause1.equals(clause2));
				assertFalse(clause2.equals(clause1));
			}
		}
	}
}
