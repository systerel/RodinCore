package org.eventb.pp.core.provers;

import java.util.List;

import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.provers.equality.IFactResult;
import org.eventb.internal.pp.core.provers.equality.unionfind.EqualitySolver;
import org.eventb.internal.pp.core.provers.equality.unionfind.FactResult;
import org.eventb.internal.pp.core.provers.equality.unionfind.Node;
import org.eventb.internal.pp.core.provers.equality.unionfind.QueryResult;
import org.eventb.internal.pp.core.provers.equality.unionfind.Source;
import org.eventb.internal.pp.core.provers.equality.unionfind.SourceTable;
import org.eventb.internal.pp.core.provers.equality.unionfind.Source.FactSource;
import org.eventb.internal.pp.core.provers.equality.unionfind.Source.QuerySource;
import org.eventb.pp.AbstractPPTest;

public class TestEqualitySolver extends AbstractPPTest {

	private Node a;
	private Node b;
	private Node c;
	private Node d;
	private Node e;
	private Node f;

	private static class MyQuerySource extends QuerySource {
		String name;
		
		MyQuerySource(String name) {
			super();
			
			this.name = name;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof MyQuerySource) {
				MyQuerySource tmp = (MyQuerySource) obj;
				return name.equals(tmp.name);
			}
			return false;
		}
		
		@Override
		public int hashCode() {
			return name.hashCode();
		}
		
		@Override
		public String toString() {
			return name;
		}

		@Override
		public void backtrack(Level level) {
			assert false;
		}

		@Override
		public boolean isValid() {
			return true;
		}
	}
	
	private static class MyFactSource extends FactSource {
		Level level;
		String name;
		
		MyFactSource(String name, Level level) {
			super();
			
			this.level = level;
			this.name = name;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof MyFactSource) {
				MyFactSource tmp = (MyFactSource) obj;
				return name.equals(tmp.name) && level.equals(tmp.level);
			}
			return false;
		}
		
		@Override
		public Level getLevel() {
			return level;
		}
		
		@Override
		public int hashCode() {
			return name.hashCode();
		}
		
		@Override
		public String toString() {
			return name+(level.equals(Level.base)?"":"/"+level);
		}

		@Override
		public void backtrack(Level level) {
			assert false;
		}

		@Override
		public boolean isValid() {
			assert false;
			return true;
		}
	}
	
	private static FactSource R1L0 = new MyFactSource("R1",Level.base);
	private static FactSource R2L0 = new MyFactSource("R2",Level.base);
	private static FactSource R3L0 = new MyFactSource("R3",Level.base);
	private static FactSource R4L0 = new MyFactSource("R4",Level.base);
	private static FactSource R5L0 = new MyFactSource("R5",Level.base);
	
	private static QuerySource R1L0q = new MyQuerySource("R1");
	private static QuerySource R2L0q = new MyQuerySource("R2");
	private static QuerySource R3L0q = new MyQuerySource("R3");
	private static QuerySource R4L0q = new MyQuerySource("R4");
	private static QuerySource R5L0q = new MyQuerySource("R5");
	private static QuerySource R6L0q = new MyQuerySource("R6");
	private static QuerySource R7L0q = new MyQuerySource("R7");
	
	private static FactSource R6L0 = new MyFactSource("R6",Level.base);
	private static FactSource R7L0 = new MyFactSource("R7",Level.base);
	private static FactSource R8L0 = new MyFactSource("R8",Level.base);
	private static FactSource R9L0 = new MyFactSource("R9",Level.base);
	
	private static FactSource R1L1 = new MyFactSource("R1",ONE);
	private static FactSource R2L1 = new MyFactSource("R2",ONE);
	private static FactSource R3L1 = new MyFactSource("R3",ONE);

	private static FactSource R1L3 = new MyFactSource("R1",THREE);
	private static FactSource R2L3 = new MyFactSource("R2",THREE);
	private static FactSource R3L3 = new MyFactSource("R3",THREE);

	private static FactSource R1L7 = new MyFactSource("R1",SEVEN);
	private static FactSource R2L7 = new MyFactSource("R2",SEVEN);
	private static FactSource R3L7 = new MyFactSource("R3",SEVEN);

	private static QuerySource ESq = new MyQuerySource("ES");
	private static FactSource ES = new MyFactSource("ES",Level.base);
	

	private EqualitySolver solver;
	
	@Override
	protected void setUp() throws Exception {
		// init solver
		init();
	}

	private void init() {
		solver = new EqualitySolver(new SourceTable());

		a = new Node("a");
		b = new Node("b");
		c = new Node("c");
		d = new Node("d");
		e = new Node("e");
		f = new Node("f");
	}

	// deductions
	public void testEmptyManager() {
		init();
		assertNull(solver.addFactEquality(a,b,ES));
		init();
		assertNull(solver.addFactEquality(a,b,ES));
		init();
		assertNull(solver.addQuery(a,b,ESq,true));
		init();
		assertNull(solver.addQuery(a,b,ESq,false));
	}

	
	public void testSimpleEquality() {
		solver.addFactEquality(a,b,R1L0);
		assertEquals(mSet(
				"b->a"
		),solver.dump());
		solver.addFactEquality(b,c,R2L0);
		assertEquals(mSet(
				"b->a",
				"c->a"
		),solver.dump());
		solver.addFactEquality(c,d,R3L0);
		assertEquals(mSet(
				"b->a",
				"c->a",
				"d->a"
		),solver.dump());
	}
	
	public void testMergingTree() {
		solver.addFactEquality(a,b,R1L0);
		solver.addFactEquality(b,c,R2L0);
		solver.addFactEquality(d,e,R3L0);
		solver.addFactEquality(e,f,R4L0);
		assertEquals(mSet(
				"b->a",
				"c->a",
				"e->d",
				"f->d"		
		),solver.dump());
		
		solver.addFactEquality(c,d,R5L0);
		assertEquals(mSet(
				"b->a",
				"c->a",
				"e->d",
				"f->d",
				"d->a"
		),solver.dump());
	}
	
	public void testAlreadyEqual() {
		solver.addFactEquality(a,b,R1L0);
		solver.addFactEquality(b,c,R2L0);
		solver.addFactEquality(a,c,R3L0);
		assertEquals(mSet(
				"b->a",
				"c->a"
		),solver.dump());
	}
	
	public void testOptimization1() {
		solver.addFactEquality(a,b,R1L0);
		solver.addFactEquality(c,d,R2L0);
		solver.addFactEquality(a,c,R3L0);
		assertEquals(mSet(
				"b->a",
				"d->c",
				"c->a"
		),solver.dump());
		solver.addFactEquality(b,d,R4L0);
		assertEquals(mSet(
				"b->a",
				"c->a",
				"d->a"
		),solver.dump());
	}
	
	public void testOptimization2() {
		solver.addFactEquality(a, b, R1L0);
		solver.addFactEquality(c, d, R2L0);
		solver.addFactEquality(a, c, R3L0);
		solver.addFactEquality(d, e, R4L0);
		assertEquals(mSet(
				"b->a",
				"d->a",
				"c->a",
				"e->a"
		),solver.dump());
	}
	
	public void testFactInequality() {
		solver.addFactInequality(a, b, ES);
		assertEquals(mSet(
				"a[F, ≠b]"
		),solver.dump());
	}

	public void testFactInequalityComplex() {
		solver.addFactEquality(a, b, ES);
		solver.addFactInequality(b, c, ES);
		assertEquals(mSet(
				"b->a",
				"a[F, ≠c]"
		),solver.dump());
	}

	public void testFactInequalityComplex2() {
		solver.addFactEquality(a, b, ES);
		solver.addFactInequality(c, d, ES);
		solver.addFactEquality(a, c, ES);
		assertEquals(mSet(
				"b->a",
				"c->a",
				"a[F, ≠d]"
		), solver.dump());
	}
	
	public void testFactInequalityComplex3() {
		solver.addFactInequality(b, c, ES);		
		solver.addFactEquality(a, c, ES);
		assertEquals(mSet(
				"c->a",
				"b[F, ≠a]"
		), solver.dump());
	}
	
	public void testSimpleContradictionEqualityFirst() {
		solver.addFactEquality(a, b, ES);
		IFactResult result = solver.addFactInequality(a, b, ES);
		assertNotNull(result);
		assertNull(result.getSolvedQueries());
	}
	
	public void testSimpleContradictionInequalityFirst() {
		solver.addFactInequality(a, b, ES);
		IFactResult result = solver.addFactEquality(a, b, ES);
		assertNotNull(result);
		assertNull(result.getSolvedQueries());
	}
	
	public void testContradiction1() {
		assertNull(solver.addFactEquality(a, b, R1L0));
		assertNull(solver.addFactEquality(c, d, R2L0));
		assertNull(solver.addFactInequality(b, d, R3L0));
		FactResult result = solver.addFactEquality(b, d, R4L0);
		assertEquals(result.getContradictionSource(),mSet(R3L0,R4L0));
		assertNull(result.getSolvedQueries());
	}
	
	public void testContradiction2() {
		assertNull(solver.addFactEquality(a, b, R1L0));
		assertNull(solver.addFactEquality(c, d, R2L0));
		assertNull(solver.addFactInequality(d, e, R3L0));
		assertNull(solver.addFactEquality(b, e, R4L0));
		FactResult result = solver.addFactEquality(b, d, R5L0);
		assertEquals(result.getContradictionSource(),mSet(R5L0,R3L0,R4L0));
		assertNull(result.getSolvedQueries());
	}
	
	public void testSimpleQueryContradiction1() {
		assertNull(solver.addQuery(a, b, ESq, true));
		IFactResult result = solver.addFactEquality(a, b, ES);
		assertTrue(result.getSolvedQueries().size()==1);
		assertTrue(result.getSolvedQueries().get(0).getValue());
	}
	
	public void testSimpleQueryContradiction2() {
		assertNull(solver.addQuery(a, b, ESq, false));
		IFactResult result = solver.addFactEquality(a, b, ES);
		assertTrue(result.getSolvedQueries().size()==1);
		assertFalse(result.getSolvedQueries().get(0).getValue());
	}
	
	public void testSimpleQueryContradiction3() {
		assertNull(solver.addQuery(a, b, ESq, true));
		IFactResult result = solver.addFactInequality(a, b, ES);
		assertTrue(result.getSolvedQueries().size()==1);
		assertFalse(result.getSolvedQueries().get(0).getValue());
	}
	
	// TODO check this
	public void testSimpleQueryContradiction4() {
		assertNull(solver.addQuery(a, b, ESq, false));
		IFactResult result = solver.addFactInequality(a, b, ES);
		assertTrue(result.getSolvedQueries().size()==1);
		assertTrue(result.getSolvedQueries().get(0).getValue());
	}
	
	// TODO check this
	public void testSimpleQueryContradiction5() {
		solver.addFactInequality(a, b, ES);
		QueryResult result = solver.addQuery(a, b, ESq, true);
		assertNotNull(result);
		assertFalse(result.getValue());
	}
	
	// sources without levels
	public void testSourceTableSimple() {
		solver.addFactEquality(a, b, R1L0);
		solver.addFactEquality(a, c, R2L0);
		assertEquals(mSet(
				"a,b[R1]",
				"a,c[R2]")
		,solver.getSourceTable().dump());
	}
	
	public void testSourceTableWithEdge1() {
		solver.addFactEquality(a, b, R1L0);
		solver.addFactEquality(b, c, R2L0);
		assertEquals(mSet(
				"a,b[R1]",
				"a,c[R1, R2]",
				"b,c[R2]"
		),solver.getSourceTable().dump());
	}
	
	public void testSourceTableWithEdge2() {
		solver.addFactEquality(a, b, R1L0);
		solver.addFactEquality(c, d, R2L0);
		solver.addFactEquality(b, d, R3L0);
		assertEquals(mSet(
				"a,b[R1]",
				"c,d[R2]",
				"b,d[R3]",
				"a,c[R1, R2, R3]"
		),solver.getSourceTable().dump());
	}
	
	public void testSourceTableWithEdgeRedundant() {
		solver.addFactEquality(a, b, R1L0);
		solver.addFactEquality(b, c, R2L0);
		solver.addFactEquality(a, c, R3L0);
		assertEquals(mSet(
				"a,b[R1]",
				"a,c[R3]",
				"b,c[R2]"
		),solver.getSourceTable().dump());
	}
	
	public void testSourceTableOptimisation() {
		solver.addFactEquality(a, b, R1L0);
		solver.addFactEquality(c, d, R2L0);
		solver.addFactEquality(a, c, R3L0);
		solver.addFactEquality(b, d, R4L0);
		assertEquals(mSet(
				"a,b[R1]",
				"c,d[R2]",
				"a,c[R3]",
				"b,d[R4]",
				"a,d[R2, R3]"
		),solver.getSourceTable().dump());
	}

	
	public void testSimpleSource1() {
		solver.addFactEquality(a, b, R1L0);
		FactResult result = solver.addFactInequality(a, b, R2L0);
		assertEquals(result.getContradictionSource(), mSet(R2L0,R1L0));
	}
	
	public void testSimpleSource2() {
		solver.addFactInequality(a, b, R1L0);
		FactResult result = solver.addFactEquality(a, b, R2L0);
		assertEquals(result.getContradictionSource(), mSet(R2L0,R1L0));
	}
	
	
	public void testSource1() {
		solver.addFactEquality(a, b, R1L0);
		solver.addFactEquality(c, d, R2L0);
		solver.addFactInequality(b, d, R3L0);
		FactResult result = solver.addFactEquality(a, c, R4L0);
		assertEquals(result.getContradictionSource(), mSet(R4L0,R3L0,R2L0,R1L0));
	}
	
	public void testSource2() {
		solver.addFactEquality(a, b, R1L0);
		solver.addFactEquality(b, c, R2L0);
		FactResult result = solver.addFactInequality(a, c, R3L0);
		assertEquals(result.getContradictionSource(), mSet(R1L0,R2L0,R3L0));
	}
	
	public void testSimpleSourceQuery1() {
		solver.addFactEquality(a, b, R1L0);
		QueryResult result = solver.addQuery(a, b, R2L0q, true);
		assertEquals(result.getSolvedValueSource(), mSet(R1L0));
		assertEquals(result.getQuerySource(), R2L0q);
		assertTrue(result.getValue());
	}
	
	public void testSimpleSourceQuery2() {
		solver.addFactInequality(a, b, R1L0);
		QueryResult result = solver.addQuery(a, b, R2L0q, true);
		assertEquals(result.getSolvedValueSource(), mSet(R1L0));
		assertEquals(result.getQuerySource(), R2L0q);
		assertFalse(result.getValue());
	}
	
	public void testSimpleSourceQuery3() {
		solver.addFactEquality(a, b, R1L0);
		QueryResult result = solver.addQuery(a, b, R2L0q, false);
		assertEquals(result.getSolvedValueSource(), mSet(R1L0));
		assertEquals(result.getQuerySource(), R2L0q);
		assertFalse(result.getValue());
	}
	
	public void testSimpleSourceQuery4() {
		solver.addFactInequality(a, b, R1L0);
		QueryResult result = solver.addQuery(a, b, R2L0q, false);
		assertEquals(result.getSolvedValueSource(), mSet(R1L0));
		assertEquals(result.getQuerySource(), R2L0q);
		assertTrue(result.getValue());
	}
	
	public void testComplexSourceQuery1() {
		assertNull(solver.addQuery(c, f, R6L0q, true));
		
		assertNull(solver.addFactEquality(a,b,R1L0));
		assertNull(solver.addFactEquality(b,c,R2L0));
		assertNull(solver.addFactEquality(d,e,R3L0));
		assertNull(solver.addFactEquality(e,f,R4L0));
		FactResult result = solver.addFactEquality(a,d,R5L0);
		assertNotNull(result);
		List<QueryResult> queries = result.getSolvedQueries();
		assertTrue(queries.size() == 1);
		assertTrue(queries.get(0).getValue());
		
		assertEquals(mSet(R1L0,R2L0,R3L0,R4L0,R5L0), queries.get(0).getSolvedValueSource());
		assertEquals(R6L0q, queries.get(0).getQuerySource());
	}
	
	public void testComplexSourceQuery12() {
		assertNull(solver.addQuery(c, f, R6L0q, false));
		
		assertNull(solver.addFactEquality(a,b,R1L0));
		assertNull(solver.addFactEquality(b,c,R2L0));
		assertNull(solver.addFactEquality(d,e,R3L0));
		assertNull(solver.addFactEquality(e,f,R4L0));
		FactResult result = solver.addFactEquality(a,d,R5L0);
		assertNotNull(result);
		List<QueryResult> queries = result.getSolvedQueries();
		assertTrue(queries.size() == 1);
		assertFalse(queries.get(0).getValue());
			
		assertEquals(mSet(R1L0,R2L0,R3L0,R4L0,R5L0), queries.get(0).getSolvedValueSource());
		assertEquals(R6L0q, queries.get(0).getQuerySource());
	}
	
	public void testComplexSourceQuery2() {
		assertNull(solver.addFactEquality(a,b,R1L0));
		assertNull(solver.addFactEquality(b,c,R2L0));
		assertNull(solver.addFactEquality(d,e,R3L0));
		assertNull(solver.addFactEquality(e,f,R4L0));
		assertNull(solver.addFactInequality(a, d, R5L0));
		QueryResult result = solver.addQuery(c, f, R6L0q, true);
		assertNotNull(result);
		assertFalse(result.getValue());
		
		assertEquals(mSet(R1L0,R2L0,R3L0,R4L0,R5L0), result.getSolvedValueSource());
		assertEquals(R6L0q, result.getQuerySource());
	}
	
	public void testComplexSourceQuery22() {
		assertNull(solver.addFactEquality(a,b,R1L0));
		assertNull(solver.addFactEquality(b,c,R2L0));
		assertNull(solver.addFactEquality(d,e,R3L0));
		assertNull(solver.addFactEquality(e,f,R4L0));
		assertNull(solver.addFactInequality(a, d, R5L0));
		QueryResult result = solver.addQuery(c, f, R6L0q, false);
		assertNotNull(result);
		assertTrue(result.getValue());
		
		assertEquals(mSet(R1L0,R2L0,R3L0,R4L0,R5L0), result.getSolvedValueSource());
		assertEquals(R6L0q, result.getQuerySource());
	}
	
	public void testComplexSourceQuery3() {
		assertNull(solver.addQuery(a, d, R6L0q, true));
		assertNull(solver.addQuery(c, f, R7L0q, true));
		
		assertNull(solver.addFactEquality(a,b,R1L0));
		assertNull(solver.addFactEquality(b,c,R2L0));
		assertNull(solver.addFactEquality(d,e,R3L0));
		assertNull(solver.addFactEquality(e,f,R4L0));
		FactResult result = solver.addFactInequality(a, d, R5L0);
		assertNotNull(result);
		List<QueryResult> queries = result.getSolvedQueries();
		assertTrue(queries.size() == 2);
		
		assertEquals(mSet(R5L0), queries.get(0).getSolvedValueSource());
		assertEquals(R6L0q, queries.get(0).getQuerySource());
		assertFalse(queries.get(0).getValue());
		assertEquals(mSet(R1L0,R2L0,R3L0,R4L0,R5L0), queries.get(1).getSolvedValueSource());
		assertEquals(R7L0q, queries.get(1).getQuerySource());
		assertFalse(queries.get(1).getValue());
	}
	
	public void testComplexSourceQuery32() {
		assertNull(solver.addQuery(a, d, R6L0q, false));
		assertNull(solver.addQuery(c, f, R7L0q, false));
		
		assertNull(solver.addFactEquality(a,b,R1L0));
		assertNull(solver.addFactEquality(b,c,R2L0));
		assertNull(solver.addFactEquality(d,e,R3L0));
		assertNull(solver.addFactEquality(e,f,R4L0));
		FactResult result = solver.addFactInequality(a, d, R5L0);
		assertNotNull(result);
		List<QueryResult> queries = result.getSolvedQueries();
		assertTrue(queries.size() == 2);
		
		assertEquals(mSet(R5L0), queries.get(0).getSolvedValueSource());
		assertEquals(R6L0q, queries.get(0).getQuerySource());
		assertTrue(queries.get(0).getValue());
		assertEquals(mSet(R1L0,R2L0,R3L0,R4L0,R5L0), queries.get(1).getSolvedValueSource());
		assertEquals(R7L0q, queries.get(1).getQuerySource());
		assertTrue(queries.get(1).getValue());
	}
	
	public void testComplexSourceQuery4() {
		assertNull(solver.addQuery(a, d, R6L0q, true));
		assertNull(solver.addQuery(c, f, R7L0q, true));
		
		assertNull(solver.addFactEquality(a,b,R1L0));
		assertNull(solver.addFactEquality(b,c,R2L0));
		assertNull(solver.addFactEquality(d,e,R3L0));
		assertNull(solver.addFactEquality(e,f,R4L0));
		FactResult result = solver.addFactEquality(a, d, R5L0);
		assertNotNull(result);
		List<QueryResult> queries = result.getSolvedQueries();
		assertTrue(queries.size() == 2);
		
		assertEquals(mSet(R5L0), queries.get(0).getSolvedValueSource());
		assertEquals(R6L0q, queries.get(0).getQuerySource());
		assertTrue(queries.get(0).getValue());
		assertEquals(mSet(R1L0,R2L0,R3L0,R4L0,R5L0), queries.get(1).getSolvedValueSource());
		assertEquals(R7L0q, queries.get(1).getQuerySource());
		assertTrue(queries.get(1).getValue());
	}
	
	public void testComplexSourceQuery42() {
		assertNull(solver.addQuery(a, d, R6L0q, false));
		assertNull(solver.addQuery(c, f, R7L0q, false));
		
		assertNull(solver.addFactEquality(a,b,R1L0));
		assertNull(solver.addFactEquality(b,c,R2L0));
		assertNull(solver.addFactEquality(d,e,R3L0));
		assertNull(solver.addFactEquality(e,f,R4L0));
		FactResult result = solver.addFactEquality(a, d, R5L0);
		assertNotNull(result);
		List<QueryResult> queries = result.getSolvedQueries();
		assertTrue(queries.size() == 2);
		
		assertEquals(mSet(R5L0), queries.get(0).getSolvedValueSource());
		assertEquals(R6L0q, queries.get(0).getQuerySource());
		assertFalse(queries.get(0).getValue());
		assertEquals(mSet(R1L0,R2L0,R3L0,R4L0,R5L0), queries.get(1).getSolvedValueSource());
		assertEquals(R7L0q, queries.get(1).getQuerySource());
		assertFalse(queries.get(1).getValue());
	}
	
	public void testSimpleQuerySuppressed1() {
		assertNull(solver.addQuery(a, d, R3L0q, true));
		assertNull(solver.addQuery(a, d, R5L0q, false));
		
		assertNull(solver.addFactEquality(a, b, R1L0));
		assertNotNull(solver.addFactInequality(a, d, R4L0));
		// query should not be there after being solved
		assertNull(solver.addFactInequality(b, d, R2L0));
	}
	
	// origin with different levels
//	 sources without levels
	public void testSourceTableSimpleWithLevels() {
		solver.addFactEquality(a, b, R1L1);
		solver.addFactEquality(a, c, R2L1);
		assertEquals(mSet(
				"a,b[R1/1]",
				"a,c[R2/1]")
		,solver.getSourceTable().dump());
	}

	public void testSourceTableWithEdgeRedundantWithLevel1() {
		solver.addFactEquality(a, b, R1L0);
		solver.addFactEquality(b, c, R2L0);
		solver.addFactEquality(a, c, R3L1);
		assertEquals(mSet(
				"a,b[R1]",
				"a,c[R1, R2]",
				"b,c[R2]"
		),solver.getSourceTable().dump());
	}
	
	public void testSourceTableWithEdgeRedundantWithLevel2() {
		solver.addFactEquality(a, b, R1L1);
		solver.addFactEquality(b, c, R2L0);
		solver.addFactEquality(a, c, R3L1);
		assertEquals(mSet(
				"a,b[R1/1]",
				"a,c[R3/1]",
				"b,c[R2]"
		),solver.getSourceTable().dump());
	}
	
	public void testSourceTableOptimisationWithoutLevels() {
		solver.addFactEquality(a, b, R1L0);
		solver.addFactEquality(c, d, R2L0);
		solver.addFactEquality(a, c, R3L0);
		solver.addFactEquality(d, e, R4L0);
		assertEquals(mSet(
				"b->a",
				"d->a",
				"c->a",
				"e->a"
		),solver.dump());
		assertEquals(mSet(
				"a,b[R1]",
				"c,d[R2]",
				"a,c[R3]",
				"d,e[R4]",
				"a,d[R2, R3]",
				"a,e[R4, R2, R3]"
		),solver.getSourceTable().dump());
	}
	
	public void testSourceTableOptimisationWithLevels() {
		solver.addFactEquality(a, b, R1L0);
		solver.addFactEquality(c, d, R2L1);
		solver.addFactEquality(a, c, R3L1);
		solver.addFactEquality(d, e, R4L0);
		solver.addFactEquality(b, c, R5L0);
		assertEquals(mSet(
				"b->a",
				"d->a",
				"c->a",
				"e->a"
		),solver.dump());
		assertEquals(mSet(
				"a,b[R1]",
				"c,d[R2]",
				"a,c[R3]",
				"d,e[R4]",
				"a,d[R2, R3]",
				"a,e[R4, R2, R3]"
		),solver.getSourceTable().dump());
	}
	
	
	public void testRedundantQuery() {
		solver.addQuery(a, b, R1L0q, true);
		solver.addQuery(a, b, R2L0q, true);
		
		FactResult result = solver.addFactEquality(a, b, R3L0);
		assertNotNull(result);
		assertFalse(result.hasContradiction());
		assertEquals(result.getSolvedQueries().size(),2);
	}
	
	// backtracking
	
	
}
