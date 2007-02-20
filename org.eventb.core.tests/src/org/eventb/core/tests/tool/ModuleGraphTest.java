/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.tests.tool;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eventb.core.tool.IModule;
import org.eventb.internal.core.tool.ModuleDesc;
import org.eventb.internal.core.tool.ModuleFactory;
import org.eventb.internal.core.tool.ModuleManager;
import org.eventb.internal.core.tool.graph.ModuleGraph;

/**
 * @author Stefan Hallerstede
 *
 */
public class ModuleGraphTest extends Declarations {
	
	private static abstract class ModuleTest {
		protected final List<ModuleDesc<? extends IModule>> items;
		ModuleTest(ModuleDesc<? extends IModule>[] items) {
			this.items = Arrays.asList(items);
		}
		
		protected ModuleGraph getAnalysedGraph() {
			return ModuleManager.sortModules(items, "T");
		}
		
		protected abstract void test();
	};
	
	private static class SortingTest extends ModuleTest {
		private final String sorted;
		public SortingTest(ModuleDesc<? extends IModule>[] items, String sorted) {
			super(items);
			this.sorted = sorted;
		}
		public void test() {
			ModuleGraph graph = getAnalysedGraph();
			assertEquals("sorting failed", "[" + sorted + "]", graph.getSorted().toString());
		}
	}
	
	private static class FailingTest extends ModuleTest {
		public FailingTest(ModuleDesc<? extends IModule>[] items) {
			super(items);
		}
		@Override
		protected void test() {
			try {
				getAnalysedGraph();
				fail("analysis should have failed");
			} catch (Exception e) {
			}
			
		}
	}
	
	private static class FactoryTest extends ModuleTest {
		private final String sorted;
		private final Map<String, ModuleDesc<? extends IModule>> map;		
		private final ModuleDesc<? extends IModule> root;
		public FactoryTest(
				ModuleDesc<? extends IModule>[] items, 
				ModuleDesc<? extends IModule> root, 
				String sorted) {
			super(items);
			this.sorted = sorted;
			this.root = root;
			map = new HashMap<String, ModuleDesc<? extends IModule>>();
			for (ModuleDesc<? extends IModule> desc : items) {
				map.put(desc.getId(), desc);
			}
		}
		public void test() {
			ModuleGraph graph = getAnalysedGraph();
			ModuleFactory factory = new ModuleFactory(graph, map);
			String result = factory.toString(root);
			assertEquals("sorted factory failed", "[" + sorted + "]", result);
		}
	}
	
	private static ModuleDesc[][] moduleDescs = new ModuleDesc[][] {
		new ModuleDesc[] {
		},
		new ModuleDesc[] {
				new ProcDesc("c", "org.m.c")
		},
		new ModuleDesc[] {
				new ProcDesc("c", "?")
		},
		new ModuleDesc[] {
				new ProcDesc("2", "org.m.1"),
				new ProcDesc("1", null)
		},
		new ModuleDesc[] {
				new FilterDesc("a", "org.m.b"),
				new ProcDesc("c", "org.m.b"),
				new ProcDesc("b", null),
				new FilterDesc("f", "org.m.b")
		},
		new ModuleDesc[] {
				new FilterDesc("Y", null)
		},
		new ModuleDesc[] {
				new ProcDesc("X", null),
				new FilterDesc("Y", "org.m.X"),
				new ProcDesc("Z", "org.m.Y")
		},
		new ModuleDesc[] {
				new ProcDesc("X", null),
				new ProcDesc("Y", "org.m.X", "org.m.Y")
		},
		new ModuleDesc[] {
				new ProcDesc("X", null),
				new ProcDesc("Y", "org.m.X"),
				new FilterDesc("Z", "org.m.Y", "org.m.X")
		},
		new ModuleDesc[] {
				new ProcDesc("6", null),
				new FilterDesc("1", "org.m.4"),
				new FilterDesc("2", "org.m.4", "org.m.1"),
				new ProcDesc("4", "org.m.6", "org.m.3"),
				new ProcDesc("3", "org.m.6"),
				new ProcDesc("5", "org.m.6"),
				new ProcDesc("7", null)
		},
		new ModuleDesc[] {
				new ProcDesc("C", null),
				new ProcDesc("B", "org.m.C", "org.m.X"),
				new ProcDesc("A", "org.m.C"),
				new ProcDesc("X", "org.m.A"),
				new ProcDesc("Y", "org.m.B")
		},
		new ModuleDesc[] {
				new ProcDesc("C", null),
				new ProcDesc("B", "org.m.C"),
				new ProcDesc("Y", "org.m.B", "org.m.X"),
				new ProcDesc("A", "org.m.C"),
				new ProcDesc("X", "org.m.A")
		}
	};
	
	@SuppressWarnings("unchecked")
	private static ModuleTest[] testItems = new ModuleTest[] {
		// empty list should work
		new SortingTest(moduleDescs[0], ""),
		// self-loop should fail
		new FailingTest(moduleDescs[1]),
		// unknown parent should fail
		new FailingTest(moduleDescs[2]),
		// order between two nodes: parent last
		new SortingTest(moduleDescs[3], "org.m.2, org.m.1"),
		// order between children: first filters, then processors; but preserve relative order
		new SortingTest(moduleDescs[4], "org.m.a, org.m.f, org.m.c, org.m.b"),
		// a filter must have a parent
		new FailingTest(moduleDescs[5]),
		// a filter cannot be a parent
		new FailingTest(moduleDescs[6]),
		// a parent (transitively) cannot be a prereq
		new FailingTest(moduleDescs[7]),
		new FailingTest(moduleDescs[8]),
		// preserve relative order
		new SortingTest(moduleDescs[9], "org.m.1, org.m.2, org.m.3, org.m.4, org.m.5, org.m.6, org.m.7"),
		// submodule prereq requirements must be observed by the factory
		new FactoryTest(moduleDescs[10], moduleDescs[10][0], "org.m.X, org.m.A, org.m.Y, org.m.B, org.m.C"),
		new FactoryTest(moduleDescs[11], moduleDescs[11][0], "org.m.X, org.m.A, org.m.Y, org.m.B, org.m.C")
	};
	
	/**
	 * Test sorting of empty graph
	 */
	public void test() throws Exception {
		for (int i=0; i<testItems.length; i++) {
			ModuleTest testItem = testItems[i];
			testItem.test();
		}
	}

}
