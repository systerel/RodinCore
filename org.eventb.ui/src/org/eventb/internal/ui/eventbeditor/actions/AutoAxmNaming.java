package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eventb.core.ITheorem;
import org.rodinp.core.IRodinFile;

public class AutoAxmNaming extends AutoElementNaming {

	public void run(IAction action) {
		IRodinFile inputFile = editor.getRodinInput();
		String prefix = null;
		try {
			prefix = inputFile.getResource().getPersistentProperty(
					PrefixAxmName.QUALIFIED_NAME);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (prefix == null) prefix = PrefixAxmName.DEFAULT_PREFIX;

		rename(ITheorem.ELEMENT_TYPE, prefix);
	}


}