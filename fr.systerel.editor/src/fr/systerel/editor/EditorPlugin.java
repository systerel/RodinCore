/*******************************************************************************
 * Copyright (c) 2008, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor;

import static org.rodinp.core.emf.api.itf.ImplicitChildProviderManager.addProviderFor;
import static org.rodinp.core.emf.api.itf.ImplicitChildProviderManager.getProviderFor;

import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.IImplicitChildProvider;
import org.eventb.ui.itemdescription.IElementDesc;
import org.eventb.ui.itemdescription.IElementDescRegistry;
import org.osgi.framework.BundleContext;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.emf.api.itf.ICoreImplicitChildProvider;

import fr.systerel.editor.internal.documentModel.DocumentMapper;
import fr.systerel.editor.internal.documentModel.RodinPartitioner;
import fr.systerel.editor.internal.editors.DNDManager;
import fr.systerel.editor.internal.editors.RodinEditor;
import fr.systerel.editor.internal.editors.SelectionController;
import fr.systerel.editor.internal.presentation.RodinConfiguration;
import fr.systerel.editor.internal.presentation.updaters.ProblemMarkerAnnotationsUpdater;

/**
 * The activator class controls the plug-in life cycle
 */
public class EditorPlugin extends AbstractUIPlugin {
	
	// The constant indicating debug
	public static boolean DEBUG;

	// The plug-in ID
	public static final String PLUGIN_ID = "fr.systerel.editor";

	// The debug prefix for debug messages
	public static final String DEBUG_PREFIX = "***** RodinEditor :";

	// Tracing options
	private static final String SELECTION_TRACE = PLUGIN_ID + "/debug/selection"; //$NON-NLS-1$
	private static final String DND_TRACE = PLUGIN_ID + "/debug/dnd"; //$NON-NLS-1$
	private static final String PARTITION_TRACE = PLUGIN_ID + "/debug/partition"; //$NON-NLS-1$
	private static final String FOLDING_TRACE = PLUGIN_ID + "/debug/folding"; //$NON-NLS-1$
	private static final String MARKER_POSITION_TRACE = PLUGIN_ID + "/debug/marker_position"; //$NON-NLS-1$
	private static final String EDITOR_REFRESHING_TIME = PLUGIN_ID + "/debug/editor_refresh_time"; //$NON-NLS-1$
	private static final String COLORED_CONTENTTYPE_BACKGROUND = PLUGIN_ID + "/debug/debug_content_types"; //$NON-NLS-1$

	// The shared instance
	private static EditorPlugin plugin;
	
	/**
	 * The constructor
	 */
	public EditorPlugin() {
		DEBUG = isDebugging();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		if (isDebugging())
			configureDebugOptions();
		final IElementDescRegistry registry = EventBUIPlugin
				.getElementDescRegistry();
		final IElementDesc[] elementDescs = registry.getElementDescs();
		for (IElementDesc desc : elementDescs) {
			recursiveLoadImplicitProviders(desc);
		}
	}
	
	private void recursiveLoadImplicitProviders(IElementDesc desc) {
		final IInternalElementType<?>[] childTypes = desc.getChildTypes();
		final IElementDescRegistry registry = EventBUIPlugin
				.getElementDescRegistry();
		for (IInternalElementType<?> childType : childTypes) {
			final IInternalElementType<?> parentType = desc.getElementType();
			if (parentType == null)
				continue;
			if (getProviderFor(parentType, childType) == null) {
				final IImplicitChildProvider provider = desc
						.getImplicitChildProvider(childType);
				addProviderFor(new ChildProviderWrapper(provider), parentType,
						childType);
			}
			final IElementDesc childDesc = registry.getElementDesc(childType);
			recursiveLoadImplicitProviders(childDesc);
		}
	}

	private static class ChildProviderWrapper implements ICoreImplicitChildProvider {
		
		final IImplicitChildProvider provider;
		
		ChildProviderWrapper(IImplicitChildProvider provider){
			this.provider = provider;
		}
		
		@Override
		public List<? extends IInternalElement> getImplicitChildren(
				IInternalElement parent) {
			return provider.getImplicitChildren(parent);
		}
		
	}

	private void configureDebugOptions() {
		SelectionController.DEBUG = parseOption(SELECTION_TRACE);
		DNDManager.DEBUG = parseOption(DND_TRACE);
		RodinPartitioner.DEBUG = parseOption(PARTITION_TRACE);
		DocumentMapper.DEBUG = parseOption(FOLDING_TRACE);
		ProblemMarkerAnnotationsUpdater.DEBUG = parseOption(MARKER_POSITION_TRACE);
		RodinEditor.DEBUG = parseOption(EDITOR_REFRESHING_TIME);
		RodinConfiguration.DEBUG = parseOption(COLORED_CONTENTTYPE_BACKGROUND);
	}

	private static boolean parseOption(String key) {
		final String option = Platform.getDebugOption(key);
		return "true".equalsIgnoreCase(option); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static EditorPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	
	/**
	 * Get the active workbench page.
	 * 
	 * @return current active workbench page
	 */
	public static IWorkbenchPage getActivePage() {
		return getDefault().internalGetActivePage();
	}

	/**
	 * Getting the current active page from the active workbench window.
	 * 
	 * @return current active workbench page
	 */
	private IWorkbenchPage internalGetActivePage() {
		final IWorkbenchWindow activeWorkbenchWindow = getActiveWorkbenchWindow();
		if (activeWorkbenchWindow == null)
			return null;
		return activeWorkbenchWindow.getActivePage();
	}

	/**
	 * Get the current active workbench window.
	 * 
	 * @return the current active workbench window
	 */
	public static IWorkbenchWindow getActiveWorkbenchWindow() {
		return getDefault().getWorkbench().getActiveWorkbenchWindow();
	}
	
}
