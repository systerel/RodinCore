package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IAttributedElement;
import org.rodinp.core.RodinDBException;

public interface IAttributeEditor {

	public abstract String getAttribute(IAttributedElement element,
			IProgressMonitor monitor) throws RodinDBException;

	public abstract void setAttribute(IAttributedElement element,
			String newValue, IProgressMonitor monitor) throws RodinDBException;

	public abstract void setDefaultAttribute(IAttributedElement element,
			IProgressMonitor monitor) throws RodinDBException;

	public abstract void removeAttribute(IAttributedElement element,
			IProgressMonitor monitor) throws RodinDBException;
	
	public abstract String[] getPossibleValues(IAttributedElement element,
			IProgressMonitor monitor);
}
