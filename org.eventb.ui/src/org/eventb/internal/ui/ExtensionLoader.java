/*******************************************************************************
 * Copyright (c) 2005-2006 ETH Zurich.
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

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eventb.internal.ui.prover.globaltactics.GlobalTacticDropdownUI;
import org.eventb.internal.ui.prover.globaltactics.GlobalTacticToolbarUI;
import org.eventb.internal.ui.prover.globaltactics.GlobalTacticUI;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.prover.IGlobalExpertTactic;
import org.eventb.ui.prover.IGlobalSimpleTactic;
import org.osgi.framework.Bundle;

/**
 * @author htson
 *         <p>
 *         This class is a static utitlity class for loading the extension
 *         different extensions.
 */
public class ExtensionLoader {

	// Code extracted to suppress spurious warning about unsafe type cast.
	@SuppressWarnings("unchecked")
	private static Class getSubclass(Class clazz, Class subClass) {
		return clazz.asSubclass(subClass);
	}

	private static final String GLOBAL_PROOF_TACTIC_ID = EventBUIPlugin.PLUGIN_ID
			+ ".globalProofTactics";

	// Array of Proof Tactics
	// private static ArrayList<GlobalTacticUI> tactics = null;

	// Array of Global dropdown
	// private static ArrayList<GlobalTacticDropdownUI> dropdowns = null;
	//
	// Array of Toolbar
	private static ArrayList<GlobalTacticToolbarUI> toolbars = null;

	private static void internalGetGlobalTactics() {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = registry
				.getExtensionPoint(GLOBAL_PROOF_TACTIC_ID);
		IExtension[] extensions = extensionPoint.getExtensions();

		ArrayList<GlobalTacticUI> tactics = new ArrayList<GlobalTacticUI>();
		ArrayList<GlobalTacticDropdownUI> dropdowns = new ArrayList<GlobalTacticDropdownUI>();
		toolbars = new ArrayList<GlobalTacticToolbarUI>();

		GlobalTacticToolbarUI defaultToolbar = new GlobalTacticToolbarUI(
				"default");
		HashMap<String, GlobalTacticToolbarUI> toolbarMap = new HashMap<String, GlobalTacticToolbarUI>();
		HashMap<String, GlobalTacticDropdownUI> dropdownMap = new HashMap<String, GlobalTacticDropdownUI>();
		for (IExtension extension : extensions) {
			IConfigurationElement[] elements = extension
					.getConfigurationElements();
			for (IConfigurationElement element : elements) {
				String name = element.getName();

				if (name.equals("experttactic")) {
					String namespace = element.getContributor().getName();
					Bundle bundle = Platform.getBundle(namespace);
					try {
						String ID = element.getAttribute("id");
						String icon = element.getAttribute("icon");
						ImageRegistry imageRegistry = EventBUIPlugin
								.getDefault().getImageRegistry();

						ImageDescriptor desc = EventBImage.getImageDescriptor(
								namespace, icon);
						imageRegistry.put(icon, desc);

						String tooltip = element.getAttribute("tooltip");

						Class clazz = bundle.loadClass(element
								.getAttribute("class"));

						Class classObject = getSubclass(clazz,
								IGlobalExpertTactic.class);
						Constructor constructor = classObject
								.getConstructor(new Class[0]);
						String dropdown = element.getAttribute("dropdown");
						String toolbar = element.getAttribute("toolbar");
						String interrupt = element.getAttribute("interruptable");
						boolean interruptable = interrupt
								.equalsIgnoreCase("true");
						GlobalTacticUI tactic = new GlobalTacticUI(ID, icon,
								tooltip, dropdown, toolbar, constructor,
								interruptable);
						tactics.add(tactic);

					} catch (Exception e) {
						// TODO Exception handle
						e.printStackTrace();
					}
				} else if (name.equals("simpletactic")) {
					String namespace = element.getContributor().getName();
					Bundle bundle = Platform.getBundle(namespace);
					try {
						String ID = element.getAttribute("id");
						String icon = element.getAttribute("icon");
						ImageRegistry imageRegistry = EventBUIPlugin
								.getDefault().getImageRegistry();

						ImageDescriptor desc = EventBImage.getImageDescriptor(
								namespace, icon);
						imageRegistry.put(icon, desc);

						String tooltip = element.getAttribute("tooltip");

						Class clazz = bundle.loadClass(element
								.getAttribute("class"));

						Class classObject = getSubclass(clazz,
								IGlobalSimpleTactic.class);
						Constructor constructor = classObject
								.getConstructor(new Class[0]);
						String dropdown = element.getAttribute("dropdown");
						String toolbar = element.getAttribute("toolbar");
						String interrupt = element.getAttribute("interruptable");
						boolean interruptable = interrupt
								.equalsIgnoreCase("true");
						GlobalTacticUI tactic = new GlobalTacticUI(ID, icon,
								tooltip, dropdown, toolbar, constructor,
								interruptable);
						tactics.add(tactic);

					} catch (Exception e) {
						// TODO Exception handle
						e.printStackTrace();
					}
				} else if (name.equals("dropdown")) {
					String ID = element.getAttribute("id");
					String toolbar = element.getAttribute("toolbar");
					GlobalTacticDropdownUI dropdown = new GlobalTacticDropdownUI(
							ID, toolbar);
					dropdowns.add(dropdown);
					dropdownMap.put(ID, dropdown);
				} else if (name.equals("toolbar")) {
					String id = element.getAttribute("id");
					GlobalTacticToolbarUI toolbar = new GlobalTacticToolbarUI(
							id);
					toolbars.add(toolbar);
					toolbarMap.put(id, toolbar);
				}

			}
		}

		toolbars.add(defaultToolbar);

		for (GlobalTacticDropdownUI dropdown : dropdowns) {
			String toolbarID = dropdown.getToolbar();
			GlobalTacticToolbarUI toolbar = toolbarMap.get(toolbarID);
			if (toolbar == null) {
				defaultToolbar.addChildren(dropdown);
			} else {
				toolbar.addChildren(dropdown);
			}
		}

		for (GlobalTacticUI tactic : tactics) {
			String dropdownID = tactic.getDropdown();
			String toolbarID = tactic.getToolbar();
			GlobalTacticDropdownUI dropdown = dropdownMap.get(dropdownID);

			if (dropdown == null) {
				GlobalTacticToolbarUI toolbar = toolbarMap.get(toolbarID);
				if (toolbar == null) {
					defaultToolbar.addChildren(tactic);
				} else {
					toolbar.addChildren(tactic);
				}
			} else {
				dropdown.addChildren(tactic);
			}
		}

	}

//	public static ArrayList<GlobalTacticToolbarUI> getGlobalToolbar() {
//		if (toolbars == null) {
//			internalGetGlobalTactics();
//		}
//		return toolbars;
//	}
}
