package org.eventb.internal.ui.projectexplorer.actions;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eventb.core.EventBPlugin;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;

public class RodinFileInputValidator implements IInputValidator {

	IRodinProject prj;

	RodinFileInputValidator(IRodinProject prj) {
		this.prj = prj;
	}

	public String isValid(String newText) {
		if (newText.equals(""))
			return "Name must not be empty.";
		IRodinFile file = prj.getRodinFile(EventBPlugin
				.getMachineFileName(newText));
		if (file.exists())
			return "File name " + newText + " already exists.";
		file = prj.getRodinFile(EventBPlugin.getContextFileName(newText));
		if (file.exists())
			return "File name " + newText + " already exists.";
		return null;
	}

}
