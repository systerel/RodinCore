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
import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.builder.IAutomaticTool;

/**
 * Implementation of the null object pattern for ToolDescription. This class can
 * be instantiated if a node is still present in the builder's graph, and no
 * corresponding tool is provided.
 * 
 * @author Thomas Muller
 */
public class NullToolDescription extends ToolDescription {

	public NullToolDescription(String id) {
		super(id, "Unknown tool");
	}

	/**
	 * Returns a tool that does nothing.
	 */
	@Override
	protected Object createInstance() {
		return new NullTool();
	}

	/**
	 * This class does nothing (NullObject Pattern).
	 */
	private static class NullTool implements IAutomaticTool {
		
		public NullTool() {
			// Does nothing
		}

		@Override
		public void clean(IFile source, IFile target, IProgressMonitor monitor)
				throws CoreException {
			// Does nothing

		}

		@Override
		public boolean run(IFile source, IFile target, IProgressMonitor monitor)
				throws CoreException {
			// Does nothing and return false, as file has not changed
			return false;
		}
	}

}
