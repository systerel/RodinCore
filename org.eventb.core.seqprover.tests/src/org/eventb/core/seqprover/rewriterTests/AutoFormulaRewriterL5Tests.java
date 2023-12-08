/*******************************************************************************
 * Copyright (c) 2023 UPEC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     UPEC - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.rewriterTests;

import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AutoRewriterImpl;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AutoRewrites.Level;

/**
 * This is the class for testing automatic rewriter level 5.
 *
 * @author Guillaume Verdier
 */
public class AutoFormulaRewriterL5Tests extends AutoFormulaRewriterL4Tests {

	private static final AutoRewriterImpl REWRITER_L5 = new AutoRewriterImpl(Level.L5);

	public AutoFormulaRewriterL5Tests() {
		this(REWRITER_L5);
	}

	protected AutoFormulaRewriterL5Tests(AutoRewriterImpl rewriter) {
		super(rewriter);
	}

}