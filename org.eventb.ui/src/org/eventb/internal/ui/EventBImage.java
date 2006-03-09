package org.eventb.internal.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;

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
	public static final String IMG_LOCAL_VARIABLE = "Local variable";
	public static final String IMG_GUARD = "Guard";
	public static final String IMG_ACTION = "Action";
	public static final String IMG_CARRIER_SET = "CarrierSet";
	public static final String IMG_CONSTANT = "Constant";
	public static final String IMG_AXIOM = "Axiom";
	public static final String IMG_VARIABLES = "Variables";
	public static final String IMG_INVARIANTS = "Invariants";
	public static final String IMG_THEOREMS = "Theorems";
	public static final String IMG_INITIALISATION = "Initialisation";
	public static final String IMG_EVENTS = "Events";
	public static final String IMG_LOCAL_VARIABLES = "Local variables";
	public static final String IMG_GUARDS = "Guards";
	public static final String IMG_ACTIONS = "Actions";
	public static final String IMG_CARRIER_SETS = "CarrierSets";
	public static final String IMG_CONSTANTS = "Constants";
	public static final String IMG_AXIOMS = "Axioms";
	public static final String IMG_DISCHARGED = "Discharged";
	public static final String IMG_PENDING = "Pending";
	public static final String IMG_APPLIED = "Applied";
	
	// Just for fun :-)
	public static final String IMG_PENGUIN = "Penguine";

	/**
	 * Image IDs for buttons, menu, etc.
	 */
	public static final String IMG_DELETE = "Delete";
	public static final String IMG_NEW_PROJECT = "New Project";
	public static final String IMG_NEW_COMPONENT = "New Component";
	
	/**
	 * Paths to the icons for buttons, menus, etc.
	 */
	public static final String IMG_PREVPO_PATH = "icons/prev.gif";
	public static final String IMG_NEXTPO_PATH = "icons/next.gif";
	
	public static final String IMG_NEW_VARIABLES_PATH = "icons/new-variables.gif";
	
	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path relative path of the image
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(EventBUIPlugin.PLUGIN_ID, path);
	}

	/**
	 * Initialialise the image registry.
	 * Additional image should be added here.
	 */
	public static void initializeImageRegistry(ImageRegistry registry) {
		registerImage(registry, IMG_PROJECT, "project.gif");
		registerImage(registry, IMG_MACHINE, "machine.gif");
		registerImage(registry, IMG_CONTEXT, "context.gif");
		registerImage(registry, IMG_VARIABLE, "variable.gif");
		registerImage(registry, IMG_INVARIANT, "invariant.gif");
		registerImage(registry, IMG_THEOREM, "theorem.gif");
		registerImage(registry, IMG_EVENT, "event.gif");
		registerImage(registry, IMG_LOCAL_VARIABLE, "localvariable.gif");
		registerImage(registry, IMG_GUARD, "guard.gif");
		registerImage(registry, IMG_ACTION, "action.gif");
		registerImage(registry, IMG_CARRIER_SET, "carrierset.gif");
		registerImage(registry, IMG_CONSTANT, "constant.gif");
		registerImage(registry, IMG_AXIOM, "axiom.gif");
		registerImage(registry, IMG_VARIABLES, "variables.gif");
		registerImage(registry, IMG_INVARIANTS, "invariants.gif");
		registerImage(registry, IMG_THEOREMS, "theorems.gif");
		registerImage(registry, IMG_INITIALISATION, "initialisation.gif");
		registerImage(registry, IMG_EVENTS, "events.gif");
		registerImage(registry, IMG_LOCAL_VARIABLES, "localvariables.gif");
		registerImage(registry, IMG_GUARDS, "guards.gif");
		registerImage(registry, IMG_ACTIONS, "actions.gif");
		registerImage(registry, IMG_CARRIER_SETS, "carriersets.gif");
		registerImage(registry, IMG_CONSTANTS, "constants.gif");
		registerImage(registry, IMG_AXIOMS, "axioms.gif");
		registerImage(registry, IMG_DELETE, "delete.gif");
		registerImage(registry, IMG_NEW_PROJECT, "new-project.gif");
		registerImage(registry, IMG_NEW_COMPONENT, "new-component.gif");
		
		registerImage(registry, IMG_PENDING, "pending.gif");
		registerImage(registry, IMG_APPLIED, "applied.gif");
		registerImage(registry, IMG_DISCHARGED, "discharged.gif");
		
		registerImage(registry, IMG_PENGUIN, "penguins-dancing.gif");
	}

	/*
	 * Register the image with the image registry. This method is used 
	 * in the initialisation of the image registry. 
	 * @param registry the registry
	 * @param key the key to retrieve the image
	 * @param fileName the name of the image file in folder "icons"
	 */
	private static void registerImage(ImageRegistry registry, String key,
			String fileName) {
			ImageDescriptor desc = getImageDescriptor("icons/" + fileName);
			registry.put(key, desc);
	}
}
