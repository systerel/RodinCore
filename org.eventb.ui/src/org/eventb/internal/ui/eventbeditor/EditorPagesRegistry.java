/*******************************************************************************
 * Copyright (c) 2005, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - refactored code for testing with an alternate extension point
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.preferences.ContextEditorPagesPreference;
import org.eventb.internal.ui.preferences.MachineEditorPagesPreference;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.eventbeditor.EventBEditorPage;

/**
 * @author htson
 *         <p>
 *         This class is a singleton utility class for loading the pages used
 *         in EventBEditor
 */
public class EditorPagesRegistry implements IEditorPagesRegistry {

	private static final String EXTENSION_POINT_ID = EventBUIPlugin.PLUGIN_ID
			+ ".editorPages";

	// Singleton instance of the class
	private static IEditorPagesRegistry instance;

	// Registry information: Mapping from editor id to related information.
	private Map<String, EditorInfo> registry;

	// Default priority
	private static final int DEFAULT_PRIORITY = 10000;
	
	// Alternate extension point id for testing purposes
	private String alternateExtensionPointId;

	/**
	 * @author htson
	 *         <p>
	 *         Utility class for storing information related to an Editor id.
	 */
	private class EditorInfo {

		// Array of information related to the editor pages.
		ArrayList<PageInfo> infos;

		/**
		 * Constructor.
		 */
		public EditorInfo() {
			infos = new ArrayList<PageInfo>();
		}

		/**
		 * Create all editor pages.
		 * 
		 * @return a list of editor pages. All the instances of the pages are
		 *         NOT <code>null</code>.
		 */
		public EventBEditorPage[] createAllPages() {
			Collection<EventBEditorPage> pages = new LinkedList<EventBEditorPage>();
			for (PageInfo info : infos) {
				EventBEditorPage page = info.createPage();
				if (page != null) {
					pages.add(page);
				}
			}
			return pages.toArray(new EventBEditorPage[pages.size()]);
		}

		/**
		 * Return the list of all page IDs registered.
		 * 
		 * @return the list of all page IDs registered.
		 */
		public List<String> getAllPageIDs() {
			List<String> pageIDs = new ArrayList<String>();
			for (PageInfo info : infos) {
				pageIDs.add(info.getID());
			}
			return pageIDs;
		}
		
		/**
		 * Register a page information.
		 * 
		 * @param info
		 *            information of a page.
		 */
		public void registerPage(PageInfo info) {
			infos.add(info);
		}

		/**
		 * Sort the registered page according to their priority (ascending order). 
		 */
		void sortPages() {
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

		/**
		 * Check if the page id is a valid page.
		 * 
		 * @param pageID
		 *            a page ID
		 * @return <code>true</code> if the page id is valid page for editor
		 *         with given input editor id. Return <code>false</code>
		 *         otherwise.
		 */
		public boolean isValid(String pageID) {
			for (PageInfo info : infos) {
				if (info.getID().equals(pageID))
					return true;
			}
			return false;
		}

		/**
		 * Create an editor page corresponding to the given page id.
		 * 
		 * @param pageID
		 *            a page ID
		 * @return an editor page corresponding to the input page id for the
		 *         given editor. Return <code>null</code> if the page ID is
		 *         invalid or there is some problem in creating the page.
		 */
		public EventBEditorPage createPage(String pageID) {
			for (PageInfo info : infos) {
				if (info.getID().equals(pageID))
					return info.createPage();
			}
			return null;
		}

		/**
		 * Get the name of the editor page corresponding to the given page id
		 * 
		 * @param pageID
		 *            a page ID
		 * @return the name of the corresponding page to the input page id.
		 *         Return <code>null</code> if the page id is invalid. If the
		 *         page does not have a name, its id will be returned.
		 */
		public String getPageName(String pageID) {
			for (PageInfo info : infos) {
				if (info.getID().equals(pageID))
					return info.getPageName();
			}
			return null;
		}

		/**
		 * Return the list of default page IDs.
		 * 
		 * @return the list of default page IDs for given editor.
		 */
		public List<String> getDefaultPageIDs() {
			List<String> pageIDs = new ArrayList<String>();
			for (PageInfo info : infos) {
				if (info.isDefault())
					pageIDs.add(info.getID());
			}
			return pageIDs;
		}

	}

	/**
	 * @author htson
	 *         <p> Utility class for storing information related to an editor page.
	 */
	private final class PageInfo {

		// The configuration element.
		private final IConfigurationElement configElement;
		
		// The unique id of this page
		private final String id;
		
		// The priority of the page
		private final int priority;

		/**
		 * Constructor: Create a page information with the configuration element.
		 * <p>
		 *
		 * @param configElement
		 */
		PageInfo(IConfigurationElement configElement) {
			this.configElement = configElement;
			// TODO check that id is present.
			this.id = configElement.getAttribute("id");
			this.priority = readPriority();
		}

		/**
		 * Check if the page should be a default page in the editor.
		 * 
		 * @return <code>true</code> if this page is a default page. Return
		 *         <code>false</code> otherwise. If a page is not declared as
		 *         default or not, it will be treated as a default page.
		 */
		public boolean isDefault() {
			String isDefault = configElement.getAttribute("isDefault");
			if (isDefault == null)
				return true;
			return isDefault.equalsIgnoreCase("true");
		}

		/**
		 * Get the name of the editor page.
		 * 
		 * @return the name of the editor page. If the attribute is not defined,
		 *         the id of the page will be used.
		 */
		public String getPageName() {
			String name = configElement.getAttribute("name");
			if (name == null)
				return id;
			return name;
		}

		/**
		 * Get the unique id of the page.
		 * 
		 * @return the unique id of the page.
		 */
		public String getID() {
			return id;
		}

		/**
		 * Utility method for reading the priority of the page.
		 * 
		 * @return the priority of the page. If the attribute is not defined,
		 *         the default priority ({@link EditorPagesRegistry#DEFAULT_PRIORITY})
		 *         will be used.
		 */
		private int readPriority() {
			String priorityValue = configElement.getAttribute("priority");
			if (priorityValue == null) {
				UIUtils.log(null,
						"Missing priority attribute (using default),"
						+ " for editor page extension " + id);
				return DEFAULT_PRIORITY;
			}
			try {
				return Integer.parseInt(priorityValue);
			} catch (NumberFormatException e) {
				UIUtils.log(e,
						"Illegal priority " + priorityValue
						+ ", using default instead,"
						+ " for editor page extension " + id);
				return DEFAULT_PRIORITY;
			}
		}
		
		/**
		 * Get the priority of the page.
		 * 
		 * @return the priority of the page.
		 */
		public int getPriority() {
			return this.priority;
		}

		/**
		 * Create an editor page.
		 * 
		 * @return an editor page. Return <code>null</code> if there is some
		 *         problem in creating the page.
		 */
		public EventBEditorPage createPage() {
			try {
				return (EventBEditorPage) configElement
						.createExecutableExtension("class");
			} catch (Exception e) {
				EventBEditorUtils.debugAndLogError(e,
						"Failed to create a page for editor extension " + id);
				return null;
			}
		}
	}

	/**
	 * Constructor.
	 * <p>
	 * Private constructor for singleton class.
	 */
	private EditorPagesRegistry() {
		// Singleton to hide the constructor
	}

	/**
	 * Return the unique singleton instance of this class.
	 * 
	 * @return the unique singleton instance of this class.
	 */
	public static IEditorPagesRegistry getDefault() {
		if (instance == null)
			instance = new EditorPagesRegistry();
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.IEditorPagesRegistry#createPages(java.lang.String)
	 */
	@Override
	public synchronized EventBEditorPage[] createAllPages(String editorID) {
		if (registry == null)
			loadRegistry();
		EditorInfo info = registry.get(editorID);
		if (info == null) {
			return new EventBEditorPage[0];
		}
		return info.createAllPages();
	}

	/**
	 * Utility method for loading registry information.
	 */
	private synchronized void loadRegistry() {
		if (registry != null) {
			// avoid to read the registry at the same time in different threads
			return;
		}

		registry = new HashMap<String, EditorInfo>();

		IExtensionPoint extensionPoint = getExtensionPoint();
		IConfigurationElement[] configurations = extensionPoint
				.getConfigurationElements();

		for (IConfigurationElement configuration : configurations) {
			PageInfo info = new PageInfo(configuration);
			IConfigurationElement[] targets = configuration
					.getChildren("target");
			for (IConfigurationElement target : targets) {
				String targetID = target.getAttribute("id");
				registerPage(targetID, info);
			}
		}

		// Sort the pages for each target editor.
		sortPages();

	}

	private IExtensionPoint getExtensionPoint() {
		final String id;
		if (alternateExtensionPointId != null) {
			id = alternateExtensionPointId;
		} else {
			id = EXTENSION_POINT_ID;
		}
		return Platform.getExtensionRegistry().getExtensionPoint(id);
	}

	/**
	 * Utility method for sorting pages for each editor according to their
	 * priority in ascending order.
	 */
	private synchronized void sortPages() {
		assert registry != null;

		for (String targetID : registry.keySet()) {
			EditorInfo infos = registry.get(targetID);
			infos.sortPages();
		}
	}

	/**
	 * Register a page information for a target editor.
	 * 
	 * @param targetID
	 *            the target editor ID.
	 * @param info
	 *            the page information.
	 */
	private synchronized void registerPage(String targetID, PageInfo info) {
		assert registry != null;

		EditorInfo infos = registry.get(targetID);
		if (infos == null) {
			infos = new EditorInfo();
			registry.put(targetID, infos);
		}
		infos.registerPage(info);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.IEditorPagesRegistry#isValid(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public synchronized boolean isValid(String editorID, String pageID) {
		if (registry == null)
			loadRegistry();
		EditorInfo info = registry.get(editorID);
		if (info == null) {
			return false;
		}
		return info.isValid(pageID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.IEditorPagesRegistry#createPage(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public synchronized EventBEditorPage createPage(String editorID, String pageID) {
		if (registry == null)
			loadRegistry();
		EditorInfo info = registry.get(editorID);
		if (info == null) {
			return null;
		}
		return info.createPage(pageID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.IEditorPagesRegistry#getPageName(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public synchronized String getPageName(String editorID, String pageID) {
		if (registry == null)
			loadRegistry();
		EditorInfo info = registry.get(editorID);
		if (info == null) {
			return null;
		}
		return info.getPageName(pageID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.IEditorPagesRegistry#getPageIDs(java.lang.String)
	 */
	@Override
	public synchronized List<String> getAllPageIDs(String editorID) {
		if (registry == null)
			loadRegistry();
		EditorInfo info = registry.get(editorID);
		if (info == null) {
			return null;
		}
		return info.getAllPageIDs();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.IEditorPagesRegistry#getDefaultPageIDs(java.lang.String)
	 */
	@Override
	public List<String> getDefaultPageIDs(String editorID) {
		if (registry == null)
			loadRegistry();
		EditorInfo info = registry.get(editorID);
		if (info == null) {
			return new ArrayList<String>();
		}
		return info.getDefaultPageIDs();
	}

	/**
	 * Change the id of the extension point to use. This method is reserved for
	 * testing purpose only.
	 * 
	 * @param extensionPointId
	 *            the id of the extension point to use or <code>null</code> to
	 *            revert to the regular one
	 */
	public synchronized void setAlternateExtensionPointID(String extensionPointId) {
		this.alternateExtensionPointId = extensionPointId;
		registry = null; // Force to reload the registry
		
		// FIXME should be done through an observer pattern
		// with this registry as a subject and the PagesPreferences as clients.
		// Should also be done for other changes to this registry.
		MachineEditorPagesPreference.getDefault().setDefault();
		ContextEditorPagesPreference.getDefault().setDefault();
	}

}