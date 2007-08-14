package org.eventb.pp.core.provers.equality;

import static org.eventb.pp.Util.cClause;
import static org.eventb.pp.Util.cProp;
import static org.eventb.pp.Util.mSet;

import java.util.List;

import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.provers.equality.EquivalenceManager;
import org.eventb.internal.pp.core.provers.equality.IInstantiationResult;
import org.eventb.internal.pp.core.provers.equality.unionfind.FactResult;
import org.eventb.internal.pp.core.provers.equality.unionfind.InstantiationResult;
import org.eventb.pp.AbstractPPTest;

public class TestEquivalenceManager extends AbstractPPTest {
	
	private EquivalenceManager manager;
	
	@Override
	protected void setUp() throws Exception {
		// init solver
		manager = new EquivalenceManager();
	}
	
	public void testSimpleContradiction1() {
		assertNull(manager.addFactEquality(ab, cClause(ab)));
		assertNotNull(manager.addFactEquality(nab, cClause(nab)));
	}
	
	public void testSimpleContradiction2() {
		assertNull(manager.addFactEquality(nab, cClause(ab)));
		assertNotNull(manager.addFactEquality(ab, cClause(nab)));
	}
	
	public void testQueryInformationRemovedOnNodes() {
		assertNull(manager.addQueryEquality(ab, cClause(cProp(0),ab)));
		assertNotNull(manager.addFactEquality(ab, cClause(ab)));
		
		manager.backtrack(Level.base);
		assertNull(manager.addFactEquality(ab, cClause(ab)));
	}
	
	public void testQueryInformationRemovedOnNodes2() {
		assertNull(manager.addQueryEquality(nab, cClause(cProp(0),nab)));
		assertNotNull(manager.addFactEquality(ab, cClause(ab)));
		
		manager.backtrack(Level.base);
		assertNull(manager.addFactEquality(ab, cClause(ab)));
	}
	
	public void testMultipleQuerySameClause() {
		assertNull(manager.addQueryEquality(ab, cClause(cProp(0),ab,bc)));
		assertNull(manager.addQueryEquality(bc, cClause(cProp(0),ab,bc)));
		
		FactResult result = manager.addFactEquality(ab, cClause(ab)); 
		assertNotNull(result);
		assertTrue(result.getSolvedQueries().size() == 1);
		assertTrue(result.getSolvedQueries().get(0).getQuerySource().getClauses().size() == 1);
		
		assertNull(manager.addQueryEquality(bc, cClause(cProp(0),bc)));
	}
	
	public void testSimpleLevel() {
		assertNull(manager.addFactEquality(ab, cClause(ONE,ab)));
		FactResult result = manager.addFactEquality(nab, cClause(BASE,nab));
		assertNotNull(result);
		assertEquals(result.getContradictionLevel(),ONE);
	}
	
	public void testLevelUnitClauses() {
		assertNull(manager.addFactEquality(ab, cClause(ONE,ab)));
		assertNull(manager.addFactEquality(ab, cClause(BASE,ab)));
		
		FactResult result = manager.addFactEquality(nab, cClause(BASE,nab));
		assertNotNull(result);
		assertEquals(result.getContradictionLevel(),BASE);
	}
	
	public void testRemoveInexistantClause() {
		manager.removeQueryEquality(ab, cClause(cProp(0),ab));
	}
	
	public void testRemoveInexistantClauseWithLevel() {
		manager.addQueryEquality(ab, cClause(BASE,cProp(0),ab));
		manager.removeQueryEquality(ab, cClause(ONE, cProp(0),ab));
		manager.backtrack(BASE);
		assertNotNull(manager.addFactEquality(ab, cClause(ab)));
	}
	
	public void testRemoveInexistantClauseWithLevel2() {
		manager.addQueryEquality(ab, cClause(ONE,cProp(0),ab));
		manager.addQueryEquality(ab, cClause(BASE, cProp(0),ab));
		manager.backtrack(BASE);
		assertNotNull(manager.addFactEquality(ab, cClause(ab)));
	}
	
	public void testRemoveInexistantClauseWithLevel3() {
		manager.addQueryEquality(ab, cClause(BASE,cProp(0),ab));
		manager.addQueryEquality(ab, cClause(ONE, cProp(0),ab));
		manager.backtrack(BASE);
		assertNotNull(manager.addFactEquality(ab, cClause(ab)));
	}
	
	public void testRemoveClauseRootInfo() {
		manager.addQueryEquality(ab, cClause(cProp(0),ab));
		manager.removeQueryEquality(ab, cClause(cProp(0),ab));
		
		assertNull(manager.addFactEquality(ab, cClause(ab)));
	}
	
	public void testRemoveClauseRootInfo2() {
		manager.addQueryEquality(nab, cClause(cProp(0),nab));
		manager.removeQueryEquality(nab, cClause(cProp(0),nab));
		
		assertNull(manager.addFactEquality(ab, cClause(ab)));
	}
	
	public void testRemoveClauseRootInfoWithBacktrack() {
		manager.addQueryEquality(ab, cClause(cProp(0),ab));
		manager.removeQueryEquality(ab, cClause(cProp(0),ab));
		manager.backtrack(BASE);
		
		assertNull(manager.addFactEquality(ab, cClause(ab)));
	}

	public void testBacktrack1() {
		manager.addFactEquality(ab, cClause(ab));
		manager.backtrack(BASE);
		assertNotNull(manager.addFactEquality(nab, cClause(ab)));
	}
	
	public void testBacktrack2() {
		manager.addFactEquality(nab, cClause(nab));
		manager.backtrack(BASE);
		assertNotNull(manager.addFactEquality(ab, cClause(ab)));
	}
	
	public void testQueryBacktrack1() {
		manager.addQueryEquality(ab, cClause(ab));
		manager.backtrack(BASE);
		assertNotNull(manager.addFactEquality(nab, cClause(ab)));
	}
	
	public void testQueryBacktrack2() {
		manager.addQueryEquality(nab, cClause(nab));
		manager.backtrack(BASE);
		assertNotNull(manager.addFactEquality(ab, cClause(ab)));
	}
	
	
	public void testRedundantQuery() {
		manager.addQueryEquality(ab, cClause(ab,cProp(0)));
		manager.addQueryEquality(ab, cClause(ab,cProp(1)));
		
		FactResult result = manager.addFactEquality(ab, cClause(ab));
		assertNotNull(result);
		assertFalse(result.hasContradiction());
		assertEquals(result.getSolvedQueries().size(), 1);
	}
	
	public void testRedundantFact() {
		manager.addFactEquality(nab, cClause(nab));
		manager.addFactEquality(nab, cClause(ONE,nab));
		
		FactResult result = manager.addFactEquality(ab, cClause(ab));
		assertNotNull(result);
		assertEquals(BASE, result.getContradictionLevel());
		
//		manager.backtrack(BASE);
	}
	
	public void testRedundantFact2() {
		manager.addFactEquality(nab, cClause(ONE,nab));
		manager.addFactEquality(nab, cClause(nab));
		
		FactResult result = manager.addFactEquality(ab, cClause(ab));
		assertNotNull(result);
		assertEquals(BASE, result.getContradictionLevel());
		
//		manager.backtrack(BASE);
	}
	
	// instantiations
	public void testInstantiation() {
		assertNull(manager.addInstantiationEquality(xa, cClause(cProp(0),xa)));
		FactResult result = manager.addFactEquality(nab, cClause(nab));
		assertNotNull(result);
		assertEquals(1, result.getSolvedInstantiations().size());
		IInstantiationResult result1 = result.getSolvedInstantiations().get(0);
		assertEquals(b, result1.getInstantiationValue());
		assertEquals(xa, result1.getEquality());
		assertEquals(mSet(cClause(cProp(0),xa)), result1.getSolvedClauses());
		assertEquals(mSet(cClause(nab)), result1.getSolvedValueOrigin());
	}
	
	public void testInstantiation2() {
		assertNull(manager.addInstantiationEquality(xb, cClause(cProp(0),xb)));
		FactResult result = manager.addFactEquality(nab, cClause(nab));
		assertNotNull(result);
		assertEquals(1, result.getSolvedInstantiations().size());
		IInstantiationResult result1 = result.getSolvedInstantiations().get(0);
		assertEquals(a, result1.getInstantiationValue());
		assertEquals(xb, result1.getEquality());
		assertEquals(mSet(cClause(cProp(0),xb)), result1.getSolvedClauses());
		assertEquals(mSet(cClause(nab)), result1.getSolvedValueOrigin());
	}
	
	
	public void testInstantiationFactFirst() {
		assertNull(manager.addFactEquality(nab, cClause(nab)));
		List<InstantiationResult> result = manager.addInstantiationEquality(xa, cClause(cProp(0),xa));
		assertNotNull(result);
		assertEquals(1, result.size());
		IInstantiationResult result1 = result.get(0);
		assertEquals(b, result1.getInstantiationValue());
		assertEquals(xa, result1.getEquality());
		assertEquals(mSet(cClause(cProp(0),xa)), result1.getSolvedClauses());
		assertEquals(mSet(cClause(nab)), result1.getSolvedValueOrigin());
	}
	
	public void testInstantiationStays() {
		assertNull(manager.addInstantiationEquality(xa, cClause(cProp(0),xa)));
		manager.addFactEquality(nab, cClause(nab));
		FactResult result = manager.addFactEquality(nac, cClause(nac));
		assertNotNull(result);
		assertEquals(1, result.getSolvedInstantiations().size());
		IInstantiationResult result1 = result.getSolvedInstantiations().get(0);
		assertEquals(c, result1.getInstantiationValue());
		assertEquals(xa, result1.getEquality());
		assertEquals(mSet(cClause(cProp(0),xa)), result1.getSolvedClauses());
		assertEquals(mSet(cClause(nac)), result1.getSolvedValueOrigin());
	}
	
	public void testRedundantInstantiation() {
		assertNull(manager.addInstantiationEquality(xa, cClause(cProp(0),xa)));
		manager.addFactEquality(nab, cClause(nab));
		assertNull(manager.addFactEquality(bc, cClause(bc)));
		assertNull(manager.addFactEquality(nac, cClause(nac)));
	}
	
	public void testBacktrackInstantiation() {
		assertNull(manager.addInstantiationEquality(xa, cClause(ONE, cProp(0),xa)));
		manager.backtrack(BASE);
		assertNull(manager.addFactEquality(nab, cClause(nab)));
	}
	
	public void testSeveralSolvedInstantiations() {
		assertNull(manager.addInstantiationEquality(xa, cClause(cProp(0),xa)));
		assertNull(manager.addInstantiationEquality(xa, cClause(cProp(1),xa)));
		FactResult result = manager.addFactEquality(nab, cClause(nab));
		assertNotNull(result);
		assertEquals(1, result.getSolvedInstantiations().size());
		IInstantiationResult result1 = result.getSolvedInstantiations().get(0);
		assertEquals(b, result1.getInstantiationValue());
		assertEquals(xa, result1.getEquality());
		assertEquals(mSet(cClause(cProp(0),xa),cClause(cProp(1),xa)), result1.getSolvedClauses());
		assertEquals(mSet(cClause(nab)), result1.getSolvedValueOrigin());
	}
	
	public void testSeveralSolvedInstantiationsWithBacktrack() {
		assertNull(manager.addInstantiationEquality(xa, cClause(ONE,cProp(0),xa)));
		assertNull(manager.addInstantiationEquality(xa, cClause(cProp(1),xa)));
		manager.backtrack(BASE);
		FactResult result = manager.addFactEquality(nab, cClause(nab));
		assertNotNull(result);
		assertEquals(1, result.getSolvedInstantiations().size());
		IInstantiationResult result1 = result.getSolvedInstantiations().get(0);
		assertEquals(b, result1.getInstantiationValue());
		assertEquals(xa, result1.getEquality());
		assertEquals(mSet(cClause(cProp(1),xa)), result1.getSolvedClauses());
		assertEquals(mSet(cClause(nab)), result1.getSolvedValueOrigin());
	}
	
	public void testSeveralSolvedInstantiationsWithBacktrackAfter() {
		assertNull(manager.addInstantiationEquality(xa, cClause(ONE,cProp(0),xa)));
		assertNull(manager.addInstantiationEquality(xa, cClause(cProp(1),xa)));
		manager.addFactEquality(bc, cClause(bc));
		FactResult result = manager.addFactEquality(nab, cClause(nab));
		assertNotNull(result);
		assertEquals(1, result.getSolvedInstantiations().size());
		IInstantiationResult result1 = result.getSolvedInstantiations().get(0);
		assertEquals(b, result1.getInstantiationValue());
		assertEquals(xa, result1.getEquality());
		assertEquals(mSet(cClause(ONE,cProp(0),xa),cClause(cProp(1),xa)), result1.getSolvedClauses());
		assertEquals(mSet(cClause(nab)), result1.getSolvedValueOrigin());
		
		manager.backtrack(BASE);
		assertNull(manager.addFactEquality(nac, cClause(nac)));
	}
	
	public void testSeveralSolvedInstantiationsWithBacktrackAfter2() {
		assertNull(manager.addInstantiationEquality(xa, cClause(ONE,cProp(0),xa)));
		assertNull(manager.addInstantiationEquality(xa, cClause(cProp(1),xa)));
		manager.addFactEquality(bc, cClause(ONE, bc));
		assertNotNull(manager.addFactEquality(nab, cClause(nab)));
		
		manager.backtrack(BASE);
		
		FactResult result = manager.addFactEquality(nac, cClause(nac));
		
		assertNotNull(result);
		assertEquals(1, result.getSolvedInstantiations().size());
		IInstantiationResult result1 = result.getSolvedInstantiations().get(0);
		assertEquals(c, result1.getInstantiationValue());
		assertEquals(xa, result1.getEquality());
		assertEquals(mSet(cClause(cProp(1),xa)), result1.getSolvedClauses());
	}
	
	public void testInstantiationOnEqualTree() {
		manager.addFactEquality(ab, cClause(ab));
		manager.addFactEquality(bc, cClause(bc));
		manager.addInstantiationEquality(xc, cClause(cProp(0),xc));
		
		FactResult result = manager.addFactEquality(ncd, cClause(ncd));
		assertNotNull(result);
		assertEquals(1, result.getSolvedInstantiations().size());
		IInstantiationResult result1 = result.getSolvedInstantiations().get(0);
		assertEquals(d, result1.getInstantiationValue());
		assertEquals(xc, result1.getEquality());
		assertEquals(mSet(cClause(cProp(0),xc)), result1.getSolvedClauses());
		assertEquals(mSet(cClause(ncd)), result1.getSolvedValueOrigin());
	}	
	
	public void testInstantiationOnEqualTree2() {
		manager.addFactEquality(ab, cClause(ab));
		manager.addFactEquality(bc, cClause(bc));
		manager.addInstantiationEquality(xc, cClause(cProp(0),xc));
		
		FactResult result = manager.addFactEquality(nbd, cClause(nbd));
		assertNotNull(result);
		assertEquals(1, result.getSolvedInstantiations().size());
		IInstantiationResult result1 = result.getSolvedInstantiations().get(0);
		assertEquals(d, result1.getInstantiationValue());
		assertEquals(xc, result1.getEquality());
		assertEquals(mSet(cClause(cProp(0),xc)), result1.getSolvedClauses());
		assertEquals(mSet(cClause(bc),cClause(nbd)), result1.getSolvedValueOrigin());
	}	
	
//	public void testRemoveQueryEqualityWithLevels() {
//		manager.addQueryEquality(ab, cClause(ONE,ab));
//		manager.addFactEquality(nab, cClause(THREE,nab));
//		
//	}
	
}
