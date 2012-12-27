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

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.utils.Messages;

/**
 * LabelProvider for the proof skeleton viewer. 
 * @author Nicolas Beauger
 *
 */
public class PrfSklLabelProvider extends LabelProvider {

	public PrfSklLabelProvider() {
		// Do nothing
	}
	
	@Override
	public String getText(Object element) {
		if (element instanceof IProofTreeNode) {
			final IProofRule rule = ((IProofTreeNode) element).getRule();
			if (rule == null) {
				return Messages.proofskeleton_pendingnode;
			}
			return rule.getDisplayName();
		}
		return super.getText(element);
	}
	
	@Override
	public Image getImage(Object element) {
		if (element instanceof IProofTreeNode) {
			return EventBImage
					.getProofTreeNodeImage((IProofTreeNode) element);
		}
		return super.getImage(element);
	}

}
