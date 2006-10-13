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

package org.eventb.internal.ui.eventbeditor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eventb.internal.ui.EventBUIPlugin;
import org.eventb.internal.ui.UIUtils;
import org.eventb.ui.eventbeditor.EventBEditorPage;

/**
 * @author htson
 *         <p>
 *         This class is a singleton utitlity class for loading the pages used
 *         in EventBEditor
 */
public class EditorPagesRegistry {

	private static EditorPagesRegistry instance;

	private Map<String, PagesInfo> registry;

	private static final String EDITORPAGE_ID = EventBUIPlugin.PLUGIN_ID
			+ ".editorPages";

	private class PagesInfo {

		ArrayList<PageInfo> infos;

		PagesInfo() {
			infos = new ArrayList<PageInfo>();
		}

		public EventBEditorPage[] getPages() {
			Collection<EventBEditorPage> pages = new LinkedList<EventBEditorPage>();
			for (PageInfo info : infos) {
				try {
					pages.add(info.createPage());
				} catch (CoreException e) {
					if (EventBEditorUtils.DEBUG)
						e.printStackTrace();
					UIUtils.log(e, "Cannot create a page of type "
							+ info.getPageClass());
				}
			}
			return pages.toArray(new EventBEditorPage[pages.size()]);
		}

		public void addPage(PageInfo info) {
			infos.add(info);
		}

		public void sortPages() {
			boolean sorted = false;
			int size = infos.size();
			while (!sorted) {
				sorted = true;
				for (int i = 0; i < size - 1; i++) {
					PageInfo curr = infos.get(i);
					PageInfo next = infos.get(i + 1);
					if (curr.getPriority() > next.getPriority()) {
						// Swap element
						infos.set(i, next);
						infos.set(i + 1, curr);
						sorted = false;
					}
				}
			}
		}

	}

	private class PageInfo {
		IConfigurationElement configuration;

		PageInfo(IConfigurationElement configuration) {
			this.configuration = configuration;
		}

		public int getPriority() {
			try {
				return Integer.parseInt(configuration
						.getAttributeAsIs("priority"));
			} catch (NumberFormatException e) {
				UIUtils.log(e,
						"Priority must be an integer, assign default priority (10000) to page "
								+ configuration.getAttributeAsIs("id"));
				return 10000; // Lowest priority
			}
		}

		public String getPageClass() {
			return configuration.getAttributeAsIs("class");
		}

		public EventBEditorPage createPage() throws CoreException {
			return (EventBEditorPage) configuration
					.createExecutableExtension("class");
		}
	}

	private EditorPagesRegistry() {
		// Singleton to hide the constructor
	}

	public static EditorPagesRegistry getDefault() {
		if (instance == null)
			instance = new EditorPagesRegistry();
		return instance;
	}

	public synchronized EventBEditorPage[] getPages(String editorID) {
		if (registry == null)
			loadRegistry();
		PagesInfo info = registry.get(editorID);
		if (info == null) {
			return new EventBEditorPage[0];
		}
		return info.getPages();
	}

	private synchronized void loadRegistry() {
		if (registry != null) {
			// avoid to read the registry at the same time in different threads
			return;
		}

		registry = new HashMap<String, PagesInfo>();

		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = reg.getExtensionPoint(EDITORPAGE_ID);
		IConfigurationElement[] configurations = extensionPoint
				.getConfigurationElements();

		for (IConfigurationElement configuration : configurations) {
			IConfigurationElement[] targets = configuration
					.getChildren("target");
			PageInfo info = new PageInfo(configuration);
			for (IConfigurationElement target : targets) {
				String targetID = target.getAttributeAsIs("id");
				addPage(targetID, info);
			}
		}

		// Sort the pages for each target
		sortPages();

	}

	private synchronized void sortPages() {
		assert registry != null;

		for (String targetID : registry.keySet()) {
			PagesInfo infos = registry.get(targetID);
			infos.sortPages();
		}
	}

	private synchronized void addPage(String targetID, PageInfo info) {
		assert registry != null;

		PagesInfo infos = registry.get(targetID);
		if (infos == null) {
			infos = new PagesInfo();
			registry.put(targetID, infos);
		}
		infos.addPage(info);
	}

}
