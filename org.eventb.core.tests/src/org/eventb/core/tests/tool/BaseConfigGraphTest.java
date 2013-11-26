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
package org.eventb.core.tests.tool;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eventb.internal.core.tool.BaseConfig;
import org.eventb.internal.core.tool.BasicDesc.ModuleLoadingException;
import org.eventb.internal.core.tool.graph.ConfigGraph;
import org.junit.Test;

public class BaseConfigGraphTest {

	private static class ConfigItem extends BaseConfig {
		@Override
		public String[] getIncluded() {
			return included;
		}
		@Override
		public boolean equals(Object obj) {
			return getId().equals(((ConfigItem) obj).getId());
		}
		@Override
		public String getBundleName() {
			return "org.test";
		}
		@Override
		public String getId() {
			return getBundleName() + "." + name;
		}
		@Override
		public String getName() {
			return name;
		}
		@Override
		public String toString() {
			return getId();
		}
		private final String name;
		private final String[] included;
		public ConfigItem(String name, String... included) throws ModuleLoadingException {
			super(new DummyConfigurationElement());
			this.name = name;
			this.included = included;
		}
	}
	
	private static abstract class ConfigTest {
		protected final List<BaseConfig> items;
		ConfigTest(ConfigItem[] items) {
			this.items = Arrays.asList( (BaseConfig[]) items);
		}
		
		protected ConfigGraph getAnalysedGraph() {
			ConfigGraph graph = new ConfigGraph("BASE");
			graph.addAll(items);
			graph.analyse();
			return graph;
		}
		
		protected abstract void test();
	}
	
	private static class SortingTest extends ConfigTest {
		private final String sorted;
		public SortingTest(ConfigItem[] items, String sorted) {
			super(items);
			this.sorted = sorted;
		}
		
		@Override
		public void test() {
			ConfigGraph graph = getAnalysedGraph();
			assertEquals("sorting failed", "[" + sorted + "]", graph.getSorted().toString());
		}
	}
	
	private static class FailingTest extends ConfigTest {
		public FailingTest(ConfigItem[] items) {
			super(items);
		}
		@Override
		protected void test() {
			try {
				getAnalysedGraph();
				fail("analysis should have failed");
			} catch (Exception e) {
				// Ignore
			}
			
		}
	}
	
	private static class ClosureTest extends ConfigTest {
		private final ConfigItem item;
		private final Map<String, ConfigItem> map;
		private final String closure;
		public ClosureTest(ConfigItem[] items, ConfigItem item, String closure) {
			super(items);
			this.item = item;
			this.closure = closure;
			this.map = new HashMap<String, ConfigItem>(items.length * 4 / 3 + 1);
			for (ConfigItem ci : items) {
				map.put(ci.getId(), ci);
			}
		}
		@Override
		protected void test() {
			getAnalysedGraph();
			assertEquals("wrong closure ", "[" + closure + "]", item.computeClosure(map).toString());
		}
	}
	
	private static ConfigItem[][] makeConfigItems() throws ModuleLoadingException { 
		return new ConfigItem[][] {
				new ConfigItem[] {
				},
				new ConfigItem[] {
						new ConfigItem("c", "org.test.c")
				},
				new ConfigItem[] {
						new ConfigItem("c", "?")
				},
				new ConfigItem[] {
						new ConfigItem("2", "org.test.1"),
						new ConfigItem("1")
				},
				new ConfigItem[] {
						new ConfigItem("X", "org.test.Y", "org.test.Z"),
						new ConfigItem("Y"),
						new ConfigItem("Z")
				},
				new ConfigItem[] {
						new ConfigItem("A"),
						new ConfigItem("B", "org.test.A"),
						new ConfigItem("X", "org.test.Y", "org.test.Z"),
						new ConfigItem("Y"),
						new ConfigItem("Z")
				}
		};
	}

	private static ConfigTest[] makeTestItems() throws ModuleLoadingException {
		final ConfigItem[][] configItems = makeConfigItems();
		return new ConfigTest[] {
				// empty list should work
				new SortingTest(configItems[0], ""),
				// self-loops are not allowed
				new FailingTest(configItems[1]),
				// unknown references are not allowed
				new FailingTest(configItems[2]),
				// order included first
				new SortingTest(configItems[3], "org.test.1, org.test.2"),
				new SortingTest(configItems[4], "org.test.Y, org.test.Z, org.test.X"),
				// compute some closures
				new ClosureTest(configItems[4], configItems[4][0], "org.test.X, org.test.Y, org.test.Z"),
				new ClosureTest(configItems[5], configItems[5][2], "org.test.X, org.test.Y, org.test.Z")
		};
	}

	/**
	 * Test sorting of empty graph
	 */
	@Test
	public void test() throws Exception {
		final ConfigTest[] testItems = makeTestItems();
		
		for (int i=0; i<testItems.length; i++) {
			ConfigTest testItem = testItems[i];
			testItem.test();
		}
	}

}
