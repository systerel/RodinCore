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

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eventb.internal.ui.prover.globaltactics.GlobalTacticDropdownUI;
import org.eventb.internal.ui.prover.globaltactics.GlobalTacticUI;
import org.eventb.ui.prover.IGlobalTactic;
import org.osgi.framework.Bundle;

/**
 * @author htson
 *         <p>
 *         This class is a static utitlity class for loading the extension
 *         different extensions.
 */
public class ExtensionLoader {

	/*
	 * Array of constructors for machine pages.
	 */
	private static Constructor[] machinePages = null;

	private static final String MACHINE_PAGE_ID = EventBUIPlugin.PLUGIN_ID
			+ ".machinePages";

	/*
	 * Array of constructors for context pages.
	 */
	private static Constructor[] contextPages = null;

	private static final String CONTEXT_PAGE_ID = EventBUIPlugin.PLUGIN_ID
			+ ".contextPages";

	/**
	 * Get constructors for machine pages. The class atrribute for this
	 * extension point should be an extension of FormEditor.
	 * <p>
	 * 
	 * @return array of constructors
	 */
	public static Constructor[] getMachinePages() {
		if (machinePages != null)
			return machinePages;
		Class subClass = IFormPage.class;
		Class[] classes = { FormEditor.class };
		machinePages = getConstructor(MACHINE_PAGE_ID, subClass, classes);
		return machinePages;
	}

	/**
	 * Get constructors for context pages. The class atrribute for this
	 * extension point should be an extension of FormEditor.
	 * <p>
	 * 
	 * @return array of constructors
	 */
	public static Constructor[] getContextPages() {
		if (contextPages != null)
			return contextPages;
		Class subClass = IFormPage.class;
		Class[] classes = { FormEditor.class };
		contextPages = getConstructor(CONTEXT_PAGE_ID, subClass, classes);
		return contextPages;
	}

	/**
	 * Utility method for getting certain list of constructors from the
	 * extension registry.
	 * <p>
	 * 
	 * @param extensionName
	 *            name of the extension
	 * @param subClass
	 *            the required class that the extension must subclass
	 * @param classes
	 *            list of the parameters' classes of the constructor for this
	 *            extension
	 *            <p>
	 * @return an array of constructors for the extension
	 */
	private static Constructor[] getConstructor(String extensionName,
			Class subClass, Class[] classes) {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = registry
				.getExtensionPoint(extensionName);
		IExtension[] extensions = extensionPoint.getExtensions();

		ArrayList<Constructor> list = new ArrayList<Constructor>();
		for (int i = 0; i < extensions.length; i++) {
			IConfigurationElement[] elements = extensions[i]
					.getConfigurationElements();
			for (int j = 0; j < elements.length; j++) {
				Bundle bundle = Platform.getBundle(elements[j].getNamespace());
				try {
					Class clazz = bundle.loadClass(elements[j]
							.getAttribute("class"));
					Class classObject = getSubclass(clazz, subClass);
					Constructor constructor = classObject
							.getConstructor(classes);
					list.add(constructor);
				} catch (Exception e) {
					// TODO Exception handle
					e.printStackTrace();
				}
			}
		}

		return (Constructor[]) list.toArray(new Constructor[list.size()]);
	}

	// Code extracted to suppress spurious warning about unsafe type cast.
	@SuppressWarnings("unchecked")
	private static Class getSubclass(Class clazz, Class subClass) {
		return clazz.asSubclass(subClass);
	}

	// Array of Proof Tactics
	private static GlobalTacticUI[] tactics = null;

	private static final String GLOBAL_PROOF_TACTIC_ID = EventBUIPlugin.PLUGIN_ID
			+ ".globalProofTactics";

	/**
	 * Read the extensions for global tactics.
	 * <p>
	 * 
	 * @return the set of Proof Tactics from the Global tactic extension
	 */
	public static GlobalTacticUI[] getGlobalTactics() {
		if (tactics == null) {
			IExtensionRegistry registry = Platform.getExtensionRegistry();
			IExtensionPoint extensionPoint = registry
					.getExtensionPoint(GLOBAL_PROOF_TACTIC_ID);
			IExtension[] extensions = extensionPoint.getExtensions();

			ArrayList<GlobalTacticUI> list = new ArrayList<GlobalTacticUI>();
			for (int i = 0; i < extensions.length; i++) {
				IConfigurationElement[] elements = extensions[i]
						.getConfigurationElements();
				for (int j = 0; j < elements.length; j++) {
					Bundle bundle = Platform.getBundle(elements[j]
							.getNamespace());
					try {
						String ID = elements[j].getAttribute("id");
						String icon = elements[j].getAttribute("icon");
						ImageRegistry imageRegistry = EventBUIPlugin
								.getDefault().getImageRegistry();

						ImageDescriptor desc = EventBImage
								.getImageDescriptor(icon);
						imageRegistry.put(icon, desc);

						String tooltip = elements[j].getAttribute("tooltip");

						Class clazz = bundle.loadClass(elements[j]
								.getAttribute("class"));

						Class classObject = getSubclass(clazz,
								IGlobalTactic.class);
						Constructor constructor = classObject
								.getConstructor(new Class[0]);
						String dropdown = elements[j].getAttribute("dropdown");

						GlobalTacticUI tactic = new GlobalTacticUI(ID, icon, tooltip,
								dropdown, constructor);
						list.add(tactic);
					} catch (Exception e) {
						// TODO Exception handle
						e.printStackTrace();
					}
				}
			}
			tactics = (GlobalTacticUI[]) list
					.toArray(new GlobalTacticUI[list.size()]);
		}
		return tactics;
	}

	// Array of Global dropdown
	private static GlobalTacticDropdownUI[] dropdowns = null;

	private static final String GLOBAL_PROOF_DROPDOWN_ID = EventBUIPlugin.PLUGIN_ID
			+ ".globalProofTacticDropdowns";

	/**
	 * Read the extensions for global dropdown.
	 * <p>
	 * 
	 * @return A set of dropdown from the global dropdown extension
	 */
	public static GlobalTacticDropdownUI [] getGlobalDropdowns() {
		if (dropdowns == null) {
			IExtensionRegistry registry = Platform.getExtensionRegistry();
			IExtensionPoint extensionPoint = registry
					.getExtensionPoint(GLOBAL_PROOF_DROPDOWN_ID);
			IExtension[] extensions = extensionPoint.getExtensions();

			ArrayList<GlobalTacticDropdownUI> list = new ArrayList<GlobalTacticDropdownUI>();
			for (int i = 0; i < extensions.length; i++) {
				IConfigurationElement[] elements = extensions[i]
						.getConfigurationElements();
				for (int j = 0; j < elements.length; j++) {
					String ID = elements[j].getAttribute("id");
					GlobalTacticDropdownUI dropdown = new GlobalTacticDropdownUI(ID);
					list.add(dropdown);
				}
			}
			dropdowns = (GlobalTacticDropdownUI[]) list
					.toArray(new GlobalTacticDropdownUI[list.size()]);
		}
		return dropdowns;
	}
}
