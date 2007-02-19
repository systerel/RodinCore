/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.tests.tool;

import junit.framework.TestCase;

import org.eventb.core.tool.IFilterModule;
import org.eventb.core.tool.IProcessorModule;
import org.eventb.internal.core.tool.FilterModuleDesc;
import org.eventb.internal.core.tool.ModuleDesc;
import org.eventb.internal.core.tool.ProcessorModuleDesc;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class Declarations extends TestCase {
	
	protected static class ProcDesc extends ProcessorModuleDesc<IProcessorModule> {

		public ProcDesc(String name, String parent, String... prereqs) {
			super(new DummyConfigurationElement());
			this.name = name;
			this.parent = parent;
			this.prereqs = prereqs;
		}

		private final String name;
		private final String parent;
		private final String[] prereqs;
		@Override
		public String getParent() {
			return parent;
		}

		@Override
		public String[] getPrereqs() {
			return prereqs;
		}

		@Override
		public boolean equals(Object obj) {
			return getId().equals(((ModuleDesc) obj).getId());
		}

		@Override
		public String getBundleName() {
			return "org.m";
		}

		@Override
		public String getId() {
			return getBundleName() + "." + getName();
		}

		@Override
		public String getName() {
			return name;
		}
		
		@Override
		public String toString() {
			return getId();
		}
		
	}
	
	protected static class FilterDesc extends FilterModuleDesc<IFilterModule> {

		public FilterDesc(String name, String parent, String... prereqs) {
			super(new DummyConfigurationElement());
			this.name = name;
			this.parent = parent;
			this.prereqs = prereqs;
		}

		private final String name;
		private final String parent;
		private final String[] prereqs;
		@Override
		public String getParent() {
			return parent;
		}

		@Override
		public String[] getPrereqs() {
			return prereqs;
		}

		@Override
		public boolean equals(Object obj) {
			return getId().equals(((ModuleDesc) obj).getId());
		}

		@Override
		public String getBundleName() {
			return "org.m";
		}

		@Override
		public String getId() {
			return getBundleName() + "." + getName();
		}

		@Override
		public String getName() {
			return name;
		}
		
		@Override
		public String toString() {
			return getId();
		}
		
	}
	

}
