/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.internal.actions.operations;

import org.eclipse.core.commands.operations.IUndoContext;
import org.rodinp.core.IRodinFile;

public class RodinFileUndoContext implements IUndoContext {
	final IRodinFile rodinFile;

	public RodinFileUndoContext(IRodinFile rodinFile) {
		this.rodinFile = rodinFile;
	}

	@Override
	public String getLabel() {
		return rodinFile.getBareName();
	}

	@Override
	public boolean matches(IUndoContext context) {
		if (!(context instanceof RodinFileUndoContext))
			return false;
		return rodinFile.equals(((RodinFileUndoContext) context).rodinFile);
	}

	public IRodinFile getRodinFile() {
		return rodinFile;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof RodinFileUndoContext))
			return false;
		return rodinFile.equals(((RodinFileUndoContext) obj).rodinFile);
	}
	
	@Override
	public int hashCode() {
		return rodinFile.hashCode();
	}
}