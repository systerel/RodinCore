/*******************************************************************************
 * Copyright (c) 2008, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.internal.explorer.navigator.filters;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eventb.core.IContextRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IPORoot;
import org.eventb.core.IPRRoot;
import org.eventb.core.IPSRoot;
import org.eventb.core.ISCContextRoot;
import org.eventb.core.ISCMachineRoot;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;

/**
 * An abstract Filter that filters out files of a certain content type
 * 
 */
public abstract class FileTypeFilter extends ViewerFilter {

	/**
	 * 
	 * @return The type that this filter should exclude.
	 */
	protected abstract IInternalElementType<?> getType();

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (!(element instanceof IFile)) {
			return true;
		}
		IRodinFile el = RodinCore.valueOf((IFile) element);
		if (el == null) {
			return true;
		}
		return getType() != el.getRootElementType();
	}

	public static class UncheckedMachineFilter extends FileTypeFilter {
		@Override
		protected IInternalElementType<?> getType() {
			return IMachineRoot.ELEMENT_TYPE;
		}
	}

	public static class CheckedMachineFilter extends FileTypeFilter {
		@Override
		protected IInternalElementType<?> getType() {
			return ISCMachineRoot.ELEMENT_TYPE;
		}
	}

	public static class UncheckedContextFilter extends FileTypeFilter {
		@Override
		protected IInternalElementType<?> getType() {
			return IContextRoot.ELEMENT_TYPE;
		}
	}

	public static class CheckedContextFilter extends FileTypeFilter {
		@Override
		protected IInternalElementType<?> getType() {
			return ISCContextRoot.ELEMENT_TYPE;
		}
	}

	public static class POFilter extends FileTypeFilter {
		@Override
		protected IInternalElementType<?> getType() {
			return IPORoot.ELEMENT_TYPE;
		}
	}

	public static class PSFilter extends FileTypeFilter {
		@Override
		protected IInternalElementType<?> getType() {
			return IPSRoot.ELEMENT_TYPE;
		}
	}

	public static class PRFilter extends FileTypeFilter {
		@Override
		protected IInternalElementType<?> getType() {
			return IPRRoot.ELEMENT_TYPE;
		}
	}

}
