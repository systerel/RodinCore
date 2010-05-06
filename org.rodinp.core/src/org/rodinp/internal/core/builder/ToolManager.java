/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - removed deprecated methods
 *     Systerel - separation of file and root element
 *     Systerel - now returns a NullToolDescription if no tool is known
 *******************************************************************************/
package org.rodinp.internal.core.builder;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.internal.core.util.Util;

/**
 * This class implements the repository of tools known to the Rodin Builder.
 * <p>
 * Tools are registered through the extension point "autoTools".
 * </p>
 * 
 * TODO make this class dynamic aware
 * 
 * @author Stefan Hallerstede
 * @author Laurent Voisin
 */
public class ToolManager {
	
	/**
	 * The singleton ToolManager instance.
	 */
	private static final ToolManager MANAGER = new ToolManager();  
	
	// Local id of the automatic tools extension point of this plugin
	private static final String AUTO_TOOLS_ID = "autoTools";

	private static final ExtractorDescription[] NO_EXTRACTOR_DESC = new ExtractorDescription[0];
	
	public static ToolManager getToolManager() {
		return MANAGER;
	}

	// Map from extractor id to extractor description
	private HashMap<String, ExtractorDescription> extractors;
	
	// Map from input type to list of extractor description
	private HashMap<IInternalElementType<?>, List<ExtractorDescription>> extractorsForType;
	
	// Map from tool id to tool description
	private HashMap<String, ToolDescription> tools;
	
	// singleton class
	private ToolManager() {
		// empty on purpose (fields are initialized to null by default).
	}
	
	private void add(ExtractorDescription extractorDesc) {
		final String id = extractorDesc.getId();
		extractors.put(id, extractorDesc);
		for (IInternalElementType<?> inputType : extractorDesc.getInputTypes()) {
			addExtractorForType(extractorDesc, inputType);
		}
	}
	
	private void addExtractorForType(ExtractorDescription extractorDesc,
			IInternalElementType<?> inputType) {

		List<ExtractorDescription> extractorSet = extractorsForType.get(inputType);
		if (extractorSet == null) {
			extractorSet = new LinkedList<ExtractorDescription>();
			extractorsForType.put(inputType, extractorSet);
		}
		if (! extractorSet.contains(extractorDesc)) {
			extractorSet.add(extractorDesc);
		}
	}
	
	private void add(ToolDescription toolDesc) {
		final String id = toolDesc.getId();
		if (tools.get(id) != null)
			Util.log(null, "Duplicate tool for id " + id); //$NON-NLS-1$
		tools.put(id, toolDesc);
	}

	private void computeToolList() {
		if (tools != null)
			// already done
			return;
		
		tools = new HashMap<String, ToolDescription>();
		extractors = new HashMap<String, ExtractorDescription>();
		extractorsForType = new HashMap<IInternalElementType<?>, List<ExtractorDescription>>();
		
		// Read the extension point extensions.
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] elements = 
			registry.getConfigurationElementsFor(RodinCore.PLUGIN_ID, AUTO_TOOLS_ID);
		for (IConfigurationElement element: elements) {
			ToolDescription toolDesc = new ToolDescription(element);
			add(toolDesc);
			IConfigurationElement[] children = element.getChildren();
			for (IConfigurationElement child : children) {
				ExtractorDescription extractorDesc = new ExtractorDescription(child, toolDesc);
				add(extractorDesc);
			}
		}
	}
	
	public ExtractorDescription[] getExtractorDescriptions(IInternalElementType<?> inputType) {
		computeToolList();
		List<ExtractorDescription> extractorSet = extractorsForType.get(inputType);
		if (extractorSet == null || extractorSet.size() == 0) {
			return NO_EXTRACTOR_DESC;
		}
		ExtractorDescription[] result = new ExtractorDescription[extractorSet.size()];
		int idx = 0;
		for (ExtractorDescription extractorDescription : extractorSet) {
			result[idx ++] = extractorDescription;
		}
		return result;
	}

	public ToolDescription getToolDescription(String id) {
		computeToolList();
		ToolDescription toolDesc = tools.get(id);
		if (toolDesc == null){
			return new NullToolDescription();
		}
		return toolDesc;
	}
	
	@SuppressWarnings("unused")
	private void removeExtractor(String id) {
		final ExtractorDescription extractorDesc = extractors.get(id);
		if (extractorDesc != null) {
			for (IInternalElementType<?> inputType : extractorDesc.getInputTypes()) {
				removeExtractorForType(inputType, extractorDesc);
			}
			extractors.remove(id);
		}
	}
	
	private void removeExtractorForType(IInternalElementType<?> inputType,
			ExtractorDescription extractorDesc) {

		List<ExtractorDescription> extractorSet = extractorsForType.get(inputType);
		if (extractorSet != null) { 
			extractorSet.remove(extractorDesc);
		}
	}

	@SuppressWarnings("unused")
	private void removeTool(String id) {
		tools.remove(id);
	}
	
}
