/*******************************************************************************
 * Copyright (c) 2006, 2014 ETH Zurich and others.
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

import org.eventb.core.tool.IFilterModule;
import org.eventb.core.tool.IProcessorModule;
import org.eventb.internal.core.tool.FilterModuleDesc;
import org.eventb.internal.core.tool.ProcessorModuleDesc;
import org.eventb.internal.core.tool.RootModuleDesc;
import org.rodinp.core.IInternalElementType;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class Declarations {
	
	protected static class ProcDesc extends ProcessorModuleDesc<IProcessorModule> {

		public ProcDesc(String name, String parent, String... prereqs) throws ModuleLoadingException {
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
		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + getId().hashCode();
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!super.equals(obj))
				return false;
			if (getClass() != obj.getClass())
				return false;
			final ProcDesc other = (ProcDesc) obj;
			return getId().equals(other.getId());
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
	
	protected static class RootDesc extends RootModuleDesc<IProcessorModule> {

		public RootDesc(String name, IProcessorModule module, IInternalElementType<?> type) throws ModuleLoadingException {
			super(new DummyConfigurationElement());
			this.name = name;
			this.type = type;
			this.module = module;
		}

		private final String name;
		private final IInternalElementType<?> type;
		private final IProcessorModule module;
		
		@Override
		public String getParent() {
			return null;
		}

		@Override
		public String[] getPrereqs() {
			return new String[0];
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + getId().hashCode();
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!super.equals(obj))
				return false;
			if (getClass() != obj.getClass())
				return false;
			final RootDesc other = (RootDesc) obj;
			return getId().equals(other.getId());
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

		@Override
		public IInternalElementType<?> getElementType() {
			return type;
		}

		@Override
		protected void computeClass() {
			// do nothing
		}

		@Override
		public IProcessorModule createInstance() {
			return module;
		}
		
	}
	
	protected static class FilterDesc extends FilterModuleDesc<IFilterModule> {

		public FilterDesc(String name, String parent, String... prereqs) throws ModuleLoadingException {
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
		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + getId().hashCode();
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!super.equals(obj))
				return false;
			if (getClass() != obj.getClass())
				return false;
			final FilterDesc other = (FilterDesc) obj;
			return getId().equals(other.getId());
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
