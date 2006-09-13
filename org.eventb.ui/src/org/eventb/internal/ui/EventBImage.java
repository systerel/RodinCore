/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eventb.core.ICommentedElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This class handles the images that are used in the Event-B UI.
 */
public class EventBImage {

	/**
	 * Image IDs for RODIN Elements
	 */
	public static final String IMG_MACHINE = "IMachine";

	public static final String IMG_CONTEXT = "IContext";

	public static final String IMG_VARIABLES = "Variables";

	public static final String IMG_INVARIANTS = "Invariants";

	public static final String IMG_THEOREMS = "Theorems";

	public static final String IMG_EVENTS = "Events";

	public static final String IMG_CARRIER_SETS = "CarrierSets";

	public static final String IMG_CONSTANTS = "Constants";

	public static final String IMG_AXIOMS = "Axioms";

	public static final String IMG_DISCHARGED = "Discharged";

	public static final String IMG_DISCHARGED_BROKEN = "Discharged Broken";

	public static final String IMG_PENDING = "Pending";

	public static final String IMG_PENDING_BROKEN = "Pending Broken";

	public static final String IMG_REVIEWED = "REVIEWED";

	public static final String IMG_REVIEWED_BROKEN = "Reviewed Broken";

	public static final String IMG_APPLIED = "Applied";

	public static final String IMG_UNATTEMPTED = "Unattempted";

	public static final String IMG_DEFAULT = "Default";

	public static final String IMG_REFINES = "Refines";
	
	public static final String IMG_NULL = "NULL";

	// Just for fun :-)
	public static final String IMG_PENGUIN = "Penguine";

	/**
	 * Image IDs for buttons, menu, etc.
	 */
	public static final String IMG_NEW_PROJECT = "New Project";

	public static final String IMG_NEW_COMPONENT = "New Component";

	/**
	 * Paths to the icons for buttons, menus, etc.
	 */
	public static final String IMG_NEW_VARIABLES_PATH = "icons/full/ctool16/newvar_edit.gif";

	public static final String IMG_NEW_INVARIANTS_PATH = "icons/full/ctool16/newinv_edit.gif";

	public static final String IMG_NEW_THEOREMS_PATH = "icons/full/ctool16/newthm_edit.gif";

	public static final String IMG_NEW_EVENT_PATH = "icons/full/ctool16/newevt_edit.gif";

	public static final String IMG_NEW_CARRIER_SETS_PATH = "icons/full/ctool16/newset_edit.gif";

	public static final String IMG_NEW_CONSTANTS_PATH = "icons/full/ctool16/newcst_edit.gif";

	public static final String IMG_NEW_AXIOMS_PATH = "icons/full/ctool16/newaxm_edit.gif";

	public static final String IMG_NEW_GUARD_PATH = "icons/full/ctool16/newgrd_edit.gif";

	public static final String IMG_NEW_ACTION_PATH = "icons/full/ctool16/newact_edit.gif";

	public static final String IMG_UP_PATH = "icons/full/ctool16/up_edit.gif";

	public static final String IMG_DOWN_PATH = "icons/full/ctool16/down_edit.gif";

	public static final String IMG_MACHINE_PATH = "icons/full/obj16/mch_obj.gif";

	public static final String IMG_REFINE_OVERLAY_PATH = "icons/full/ovr16/ref_ovr.gif";

	public static final String IMG_COMMENT_OVERLAY_PATH = "icons/full/ovr16/cmt_ovr.gif";

	public static final String IMG_ERROR_OVERLAY_PATH = "icons/full/ovr16/error_ovr.gif";

	public static final String IMG_EXPERT_MODE_PATH = "icons/full/ctool16/xp_prover.gif";

	private static Map<String, Image> images = new HashMap<String, Image>();

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path.
	 * <p>
	 * 
	 * @param path
	 *            relative path of the image
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return getImageDescriptor(EventBUIPlugin.PLUGIN_ID, path);
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path.
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
	 * Initialialise the image registry. Additional image should be added here.
	 * <p>
	 * 
	 * @param registry
	 *            The image registry
	 */
	public static void initializeImageRegistry(ImageRegistry registry) {
		registerImage(registry, IMG_MACHINE, "icons/full/obj16/mch_obj.gif");
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

		registerImage(registry, IMG_PENDING, "icons/pending.gif");
		registerImage(registry, IMG_PENDING_BROKEN, "icons/pending_broken.gif");
		registerImage(registry, IMG_APPLIED, "icons/applied.gif");
		registerImage(registry, IMG_DISCHARGED, "icons/discharged.gif");
		registerImage(registry, IMG_DISCHARGED_BROKEN,
				"icons/discharged_broken.gif");
		registerImage(registry, IMG_REVIEWED, "icons/reviewed.png");
		registerImage(registry, IMG_REVIEWED_BROKEN,
				"icons/reviewed_broken.png");
		registerImage(registry, IMG_UNATTEMPTED, "icons/unattempted.gif");
		registerImage(registry, IMG_DEFAULT, "icons/sample.gif");
		registerImage(registry, IMG_REFINES, "icons/full/ctool16/refines.gif");

		registerImage(registry, IMG_PENGUIN, "icons/penguins-dancing.gif");
		registerImage(registry, IMG_NULL, "icons/full/ctool16/null.gif");
	}

	/**
	 * Register the image with the image registry. This method is used in the
	 * initialisation of the image registry.
	 * <p>
	 * 
	 * @param registry
	 *            the registry
	 * @param key
	 *            the key to retrieve the image
	 * @param fileName
	 *            the name of the image file in folder "icons"
	 */
	public static void registerImage(ImageRegistry registry, String key,
			String fileName) {
		ImageDescriptor desc = getImageDescriptor(fileName);
		registry.put(key, desc);
	}

	public static void registerImage(ImageRegistry registry, String key,
			String pluginID, String fileName) {
		ImageDescriptor desc = getImageDescriptor(pluginID, fileName);
		registry.put(key, desc);
	}

//	public static Image getOverlayIcon(String name) {
//		if (name.equals("IMG_REFINES_MACHINE")) {
//			OverlayIcon icon = new OverlayIcon(
//					getImageDescriptor(IMG_MACHINE_PATH));
//			icon.addTopRight(getImageDescriptor(IMG_REFINE_OVERLAY_PATH));
//			icon.addBottomLeft(getImageDescriptor(IMG_ERROR_OVERLAY_PATH));
//			Image image = icon.createImage();
////			images.put(image);
//			return image;
//		}
//		return null;
//	}

	public static void disposeImages() {
		for (Image image : images.values()) {
			image.dispose();
		}
		images = new HashMap<String, Image>();
	}

	public static Image getImage(String key) {
		ImageRegistry registry = EventBUIPlugin.getDefault().getImageRegistry();
		return registry.get(key);
	}

	/**
	 * Getting the impage corresponding to a Rodin element.
	 * <p>
	 * 
	 * @param element
	 *            A Rodin element
	 * @return The image for displaying corresponding to the input element
	 */
	public static Image getRodinImage(IRodinElement element) {
		Collection<ElementUI> elementUIs = ElementUILoader.getElementUIs();

		for (ElementUI elementUI : elementUIs) {
			Class clazz = elementUI.getElementClass();
			if (clazz.isInstance(element)) {
				String pluginID = elementUI.getPluginID();
				String path = elementUI.getPath();
				
				// Compute the key
				// key = pluginID:path:overlay
				// overlay = comment + error 
				String key = pluginID + ":" + path;
				
				String overlay = "0";
				if (element instanceof ICommentedElement) {
					ICommentedElement commentedElement = (ICommentedElement) element;
					try {
						String comment = commentedElement.getComment(new NullProgressMonitor());
						if (!comment.equals("")) overlay = "1";
						System.out.println(overlay); 
					} catch (RodinDBException e) {
						// Do nothing
					}
				}
				key += ":" + overlay;
				
				Image image = images.get(key);
				if (image == null) {
					UIUtils.debugEventBEditor("Create a new image: " + key);
					OverlayIcon icon = new OverlayIcon(
							getImageDescriptor(pluginID, path));
					if (overlay == "1")
						icon.addTopRight(getImageDescriptor(IMG_COMMENT_OVERLAY_PATH));
					image = icon.createImage();
					images.put(key, image);					
				}
				return image;
			}
		}

		return null;
	}
	
}
