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
package org.eventb.pp.core.provers.equality;

import static org.eventb.internal.pp.core.elements.terms.Util.mSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.elements.terms.AbstractPPTest;
import org.eventb.internal.pp.core.provers.equality.IFactResult;
import org.eventb.internal.pp.core.provers.equality.unionfind.Equality;
import org.eventb.internal.pp.core.provers.equality.unionfind.EqualitySolver;
import org.eventb.internal.pp.core.provers.equality.unionfind.FactResult;
import org.eventb.internal.pp.core.provers.equality.unionfind.Instantiation;
import org.eventb.internal.pp.core.provers.equality.unionfind.InstantiationResult;
import org.eventb.internal.pp.core.provers.equality.unionfind.Node;
import org.eventb.internal.pp.core.provers.equality.unionfind.QueryResult;
import org.eventb.internal.pp.core.provers.equality.unionfind.Source;
import org.eventb.internal.pp.core.provers.equality.unionfind.Source.FactSource;
import org.eventb.internal.pp.core.provers.equality.unionfind.Source.QuerySource;
import org.eventb.internal.pp.core.provers.equality.unionfind.SourceTable;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

@SuppressWarnings("unused")
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
			return name+(level.equals(Level.BASE)?"":"/"+level);
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
	
	private static FactSource R1L0 = new MyFactSource("R1", BASE);
	private static FactSource R2L0 = new MyFactSource("R2", BASE);
	private static FactSource R3L0 = new MyFactSource("R3", BASE);
	private static FactSource R4L0 = new MyFactSource("R4", BASE);
	private static FactSource R5L0 = new MyFactSource("R5", BASE);
	
	private static QuerySource R1L0q = new MyQuerySource("R1");
	private static QuerySource R2L0q = new MyQuerySource("R2");
	private static QuerySource R3L0q = new MyQuerySource("R3");
	private static QuerySource R4L0q = new MyQuerySource("R4");
	private static QuerySource R5L0q = new MyQuerySource("R5");
	private static QuerySource R6L0q = new MyQuerySource("R6");
	private static QuerySource R7L0q = new MyQuerySource("R7");
	
	private static FactSource R6L0 = new MyFactSource("R6", BASE);
	private static FactSource R7L0 = new MyFactSource("R7", BASE);
	private static FactSource R8L0 = new MyFactSource("R8", BASE);
	private static FactSource R9L0 = new MyFactSource("R9", BASE);
	
	private static FactSource R1L1 = new MyFactSource("R1", ONE);
	private static FactSource R2L1 = new MyFactSource("R2", ONE);
	private static FactSource R3L1 = new MyFactSource("R3", ONE);
	private static FactSource R4L1 = new MyFactSource("R4", ONE);

	private static FactSource R1L3 = new MyFactSource("R1", THREE);
	private static FactSource R2L3 = new MyFactSource("R2", THREE);
	private static FactSource R3L3 = new MyFactSource("R3", THREE);

	private static FactSource R1L7 = new MyFactSource("R1", SEVEN);
	private static FactSource R2L7 = new MyFactSource("R2", SEVEN);
	private static FactSource R3L7 = new MyFactSource("R3", SEVEN);

	private static QuerySource ESq = new MyQuerySource("ES");
	private static FactSource ES = new MyFactSource("ES", BASE);
	

	private EqualitySolver solver;
	
	private static <T extends Source> Equality<T> E(Node node1, Node node2, T source) {
		return new Equality<T>(node1, node2, source);
	}
	
	private static Instantiation I(Node node, QuerySource source) {
		return new Instantiation(node, source);
	}
	
	
    @Before
	public void setUp() throws Exception {
		// init solver
		init();
	}

	private void init() {
		solver = new EqualitySolver(new SourceTable());

		a = new Node(AbstractPPTest.a);
		b = new Node(AbstractPPTest.b);
		c = new Node(AbstractPPTest.c);
		d = new Node(AbstractPPTest.d);
		e = new Node(AbstractPPTest.e);
		f = new Node(AbstractPPTest.f);
	}

	// deductions
    @Test
	public void testEmptyManager() {
		init();
		assertNull(solver.addFactEquality(E(a,b,ES)));
		init();
		assertNull(solver.addFactEquality(E(a,b,ES)));
		init();
		assertNull(solver.addQuery(E(a,b,ESq),true));
		init();
		assertNull(solver.addQuery(E(a,b,ESq),false));
	}

	
    @Test
	public void testSimpleEquality() {
		solver.addFactEquality(E(a,b,R1L0));
		assertEquals(mSet(
				"b->a"
		),solver.dump());
		solver.addFactEquality(E(b,c,R2L0));
		assertEquals(mSet(
				"b->a",
				"c->a"
		),solver.dump());
		solver.addFactEquality(E(c,d,R3L0));
		assertEquals(mSet(
				"b->a",
				"c->a",
				"d->a"
		),solver.dump());
	}
	
    @Test
	public void testMergingTree() {
		solver.addFactEquality(E(a,b,R1L0));
		solver.addFactEquality(E(b,c,R2L0));
		solver.addFactEquality(E(d,e,R3L0));
		solver.addFactEquality(E(e,f,R4L0));
		assertEquals(mSet(
				"b->a",
				"c->a",
				"e->d",
				"f->d"		
		),solver.dump());
		
		solver.addFactEquality(E(c,d,R5L0));
		assertEquals(mSet(
				"b->a",
				"c->a",
				"e->d",
				"f->d",
				"d->a"
		),solver.dump());
	}
	
    @Test
	public void testAlreadyEqual() {
		solver.addFactEquality(E(a,b,R1L0));
		solver.addFactEquality(E(b,c,R2L0));
		solver.addFactEquality(E(a,c,R3L0));
		assertEquals(mSet(
				"b->a",
				"c->a"
		),solver.dump());
	}
	
    @Test
	public void testOptimization1() {
		solver.addFactEquality(E(a,b,R1L0));
		solver.addFactEquality(E(c,d,R2L0));
		solver.addFactEquality(E(a,c,R3L0));
		assertEquals(mSet(
				"b->a",
				"d->c",
				"c->a"
		),solver.dump());
		solver.addFactEquality(E(b,d,R4L0));
		assertEquals(mSet(
				"b->a",
				"c->a",
				"d->a"
		),solver.dump());
	}
	
    @Test
	public void testOptimization2() {
		solver.addFactEquality(E(a, b, R1L0));
		solver.addFactEquality(E(c, d, R2L0));
		solver.addFactEquality(E(a, c, R3L0));
		solver.addFactEquality(E(d, e, R4L0));
		assertEquals(mSet(
				"b->a",
				"d->a",
				"c->a",
				"e->a"
		),solver.dump());
	}
	
    @Test
	public void testFactInequality() {
		solver.addFactInequality(E(a, b, ES));
		assertEquals(mSet(
				"a[F, ≠b]",
				"b[F, ≠a]"
		),solver.dump());
	}

    @Test
	public void testFactInequalityComplex() {
		solver.addFactEquality(E(a, b, ES));
		solver.addFactInequality(E(b, c, ES));
		assertEquals(mSet(
				"b->a",
				"a[F, ≠c]",
				"c[F, ≠a]"
		),solver.dump());
	}

    @Test
	public void testFactInequalityComplex2() {
		solver.addFactEquality(E(a, b, ES));
		solver.addFactInequality(E(c, d, ES));
		solver.addFactEquality(E(a, c, ES));
		assertEquals(mSet(
				"b->a",
				"c->a",
				"a[F, ≠d]",
				"d[F, ≠a]"
		), solver.dump());
	}
	
    @Test
	public void testFactInequalityComplex3() {
		solver.addFactInequality(E(b, c, ES));		
		solver.addFactEquality(E(a, c, ES));
		assertEquals(mSet(
				"c->a",
				"b[F, ≠a]",
				"a[F, ≠b]"
		), solver.dump());
	}
	
    @Test
	public void testSimpleContradictionEqualityFirst() {
		solver.addFactEquality(E(a, b, ES));
		IFactResult result = solver.addFactInequality(E(a, b, ES));
		assertNotNull(result);
		assertNull(result.getSolvedQueries());
	}
	
    @Test
	public void testSimpleContradictionInequalityFirst() {
		solver.addFactInequality(E(a, b, ES));
		IFactResult result = solver.addFactEquality(E(a, b, ES));
		assertNotNull(result);
		assertNull(result.getSolvedQueries());
	}
	
    @Test
	public void testContradiction1() {
		assertNull(solver.addFactEquality(E(a, b, R1L0)));
		assertNull(solver.addFactEquality(E(c, d, R2L0)));
		assertNull(solver.addFactInequality(E(b, d, R3L0)));
		FactResult result = solver.addFactEquality(E(b, d, R4L0));
		assertEquals(result.getContradictionSource(),mSet(R3L0,R4L0));
		assertNull(result.getSolvedQueries());
	}
	
    @Test
	public void testContradiction2() {
		assertNull(solver.addFactEquality(E(a, b, R1L0)));
		assertNull(solver.addFactEquality(E(c, d, R2L0)));
		assertNull(solver.addFactInequality(E(d, e, R3L0)));
		assertNull(solver.addFactEquality(E(b, e, R4L0)));
		FactResult result = solver.addFactEquality(E(b, d, R5L0));
		assertEquals(result.getContradictionSource(),mSet(R5L0,R3L0,R4L0));
		assertNull(result.getSolvedQueries());
	}
	
    @Test
	public void testRedundantInequality() {
		assertNull(solver.addFactEquality(E(a, b, R1L0)));
		assertNull(solver.addFactEquality(E(a, c, R2L1)));
		assertNull(solver.addFactInequality(E(b, d, R3L0)));
		assertNull(solver.addFactInequality(E(c, e, R4L1)));
		assertNull(solver.addFactEquality(E(d, e, R5L0)));
		FactResult result = solver.addFactEquality(E(a, d, R6L0));
		
		assertNotNull(result);
		assertEquals(Level.BASE, result.getContradictionLevel());
	}
	
    @Test
	public void testSimpleQueryContradiction1() {
		assertNull(solver.addQuery(E(a, b, ESq), true));
		IFactResult result = solver.addFactEquality(E(a, b, ES));
		assertTrue(result.getSolvedQueries().size()==1);
		assertTrue(result.getSolvedQueries().get(0).getValue());
	}
	
    @Test
	public void testSimpleQueryContradiction2() {
		assertNull(solver.addQuery(E(a, b, ESq), false));
		IFactResult result = solver.addFactEquality(E(a, b, ES));
		assertTrue(result.getSolvedQueries().size()==1);
		assertFalse(result.getSolvedQueries().get(0).getValue());
	}
	
    @Test
	public void testSimpleQueryContradiction3() {
		assertNull(solver.addQuery(E(a, b, ESq), true));
		IFactResult result = solver.addFactInequality(E(a, b, ES));
		assertTrue(result.getSolvedQueries().size()==1);
		assertFalse(result.getSolvedQueries().get(0).getValue());
	}
	
	// TODO check this
    @Test
	public void testSimpleQueryContradiction4() {
		assertNull(solver.addQuery(E(a, b, ESq), false));
		IFactResult result = solver.addFactInequality(E(a, b, ES));
		assertTrue(result.getSolvedQueries().size()==1);
		assertTrue(result.getSolvedQueries().get(0).getValue());
	}
	
	// TODO check this
    @Test
	public void testSimpleQueryContradiction5() {
		solver.addFactInequality(E(a, b, ES));
		QueryResult result = solver.addQuery(E(a, b, ESq), true);
		assertNotNull(result);
		assertFalse(result.getValue());
	}
	
	// sources without levels
    @Test
	public void testSourceTableSimple() {
		solver.addFactEquality(E(a, b, R1L0));
		solver.addFactEquality(E(a, c, R2L0));
		assertEquals(mSet(
				"a,b[R1]",
				"a,c[R2]")
		,solver.getSourceTable().dump());
	}
	
    @Test
	public void testSourceTableWithEdge1() {
		solver.addFactEquality(E(a, b, R1L0));
		solver.addFactEquality(E(b, c, R2L0));
		assertEquals(mSet(
				"a,b[R1]",
				"a,c[R2, R1]",
				"b,c[R2]"
		),solver.getSourceTable().dump());
	}
	
    @Test
	public void testSourceTableWithEdge2() {
		solver.addFactEquality(E(a, b, R1L0));
		solver.addFactEquality(E(c, d, R2L0));
		solver.addFactEquality(E(b, d, R3L0));
		assertEquals(mSet(
				"a,b[R1]",
				"c,d[R2]",
				"b,d[R3]",
				"a,c[R2, R3, R1]"
		),solver.getSourceTable().dump());
	}
	
    @Test
	public void testSourceTableWithEdgeRedundant() {
		solver.addFactEquality(E(a, b, R1L0));
		solver.addFactEquality(E(b, c, R2L0));
		solver.addFactEquality(E(a, c, R3L0));
		assertEquals(mSet(
				"a,b[R1]",
				"a,c[R3]",
				"b,c[R2]"
		),solver.getSourceTable().dump());
	}
	
    @Test
	public void testSourceTableOptimisation() {
		solver.addFactEquality(E(a, b, R1L0));
		solver.addFactEquality(E(c, d, R2L0));
		solver.addFactEquality(E(a, c, R3L0));
		solver.addFactEquality(E(b, d, R4L0));
		assertEquals(mSet(
				"a,b[R1]",
				"c,d[R2]",
				"a,c[R3]",
				"b,d[R4]",
				"a,d[R2, R3]"
		),solver.getSourceTable().dump());
	}

	
    @Test
	public void testSimpleSource1() {
		solver.addFactEquality(E(a, b, R1L0));
		FactResult result = solver.addFactInequality(E(a, b, R2L0));
		assertEquals(result.getContradictionSource(), mSet(R2L0,R1L0));
	}
	
    @Test
	public void testSimpleSource2() {
		solver.addFactInequality(E(a, b, R1L0));
		FactResult result = solver.addFactEquality(E(a, b, R2L0));
		assertEquals(result.getContradictionSource(), mSet(R2L0,R1L0));
	}
	
	
    @Test
	public void testSource1() {
		solver.addFactEquality(E(a, b, R1L0));
		solver.addFactEquality(E(c, d, R2L0));
		solver.addFactInequality(E(b, d, R3L0));
		FactResult result = solver.addFactEquality(E(a, c, R4L0));
		assertEquals(result.getContradictionSource(), mSet(R4L0,R3L0,R2L0,R1L0));
	}
	
    @Test
	public void testSource2() {
		solver.addFactEquality(E(a, b, R1L0));
		solver.addFactEquality(E(b, c, R2L0));
		FactResult result = solver.addFactInequality(E(a, c, R3L0));
		assertEquals(result.getContradictionSource(), mSet(R1L0,R2L0,R3L0));
	}
	
    @Test
	public void testSimpleSourceQuery1() {
		solver.addFactEquality(E(a, b, R1L0));
		QueryResult result = solver.addQuery(E(a, b, R2L0q), true);
		assertEquals(result.getSolvedValueSource(), mSet(R1L0));
		assertEquals(result.getQuerySource(), R2L0q);
		assertTrue(result.getValue());
	}
	
    @Test
	public void testSimpleSourceQuery2() {
		solver.addFactInequality(E(a, b, R1L0));
		QueryResult result = solver.addQuery(E(a, b, R2L0q), true);
		assertEquals(result.getSolvedValueSource(), mSet(R1L0));
		assertEquals(result.getQuerySource(), R2L0q);
		assertFalse(result.getValue());
	}
	
    @Test
	public void testSimpleSourceQuery3() {
		solver.addFactEquality(E(a, b, R1L0));
		QueryResult result = solver.addQuery(E(a, b, R2L0q), false);
		assertEquals(result.getSolvedValueSource(), mSet(R1L0));
		assertEquals(result.getQuerySource(), R2L0q);
		assertFalse(result.getValue());
	}
	
    @Test
	public void testSimpleSourceQuery4() {
		solver.addFactInequality(E(a, b, R1L0));
		QueryResult result = solver.addQuery(E(a, b, R2L0q), false);
		assertEquals(result.getSolvedValueSource(), mSet(R1L0));
		assertEquals(result.getQuerySource(), R2L0q);
		assertTrue(result.getValue());
	}
	
    @Test
	public void testComplexSourceQuery1() {
		assertNull(solver.addQuery(E(c, f, R6L0q), true));
		
		assertNull(solver.addFactEquality(E(a,b,R1L0)));
		assertNull(solver.addFactEquality(E(b,c,R2L0)));
		assertNull(solver.addFactEquality(E(d,e,R3L0)));
		assertNull(solver.addFactEquality(E(e,f,R4L0)));
		FactResult result = solver.addFactEquality(E(a,d,R5L0));
		assertNotNull(result);
		List<QueryResult> queries = result.getSolvedQueries();
		assertTrue(queries.size() == 1);
		assertTrue(queries.get(0).getValue());
		
		assertEquals(mSet(R1L0,R2L0,R3L0,R4L0,R5L0), queries.get(0).getSolvedValueSource());
		assertEquals(R6L0q, queries.get(0).getQuerySource());
	}
	
    @Test
	public void testComplexSourceQuery12() {
		assertNull(solver.addQuery(E(c, f, R6L0q), false));
		
		assertNull(solver.addFactEquality(E(a,b,R1L0)));
		assertNull(solver.addFactEquality(E(b,c,R2L0)));
		assertNull(solver.addFactEquality(E(d,e,R3L0)));
		assertNull(solver.addFactEquality(E(e,f,R4L0)));
		FactResult result = solver.addFactEquality(E(a,d,R5L0));
		assertNotNull(result);
		List<QueryResult> queries = result.getSolvedQueries();
		assertTrue(queries.size() == 1);
		assertFalse(queries.get(0).getValue());
			
		assertEquals(mSet(R1L0,R2L0,R3L0,R4L0,R5L0), queries.get(0).getSolvedValueSource());
		assertEquals(R6L0q, queries.get(0).getQuerySource());
	}
	
    @Test
	public void testComplexSourceQuery2() {
		assertNull(solver.addFactEquality(E(a,b,R1L0)));
		assertNull(solver.addFactEquality(E(b,c,R2L0)));
		assertNull(solver.addFactEquality(E(d,e,R3L0)));
		assertNull(solver.addFactEquality(E(e,f,R4L0)));
		assertNull(solver.addFactInequality(E(a, d, R5L0)));
		QueryResult result = solver.addQuery(E(c, f, R6L0q), true);
		assertNotNull(result);
		assertFalse(result.getValue());
		
		assertEquals(mSet(R1L0,R2L0,R3L0,R4L0,R5L0), result.getSolvedValueSource());
		assertEquals(R6L0q, result.getQuerySource());
	}
	
    @Test
	public void testComplexSourceQuery22() {
		assertNull(solver.addFactEquality(E(a,b,R1L0)));
		assertNull(solver.addFactEquality(E(b,c,R2L0)));
		assertNull(solver.addFactEquality(E(d,e,R3L0)));
		assertNull(solver.addFactEquality(E(e,f,R4L0)));
		assertNull(solver.addFactInequality(E(a, d, R5L0)));
		QueryResult result = solver.addQuery(E(c, f, R6L0q), false);
		assertNotNull(result);
		assertTrue(result.getValue());
		
		assertEquals(mSet(R1L0,R2L0,R3L0,R4L0,R5L0), result.getSolvedValueSource());
		assertEquals(R6L0q, result.getQuerySource());
	}
	
    @Test
	public void testComplexSourceQuery3() {
		assertNull(solver.addQuery(E(a, d, R6L0q), true));
		assertNull(solver.addQuery(E(c, f, R7L0q), true));
		
		assertNull(solver.addFactEquality(E(a,b,R1L0)));
		assertNull(solver.addFactEquality(E(b,c,R2L0)));
		assertNull(solver.addFactEquality(E(d,e,R3L0)));
		assertNull(solver.addFactEquality(E(e,f,R4L0)));
		FactResult result = solver.addFactInequality(E(a, d, R5L0));
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
	
    @Test
	public void testComplexSourceQuery32() {
		assertNull(solver.addQuery(E(a, d, R6L0q), false));
		assertNull(solver.addQuery(E(c, f, R7L0q), false));
		
		assertNull(solver.addFactEquality(E(a,b,R1L0)));
		assertNull(solver.addFactEquality(E(b,c,R2L0)));
		assertNull(solver.addFactEquality(E(d,e,R3L0)));
		assertNull(solver.addFactEquality(E(e,f,R4L0)));
		FactResult result = solver.addFactInequality(E(a, d, R5L0));
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
	
    @Test
	public void testComplexSourceQuery4() {
		assertNull(solver.addQuery(E(a, d, R6L0q), true));
		assertNull(solver.addQuery(E(c, f, R7L0q), true));
		
		assertNull(solver.addFactEquality(E(a,b,R1L0)));
		assertNull(solver.addFactEquality(E(b,c,R2L0)));
		assertNull(solver.addFactEquality(E(d,e,R3L0)));
		assertNull(solver.addFactEquality(E(e,f,R4L0)));
		FactResult result = solver.addFactEquality(E(a, d, R5L0));
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
	
    @Test
	public void testComplexSourceQuery42() {
		assertNull(solver.addQuery(E(a, d, R6L0q), false));
		assertNull(solver.addQuery(E(c, f, R7L0q), false));
		
		assertNull(solver.addFactEquality(E(a,b,R1L0)));
		assertNull(solver.addFactEquality(E(b,c,R2L0)));
		assertNull(solver.addFactEquality(E(d,e,R3L0)));
		assertNull(solver.addFactEquality(E(e,f,R4L0)));
		FactResult result = solver.addFactEquality(E(a, d, R5L0));
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
	
//	public void testSimpleQuerySuppressed1() {
//		assertNull(solver.addQuery(E(a, d, R3L0q), true));
//		assertNull(solver.addQuery(E(a, d, R5L0q), false));
//		
//		assertNull(solver.addFactEquality(E(a, b, R1L0)));
//		assertNotNull(solver.addFactInequality(E(a, d, R4L0)));
//		// query should not be there after being solved
//		assertNull(solver.addFactInequality(E(b, d, R2L0)));
//	}
	
	// origin with different levels
//	 sources without levels
    @Test
	public void testSourceTableSimpleWithLevels() {
		solver.addFactEquality(E(a, b, R1L1));
		solver.addFactEquality(E(a, c, R2L1));
		assertEquals(mSet(
				"a,b[R1/1]",
				"a,c[R2/1]")
		,solver.getSourceTable().dump());
	}

    @Test
	public void testSourceTableWithEdgeRedundantWithLevel1() {
		solver.addFactEquality(E(a, b, R1L0));
		solver.addFactEquality(E(b, c, R2L0));
		solver.addFactEquality(E(a, c, R3L1));
		assertEquals(mSet(
				"a,b[R1]",
				"a,c[R2, R1]",
				"b,c[R2]"
		),solver.getSourceTable().dump());
	}
	
    @Test
	public void testSourceTableWithEdgeRedundantWithLevel2() {
		solver.addFactEquality(E(a, b, R1L1));
		solver.addFactEquality(E(b, c, R2L0));
		solver.addFactEquality(E(a, c, R3L1));
		assertEquals(mSet(
				"a,b[R1/1]",
				"a,c[R3/1]",
				"b,c[R2]"
		),solver.getSourceTable().dump());
	}
	
    @Test
	public void testSourceTableOptimisationWithoutLevels() {
		solver.addFactEquality(E(a, b, R1L0));
		solver.addFactEquality(E(c, d, R2L0));
		solver.addFactEquality(E(a, c, R3L0));
		solver.addFactEquality(E(d, e, R4L0));
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
	
//	public void testSourceTableOptimisationWithLevels() {
//		solver.addFactEquality(E(a, b, R1L0));
//		solver.addFactEquality(E(c, d, R2L1));
//		solver.addFactEquality(E(a, c, R3L1));
//		solver.addFactEquality(E(d, e, R4L0));
//		solver.addFactEquality(E(b, c, R5L0));
//		assertEquals(mSet(
//				"b->a",
//				"d->a",
//				"c->a",
//				"e->a"
//		),solver.dump());
//		assertEquals(mSet(
//				"a,b[R1]",
//				"c,d[R2]",
//				"a,c[R3]",
//				"d,e[R4]",
//				"a,d[R2, R3]",
//				"a,e[R4, R2, R3]"
//		),solver.getSourceTable().dump());
//	}
	
	
    @Test
	public void testRedundantQuery() {
		solver.addQuery(E(a, b, R1L0q), true);
		solver.addQuery(E(a, b, R2L0q), true);
		
		FactResult result = solver.addFactEquality(E(a, b, R3L0));
		assertNotNull(result);
		assertFalse(result.hasContradiction());
		assertEquals(result.getSolvedQueries().size(),2);
	}
	
	// instantiations
	
    @Test
	public void testSimpleInstantiationInequalityFirst() {
		solver.addFactInequality(E(a, b, R1L0));
		List<InstantiationResult> result = solver.addInstantiation(I(a, R2L0q));
		
		assertNotNull(result);
		assertEquals(1, result.size());
		InstantiationResult res1 = result.get(0);
		assertEquals(b, res1.getProposedValue());
		assertEquals(R2L0q, res1.getSolvedSource());
		assertEquals(mSet(R1L0), res1.getSolvedValueSource());
	}

    @Test
	public void testSimpleInstantiationFirst() {
		solver.addInstantiation(I(a, R2L0q));
		FactResult factResult = solver.addFactInequality(E(a, b, R1L0));
		assertNotNull(factResult);
		assertFalse(factResult.hasContradiction());
		assertEquals(1, factResult.getSolvedInstantiations().size());
		InstantiationResult result = factResult.getSolvedInstantiations().get(0);
		assertNotNull(result);
		assertEquals(b, result.getProposedValue());
		assertEquals(R2L0q, result.getSolvedSource());
		assertEquals(mSet(R1L0), result.getSolvedValueSource());
	}
	
    @Test
    @Ignore("Fails randomly")
	public void testMultipleInstantiation() {
		solver.addFactEquality(E(a, b, R1L1));
		solver.addFactEquality(E(a, c, R2L0));
		solver.addFactInequality(E(b, d, R3L1));
		solver.addFactInequality(E(c, e, R4L0));
		
		List<InstantiationResult> results = solver.addInstantiation(I(a, R6L0q));
		assertNotNull(results);
		assertEquals(2, results.size());
		InstantiationResult result1 = results.get(0);
		assertEquals(d, result1.getProposedValue());
		assertEquals(R6L0q, result1.getSolvedSource());
		assertEquals(mSet(R1L1, R3L1), result1.getSolvedValueSource());
		InstantiationResult result2 = results.get(1);
		assertEquals(e, result2.getProposedValue());
		assertEquals(R6L0q, result2.getSolvedSource());
		assertEquals(mSet(R2L0, R4L0), result2.getSolvedValueSource());
	}
	
    @Test
	public void testMultipleInstantiation2() {
		solver.addFactEquality(E(a, b, R1L0));
		solver.addFactEquality(E(a, c, R2L1));
		solver.addFactInequality(E(b, d, R3L0));
		solver.addFactInequality(E(c, e, R4L1));
		solver.addFactEquality(E(d, e, R5L0));
		
		List<InstantiationResult> results = solver.addInstantiation(I(a, R6L0q));
		assertNotNull(results);
		assertEquals(1, results.size());
		InstantiationResult result1 = results.get(0);
		assertEquals(d, result1.getProposedValue());
		assertEquals(R6L0q, result1.getSolvedSource());
		assertEquals(mSet(R1L0, R3L0), result1.getSolvedValueSource());
	}
	
	
	
}
