/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
  *******************************************************************************/


package fr.systerel.explorer.masterDetails;

import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;

import fr.systerel.explorer.ExplorerPlugin;
import fr.systerel.explorer.navigator.INavigatorDetailsTab;

/**
 * This is the Details page of the MasterDetails pattern of the Navigator. There
 * is just one details page (this one), no matter what was selected in the
 * master part. Content can be added to the details page by implementing a new
 * <code>INavigatorDetailsTab</code> and adding it via the extension point.
 * 
 */
public class NavigatorDetailsPage implements IDetailsPage, ISelectionProvider {

	private static final String MASTER_DETAILS_ID = ExplorerPlugin.PLUGIN_ID +".masterDetails";
	private static final String DETAILS_TAB_CLASS = "Class";
	private TabFolder tabFolder = null;
	private ListenerList listenerList = new ListenerList();
	private ITreeSelection selection;
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.IDetailsPage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	public void createContents(Composite parent) {
		if (tabFolder == null ) {
			tabFolder = new TabFolder(parent, SWT.NONE);
			parent.setLayout(new FillLayout());
			//load all tabs
			INavigatorDetailsTab[] tabs = loadTabs();
			for (INavigatorDetailsTab tab : tabs) {
				TabItem tabItem = tab.createTabItem(tabFolder);
				assert (tabItem != null);
				assert (tabItem.getParent() == tabFolder);
				//register all tabs as listener of this selection provider
				tab.registerAsListener(this);
			}
		}
		
	}
	
	

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.IFormPart#commit(boolean)
	 */
	public void commit(boolean onSave) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.IFormPart#dispose()
	 */
	public void dispose() {
		//the tabFolder is automatically disposed when its parent is disposed.s
		tabFolder = null;
		listenerList.clear();
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.IFormPart#initialize(org.eclipse.ui.forms.IManagedForm)
	 */
	public void initialize(IManagedForm form) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.IFormPart#isDirty()
	 */
	public boolean isDirty() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.IFormPart#isStale()
	 */
	public boolean isStale() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.IFormPart#refresh()
	 */
	public void refresh() {
		//do nothing
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.IFormPart#setFocus()
	 */
	public void setFocus() {
		// do nothing

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.IFormPart#setFormInput(java.lang.Object)
	 */
	public boolean setFormInput(Object input) {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.IPartSelectionListener#selectionChanged(org.eclipse.ui.forms.IFormPart, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IFormPart part, ISelection newSelection) {
		if (newSelection instanceof ITreeSelection) {
			this.selection = (ITreeSelection) newSelection;
			fireSelectionChanged(new SelectionChangedEvent(this, selection));
		}
		
	}


	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		listenerList.add(listener);
	}


	public ISelection getSelection() {
		return selection;
	}


	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		listenerList.remove(listener);
	}

	
	public void setSelection(ISelection selection) {
		if (selection instanceof ITreeSelection) {
			this.selection = (ITreeSelection) selection;
		}
	}
	
	/**
	 * Notifies all registered selection changed listeners that the editor's
	 * selection has changed. Only listeners registered at the time this
	 * method is called are notified.
	 * 
	 * @param event
	 *            the selection changed event
	 */
	public void fireSelectionChanged(final SelectionChangedEvent event) {
		for (Object obj : listenerList.getListeners()) {
			final ISelectionChangedListener l = (ISelectionChangedListener) obj;
			SafeRunner.run(new SafeRunnable() {
				public void run() {
					l.selectionChanged(event);
				}
			});
		}
	}
	
	/**
	 * Tries to load all tabs that are defined via the extension point.
	 * @return all loaded tabs
	 */
	private INavigatorDetailsTab[] loadTabs() {
		ArrayList<INavigatorDetailsTab> results = new ArrayList<INavigatorDetailsTab>();
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = reg.getExtensionPoint(MASTER_DETAILS_ID);
		IConfigurationElement[] configurations = extensionPoint
				.getConfigurationElements();
		for (IConfigurationElement  configuration: configurations) {
			try {
				INavigatorDetailsTab tab =  (INavigatorDetailsTab) configuration.createExecutableExtension(DETAILS_TAB_CLASS);
				results.add(tab);
			} catch (CoreException e) {
				System.out.println("Unable to instantiate details tab class for " +configuration.getAttribute("id"));
			}		
		}
		
		return results.toArray(new INavigatorDetailsTab[results.size()]);
	}

}
