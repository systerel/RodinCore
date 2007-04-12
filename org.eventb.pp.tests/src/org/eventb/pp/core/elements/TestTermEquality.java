package org.eventb.pp.core.elements;

import static org.eventb.pp.Util.cCons;
import static org.eventb.pp.Util.cDiv;
import static org.eventb.pp.Util.cELocVar;
import static org.eventb.pp.Util.cExpn;
import static org.eventb.pp.Util.cFLocVar;
import static org.eventb.pp.Util.cMinus;
import static org.eventb.pp.Util.cMod;
import static org.eventb.pp.Util.cPlus;
import static org.eventb.pp.Util.cTimes;
import static org.eventb.pp.Util.cUnMin;
import static org.eventb.pp.Util.cVar;

import java.util.HashMap;

import junit.framework.TestCase;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.internal.pp.core.elements.Sort;
import org.eventb.internal.pp.core.elements.terms.AbstractVariable;
import org.eventb.internal.pp.core.elements.terms.Constant;
import org.eventb.internal.pp.core.elements.terms.LocalVariable;
import org.eventb.internal.pp.core.elements.terms.Term;
import org.eventb.internal.pp.core.elements.terms.Variable;

public class TestTermEquality extends TestCase {

	
	private static Variable x = cVar();
	private static Variable y = cVar();
	private static Variable z = cVar();
	private static Constant a = cCons("a");
	private static Constant b = cCons("b");
	private static LocalVariable evar0 = cELocVar(0);
	private static LocalVariable evar1 = cELocVar(1);
	private static LocalVariable fvar0 = cFLocVar(0);
	private static LocalVariable fvar1 = cFLocVar(1);
	
	
	Term[][] equalTerms = new Term[][]{
			new Term[]{
					cVar(),cVar(),cVar()
			},
			new Term[]{
					x,x,y,y
			},
			new Term[]{
					cELocVar(0),cELocVar(1),cELocVar(2)
			},
			new Term[]{
					cFLocVar(0),cFLocVar(1),cFLocVar(2)
			},
			new Term[]{
					a,a
			},
			
			new Term[]{
					cPlus(cVar(),cVar()),cPlus(cVar(),cVar())
			},
			new Term[]{
					cMinus(cVar(),cVar()),cMinus(cVar(),cVar())
			},
			new Term[]{
					cTimes(cVar(),cVar()),cTimes(cVar(),cVar())
			},
			new Term[]{
					cExpn(cVar(),cVar()),cExpn(cVar(),cVar())
			},
			new Term[]{
					cMod(cVar(),cVar()),cMod(cVar(),cVar())
			},
			new Term[]{
					cUnMin(cVar(),cVar()),cUnMin(cVar(),cVar())
			},
			new Term[]{
					cDiv(cVar(),cVar()),cDiv(cVar(),cVar())
			},
			
			new Term[]{
					cPlus(x,y,z),cPlus(y,x,z)
			},			
			new Term[]{
					cPlus(x,z,y),cPlus(z,y,x)
			},
			
			new Term[]{
					cPlus(cPlus(x,z),y),cPlus(cPlus(z,y),x)
			},
			new Term[]{
					cPlus(cPlus(a,y),y),cPlus(cPlus(a,y),y)
			},
			
			new Term[]{
					cPlus(x,y),cPlus(y,x)
			},
			new Term[]{
					cMinus(x,y),cMinus(y,x)
			},
			new Term[]{
					cTimes(x,y),cTimes(y,x)
			},
			new Term[]{
					cExpn(x,y),cExpn(y,x)
			},
			new Term[]{
					cMod(x,y),cMod(y,x)
			},
			new Term[]{
					cUnMin(x),cUnMin(y)
			},
			new Term[]{
					cDiv(x,y),cDiv(y,x)
			},
			
			new Term[]{
					cPlus(cELocVar(0),cELocVar(0)),cPlus(cELocVar(0),cELocVar(0))
			},
			new Term[]{
					cMinus(cELocVar(0),cELocVar(0)),cMinus(cELocVar(0),cELocVar(0))
			},
			new Term[]{
					cTimes(cELocVar(0),cELocVar(0)),cTimes(cELocVar(0),cELocVar(0))
			},
			new Term[]{
					cExpn(cELocVar(0),cELocVar(0)),cExpn(cELocVar(0),cELocVar(0))
			},
			new Term[]{
					cMod(cELocVar(0),cELocVar(0)),cMod(cELocVar(0),cELocVar(0))
			},
			new Term[]{
					cUnMin(cELocVar(0)),cUnMin(cELocVar(0))
			},
			new Term[]{
					cDiv(cELocVar(0),cELocVar(0)),cDiv(cELocVar(0),cELocVar(0))
			},
			
			
	};

	Term[][] unequalTerms = new Term[][]{
			new Term[]{
					cVar(),cELocVar(0)
			},
			new Term[]{
					cVar(),cFLocVar(0)
			},
			new Term[]{
					cELocVar(0),cFLocVar(0)
			},
			new Term[]{
					a,cVar()
			},
			new Term[]{
					a,cELocVar(0)
			},
			new Term[]{
					a,cFLocVar(0)
			},
			new Term[]{
					a,b
			},
			
			new Term[]{
					cPlus(x,y),cPlus(x,x)
			},
			new Term[]{
					cPlus(x,y,z),cPlus(x,x,z)
			},
			new Term[]{
					cPlus(z,x,y),cPlus(x,z,x)
			},
			new Term[]{
					cPlus(z,x,y),cPlus(x,z)
			},
			new Term[]{
					cPlus(cELocVar(0),cELocVar(0)),cPlus(cELocVar(1),cELocVar(2))
			},
			new Term[]{
					cPlus(cFLocVar(0),cFLocVar(0)),cPlus(cFLocVar(1),cFLocVar(2))
			},
			new Term[]{
					cPlus(a,b),cPlus(b,a)
			},
			new Term[]{
					cPlus(x,y),cMinus(x,y)
			},
			new Term[]{
					cPlus(x,y),cTimes(x,y)
			},
			new Term[]{
					cPlus(x,y),cExpn(x,y)
			},
			new Term[]{
					cPlus(x,y),cMod(x,y)
			},
			new Term[]{
					cPlus(x,y),cDiv(x,y)
			},
			new Term[]{
					cMinus(x,y),cTimes(x,y)
			},
			new Term[]{
					cMinus(x,y),cExpn(x,y)
			},
			new Term[]{
					cMinus(x,y),cMod(x,y)
			},
			new Term[]{
					cMinus(x,y),cDiv(x,y)
			},
			new Term[]{
					cTimes(x,y),cExpn(x,y)
			},
			new Term[]{
					cTimes(x,y),cMod(x,y)
			},
			new Term[]{
					cTimes(x,y),cDiv(x,y)
			},
			new Term[]{
					cDiv(x,y),cMod(x,y)
			},
			
			
			
			new Term[]{
					x,cUnMin(a)
			},
			new Term[]{
					x,cPlus(a,b)
			},
			new Term[]{
					x,cMinus(a,b)
			},
			new Term[]{
					x,cTimes(a,b)
			},
			new Term[]{
					x,cMod(a,b)
			},
			new Term[]{
					x,cMinus(a,b)
			},
			new Term[]{
					x,cExpn(a,b)
			},

			new Term[]{
					a,cUnMin(a)
			},
			new Term[]{
					a,cPlus(a,b)
			},
			new Term[]{
					a,cMinus(a,b)
			},
			new Term[]{
					a,cTimes(a,b)
			},
			new Term[]{
					a,cMod(a,b)
			},
			new Term[]{
					a,cMinus(a,b)
			},
			new Term[]{
					a,cExpn(a,b)
			},

			new Term[]{
					cFLocVar(0),cUnMin(a)
			},
			new Term[]{
					cFLocVar(0),cPlus(a,b)
			},
			new Term[]{
					cFLocVar(0),cMinus(a,b)
			},
			new Term[]{
					cFLocVar(0),cTimes(a,b)
			},
			new Term[]{
					cFLocVar(0),cMod(a,b)
			},
			new Term[]{
					cFLocVar(0),cMinus(a,b)
			},
			new Term[]{
					cFLocVar(0),cExpn(a,b)
			},

			
			new Term[]{
					cELocVar(0),cUnMin(a)
			},
			new Term[]{
					cELocVar(0),cPlus(a,b)
			},
			new Term[]{
					cELocVar(0),cMinus(a,b)
			},
			new Term[]{
					cELocVar(0),cTimes(a,b)
			},
			new Term[]{
					cELocVar(0),cMod(a,b)
			},
			new Term[]{
					cELocVar(0),cMinus(a,b)
			},
			new Term[]{
					cELocVar(0),cExpn(a,b)
			},

			
	};

	public void testEqual() {
		HashMap<AbstractVariable, AbstractVariable> map;
		for (Term[] terms : equalTerms) {
			for (int i = 0; i < terms.length-1; i++) {
				map = new HashMap<AbstractVariable, AbstractVariable>();
				assertTrue(terms[i].equalsWithDifferentVariables(terms[i+1], map));
				map = new HashMap<AbstractVariable, AbstractVariable>();
				assertTrue(terms[i+1].equalsWithDifferentVariables(terms[i], map));
			
				assertEquals(""+terms[i]+","+terms[i+1],terms[i].hashCodeWithDifferentVariables(), terms[i+1].hashCodeWithDifferentVariables());
			}
		}
	}
	
	public void testUnEqual() {
		HashMap<AbstractVariable, AbstractVariable> map;
		for (Term[] terms : unequalTerms) {
			for (int i = 0; i < terms.length-1; i++) {
				map = new HashMap<AbstractVariable, AbstractVariable>();
				assertFalse("Term1 : "+terms[i].toString()+", term2 : "+terms[i+1].toString(),terms[i].equalsWithDifferentVariables(terms[i+1], map));
				map = new HashMap<AbstractVariable, AbstractVariable>();
				assertFalse("Term1 : "+terms[i+1].toString()+", term2 : "+terms[i].toString(),terms[i+1].equalsWithDifferentVariables(terms[i], map));
			}
		}
	}
	
	public void testEqualNormal() {
		assertEquals(x,x);
		assertEquals(x.hashCode(), x.hashCode());
		assertEquals(a,a);
		assertEquals(a.hashCode(), a.hashCode());
		assertEquals(evar0,evar0);
		assertEquals(evar0.hashCode(),evar0.hashCode());
		assertEquals(fvar0,fvar0);
		assertEquals(fvar0.hashCode(),fvar0.hashCode());
		assertEquals(cELocVar(0), cELocVar(0));
		assertEquals(cELocVar(0).hashCode(),cELocVar(0).hashCode());
		assertEquals(cFLocVar(0), cFLocVar(0));
		assertEquals(cFLocVar(0).hashCode(),cFLocVar(0).hashCode());
		assertFalse(evar0.equals(fvar0));
		assertFalse(fvar0.equals(evar0));
		
		assertFalse(cVar().equals(cVar()));
		assertFalse(x.equals(y));
		assertFalse(y.equals(x));
		assertFalse(a.equals(b));
		assertFalse(b.equals(a));
		assertFalse(a.equals(x));
		assertFalse(x.equals(a));
	}
	
	private static Sort A = new Sort(FormulaFactory.getDefault().makeGivenType("A"));
	private static Sort B = new Sort(FormulaFactory.getDefault().makeGivenType("B"));
	
	public void testEqualSort() {
		assertTrue(cVar(A).equalsWithDifferentVariables(cVar(A),new HashMap<AbstractVariable, AbstractVariable>()));
		assertFalse(cVar(A).equalsWithDifferentVariables(cVar(B),new HashMap<AbstractVariable, AbstractVariable>()));
		assertTrue(cELocVar(0,A).equalsWithDifferentVariables(cELocVar(0,A),new HashMap<AbstractVariable, AbstractVariable>()));
		assertFalse(cELocVar(0,A).equalsWithDifferentVariables(cELocVar(0,B),new HashMap<AbstractVariable, AbstractVariable>()));
		
	}
	
}
