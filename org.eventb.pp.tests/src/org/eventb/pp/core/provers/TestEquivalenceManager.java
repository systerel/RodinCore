package org.eventb.pp.core.provers;

import static org.eventb.pp.Util.cClause;
import static org.eventb.pp.Util.cProp;

import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.elements.IEquality;
import org.eventb.internal.pp.core.provers.equality.EquivalenceManager;
import org.eventb.internal.pp.core.provers.equality.unionfind.FactResult;
import org.eventb.pp.AbstractPPTest;
import org.eventb.pp.Util;

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
	
}
