package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eventb.core.IInvariant;
import org.rodinp.core.IRodinFile;

public class AutoInvNaming extends AutoElementNaming {

	public void run(IAction action) {
		IRodinFile inputFile = editor.getRodinInput();
		String prefix = null;
		try {
			prefix = inputFile.getResource().getPersistentProperty(
					PrefixInvName.QUALIFIED_NAME);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (prefix == null) prefix = PrefixInvName.DEFAULT_PREFIX;
		
		rename(IInvariant.ELEMENT_TYPE, prefix);
	}

}
