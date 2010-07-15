package org.eventb.pp.core.elements.terms;

import static org.eventb.internal.pp.core.elements.terms.Util.cPlus;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eventb.internal.pp.core.elements.terms.AbstractPPTest;
import org.junit.Test;

public class TestQuantifierBooleans extends AbstractPPTest {

    @Test
	public void testConstant() {
		assertFalse(x.isConstant());
		assertTrue(evar0.isConstant());
		
		assertTrue(fvar0.isConstant());
		assertTrue(a.isConstant());
		
		assertFalse(cPlus(x,y).isConstant());
		assertFalse(cPlus(x,a).isConstant());
		assertTrue(cPlus(a,b).isConstant());
		assertTrue(cPlus(a,evar0).isConstant());
		assertFalse(cPlus(x,evar0).isConstant());
		assertTrue(cPlus(a,fvar0).isConstant());
		assertFalse(cPlus(x,fvar0).isConstant());
	}
	
    @Test
	public void testQuantified() {
		assertTrue(evar0.isQuantified());
		assertTrue(fvar0.isQuantified());
		
		assertFalse(x.isQuantified());
		assertFalse(a.isQuantified());
		
		assertFalse(cPlus(x,y).isQuantified());
		assertFalse(cPlus(x,a).isQuantified());
		assertFalse(cPlus(a,b).isQuantified());
		assertTrue(cPlus(a,evar0).isQuantified());
		assertTrue(cPlus(x,evar0).isQuantified());
		assertTrue(cPlus(a,fvar0).isQuantified());
		assertTrue(cPlus(x,fvar0).isQuantified());
	}
	
    @Test
	public void testForall() {
		assertFalse(evar0.isForall());
		assertTrue(fvar0.isForall());
		
		assertFalse(x.isForall());
		assertFalse(a.isForall());
		
		assertFalse(cPlus(x,y).isForall());
		assertFalse(cPlus(x,a).isForall());
		assertFalse(cPlus(a,b).isForall());
		assertFalse(cPlus(a,evar0).isForall());
		assertFalse(cPlus(x,evar0).isForall());
		assertTrue(cPlus(a,fvar0).isForall());
		assertTrue(cPlus(x,fvar0).isForall());
		
	}
}
