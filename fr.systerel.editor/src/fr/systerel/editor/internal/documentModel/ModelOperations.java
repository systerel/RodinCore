/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.internal.documentModel;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.emf.api.itf.ILElement;

import fr.systerel.editor.internal.actions.operations.RodinOperationUtils;

/**
 * @author Nicolas Beauger
 * 
 */
public class ModelOperations {

	public static class ModelPosition {
		public final ILElement targetParent;
		public final ILElement nextSibling;

		/**
		 * Model position constructor.
		 * 
		 * @param targetParent
		 *            parent of the position, never <code>null</code>
		 * @param nextSibling
		 *            next sibling of the position, can be <code>null</code>,
		 *            meaning this position is the last child. If not
		 *            <code>null</code>, the next sibling's parent must be the
		 *            given parent
		 */
		public ModelPosition(ILElement targetParent, ILElement nextSibling) {
			Assert.isNotNull(targetParent);
			if (nextSibling != null) {
				Assert.isLegal(
						targetParent.equals(nextSibling.getParent()),
						"illegal model position: parent= "
								+ targetParent.getElement() + " next sibling= "
								+ nextSibling.getElement());
			}
			this.targetParent = targetParent;
			this.nextSibling = nextSibling;
		}

	}

	public static abstract class ModelOperation {

		private final ModelPosition pos;

		public ModelOperation(ModelPosition modelPos) {
			this.pos = modelPos;
		}

		public boolean perform(List<ILElement> elems) {
			final List<ILElement> toHandle = new ArrayList<ILElement>(elems);
			if (toHandle.isEmpty())
				return true;
			final int lastElemIndex = toHandle.size() - 1;
			final ILElement il = toHandle.get(lastElemIndex);
			boolean success = applyTo(il, pos);
			toHandle.remove(il);
			ModelPosition newPos = new ModelPosition(pos.targetParent, il);
			if (success) {
				for (int i = toHandle.size() - 1; i >= 0; i--) {
					final ILElement lastMoved = toHandle.get(i);
					success = applyTo(lastMoved, newPos);
					if (!success)
						break;
					newPos = new ModelPosition(pos.targetParent, lastMoved);
				}
			}
			return success;
		}

		protected abstract boolean applyTo(ILElement element, ModelPosition pos);
	}

	public static class Move extends ModelOperation {

		public Move(ModelPosition modelPos) {
			super(modelPos);
		}

		@Override
		protected boolean applyTo(ILElement element, ModelPosition pos) {
			final ILElement targetParent = pos.targetParent;
			final IInternalElement nextSibling = pos.nextSibling == null ? null
					: pos.nextSibling.getElement();
			RodinOperationUtils.move(targetParent.getElement(),
					element.getElement(), nextSibling);
			return true;
		}
	
	}
	
}
