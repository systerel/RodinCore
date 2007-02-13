package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.QualifiedName;
import org.eventb.core.IVariable;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.actions.PrefixVarName;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

public class Add implements IEditAction {

	public void run(IEventBEditor editor, final IInternalParent parent,
			final IInternalElement element) throws CoreException {
		QualifiedName qualifiedName = PrefixVarName.QUALIFIED_NAME;
		String defaultPrefix = PrefixVarName.DEFAULT_PREFIX;
		final String newName = UIUtils.getFreeElementName(editor, parent,
				IVariable.ELEMENT_TYPE, qualifiedName, defaultPrefix);
		final String newIdentifier = UIUtils.getFreeElementIdentifier(editor,
				parent, IVariable.ELEMENT_TYPE, qualifiedName, defaultPrefix);

		RodinCore.run(new IWorkspaceRunnable() {

			public void run(IProgressMonitor monitor) throws CoreException {
				IVariable var = parent.getInternalElement(
						IVariable.ELEMENT_TYPE, newName);

				var.create(element, new NullProgressMonitor());
				var.setIdentifierString(newIdentifier, monitor);
			}

		}, new NullProgressMonitor());
	}

	public boolean isApplicable(IInternalParent parent, IInternalElement element)
			throws RodinDBException {
		return true;
	}

}
