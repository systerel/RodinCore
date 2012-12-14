/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.tests.tool;

import static junit.framework.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eventb.internal.core.tool.BasicDesc.ModuleLoadingException;
import org.eventb.internal.core.tool.ModuleConfig;
import org.eventb.internal.core.tool.ModuleDesc;
import org.eventb.internal.core.tool.types.IModule;
import org.junit.Test;

/**
 * @author Stefan Hallerstede
 *
 */
public class ModuleConfigGraphTest extends Declarations {
	
	private static class ShortProcDesc extends ProcDesc {

		public ShortProcDesc(String name) throws ModuleLoadingException {
			super(name, null);
		}
		
	}
	
	private static class ConfigItem extends ModuleConfig {
		@Override
		public List<ModuleDesc<? extends IModule>> getModuleDescs() {
			return modules;
		}
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
			return "org.t";
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
		private List<ModuleDesc<? extends IModule>> modules;
		private final String name;
		private final String[] included;
		public ConfigItem(ModuleDesc<? extends IModule>[] modules, String name, String... included) throws ModuleLoadingException {
			super("x", new DummyConfigurationElement(), null);
			this.name = name;
			this.included = included;
			this.modules = Arrays.asList(modules);
		}
	}
	
	private static abstract class ConfigTest {
		protected final List<ModuleConfig> items;
		ConfigTest(ConfigItem[] items) {
			this.items = Arrays.asList( (ModuleConfig[]) items);
		}
		
		protected abstract void test();
	};
	
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
			assertEquals("wrong closure ", "[" + closure + "]", item.computeClosure(map).toString());
		}
	}


	private static ModuleDesc<?>[][] makeModules() throws ModuleLoadingException {

		return new ModuleDesc[][] {
				new ModuleDesc[] {
						new ShortProcDesc("x")
				},
				new ModuleDesc[] {
						new ShortProcDesc("y")
				},
				new ModuleDesc[] {
						new ShortProcDesc("z")
				},
				new ModuleDesc[] {
				},
				new ModuleDesc[] {
						new ShortProcDesc("1"),
						new ShortProcDesc("2")
				},
				new ModuleDesc[] {
						new ShortProcDesc("A"),
						new ShortProcDesc("B"),
						new ShortProcDesc("C"),
						new ShortProcDesc("D"),
						new ShortProcDesc("E")
				},
				new ModuleDesc[] {
						new ShortProcDesc("K"),
						new ShortProcDesc("L"),
						new ShortProcDesc("M"),
				}
		};
	}
	
	private static ConfigItem[][] makeConfigItems() throws ModuleLoadingException {
		final ModuleDesc<?>[][] modules = makeModules(); 

		return new ConfigItem[][] {
				new ConfigItem[] {
						new ConfigItem(modules[0], "X", "org.t.Y", "org.t.Z"),
						new ConfigItem(modules[1], "Y"),
						new ConfigItem(modules[2], "Z")
				},
				new ConfigItem[] {
						new ConfigItem(modules[3], "A"),
						new ConfigItem(modules[3], "B", "org.t.A"),
						new ConfigItem(modules[0], "X", "org.t.Y", "org.t.Z"),
						new ConfigItem(modules[1], "Y"),
						new ConfigItem(modules[2], "Z")
				},
				new ConfigItem[] {
						new ConfigItem(modules[4], "1"),
						new ConfigItem(modules[5], "A", "org.t.1"),
						new ConfigItem(modules[6], "K", "org.t.A")
				}
		};
	}

	private static ConfigTest[] makeTestItems() throws ModuleLoadingException {
		final ConfigItem[][] configItems = makeConfigItems();
		return new ConfigTest[] {

				// compute some closures
				new ClosureTest(configItems[0], configItems[0][0], "org.m.y, org.m.z, org.m.x"),
				new ClosureTest(configItems[1], configItems[1][2], "org.m.y, org.m.z, org.m.x"),
				new ClosureTest(configItems[1], configItems[1][1], ""),
				new ClosureTest(configItems[2], configItems[2][2], 
						"org.m.1, org.m.2, " +
						"org.m.A, org.m.B, org.m.C, org.m.D, org.m.E, " +
				"org.m.K, org.m.L, org.m.M")
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
