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

import static org.eventb.internal.pp.core.elements.terms.Util.cDiv;
import static org.eventb.internal.pp.core.elements.terms.Util.cELocVar;
import static org.eventb.internal.pp.core.elements.terms.Util.cExpn;
import static org.eventb.internal.pp.core.elements.terms.Util.cFLocVar;
import static org.eventb.internal.pp.core.elements.terms.Util.cMinus;
import static org.eventb.internal.pp.core.elements.terms.Util.cMod;
import static org.eventb.internal.pp.core.elements.terms.Util.cPlus;
import static org.eventb.internal.pp.core.elements.terms.Util.cTimes;
import static org.eventb.internal.pp.core.elements.terms.Util.cUnMin;
import static org.eventb.internal.pp.core.elements.terms.Util.cVar;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.internal.pp.core.elements.Sort;
import org.eventb.internal.pp.core.elements.terms.AbstractPPTest;
import org.eventb.internal.pp.core.elements.terms.SimpleTerm;
import org.eventb.internal.pp.core.elements.terms.Term;
import org.junit.Test;

public class TestTermEquality extends AbstractPPTest {

	
	
	Term[][] equalTerms = new Term[][]{
			new Term[]{
					x,x
			},
			new Term[]{
					y,y
			},
			new Term[]{
					cELocVar(0,A),cELocVar(1,A),cELocVar(2,A)
			},
			new Term[]{
					cFLocVar(0,A),cFLocVar(1,A),cFLocVar(2,A)
			},
			new Term[]{
					a,a
			},
			
			new Term[]{
					cPlus(x,x),cPlus(x,x)
			},
			new Term[]{
					cMinus(x,x),cMinus(x,x)
			},
			new Term[]{
					cTimes(x,x),cTimes(x,x)
			},
			new Term[]{
					cExpn(x,x),cExpn(x,x)
			},
			new Term[]{
					cMod(x,x),cMod(x,x)
			},
			new Term[]{
					cUnMin(x),cUnMin(x)
			},
			new Term[]{
					cDiv(x,x),cDiv(x,x)
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
					cPlus(cELocVar(0,A),cELocVar(0,A)),cPlus(cELocVar(0,A),cELocVar(0,A))
			},
			new Term[]{
					cMinus(cELocVar(0,A),cELocVar(0,A)),cMinus(cELocVar(0,A),cELocVar(0,A))
			},
			new Term[]{
					cTimes(cELocVar(0,A),cELocVar(0,A)),cTimes(cELocVar(0,A),cELocVar(0,A))
			},
			new Term[]{
					cExpn(cELocVar(0,A),cELocVar(0,A)),cExpn(cELocVar(0,A),cELocVar(0,A))
			},
			new Term[]{
					cMod(cELocVar(0,A),cELocVar(0,A)),cMod(cELocVar(0,A),cELocVar(0,A))
			},
			new Term[]{
					cUnMin(cELocVar(0,A)),cUnMin(cELocVar(0,A))
			},
			new Term[]{
					cDiv(cELocVar(0,A),cELocVar(0,A)),cDiv(cELocVar(0,A),cELocVar(0,A))
			},
			
			
	};

	Term[][] unequalTerms = new Term[][]{
			new Term[]{
					x, y
			},
			new Term[]{
					x, evar0
			},
			new Term[]{
					x, fvar0
			},
			new Term[]{
					evar0, fvar0
			},
			new Term[]{
					evar0, evar1
			},
			new Term[]{
					fvar0, fvar1
			},
			new Term[]{
					a,x
			},
			new Term[]{
					a,evar0
			},
			new Term[]{
					a,fvar0
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
					cPlus(cELocVar(0,A),cELocVar(0,A)),cPlus(cELocVar(1,A),cELocVar(2,A))
			},
			new Term[]{
					cPlus(cFLocVar(0,A),cFLocVar(0,A)),cPlus(cFLocVar(1,A),cFLocVar(2,A))
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
					fvar0,cUnMin(a)
			},
			new Term[]{
					fvar0,cPlus(a,b)
			},
			new Term[]{
					fvar0,cMinus(a,b)
			},
			new Term[]{
					fvar0,cTimes(a,b)
			},
			new Term[]{
					fvar0,cMod(a,b)
			},
			new Term[]{
					fvar0,cMinus(a,b)
			},
			new Term[]{
					fvar0,cExpn(a,b)
			},

			
			new Term[]{
					evar0,cUnMin(a)
			},
			new Term[]{
					evar0,cPlus(a,b)
			},
			new Term[]{
					evar0,cMinus(a,b)
			},
			new Term[]{
					evar0,cTimes(a,b)
			},
			new Term[]{
					evar0,cMod(a,b)
			},
			new Term[]{
					evar0,cMinus(a,b)
			},
			new Term[]{
					evar0,cExpn(a,b)
			},

			
	};

    @Test
	public void testEqualWithDifferentVariables() {
		HashMap<SimpleTerm, SimpleTerm> map;
		for (Term[] terms : equalTerms) {
			for (int i = 0; i < terms.length-1; i++) {
				map = new HashMap<SimpleTerm, SimpleTerm>();
				assertTrue(terms[i].equalsWithDifferentVariables(terms[i+1], map));
				map = new HashMap<SimpleTerm, SimpleTerm>();
				assertTrue(terms[i+1].equalsWithDifferentVariables(terms[i], map));
			
				assertEquals(""+terms[i]+","+terms[i+1],terms[i].hashCodeWithDifferentVariables(), terms[i+1].hashCodeWithDifferentVariables());
			}
		}
	}
	
    @Test
	public void testUnEqual() {
//		HashMap<SimpleTerm, SimpleTerm> map;
		for (Term[] terms : unequalTerms) {
			for (int i = 0; i < terms.length-1; i++) {
				assertFalse("Term1 : "+terms[i].toString()+", term2 : "+terms[i+1].toString(),terms[i].equals(terms[i+1]));
				assertFalse("Term1 : "+terms[i+1].toString()+", term2 : "+terms[i].toString(),terms[i+1].equals(terms[i]));
			}
		}
	}
	
    @Test
	public void testEqualNormal() {
		assertEquals(x,x);
		assertEquals(x.hashCode(), x.hashCode());
		assertEquals(a,a);
		assertEquals(a.hashCode(), a.hashCode());
		assertEquals(evar0,evar0);
		assertEquals(evar0.hashCode(),evar0.hashCode());
		assertEquals(fvar0,fvar0);
		assertEquals(fvar0.hashCode(),fvar0.hashCode());
		assertEquals(evar0, evar0);
		assertEquals(cELocVar(0,A).hashCodeWithDifferentVariables(),cELocVar(0,A).hashCodeWithDifferentVariables());
		assertTrue(evar0.equalsWithDifferentVariables(evar1, new HashMap<SimpleTerm, SimpleTerm>()));
		assertTrue(fvar0.equalsWithDifferentVariables(fvar1, new HashMap<SimpleTerm, SimpleTerm>()));
		assertTrue(x.equalsWithDifferentVariables(y, new HashMap<SimpleTerm, SimpleTerm>()));
		assertFalse(fvar0.equalsWithDifferentVariables(evar1, new HashMap<SimpleTerm, SimpleTerm>()));
		assertFalse(evar0.equalsWithDifferentVariables(fvar1, new HashMap<SimpleTerm, SimpleTerm>()));
		assertFalse(fvar0.equalsWithDifferentVariables(x, new HashMap<SimpleTerm, SimpleTerm>()));
		assertFalse(evar0.equalsWithDifferentVariables(y, new HashMap<SimpleTerm, SimpleTerm>()));
		
		assertFalse(evar0.equals(fvar0));
		assertFalse(fvar0.equals(evar0));
		
		assertFalse(cVar(0,A).equals(cVar(0,A)));
		assertFalse(x.equals(y));
		assertFalse(y.equals(x));
		assertFalse(a.equals(b));
		assertFalse(b.equals(a));
		assertFalse(a.equals(x));
		assertFalse(x.equals(a));
	}
	
	private static Sort A = new Sort(FormulaFactory.getDefault().makeGivenType("A"));
	private static Sort B = new Sort(FormulaFactory.getDefault().makeGivenType("B"));
	
    @Test
	public void testEqualSort() {
		assertTrue(cVar(1,A).equalsWithDifferentVariables(cVar(1,A),new HashMap<SimpleTerm, SimpleTerm>()));
		assertFalse(cVar(1,A).equalsWithDifferentVariables(cVar(1,B),new HashMap<SimpleTerm, SimpleTerm>()));
		assertTrue(cELocVar(0,A).equalsWithDifferentVariables(cELocVar(0,A),new HashMap<SimpleTerm, SimpleTerm>()));
		assertFalse(cELocVar(0,A).equalsWithDifferentVariables(cELocVar(0,B),new HashMap<SimpleTerm, SimpleTerm>()));
		
	}
	
}
