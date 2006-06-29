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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * @author htson
 *         <p>
 *         This class handles the images that are used in the Event-B UI.
 */
public class EventBImage {

	/**
	 * Image IDs for RODIN Elements
	 */
	public static final String IMG_PROJECT = "Project";

	public static final String IMG_MACHINE = "IMachine";

	public static final String IMG_CONTEXT = "IContext";

	public static final String IMG_VARIABLE = "Variable";

	public static final String IMG_INVARIANT = "Invariant";

	public static final String IMG_THEOREM = "Theorem";

	public static final String IMG_EVENT = "Event";

	public static final String IMG_GUARD = "Guard";

	public static final String IMG_ACTION = "Action";

	public static final String IMG_CARRIER_SET = "CarrierSet";

	public static final String IMG_CONSTANT = "Constant";

	public static final String IMG_AXIOM = "Axiom";

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
		return AbstractUIPlugin.imageDescriptorFromPlugin(
				EventBUIPlugin.PLUGIN_ID, path);
	}

	/**
	 * Initialialise the image registry. Additional image should be added here.
	 * <p>
	 * 
	 * @param registry
	 *            The image registry
	 */
	public static void initializeImageRegistry(ImageRegistry registry) {
		registerImage(registry, IMG_PROJECT, "full/obj16/prj_obj.gif");
		registerImage(registry, IMG_MACHINE, "full/obj16/mch_obj.gif");
		registerImage(registry, IMG_CONTEXT, "full/obj16/ctx_obj.gif");
		registerImage(registry, IMG_VARIABLE, "full/obj16/var_obj.gif");
		registerImage(registry, IMG_INVARIANT, "full/obj16/inv_obj.gif");
		registerImage(registry, IMG_THEOREM, "full/obj16/thm_obj.gif");
		registerImage(registry, IMG_EVENT, "full/obj16/evt_obj.gif");
		registerImage(registry, IMG_GUARD, "full/obj16/grd_obj.gif");
		registerImage(registry, IMG_ACTION, "full/obj16/act_obj.gif");
		registerImage(registry, IMG_CARRIER_SET, "full/obj16/set_obj.gif");
		registerImage(registry, IMG_CONSTANT, "full/obj16/cst_obj.gif");
		registerImage(registry, IMG_AXIOM, "full/obj16/axm_obj.gif");
		registerImage(registry, IMG_VARIABLES, "full/obj16/vars_obj.gif");
		registerImage(registry, IMG_INVARIANTS, "full/obj16/invs_obj.gif");
		registerImage(registry, IMG_THEOREMS, "full/obj16/thms_obj.gif");
		registerImage(registry, IMG_EVENTS, "full/obj16/evts_obj.gif");
		registerImage(registry, IMG_CARRIER_SETS, "full/obj16/sets_obj.gif");
		registerImage(registry, IMG_CONSTANTS, "full/obj16/csts_obj.gif");
		registerImage(registry, IMG_AXIOMS, "full/obj16/axms_obj.gif");
		registerImage(registry, IMG_NEW_PROJECT, "full/clcl16/newprj_wiz.gif");
		registerImage(registry, IMG_NEW_COMPONENT, "full/clcl16/newcomp_wiz.gif");

		registerImage(registry, IMG_PENDING, "pending.gif");
		registerImage(registry, IMG_PENDING_BROKEN, "pending_broken.gif");
		registerImage(registry, IMG_APPLIED, "applied.gif");
		registerImage(registry, IMG_DISCHARGED, "discharged.gif");
		registerImage(registry, IMG_DISCHARGED_BROKEN, "discharged_broken.gif");
		registerImage(registry, IMG_REVIEWED, "reviewed.png");
		registerImage(registry, IMG_REVIEWED_BROKEN, "reviewed_broken.png");
		registerImage(registry, IMG_UNATTEMPTED, "unattempted.gif");
		registerImage(registry, IMG_DEFAULT, "sample.gif");
		
		registerImage(registry, IMG_PENGUIN, "penguins-dancing.gif");
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
	private static void registerImage(ImageRegistry registry, String key,
			String fileName) {
		ImageDescriptor desc = getImageDescriptor("icons/" + fileName);
		registry.put(key, desc);
	}
}
