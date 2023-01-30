/*******************************************************************************
 * Copyright (c) 2023 Université de Lorraine and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Université de Lorraine - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import static org.eventb.internal.core.seqprover.eventbExtensions.EqHe.Level.L1;

/**
 * Rewrites an equality and hides the hypothesis if the rewritten expression is
 * an identifier.
 *
 * @author Guillaume Verdier
 */
public class HeL1 extends He {

	public HeL1() {
		super(L1);
	}

	@Override
	public String getReasonerID() {
		return super.getReasonerID() + "L1";
	}

}
