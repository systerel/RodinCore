/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Systerel - Initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.actions;

import org.eclipse.core.commands.operations.IUndoContext;

import fr.systerel.editor.internal.actions.operations.AtomicOperation;
import fr.systerel.editor.internal.actions.operations.OperationNode;

/**
 * Class to add and execute operation trees in an {@link IRodinHistory}.
 */
public class OperationUtils {

	private OperationUtils() {
		// No instantiation
	}

	public static void executeAtomic(IRodinHistory history,
			IUndoContext context, String operationLabel,
			OperationTree... operations) {
		final OperationNode cmd = new OperationNode();
		for (OperationTree op : operations) {
			cmd.addCommand(op);
		}
		final AtomicOperation atom = new AtomicOperation(context, cmd);
		atom.setLabel(operationLabel);
		history.addOperation(atom);
	}

	public static void executeAtomic(IRodinHistory history,
			IUndoContext context, String operationLabel,
			OperationTree operations) {
		final AtomicOperation atom = new AtomicOperation(context, operations);
		history.addOperation(atom);
	}

}
