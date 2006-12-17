package org.eventb.internal.ui.projectexplorer.actions;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eventb.core.IEventBProject;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;

public class RodinFileInputValidator implements IInputValidator {

	IEventBProject prj;

	RodinFileInputValidator(IRodinProject prj) {
		this.prj = (IEventBProject) prj.getAdapter(IEventBProject.class);
	}

	public String isValid(String newText) {
		if (newText.equals(""))
			return "Name must not be empty.";
		IRodinFile file = prj.getMachineFile(newText);
		if (file != null && file.exists())
			return "File name " + newText + " already exists.";
		file = prj.getContextFile(newText);
		if (file != null && file.exists())
			return "File name " + newText + " already exists.";
		return null;
	}

}
