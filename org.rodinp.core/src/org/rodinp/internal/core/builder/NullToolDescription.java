/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core.builder;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.rodinp.core.builder.IAutomaticTool;

/**
 * Implementation of the null object pattern for ToolDescription. This class can
 * be instantiated if a node is still present in the builder's graph, and no
 * corresponding tool is provided.
 * 
 * @author Thomas Muller
 */
public class NullToolDescription extends ToolDescription {

	private static final String desc = "NullToolDescription";

	/**
	 * The unique id of this class.
	 */
	private final String id;

	public NullToolDescription() {
		super(getNullConfiguration());
		this.id = this.bundleName + "." + desc;
	}

	/**
	 * Returns a tool that does nothing.
	 */
	@Override
	protected Object getExecutableExtension() {
		return new NullTool();
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return name + "(id=" + id + ", class=" + className + ")";
	}

	/**
	 * This class does nothing (NullObject Pattern).
	 */
	final static class NullTool implements IAutomaticTool {

		public void clean(IFile source, IFile target, IProgressMonitor monitor)
				throws CoreException {
			// Does nothing

		}

		public boolean run(IFile source, IFile target, IProgressMonitor monitor)
				throws CoreException {
			// Does nothing and return false, as file has not changed
			return false;
		}
	}

	/**
	 * @return new {@link NullConfigurationElement}
	 */
	private static IConfigurationElement getNullConfiguration() {
		return new NullConfigurationElement();
	}

	/**
	 * Null configuration used to create new instances of
	 * {@link NullToolDescription}
	 */
	final static class NullConfigurationElement implements
			IConfigurationElement {

		private static final String nullConf = "NullConfigurationElement";

		public Object createExecutableExtension(String propertyName)
				throws CoreException {
			return new NullToolDescription.NullTool();
		}

		public String getAttribute(String name)
				throws InvalidRegistryObjectException {
			return null;
		}

		@Deprecated
		public String getAttributeAsIs(String name)
				throws InvalidRegistryObjectException {
			return null;
		}

		public String[] getAttributeNames()
				throws InvalidRegistryObjectException {
			return new String[0];
		}

		public IConfigurationElement[] getChildren()
				throws InvalidRegistryObjectException {
			return new IConfigurationElement[0];
		}

		public IConfigurationElement[] getChildren(String name)
				throws InvalidRegistryObjectException {
			return new IConfigurationElement[0];
		}

		public IContributor getContributor()
				throws InvalidRegistryObjectException {
			return new IContributor() {
				public String getName() {
					return nullConf + ".Contributor";
				}
			};
		}

		public IExtension getDeclaringExtension()
				throws InvalidRegistryObjectException {
			throw new InvalidRegistryObjectException();
		}

		public String getName() throws InvalidRegistryObjectException {
			return nullConf;
		}

		@Deprecated
		public String getNamespace() throws InvalidRegistryObjectException {
			return nullConf + ".NameSpace";
		}

		public String getNamespaceIdentifier()
				throws InvalidRegistryObjectException {
			return nullConf + ".NameSpace";
		}

		public Object getParent() throws InvalidRegistryObjectException {
			return null;
		}

		public String getValue() throws InvalidRegistryObjectException {
			return null;
		}

		@Deprecated
		public String getValueAsIs() throws InvalidRegistryObjectException {
			return null;
		}

		public boolean isValid() {
			return false;
		}
	}

}
