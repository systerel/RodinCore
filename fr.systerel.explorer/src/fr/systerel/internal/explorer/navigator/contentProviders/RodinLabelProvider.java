/*******************************************************************************
 * Copyright (c) 2008, 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/

package fr.systerel.internal.explorer.navigator.contentProviders;

import org.eclipse.core.resources.IContainer;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IEvent;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.IInvariant;
import org.eventb.core.ILabeledElement;
import org.eventb.core.IPSStatus;
import org.eventb.core.IVariable;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.OverlayIcon;
import org.eventb.internal.ui.UIUtils;
import org.eventb.ui.IEventBSharedImages;
import org.rodinp.core.IElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

import fr.systerel.explorer.IElementNode;
import fr.systerel.internal.explorer.model.IModelElement;
import fr.systerel.internal.explorer.model.ModelController;
import fr.systerel.internal.explorer.model.ModelElementNode;
import fr.systerel.internal.explorer.model.ModelPOContainer;
import fr.systerel.internal.explorer.statistics.IStatistics;
import fr.systerel.internal.explorer.statistics.Statistics;

/**
 * This class provides labels to all <code>ContentProvider</code> classes.
 */
public class RodinLabelProvider implements ILabelProvider {

	public Image getImage(Object element) {
		final Image image = getIcon(element);
		if (image == null)
			return null;

		final String ovrName = getOverlayIcon(element);
		if (ovrName == null)
			return image;

		final ImageDescriptor desc = ImageDescriptor.createFromImage(image);
		final OverlayIcon ovrIcon = new OverlayIcon(desc);
		ovrIcon.addBottomRight(EventBImage.getImageDescriptor(ovrName));
		return ovrIcon.createImage();
	}

	private Image getIcon(Object element) {

		if (element instanceof IPSStatus) {
			IPSStatus status = ((IPSStatus) element);
			return EventBImage.getPRSequentImage(status);
		}
		if (element instanceof IRodinElement) {
			return EventBImage.getRodinImage((IRodinElement) element);

		} else if (element instanceof IElementNode) {
			IElementNode node = (IElementNode) element;

			if (node.getChildrenType().equals(IInvariant.ELEMENT_TYPE)) {
				return EventBImage.getImage(IEventBSharedImages.IMG_INVARIANT);
			}
			if (node.getChildrenType().equals(IEvent.ELEMENT_TYPE)) {
				return EventBImage.getImage(IEventBSharedImages.IMG_EVENT);
			}
			if (node.getChildrenType().equals(IVariable.ELEMENT_TYPE)) {
				return EventBImage.getImage(IEventBSharedImages.IMG_VARIABLE);
			}
			if (node.getChildrenType().equals(IAxiom.ELEMENT_TYPE)) {
				return EventBImage.getImage(IEventBSharedImages.IMG_AXIOM);
			}
			if (node.getChildrenType().equals(ICarrierSet.ELEMENT_TYPE)) {
				return EventBImage
						.getImage(IEventBSharedImages.IMG_CARRIER_SET);
			}
			if (node.getChildrenType().equals(IConstant.ELEMENT_TYPE)) {
				return EventBImage.getImage(IEventBSharedImages.IMG_CONSTANT);
			}
			if (node.getChildrenType().equals(IPSStatus.ELEMENT_TYPE)) {
				ModelPOContainer parent = ((ModelElementNode) node)
						.getModelParent();
				boolean discharged = parent.getMinConfidence() > IConfidence.REVIEWED_MAX;
				boolean reviewed = parent.getMinConfidence() > IConfidence.PENDING;

				if (discharged) {
					return EventBImage
							.getImage(IEventBSharedImages.IMG_DISCHARGED);
				} else if (reviewed) {
					return EventBImage
							.getImage(IEventBSharedImages.IMG_REVIEWED);
				} else {
					return EventBImage
							.getImage(IEventBSharedImages.IMG_PENDING);
				}
			}

		} else if (element instanceof IContainer) {
			return PlatformUI.getWorkbench().getSharedImages().getImage(
					ISharedImages.IMG_OBJS_INFO_TSK);
		}
		return null;
	}

	public String getText(Object obj) {
		if (obj instanceof ILabeledElement) {
			try {
				return ((ILabeledElement) obj).getLabel();
			} catch (RodinDBException e) {
				UIUtils.log(e, "when getting label for " +obj);
			}
		} else if (obj instanceof IIdentifierElement) {
			try {
				return ((IIdentifierElement) obj).getIdentifierString();
			} catch (RodinDBException e) {
				UIUtils.log(e, "when getting identifier for " +obj);
			}
		} else if (obj instanceof IRodinElement) {
			return ((IRodinElement) obj).getElementName();

		} else if (obj instanceof ModelPOContainer) {
			return ModelPOContainer.DISPLAY_NAME;

		} else if (obj instanceof IElementNode) {
			return ((IElementNode) obj).getLabel();

		} else if (obj instanceof IContainer) {
			return ((IContainer) obj).getName();
		}
		return obj.toString();
	}

	private String getOverlayIcon(Object obj) {
		if (!hasStatistics(obj))
			return null;
        // Proof status doesn't need overlay icon
		if (obj instanceof IElementNode) {
			final IElementNode node = (IElementNode) obj;
			if (node.getChildrenType().equals(IPSStatus.ELEMENT_TYPE))
				return null;
		}

		final IModelElement model = ModelController.getModelElement(obj);
		if (model == null)
			return null;

		final IStatistics s = new Statistics(model);
		if (s.getUndischargedRest() > 0) {
			return IEventBSharedImages.IMG_PENDING_OVERLAY_PATH;
		} else if (s.getReviewed() > 0) {
			return IEventBSharedImages.IMG_REVIEWED_OVERLAY_PATH;
		} else {
			return null;
		}
	}

	private boolean hasStatistics(Object obj) {
		if (obj instanceof IConstant || obj instanceof ICarrierSet
				|| obj instanceof IVariable)
			return false;
		if (obj instanceof IElementNode) {
			final IElementNode node = (IElementNode) obj;
			final IElementType<?> type = node.getChildrenType();
			return (type != IConstant.ELEMENT_TYPE
					&& type != ICarrierSet.ELEMENT_TYPE && type != IVariable.ELEMENT_TYPE);
		}
		return true;
	}

	public void addListener(ILabelProviderListener listener) {
		// do nothing

	}

	public void dispose() {
		// do nothing

	}

	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
		// do nothing

	}

}
