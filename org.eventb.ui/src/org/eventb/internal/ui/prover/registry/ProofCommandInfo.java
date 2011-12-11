/*******************************************************************************
 * Copyright (c) 2005, 2011 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - refactored to use ITacticProvider2 and ITacticApplication
 *******************************************************************************/
package org.eventb.internal.ui.prover.registry;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IUserSupport;
import org.eventb.internal.ui.prover.ICommandApplication;
import org.eventb.ui.prover.IProofCommand;

public class ProofCommandInfo extends TacticUIInfo {
	private final CommandApplication commandApplication;

	public ProofCommandInfo(String id, Target target, ImageDescriptor iconDesc,
			boolean interrupt, String tooltip, int priority, String name,
			String dropdown, String toolbar, boolean skipPostTactic,
			IProofCommand command) {
		super(id, target, iconDesc, interrupt, tooltip, priority, name, dropdown,
				toolbar, skipPostTactic);
		this.commandApplication = new CommandApplication(command, iconDesc,
				tooltip);
	}

	public boolean isApplicable(IUserSupport us, Predicate hyp,
			String globalInput) {
		return commandApplication.getProofCommand().isApplicable(us, hyp,
				globalInput);
	}

	public ICommandApplication getCommandApplication() {
		return commandApplication;
	}
}