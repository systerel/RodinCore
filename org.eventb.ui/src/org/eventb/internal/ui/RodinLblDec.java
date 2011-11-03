/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui;

import static org.eventb.internal.ui.EventBImage.getImageDescriptor;
import static org.eventb.ui.IEventBSharedImages.IMG_COMMENT_OVERLAY_PATH;
import static org.eventb.ui.IEventBSharedImages.IMG_ERROR_OVERLAY_PATH;
import static org.eventb.ui.IEventBSharedImages.IMG_WARNING_OVERLAY_PATH;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eventb.core.ICommentedElement;
import org.eventb.core.IPSStatus;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.core.seqprover.IProofSkeleton;
import org.eventb.internal.ui.markers.IMarkerRegistry;
import org.eventb.internal.ui.markers.MarkerRegistry;
import org.eventb.ui.IEventBSharedImages;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Nicolas Beauger
 * 
 */
public class RodinLblDec {

	public static abstract class DefaultLblDec implements
			ILightweightLabelDecorator {

		@Override
		public void addListener(ILabelProviderListener listener) {
			// do nothing
		}

		@Override
		public void dispose() {
			// do nothing
		}

		@Override
		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		@Override
		public void removeListener(ILabelProviderListener listener) {
			// do nothing
		}

	}

	public static class StatusDec extends DefaultLblDec {

		private static final int F_AUTO = 0x00001;

		private static final int F_INACCURATE = 0x00002;

		private static final int F_REVIEWED_BROKEN = 0x00004;

		private static final int F_DISCHARGED_BROKEN = 0x00008;

		private static int getOverlay(IPSStatus status) throws RodinDBException {
			final int confidence = status.getConfidence();

			int overlay = 0;

			final boolean isAttempted = confidence > IConfidence.UNATTEMPTED;
			if (isAttempted) {
				final boolean isProofBroken = status.isBroken();
				if (isProofBroken) {
					if (confidence == IConfidence.PENDING) {
						// Do nothing
					} else if (confidence <= IConfidence.REVIEWED_MAX)
						overlay = overlay | F_REVIEWED_BROKEN;
					else if (confidence <= IConfidence.DISCHARGED_MAX)
						overlay = overlay | F_DISCHARGED_BROKEN;
				}
			}

			final boolean isAutomatic = !status.getHasManualProof();
			if (isAutomatic && isAttempted) {
				overlay = overlay | F_AUTO;
			}

			final boolean isAccurate = status.getPOSequent().isAccurate();
			if (!isAccurate) {
				overlay = overlay | F_INACCURATE;
			}

			return overlay;
		}

		@Override
		public void decorate(Object element, IDecoration decoration) {
			if (!(element instanceof IPSStatus))
				return;
			try {
				final int overlay = getOverlay((IPSStatus) element);
				if ((overlay & F_AUTO) != 0)
					decoration
							.addOverlay(
									getImageDescriptor(IEventBSharedImages.IMG_AUTO_OVERLAY_PATH),
									IDecoration.TOP_RIGHT);
				if ((overlay & F_INACCURATE) != 0)
					decoration
							.addOverlay(
									getImageDescriptor(IEventBSharedImages.IMG_WARNING_OVERLAY_PATH),
									IDecoration.BOTTOM_LEFT);
				if ((overlay & F_REVIEWED_BROKEN) != 0) {
					decoration
							.addOverlay(
									getImageDescriptor(IEventBSharedImages.IMG_REVIEWED_OVERLAY_PATH),
									IDecoration.BOTTOM_RIGHT);
				}
				if ((overlay & F_DISCHARGED_BROKEN) != 0) {
					decoration
							.addOverlay(
									getImageDescriptor(IEventBSharedImages.IMG_DISCHARGED_OVERLAY_PATH),
									IDecoration.BOTTOM_RIGHT);
				}
			} catch (RodinDBException e) {
				// Do nothing
				if (UIUtils.DEBUG)
					e.printStackTrace();
			}
		}
	}

	public static abstract class SimpleDec extends DefaultLblDec {

		private final String ovrPath;

		public SimpleDec(String ovrPath) {
			this.ovrPath = ovrPath;
		}

		protected abstract boolean isDecorated(Object element)
				throws CoreException;

		@Override
		public void decorate(Object element, IDecoration decoration) {
			try {
				if (isDecorated(element)) {
					decoration.addOverlay(getImageDescriptor(ovrPath));
				}
			} catch (CoreException e) {
				// Do nothing
				if (UIUtils.DEBUG)
					e.printStackTrace();
			}

		}

	}

	public static class CommentDec extends SimpleDec {

		public CommentDec() {
			super(IMG_COMMENT_OVERLAY_PATH);
		}

		private boolean isDecorated(ICommentedElement ce)
				throws RodinDBException {
			return ce.hasComment() && !ce.getComment().isEmpty();
		}

		private boolean isDecorated(IProofSkeleton ps) {
			return !ps.getComment().isEmpty();
		}

		@Override
		protected boolean isDecorated(Object element) throws CoreException {
			if (element instanceof ICommentedElement) {
				return isDecorated((ICommentedElement) element);
			}
			if (element instanceof IProofSkeleton) {
				return isDecorated((IProofSkeleton) element);
			}
			return false;
		}

	}

	public static abstract class RodinElemLblDec extends SimpleDec {

		public RodinElemLblDec(String ovrPath) {
			super(ovrPath);
		}

		protected abstract boolean isDecorated(IRodinElement element)
				throws CoreException;

		@Override
		protected boolean isDecorated(Object element) throws CoreException {
			if (!(element instanceof IRodinElement))
				return false;
			final IRodinElement elem = (IRodinElement) element;

			return isDecorated(elem);
		}
	}

	public static abstract class RodinSeverityLblDec extends RodinElemLblDec {

		private final int decSeverity;

		public RodinSeverityLblDec(String ovrPath, int decSeverity) {
			super(ovrPath);
			this.decSeverity = decSeverity;
		}

		@Override
		protected boolean isDecorated(IRodinElement element)
				throws CoreException {
			final IMarkerRegistry registry = MarkerRegistry.getDefault();
			
			try {
				final int severity = registry.getMaxMarkerSeverity(element);
				return severity == decSeverity;
			} catch (CoreException e) {
				return false;
			}
		}
	}

	public static class WarningDec extends RodinSeverityLblDec {

		public WarningDec() {
			super(IMG_WARNING_OVERLAY_PATH, IMarker.SEVERITY_WARNING);
		}
	}

	public static class ErrorDec extends RodinSeverityLblDec {

		public ErrorDec() {
			super(IMG_ERROR_OVERLAY_PATH, IMarker.SEVERITY_ERROR);
		}
	}

}
