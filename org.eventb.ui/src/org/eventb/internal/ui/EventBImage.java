/*******************************************************************************
 * Copyright (c) 2005, 2011 ETH Zurich and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich - initial API and implementation
 *     Systerel - used element description in getImageDescriptor()
 *     Systerel - used eclipse decorator mechanism
 ******************************************************************************/

package org.eventb.internal.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eventb.core.IPSStatus;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.internal.ui.eventbeditor.elementdesc.ElementDescRegistry;
import org.eventb.internal.ui.eventbeditor.elementdesc.IElementDesc;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.IEventBSharedImages;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         A registry for common images used by the workbench which may be
 *         useful to other plug-ins.
 *         <p>
 *         This class provides <code>Image</code> and
 *         <code>ImageDescriptor</code>s for each named image in the
 *         interface. All <code>Image</code> objects provided by this class
 *         are managed by this class and must never be disposed by other
 *         clients.
 *         </p>
 *         <p>
 *         This class is not intended to be extended by clients.
 *         </p>
 */
public class EventBImage {

	// Just for fun :-)
	public static final String IMG_PENGUIN = "Penguine";

	/**
	 * Image IDs for buttons, menu, etc.
	 */
	public static final String IMG_NEW_PROJECT = "New Project";

	public static final String IMG_NEW_COMPONENT = "New Component";

	public static final String IMG_DISCHARGED_SMILEY = "Discharged Smiley";
	
	public static final String IMG_PENDING_SMILEY = "Pending Smiley";

	public static final String IMG_REVIEW_SMILEY = "Review Smiley";

	/**
	 * Returns an image descriptor for the image file within the Event-B UI
	 * Plugin at the given plug-in relative path
	 * <p>
	 * 
	 * @param path
	 *            relative path of the image within this Event-B UI Plugin
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return getImageDescriptor(EventBUIPlugin.PLUGIN_ID, path);
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in and
	 * the relative path within the plugin
	 * <p>
	 * 
	 * @param path
	 *            relative path of the image
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String pluginID,
			String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(pluginID, path);
	}

	/**
	 * Initialialise the image registry. Additional image should be added here
	 * <p>
	 * 
	 * @param registry
	 *            The image registry
	 */
	public static void initializeImageRegistry(ImageRegistry registry) {
		registerImage(registry, IEventBSharedImages.IMG_MACHINE, IEventBSharedImages.IMG_MACHINE_PATH);
		registerImage(registry, IEventBSharedImages.IMG_CONTEXT, "icons/full/obj16/ctx_obj.gif");
		registerImage(registry, IEventBSharedImages.IMG_VARIABLES, "icons/full/obj16/vars_obj.gif");
		registerImage(registry, IEventBSharedImages.IMG_INVARIANTS, "icons/full/obj16/invs_obj.gif");
		registerImage(registry, IEventBSharedImages.IMG_THEOREMS, "icons/full/obj16/thms_obj.gif");
		registerImage(registry, IEventBSharedImages.IMG_EVENTS, "icons/full/obj16/evts_obj.gif");
				
		registerImage(registry, IEventBSharedImages.IMG_CARRIER_SETS,
				"icons/full/obj16/sets_obj.gif");
		registerImage(registry, IEventBSharedImages.IMG_CONSTANTS,
				"icons/full/obj16/csts_obj.gif");
		registerImage(registry, IEventBSharedImages.IMG_AXIOMS,
				"icons/full/obj16/axms_obj.gif");
		registerImage(registry, IMG_NEW_PROJECT,
				"icons/full/clcl16/newprj_wiz.gif");
		registerImage(registry, IMG_NEW_COMPONENT,
				"icons/full/clcl16/newcomp_wiz.gif");

		registerImage(registry, IEventBSharedImages.IMG_PENDING,
				IEventBSharedImages.IMG_PENDING_PATH);
		registerImage(registry, IEventBSharedImages.IMG_PENDING_PALE,
				IEventBSharedImages.IMG_PENDING_PALE_PATH);
		registerImage(registry, IEventBSharedImages.IMG_PENDING_BROKEN,
				IEventBSharedImages.IMG_PENDING_BROKEN_PATH);
		registerImage(registry, IEventBSharedImages.IMG_APPLIED,
				"icons/applied.gif");
		registerImage(registry, IEventBSharedImages.IMG_DISCHARGED,
				IEventBSharedImages.IMG_DISCHARGED_PATH);
		registerImage(registry, IEventBSharedImages.IMG_DISCHARGED_PALE,
				IEventBSharedImages.IMG_DISCHARGED_PALE_PATH);
		registerImage(registry, IEventBSharedImages.IMG_DISCHARGED_BROKEN,
				IEventBSharedImages.IMG_DISCHARGED_BROKEN_PATH);
		registerImage(registry, IEventBSharedImages.IMG_REVIEWED,
				IEventBSharedImages.IMG_REVIEWED_PATH);
		registerImage(registry, IEventBSharedImages.IMG_REVIEWED_PALE,
				IEventBSharedImages.IMG_REVIEWED_PALE_PATH);
		registerImage(registry, IEventBSharedImages.IMG_REVIEWED_BROKEN,
				IEventBSharedImages.IMG_REVIEWED_BROKEN_PATH);
		registerImage(registry, IEventBSharedImages.IMG_DEFAULT,
				"icons/sample.gif");
		registerImage(registry, IEventBSharedImages.IMG_REFINES,
				IEventBSharedImages.IMG_REFINES_PATH);

		// TODO remove constant, icon does not exist
		registerImage(registry, IMG_PENGUIN, "icons/penguins-dancing.gif");
		registerImage(registry, IEventBSharedImages.IMG_NULL,
				IEventBSharedImages.IMG_NULL_PATH);

		registerImage(registry, IEventBSharedImages.IMG_ADD,
				"icons/full/ctool16/add.gif");
		registerImage(registry, IEventBSharedImages.IMG_REMOVE,
				"icons/full/ctool16/remove.gif");

		// FIXME remove, registered twice
		registerImage(registry, IEventBSharedImages.IMG_ADD,
				"icons/full/ctool16/add.gif");
		registerImage(registry, IEventBSharedImages.IMG_REMOVE,
				"icons/full/ctool16/remove.gif");

		registerImage(registry, IEventBSharedImages.IMG_COLLAPSED,
				"icons/full/elcl16/collapsed.gif");
		registerImage(registry, IEventBSharedImages.IMG_COLLAPSED_HOVER,
				"icons/full/elcl16/collapsedHover.gif");
		registerImage(registry, IEventBSharedImages.IMG_EXPANDED,
				"icons/full/elcl16/expanded.gif");
		registerImage(registry, IEventBSharedImages.IMG_EXPANDED_HOVER,
				"icons/full/elcl16/expandedHover.gif");
		registerImage(registry, IEventBSharedImages.IMG_EXPAND_ALL,
				"icons/full/elcl16/expandall.gif");
		registerImage(registry, IEventBSharedImages.IMG_COLLAPSED,
				"icons/full/elcl16/collapsed.gif");
		registerImage(registry, IEventBSharedImages.IMG_COLLAPSED_HOVER,
				"icons/full/elcl16/collapsedHover.gif");
		registerImage(registry, IEventBSharedImages.IMG_COLLAPSE_ALL,
				"icons/full/elcl16/collapseall.gif");
		registerImage(registry, IEventBSharedImages.IMG_EXPANDED,
				"icons/full/elcl16/expanded.gif");
		registerImage(registry, IEventBSharedImages.IMG_EXPANDED_HOVER,
				"icons/full/elcl16/expandedHover.gif");

		registerImage(registry, EventBImage.IMG_DISCHARGED_SMILEY,
				"icons/full/ctool16/wink-green.gif");
		registerImage(registry, EventBImage.IMG_PENDING_SMILEY,
				"icons/full/ctool16/sad.gif");
		registerImage(registry, EventBImage.IMG_REVIEW_SMILEY,
				"icons/full/ctool16/wink-blue.gif");
		
		registerImage(registry, IEventBSharedImages.IMG_INVERSE,
				"icons/full/ctool16/inv_prover.gif");
		registerImage(registry, IEventBSharedImages.IMG_SELECT_ALL,
				"icons/full/ctool16/select_all_prover.gif");
		registerImage(registry, IEventBSharedImages.IMG_SELECT_NONE,
				"icons/full/ctool16/select_none_prover.gif");

		registerImage(registry, IEventBSharedImages.IMG_UP,
				IEventBSharedImages.IMG_UP_PATH);
		registerImage(registry, IEventBSharedImages.IMG_DOWN,
				IEventBSharedImages.IMG_DOWN_PATH);
		registerImage(registry, IEventBSharedImages.IMG_CARRIER_SET,
				IEventBSharedImages.IMG_CARRIER_SET_PATH);
		registerImage(registry, IEventBSharedImages.IMG_VARIABLE,
				IEventBSharedImages.IMG_VARIABLE_PATH);
		registerImage(registry, IEventBSharedImages.IMG_INVARIANT,
				IEventBSharedImages.IMG_INVARIANT_PATH);
		registerImage(registry, IEventBSharedImages.IMG_AXIOM,
				IEventBSharedImages.IMG_AXIOM_PATH);
		registerImage(registry, IEventBSharedImages.IMG_THEOREM,
				IEventBSharedImages.IMG_THEOREM_PATH);
		registerImage(registry, IEventBSharedImages.IMG_CONSTANT,
				IEventBSharedImages.IMG_CONSTANT_PATH);
		registerImage(registry, IEventBSharedImages.IMG_EVENT,
				IEventBSharedImages.IMG_EVENT_PATH);
		
		registerImage(registry, IEventBSharedImages.IMG_GUARD,
				IEventBSharedImages.IMG_GUARD_PATH);
		registerImage(registry, IEventBSharedImages.IMG_PARAMETER,
				IEventBSharedImages.IMG_PARAMETER_PATH);
		registerImage(registry, IEventBSharedImages.IMG_ACTION,
				IEventBSharedImages.IMG_ACTION_PATH);
		registerImage(registry, IEventBSharedImages.IMG_SH_PROVER,
				IEventBSharedImages.IMG_SH_PROVER_PATH);

	}

	/**
	 * Register an image with the image registry
	 * <p>
	 * 
	 * @param registry
	 *            the image registry
	 * @param key
	 *            the key to retrieve the image later
	 * @param path
	 *            the path to the location of the image file within this Event-B
	 *            UI plugin
	 */
	public static void registerImage(ImageRegistry registry, String key,
			String path) {
		ImageDescriptor desc = getImageDescriptor(path);
		registry.put(key, desc);
	}

	/**
	 * Register an image with the image registry
	 * <p>
	 * 
	 * @param registry
	 *            the image reigstry
	 * @param key
	 *            the key to retrieve the image later
	 * @param pluginID
	 *            the id of the plugin where the image can be found
	 * @param path
	 *            the path to the location of the image file within the plugin
	 */
	public static void registerImage(ImageRegistry registry, String key,
			String pluginID, String path) {
		ImageDescriptor desc = getImageDescriptor(pluginID, path);
		registry.put(key, desc);
	}

	/**
	 * Get an image from the image registry with a given key
	 * <p>
	 * 
	 * @param key
	 *            a key (String)
	 * @return an image associated with the input key or null if it does not
	 *         exist
	 */
	public static Image getImage(String key) {
		ImageRegistry registry = EventBUIPlugin.getDefault().getImageRegistry();
		return registry.get(key);
	}

	/**
	 * Getting an image corresponding to a Rodin element.
	 * <p>
	 * 
	 * @param element
	 *            A Rodin element
	 * @return The image for displaying corresponding to the input element
	 */
	public static Image getRodinImage(IRodinElement element) {
		final ImageDescriptor desc = getImageDescriptor(element);
		if (desc == null)
			return null;

		return getImage(desc);
	}

	/**
	 * Get the path of base image corresponding to a proof tree node
	 * <p>
	 * 
	 * @param node
	 *            a proof tree node
	 * @return the path of the base image corresponding to the input node
	 */
	private static final String getProofTreeNodeBaseImageKey(
			IProofTreeNode node) {

		int confidence = node.getConfidence();

		if (confidence == IConfidence.PENDING) {
			if (node.hasChildren())
				return IEventBSharedImages.IMG_PENDING_PALE;
			return IEventBSharedImages.IMG_PENDING;
		}
		if (confidence <= IConfidence.REVIEWED_MAX) {
			if (node.hasChildren())
				return IEventBSharedImages.IMG_REVIEWED_PALE;
			return IEventBSharedImages.IMG_REVIEWED;
		}
		if (confidence <= IConfidence.DISCHARGED_MAX) {
			if (node.hasChildren())
				return IEventBSharedImages.IMG_DISCHARGED_PALE;
			return IEventBSharedImages.IMG_DISCHARGED;
		}
		return IEventBSharedImages.IMG_NULL;
	}

	/**
	 * Get the image corresponding to a proof tree node
	 * <p>
	 * 
	 * @param node
	 *            a proof tree node
	 * @return the image corresponding to the input node
	 */
	public static Image getProofTreeNodeImage(IProofTreeNode node) {
		final String baseKey = getProofTreeNodeBaseImageKey(node);

		// key for image descriptor as OverlayIcon
		final String key = "node:" + baseKey;

		return getOverlayImage(key, baseKey);
	}

	/**
	 * Get the image corresponding with a PRSequent
	 * 
	 * @param status
	 *            a PRSequent
	 * @return the image corresponding to the input sequent
	 */
	public static Image getPRSequentImage(IPSStatus status) {

		final int confidence;
		
		try {
			confidence = status.getConfidence();
		} catch (RodinDBException e) {
			String message = "Cannot get the confidence from the status of"
					+ status.getElementName();
			if (UIUtils.DEBUG) {
				System.out.println(message);
				e.printStackTrace();
			}
			UIUtils.log(e, message);
			return null;
		}

		final String baseKey;
		boolean isAttempted = confidence > IConfidence.UNATTEMPTED;
		if (!isAttempted)
			baseKey = IEventBSharedImages.IMG_PENDING_PALE;
		else {
			boolean isProofBroken = false;
			try {
				isProofBroken = status.isBroken();
			} catch (RodinDBException e) {
				String message = "Cannot check if the proof tree of the sequent "
						+ status.getElementName() + " is brocken or not";
				if (UIUtils.DEBUG) {
					System.out.println(message);
					e.printStackTrace();
				}
				UIUtils.log(e, message);
				return null;
			}
			if (isProofBroken) {
				if (confidence == IConfidence.PENDING) {
					// Do nothing
				}
				baseKey = IEventBSharedImages.IMG_PENDING;
			} else {
				if (confidence == IConfidence.PENDING)
					baseKey = IEventBSharedImages.IMG_PENDING;
				else if (confidence <= IConfidence.REVIEWED_MAX)
					baseKey = IEventBSharedImages.IMG_REVIEWED;
				else if (confidence <= IConfidence.DISCHARGED_MAX)
					baseKey = IEventBSharedImages.IMG_DISCHARGED;
				else // cannot happen
					throw new IllegalStateException(
							"invalid proof confidence: " + confidence);
			}
		}

		// key for image descriptor as OverlayIcon
		final String key = "prsequent:" + baseKey;
		
		return getOverlayImage(key, baseKey);
	}

	// returns an image as OverlayIcon, registering it if needed 
	// an image must already be registered under baseKey
	private static Image getOverlayImage(String key, String baseKey) {
		final ImageRegistry registry = EventBUIPlugin.getDefault()
				.getImageRegistry();
		Image image = registry.get(key);
		if (image == null) {
			if (UIUtils.DEBUG)
				System.out.println("Create a new image: " + key);
			final ImageDescriptor descriptor = registry.getDescriptor(baseKey);
			if (descriptor == null) {
				throw new IllegalStateException("unknown image descriptor: "
						+ baseKey);
			}
			final OverlayIcon icon = new OverlayIcon(descriptor);
			image = icon.createImage();
			registry.put(key, image);
		}

		return image;
	}

	public static ImageDescriptor getImageDescriptor(IRodinElement element) {
		final IElementDesc elementDesc = ElementDescRegistry.getInstance()
				.getElementDesc(element.getElementType());
		return elementDesc.getImageProvider().getImageDescriptor(element);
	}

	public static Image getImage(ImageDescriptor desc) {
		final String key = "desc:" + desc;

		final ImageRegistry imageRegistry = EventBUIPlugin.getDefault()
				.getImageRegistry();
		Image image = imageRegistry.get(key);
		if (image == null) {
			if (UIUtils.DEBUG)
				System.out.println("Create a new image: " + key);
			OverlayIcon icon = new OverlayIcon(desc);
			image = icon.createImage();
			imageRegistry.put(key, image);
		}
		return image;

	}
}
