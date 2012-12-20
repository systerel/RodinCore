/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.core.tests.tool;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eventb.core.IContextRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.sc.modules.ContextModule;
import org.eventb.internal.core.sc.modules.MachineModule;
import org.eventb.internal.core.tool.BasicDesc.ModuleLoadingException;
import org.eventb.internal.core.tool.ModuleDesc;
import org.eventb.internal.core.tool.ModuleFactory;
import org.eventb.internal.core.tool.ModuleManager;
import org.eventb.internal.core.tool.graph.ModuleGraph;
import org.eventb.internal.core.tool.types.IModule;
import org.eventb.internal.core.tool.types.IProcessorModule;
import org.junit.Test;
import org.rodinp.core.IInternalElementType;

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
	}
	
	private static class SortingTest extends ModuleTest {
		private final String sorted;
		public SortingTest(ModuleDesc<? extends IModule>[] items, String sorted) {
			super(items);
			this.sorted = sorted;
		}

		@Override
		@Test
		public void test() {
			ModuleGraph graph = getAnalysedGraph();
			assertEquals("sorting failed", "[" + sorted + "]", graph.getSorted().toString());
		}
	}
	
	private static class FailingSortTest extends ModuleTest {
		public FailingSortTest(ModuleDesc<? extends IModule>[] items) {
			super(items);
		}
		@Override
		protected void test() {
			try {
				getAnalysedGraph();
				fail("analysis should have failed");
			} catch (Exception e) {
				// Success
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

		@Override
		@Test
		public void test() {
			ModuleGraph graph = getAnalysedGraph();
			ModuleFactory factory = new ModuleFactory(graph, map);
			String result = factory.toString(root);
			assertEquals("sorted factory failed", "[" + sorted + "]", result);
		}
	}
	
	private abstract static class AbstractCompleteTest extends ModuleTest {

		AbstractCompleteTest(
				ModuleDesc<? extends IModule>[] items, 
				IInternalElementType<?>[] types) {
			super(items);
			this.types = types;
			map = new HashMap<String, ModuleDesc<? extends IModule>>();
			for (ModuleDesc<? extends IModule> desc : items) {
				map.put(desc.getId(), desc);
			}
		}
		
		private final Map<String, ModuleDesc<? extends IModule>> map;		
		private final IInternalElementType<?>[] types;
		
		protected IModule[] run() {
			ModuleGraph graph = getAnalysedGraph();
			ModuleFactory factory = new ModuleFactory(graph, map);
			IModule[] modules = new IModule[types.length];
			for (int i=0; i< types.length; i++) {
				IInternalElementType<?> type = types[i];
				modules[i] = factory.getRootModule(type);
			}
			return modules;
		}
		
	}
	
	private static class GetRootTest extends AbstractCompleteTest {
		
		private final IModuleType<? extends IModule>[] mTypes;
		
		GetRootTest(
				ModuleDesc<? extends IModule>[] items, 
				IInternalElementType<?>[] types,
				IModuleType<? extends IModule>[] mTypes) {
			super(items, types);
			this.mTypes = mTypes;
		}

		@Override
		protected void test() {
			IModule[] modules = run();
			assertEquals("wrong number of modules", mTypes.length, modules.length);
			for (int i=0; i<modules.length; i++) {
				assertEquals("Wrong module type", mTypes[i], modules[i].getModuleType());
			}
		}
		
	}
	
	private static class FailingRootTest extends AbstractCompleteTest {

		FailingRootTest(
				ModuleDesc<? extends IModule>[] items, 
				IInternalElementType<?>[] types) {
			super(items, types);
		}

		@Override
		protected void test() {
			try {
				run();
				fail("test should have failed");
			} catch (Exception e) {
				// Success
			}
			
		}
		
	}
	
	private static final IInternalElementType<?>[][] TYPES = 
		new IInternalElementType<?>[][] {
			new IInternalElementType<?>[] {
			},
			new IInternalElementType<?>[] {
					IContextRoot.ELEMENT_TYPE
			},
			new IInternalElementType<?>[] {
					IContextRoot.ELEMENT_TYPE,
					IMachineRoot.ELEMENT_TYPE
			}
	};
	
	private static IProcessorModule cProcessor = new IProcessorModule() {

		@Override
		public IModuleType<?> getModuleType() {
			return ContextModule.MODULE_TYPE;
		}
		
	};
	
	private static IProcessorModule mProcessor = new IProcessorModule() {

		@Override
		public IModuleType<?> getModuleType() {
			return MachineModule.MODULE_TYPE;
		}
		
	};
	
	private static final IModuleType<?>[][] MTYPES = 
		new IModuleType[][] {
			new IModuleType[] {
			},
			new IModuleType[] {
					ContextModule.MODULE_TYPE
			},
			new IModuleType[] {
					ContextModule.MODULE_TYPE,
					MachineModule.MODULE_TYPE
			}
	};
	
	private static final ModuleDesc<?>[][] makeModuleDescs() throws ModuleLoadingException {
		return
		new ModuleDesc[][] {
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
				},
				new ModuleDesc[] {
						new RootDesc("X", null, IContextRoot.ELEMENT_TYPE),
						new RootDesc("Y", null, IContextRoot.ELEMENT_TYPE)
				},
				new ModuleDesc[] {
						new RootDesc("X", cProcessor, IContextRoot.ELEMENT_TYPE)
				},
				new ModuleDesc[] {
						new RootDesc("X", cProcessor, IContextRoot.ELEMENT_TYPE),
						new RootDesc("X", mProcessor, IMachineRoot.ELEMENT_TYPE)
				},
				new ModuleDesc[] {
						new RootDesc("C", cProcessor, IContextRoot.ELEMENT_TYPE),
						new ProcDesc("B", "org.m.C"),
						new FilterDesc("A", "org.m.B")
				}
		};
	}
	

	private static ModuleTest[] makeTestItems() throws ModuleLoadingException {
		final ModuleDesc<?>[][] moduleDescs = makeModuleDescs();
		
		return new ModuleTest[] {
				// empty list should work
				new SortingTest(moduleDescs[0], ""),
				// self-loop should fail
				new FailingSortTest(moduleDescs[1]),
				// unknown parent should fail
				new FailingSortTest(moduleDescs[2]),
				// order between two nodes: parent last
				new SortingTest(moduleDescs[3], "org.m.2, org.m.1"),
				// order between children: first filters, then processors; but
				// preserve
				// relative order
				new SortingTest(moduleDescs[4],
				"org.m.a, org.m.f, org.m.c, org.m.b"),
				// a filter must have a parent
				new FailingSortTest(moduleDescs[5]),
				// a filter cannot be a parent
				new FailingSortTest(moduleDescs[6]),
				// a parent (transitively) cannot be a prereq
				new FailingSortTest(moduleDescs[7]),
				new FailingSortTest(moduleDescs[8]),
				// preserve relative order
				new SortingTest(moduleDescs[9],
				"org.m.1, org.m.2, org.m.3, org.m.4, org.m.5, org.m.6, org.m.7"),
				// submodule prereq requirements must be observed by the factory
				new FactoryTest(moduleDescs[10], moduleDescs[10][0],
				"org.m.X, org.m.A, org.m.Y, org.m.B, org.m.C"),
				new FactoryTest(moduleDescs[11], moduleDescs[11][0],
				"org.m.X, org.m.A, org.m.Y, org.m.B, org.m.C"),
				// root modules must be unique for file element types
				new FailingRootTest(moduleDescs[12], TYPES[0]),
				// the right modules become roots
				new GetRootTest(moduleDescs[13], TYPES[1], MTYPES[1]),
				new GetRootTest(moduleDescs[14], TYPES[2], MTYPES[2]),
				// the root module has the right children
				new FactoryTest(moduleDescs[15], moduleDescs[15][0], "org.m.A, org.m.B, org.m.C"),
		};
	}
	
	/**
	 * Test sorting of empty graph
	 */
	@Test
	public void test() throws Exception {
		final ModuleTest[] testItems = makeTestItems();
		
		for (int i=0; i<testItems.length; i++) {
			ModuleTest testItem = testItems[i];
			testItem.test();
		}
	}

}
