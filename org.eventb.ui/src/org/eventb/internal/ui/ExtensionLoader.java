/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Rodin Group
 *******************************************************************************/

package org.eventb.internal.ui;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.osgi.framework.Bundle;

/**
 * @author htson
 * <p>
 * This class is a static utitlity class for loading the extension classes
 */
public class ExtensionLoader {
	
	/*
	 * Array of constructors for machine pages.
	 */
	private static Constructor [] machinePages = null;
	private static final String MACHINE_PAGE_ID = EventBUIPlugin.PLUGIN_ID + ".machinePages";
	

	/*
	 * Array of constructors for context pages.
	 */
	private static Constructor [] contextPages = null;
	private static final String CONTEXT_PAGE_ID = EventBUIPlugin.PLUGIN_ID + ".contextPages";

	
	/**
	 *  Get constructors for machine pages. The class atrribute for 
	 *  this extension point should be an extension of FormEditor.
	 *  <p>
	 *  @return array of constructors
	 */
	public static Constructor [] getMachinePages() {
		if (machinePages != null) return machinePages;
		Class subClass = IFormPage.class;
		Class [] classes = {FormEditor.class}; 
		machinePages = getConstructor(MACHINE_PAGE_ID, subClass, classes);
		return machinePages;
	}

	
	/**
	 * Get constructors for context pages. The class atrribute for 
	 * this extension point should be an extension of FormEditor.
	 * <p>
	 * @return array of constructors
	 */
	public static Constructor [] getContextPages() {
		if (contextPages != null) return contextPages;
		Class subClass = IFormPage.class;
		Class [] classes = {FormEditor.class}; 
		contextPages = getConstructor(CONTEXT_PAGE_ID, subClass, classes);
		return contextPages;
	}
	
	
	/*
	 * Utility method for getting certain list of constructors
	 * from the extension registry.
	 * <p>
	 * @param extensionName name of the extension
	 * @param subClass the required class that the extension must subclass 
	 * @param classes list of the parameters' classes of the constructor 
	 *        for this extension
	 * <p>
	 * @return an array of constructors for the extension
	 */
	private static Constructor [] getConstructor(String extensionName, Class subClass, Class [] classes) {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = registry.getExtensionPoint(extensionName); 
		IExtension [] extensions = extensionPoint.getExtensions();
		
		ArrayList<Constructor> list = new ArrayList<Constructor>();
		for (int i = 0; i < extensions.length; i++) {
			IConfigurationElement [] elements = extensions[i].getConfigurationElements();
			for (int j = 0; j < elements.length; j++) {
				Bundle bundle = Platform.getBundle(elements[j].getNamespace());
				try {
					Class clazz = bundle.loadClass(elements[j].getAttribute("class"));
					Class classObject = getSubclass(clazz, subClass);
					Constructor constructor = classObject.getConstructor(classes);
					list.add(constructor);
				} catch (Exception e) {
					// TODO Exception handle
					e.printStackTrace();
				}
			}
		}

		return (Constructor []) list.toArray(new Constructor[list.size()]);
	}


	// Code extracted to suppress spurious warning about unsafe type cast.
	@SuppressWarnings("unchecked")
	private static Class getSubclass(Class clazz, Class subClass) {
		return clazz.asSubclass(subClass);
	}
}
