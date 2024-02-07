/*******************************************************************************
 * Copyright (c) 2024 UPEC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     UPEC - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import static org.eventb.internal.core.seqprover.eventbExtensions.EqHe.Level.L2;

/**
 * Rewrites an equality and hides or deselects the hypothesis if the rewritten
 * expression is an identifier.
 *
 * @author Guillaume Verdier
 */
public class EqL2 extends Eq {

	public EqL2() {
		super(L2);
	}

	@Override
	public String getReasonerID() {
		return super.getReasonerID() + "L2";
	}

}
