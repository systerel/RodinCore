/*******************************************************************************
 * Copyright (c) 2006, 2024 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - implement toString()
 *******************************************************************************/
package org.eventb.internal.core.seqprover;

import java.util.Collection;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction;

public interface IInternalHypAction extends IHypAction {
	
	Collection<Predicate> getHyps();

	IInternalProverSequent perform(IInternalProverSequent seq);
	
	void processDependencies(ProofDependenciesBuilder proofDeps);

	default RecordPrinter print(RecordPrinter printer) {
		printer.println(getActionType()).indent();
		return printBody(printer).dedent();
	}

	RecordPrinter printBody(RecordPrinter printer);

}
