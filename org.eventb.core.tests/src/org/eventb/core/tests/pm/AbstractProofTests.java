/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.tests.pm;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eventb.core.EventBPlugin;
import org.eventb.core.IPSStatus;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.pm.IProofManager;
import org.eventb.core.seqprover.IProofSkeleton;
import org.eventb.core.tests.BuilderTest;
import org.rodinp.core.RodinDBException;

/**
 * Common stuff for writing unit tests for Proof Components.
 * 
 * @author Laurent Voisin
 */
public abstract class AbstractProofTests extends BuilderTest {

	protected static final IProofManager pm = EventBPlugin.getProofManager();

	protected static final String PO1 = "PO1"; //$NON-NLS-1$
	protected static final String PO2 = "PO2"; //$NON-NLS-1$

	protected static final String TEST = "test"; //$NON-NLS-1$

	protected static final FormulaFactory ff = FormulaFactory.getDefault();

	protected static <T> Set<T> mSet(T... objects) {
		return new HashSet<T>(Arrays.asList(objects));
	}

	protected static <T> Set<T> mSet(Iterable<T> iterable) {
		final Set<T> res = new HashSet<T>();
		for (final T item : iterable) {
			res.add(item);
		}
		return res;
	}

	protected void assertEmptyProof(IProofSkeleton skeleton) {
		assertNull(skeleton.getRule());
		assertEquals(0, skeleton.getChildNodes().length);
	}

	protected void assertNonEmptyProof(IProofSkeleton skeleton) {
		assertNotNull(skeleton.getRule());
	}

	protected void assertStatus(int confidence, boolean broken, boolean manual,
			IPSStatus status) throws RodinDBException {
		assertEquals(confidence, status.getConfidence());
		assertEquals(broken, status.isBroken());
		assertEquals(manual, status.getHasManualProof());
	}

}
