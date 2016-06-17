/*******************************************************************************
 * Copyright (c) 2010, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtensionTests;

import org.eventb.core.ast.Expression;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.internal.core.seqprover.reasonerInputs.PFunSetInput;
import org.eventb.internal.core.seqprover.reasonerInputs.PFunSetInputReasoner;

/**
 * Abstract Unit tests for the Partial Function Set Expression Input reasoner
 * {@link PFunSetInputReasoner}
 */
public abstract class AbstractPFunSetInputReasonerTests extends AbstractSingleExpressionInputReasonerTests {

	@Override
	protected IReasonerInput makeInput(Expression expr) {
		return new PFunSetInput(expr);
	}

}

