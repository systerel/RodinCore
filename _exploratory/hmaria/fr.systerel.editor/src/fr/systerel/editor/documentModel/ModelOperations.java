/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.documentModel;

import org.eclipse.core.runtime.Assert;
import org.rodinp.core.emf.api.itf.ILElement;

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

		public final ModelPosition modelPos;

		public ModelOperation(ModelPosition modelPos) {
			this.modelPos = modelPos;
		}

		public boolean perform(ILElement... elements) {
			for (ILElement element : elements) {
				if (element.isImplicit()) {
					return false;
				}
				final boolean success = applyTo(element);
				if (!success) {
					return false;
				}
			}
			return true;
		}

		protected abstract boolean applyTo(ILElement element);
	}

	public static class Move extends ModelOperation {

		public Move(ModelPosition modelPos) {
			super(modelPos);
		}

		protected boolean applyTo(ILElement element) {
			if (modelPos.targetParent.equals(element.getParent())) {
				final int oldPos = modelPos.targetParent
						.getChildPosition(element);
				final int newPos = computeNewPos(modelPos, oldPos);
				modelPos.targetParent.moveChild(newPos, oldPos);
			} else {
				modelPos.targetParent.addChild(element, modelPos.nextSibling);
			}
			return true;
		}

		private int computeNewPos(ModelPosition modelPos, int oldPos) {
			if (modelPos.nextSibling == null) {
				return modelPos.targetParent.getChildren().size() - 1;
			} else {
				final int siblingPos = modelPos.targetParent
						.getChildPosition(modelPos.nextSibling);
				if (oldPos < siblingPos) {
					return siblingPos - 1;
				} else {
					return siblingPos;
				}
			}
		}
	}

}
