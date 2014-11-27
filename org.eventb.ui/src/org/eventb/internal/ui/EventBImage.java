/*******************************************************************************
 * Copyright (c) 2005, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - used element description in getImageDescriptor()
 *     Systerel - used eclipse decorator mechanism
 *******************************************************************************/
package org.eventb.internal.ui;

import static org.eventb.core.seqprover.ProverLib.isDischarged;
import static org.eventb.core.seqprover.ProverLib.isPending;
import static org.eventb.core.seqprover.ProverLib.isReviewed;
import static org.eventb.core.seqprover.ProverLib.isUncertain;
import static org.eventb.ui.IEventBSharedImages.IMG_ACTION;
import static org.eventb.ui.IEventBSharedImages.IMG_ACTION_PATH;
import static org.eventb.ui.IEventBSharedImages.IMG_ADD;
import static org.eventb.ui.IEventBSharedImages.IMG_AXIOM;
import static org.eventb.ui.IEventBSharedImages.IMG_AXIOMS;
import static org.eventb.ui.IEventBSharedImages.IMG_AXIOM_PATH;
import static org.eventb.ui.IEventBSharedImages.IMG_CARRIER_SET;
import static org.eventb.ui.IEventBSharedImages.IMG_CARRIER_SETS;
import static org.eventb.ui.IEventBSharedImages.IMG_CARRIER_SET_PATH;
import static org.eventb.ui.IEventBSharedImages.IMG_COLLAPSED;
import static org.eventb.ui.IEventBSharedImages.IMG_COLLAPSED_HOVER;
import static org.eventb.ui.IEventBSharedImages.IMG_COLLAPSE_ALL;
import static org.eventb.ui.IEventBSharedImages.IMG_CONSTANT;
import static org.eventb.ui.IEventBSharedImages.IMG_CONSTANTS;
import static org.eventb.ui.IEventBSharedImages.IMG_CONSTANT_PATH;
import static org.eventb.ui.IEventBSharedImages.IMG_CONTEXT;
import static org.eventb.ui.IEventBSharedImages.IMG_DEFAULT;
import static org.eventb.ui.IEventBSharedImages.IMG_DISCHARGED;
import static org.eventb.ui.IEventBSharedImages.IMG_DISCHARGED_BROKEN;
import static org.eventb.ui.IEventBSharedImages.IMG_DISCHARGED_BROKEN_PATH;
import static org.eventb.ui.IEventBSharedImages.IMG_DISCHARGED_PALE;
import static org.eventb.ui.IEventBSharedImages.IMG_DISCHARGED_PALE_PATH;
import static org.eventb.ui.IEventBSharedImages.IMG_DISCHARGED_PATH;
import static org.eventb.ui.IEventBSharedImages.IMG_DOWN;
import static org.eventb.ui.IEventBSharedImages.IMG_DOWN_PATH;
import static org.eventb.ui.IEventBSharedImages.IMG_EVENT;
import static org.eventb.ui.IEventBSharedImages.IMG_EVENTS;
import static org.eventb.ui.IEventBSharedImages.IMG_EVENT_PATH;
import static org.eventb.ui.IEventBSharedImages.IMG_EXPANDED;
import static org.eventb.ui.IEventBSharedImages.IMG_EXPANDED_HOVER;
import static org.eventb.ui.IEventBSharedImages.IMG_EXPAND_ALL;
import static org.eventb.ui.IEventBSharedImages.IMG_GUARD;
import static org.eventb.ui.IEventBSharedImages.IMG_GUARD_PATH;
import static org.eventb.ui.IEventBSharedImages.IMG_INVARIANT;
import static org.eventb.ui.IEventBSharedImages.IMG_INVARIANTS;
import static org.eventb.ui.IEventBSharedImages.IMG_INVARIANT_PATH;
import static org.eventb.ui.IEventBSharedImages.IMG_INVERSE;
import static org.eventb.ui.IEventBSharedImages.IMG_MACHINE;
import static org.eventb.ui.IEventBSharedImages.IMG_MACHINE_PATH;
import static org.eventb.ui.IEventBSharedImages.IMG_NULL;
import static org.eventb.ui.IEventBSharedImages.IMG_NULL_PATH;
import static org.eventb.ui.IEventBSharedImages.IMG_PARAMETER;
import static org.eventb.ui.IEventBSharedImages.IMG_PARAMETER_PATH;
import static org.eventb.ui.IEventBSharedImages.IMG_PENDING;
import static org.eventb.ui.IEventBSharedImages.IMG_PENDING_BROKEN;
import static org.eventb.ui.IEventBSharedImages.IMG_PENDING_BROKEN_PATH;
import static org.eventb.ui.IEventBSharedImages.IMG_PENDING_PALE;
import static org.eventb.ui.IEventBSharedImages.IMG_PENDING_PALE_PATH;
import static org.eventb.ui.IEventBSharedImages.IMG_PENDING_PATH;
import static org.eventb.ui.IEventBSharedImages.IMG_REFINES;
import static org.eventb.ui.IEventBSharedImages.IMG_REFINES_PATH;
import static org.eventb.ui.IEventBSharedImages.IMG_REMOVE;
import static org.eventb.ui.IEventBSharedImages.IMG_REVIEWED;
import static org.eventb.ui.IEventBSharedImages.IMG_REVIEWED_BROKEN;
import static org.eventb.ui.IEventBSharedImages.IMG_REVIEWED_BROKEN_PATH;
import static org.eventb.ui.IEventBSharedImages.IMG_REVIEWED_PALE;
import static org.eventb.ui.IEventBSharedImages.IMG_REVIEWED_PALE_PATH;
import static org.eventb.ui.IEventBSharedImages.IMG_REVIEWED_PATH;
import static org.eventb.ui.IEventBSharedImages.IMG_SELECT_ALL;
import static org.eventb.ui.IEventBSharedImages.IMG_SELECT_NONE;
import static org.eventb.ui.IEventBSharedImages.IMG_SH_PROVER;
import static org.eventb.ui.IEventBSharedImages.IMG_SH_PROVER_PATH;
import static org.eventb.ui.IEventBSharedImages.IMG_THEOREM;
import static org.eventb.ui.IEventBSharedImages.IMG_THEOREMS;
import static org.eventb.ui.IEventBSharedImages.IMG_THEOREM_PATH;
import static org.eventb.ui.IEventBSharedImages.IMG_UNCERTAIN;
import static org.eventb.ui.IEventBSharedImages.IMG_UNCERTAIN_PALE;
import static org.eventb.ui.IEventBSharedImages.IMG_UNCERTAIN_PALE_PATH;
import static org.eventb.ui.IEventBSharedImages.IMG_UNCERTAIN_PATH;
import static org.eventb.ui.IEventBSharedImages.IMG_UP;
import static org.eventb.ui.IEventBSharedImages.IMG_UP_PATH;
import static org.eventb.ui.IEventBSharedImages.IMG_VARIABLE;
import static org.eventb.ui.IEventBSharedImages.IMG_VARIABLES;
import static org.eventb.ui.IEventBSharedImages.IMG_VARIABLE_PATH;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eventb.core.IPSStatus;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.internal.ui.eventbeditor.elementdesc.ElementDesc;
import org.eventb.internal.ui.eventbeditor.elementdesc.ElementDescRegistry;
import org.eventb.ui.EventBUIPlugin;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         A registry for common images used by the workbench which may be
 *         useful to other plug-ins.
 *         <p>
 *         This class provides <code>Image</code> and
 *         <code>ImageDescriptor</code>s for each named image in the interface.
 *         All <code>Image</code> objects provided by this class are managed by
 *         this class and must never be disposed by other clients.
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

	public static final String IMG_UNCERTAIN_SMILEY = "Uncertain Smiley";

	public static final String IMG_REVIEW_SMILEY = "Review Smiley";

	/**
	 * Returns an image descriptor for the image file within the Event-B UI
	 * Plugin at the given plug-in relative path
	 * <p>
	 * 
	 * @param path
	 *            relative path of the image within this Event-B UI Plugin
	 * @return the image descriptor, or <code>null</code> if the image could not
	 *         be found
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
	 * @return the image descriptor, or <code>null</code> if the image could not
	 *         be found
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
		registerImage(registry, IMG_MACHINE, IMG_MACHINE_PATH);
		registerImage(registry, IMG_CONTEXT, "icons/full/obj16/ctx_obj.gif");
		registerImage(registry, IMG_VARIABLES, "icons/full/obj16/vars_obj.gif");
		registerImage(registry, IMG_INVARIANTS, "icons/full/obj16/invs_obj.gif");
		registerImage(registry, IMG_THEOREMS, "icons/full/obj16/thms_obj.gif");
		registerImage(registry, IMG_EVENTS, "icons/full/obj16/evts_obj.gif");

		registerImage(registry, IMG_CARRIER_SETS,
				"icons/full/obj16/sets_obj.gif");
		registerImage(registry, IMG_CONSTANTS, "icons/full/obj16/csts_obj.gif");
		registerImage(registry, IMG_AXIOMS, "icons/full/obj16/axms_obj.gif");
		registerImage(registry, IMG_NEW_PROJECT,
				"icons/full/clcl16/newprj_wiz.gif");
		registerImage(registry, IMG_NEW_COMPONENT,
				"icons/full/clcl16/newcomp_wiz.gif");

		registerImage(registry, IMG_PENDING, IMG_PENDING_PATH);
		registerImage(registry, IMG_PENDING_PALE, IMG_PENDING_PALE_PATH);
		registerImage(registry, IMG_PENDING_BROKEN, IMG_PENDING_BROKEN_PATH);
		registerImage(registry, IMG_DISCHARGED, IMG_DISCHARGED_PATH);
		registerImage(registry, IMG_DISCHARGED_PALE, IMG_DISCHARGED_PALE_PATH);
		registerImage(registry, IMG_DISCHARGED_BROKEN,
				IMG_DISCHARGED_BROKEN_PATH);
		registerImage(registry, IMG_UNCERTAIN, IMG_UNCERTAIN_PATH);
		registerImage(registry, IMG_UNCERTAIN_PALE, IMG_UNCERTAIN_PALE_PATH);
		registerImage(registry, IMG_REVIEWED, IMG_REVIEWED_PATH);
		registerImage(registry, IMG_REVIEWED_PALE, IMG_REVIEWED_PALE_PATH);
		registerImage(registry, IMG_REVIEWED_BROKEN, IMG_REVIEWED_BROKEN_PATH);
		registerImage(registry, IMG_DEFAULT, "icons/sample.gif");
		registerImage(registry, IMG_REFINES, IMG_REFINES_PATH);

		registerImage(registry, IMG_NULL, IMG_NULL_PATH);

		registerImage(registry, IMG_ADD, "icons/full/ctool16/add.gif");
		registerImage(registry, IMG_REMOVE, "icons/full/ctool16/remove.gif");

		registerImage(registry, IMG_COLLAPSED,
				"icons/full/elcl16/collapsed.gif");
		registerImage(registry, IMG_COLLAPSED_HOVER,
				"icons/full/elcl16/collapsedHover.gif");
		registerImage(registry, IMG_EXPANDED, "icons/full/elcl16/expanded.gif");
		registerImage(registry, IMG_EXPANDED_HOVER,
				"icons/full/elcl16/expandedHover.gif");
		registerImage(registry, IMG_EXPAND_ALL,
				"icons/full/elcl16/expandall.gif");
		registerImage(registry, IMG_COLLAPSED,
				"icons/full/elcl16/collapsed.gif");
		registerImage(registry, IMG_COLLAPSED_HOVER,
				"icons/full/elcl16/collapsedHover.gif");
		registerImage(registry, IMG_COLLAPSE_ALL,
				"icons/full/elcl16/collapseall.gif");
		registerImage(registry, IMG_EXPANDED, "icons/full/elcl16/expanded.gif");
		registerImage(registry, IMG_EXPANDED_HOVER,
				"icons/full/elcl16/expandedHover.gif");

		registerImage(registry, EventBImage.IMG_DISCHARGED_SMILEY,
				"icons/full/ctool16/wink-green.gif");
		registerImage(registry, EventBImage.IMG_PENDING_SMILEY,
				"icons/full/ctool16/sad.gif");
		registerImage(registry, EventBImage.IMG_UNCERTAIN_SMILEY,
				"icons/full/ctool16/uncertain.gif");
		registerImage(registry, EventBImage.IMG_REVIEW_SMILEY,
				"icons/full/ctool16/wink-blue.gif");

		registerImage(registry, IMG_INVERSE,
				"icons/full/ctool16/inv_prover.gif");
		registerImage(registry, IMG_SELECT_ALL,
				"icons/full/ctool16/select_all_prover.gif");
		registerImage(registry, IMG_SELECT_NONE,
				"icons/full/ctool16/select_none_prover.gif");

		registerImage(registry, IMG_UP, IMG_UP_PATH);
		registerImage(registry, IMG_DOWN, IMG_DOWN_PATH);
		registerImage(registry, IMG_CARRIER_SET, IMG_CARRIER_SET_PATH);
		registerImage(registry, IMG_VARIABLE, IMG_VARIABLE_PATH);
		registerImage(registry, IMG_INVARIANT, IMG_INVARIANT_PATH);
		registerImage(registry, IMG_AXIOM, IMG_AXIOM_PATH);
		registerImage(registry, IMG_THEOREM, IMG_THEOREM_PATH);
		registerImage(registry, IMG_CONSTANT, IMG_CONSTANT_PATH);
		registerImage(registry, IMG_EVENT, IMG_EVENT_PATH);

		registerImage(registry, IMG_GUARD, IMG_GUARD_PATH);
		registerImage(registry, IMG_PARAMETER, IMG_PARAMETER_PATH);
		registerImage(registry, IMG_ACTION, IMG_ACTION_PATH);
		registerImage(registry, IMG_SH_PROVER, IMG_SH_PROVER_PATH);

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
	private static final String getProofTreeNodeBaseImageKey(IProofTreeNode node) {

		int confidence = node.getConfidence();

		if (isPending(confidence)) {
			if (node.hasChildren())
				return IMG_PENDING_PALE;
			return IMG_PENDING;
		}
		if (isUncertain(confidence)) {
			if (isUncertain(node.getRuleConfidence())) {
				return IMG_UNCERTAIN;
			}
			return IMG_UNCERTAIN_PALE;
		}
		if (isReviewed(confidence)) {
			if (node.hasChildren())
				return IMG_REVIEWED_PALE;
			return IMG_REVIEWED;
		}
		if (isDischarged(confidence)) {
			if (node.hasChildren())
				return IMG_DISCHARGED_PALE;
			return IMG_DISCHARGED;
		}
		return IMG_NULL;
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
			baseKey = IMG_PENDING_PALE;
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
				baseKey = IMG_PENDING;
			} else {
				if (isPending(confidence) || isUncertain(confidence))
					baseKey = IMG_PENDING;
				else if (isReviewed(confidence))
					baseKey = IMG_REVIEWED;
				else if (isDischarged(confidence))
					baseKey = IMG_DISCHARGED;
				else
					// cannot happen
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
		final ElementDesc elementDesc = ElementDescRegistry.getInstance()
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
