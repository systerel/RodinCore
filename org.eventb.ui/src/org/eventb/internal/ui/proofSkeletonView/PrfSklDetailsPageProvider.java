/*******************************************************************************
 * Copyright (c) 2008, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.proofSkeletonView;

import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IDetailsPageProvider;
import org.eventb.core.seqprover.IProofTreeNode;

/**
 * DetailsPageProvider for the proof skeleton viewer. It filters on
 * IProofTreeNode instances and associates with a SequentDetailsPage. The page
 * provider is required to avoid discouraged access to internal class
 * ProofTreeNode, which is the actual default key.
 * 
 * @author Nicolas Beauger
 * 
 */
public class PrfSklDetailsPageProvider implements IDetailsPageProvider {

	private static PrfSklDetailsPageProvider instance;

	private PrfSklDetailsPageProvider() {
		// Singleton : private constructor
	}

	public static PrfSklDetailsPageProvider getDefault() {
		if (instance == null) {
			instance = new PrfSklDetailsPageProvider();
		}
		return instance;
	}

	@Override
	public Object getPageKey(Object object) {
		if (object instanceof IProofTreeNode) {
			return IProofTreeNode.class;
		}
		return null;
	}

	@Override
	public IDetailsPage getPage(Object key) {
		if (key == IProofTreeNode.class) {
			return SequentDetailsPage.getDefault();
		}
		return null;
	}

}
