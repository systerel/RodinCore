/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.internal.ui.projectexplorer.actions;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eventb.core.IEventBProject;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;

public class RodinFileInputValidator implements IInputValidator {

	private final IEventBProject prj;

	public RodinFileInputValidator(IRodinProject prj) {
		this.prj = (IEventBProject) prj.getAdapter(IEventBProject.class);
	}

	@Override
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
